package co.edu.unbosque.vista;

import co.edu.unbosque.controlador.ControladorDuelo;
import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.juego.Duelista;
import co.edu.unbosque.modelo.juego.MonstruoEnCampo;
import co.edu.unbosque.modelo.juego.Posicion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Tablero del duelo. Arriba, el rival (sus puntos, su mazo y su campo; la mano no se ve).
 * Abajo, quien juega: su campo y su mano. A la derecha, la indicación de qué hacer, los
 * botones y el registro de lo que pasó.
 *
 * <p>Cómo se juega: clic en una carta de la mano y luego "Invocar en ataque" o "en defensa";
 * clic en un monstruo propio en ataque y luego en un monstruo rival (o "Ataque directo");
 * "Terminar turno" al acabar.
 *
 * <p>No tiene reglas: cada clic se lo pasa al {@link ControladorDuelo} y {@link #refrescar()}
 * vuelve a dibujar todo con lo que el controlador indica.
 *
 * <p>Responsable: Santiago.
 */
public class PantallaDuelo implements Pantalla {

    private static final String TEXTO_CLARO = "-fx-text-fill: white;";

    private final ControladorDuelo controlador;
    private final BorderPane raiz = new BorderPane();

    private final Label infoArriba = etiqueta(15, true);
    private final HBox campoArriba = filaDeCartas();
    private final Label estado = etiqueta(17, true);
    private final HBox campoAbajo = filaDeCartas();
    private final HBox manoAbajo = filaDeCartas();
    private final Label infoAbajo = etiqueta(15, true);

    private final Label mensaje = etiqueta(14, false);
    private final Button invocarEnAtaque = boton("Invocar en ataque");
    private final Button invocarEnDefensa = boton("Invocar en defensa");
    private final Button ataqueDirecto = boton("Ataque directo");
    private final Button terminarTurno = boton("Terminar turno");
    private final VBox registro = new VBox(3);

    /**
     * Arma el tablero. Complejidad: O(1).
     *
     * @throws IllegalArgumentException si el controlador es {@code null}
     */
    public PantallaDuelo(ControladorDuelo controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El duelo necesita su controlador");
        }
        this.controlador = controlador;

        estado.setStyle(estado.getStyle() + "-fx-text-fill: #ffd60a;");
        Label tituloMano = etiqueta(13, false);
        tituloMano.setText("Tu mano");
        VBox tablero = new VBox(8, infoArriba, campoArriba, estado, campoAbajo, tituloMano, manoAbajo, infoAbajo);
        tablero.setAlignment(Pos.CENTER);
        tablero.setPadding(new Insets(12));

        invocarEnAtaque.setOnAction(e -> controlador.invocar(Posicion.ATAQUE));
        invocarEnDefensa.setOnAction(e -> controlador.invocar(Posicion.DEFENSA));
        ataqueDirecto.setOnAction(e -> controlador.atacarDirecto());
        terminarTurno.setOnAction(e -> controlador.terminarTurno());
        terminarTurno.setStyle(terminarTurno.getStyle() + "-fx-font-weight: bold;");
        Button abandonar = boton("Abandonar duelo");
        abandonar.setOnAction(e -> controlador.abandonar());

        mensaje.setWrapText(true);
        mensaje.setMinHeight(60);
        mensaje.setStyle(mensaje.getStyle() + "-fx-text-fill: #ffd60a;");
        Label tituloRegistro = etiqueta(14, true);
        tituloRegistro.setText("Lo que pasó");
        Region espacio = new Region();
        VBox.setVgrow(espacio, Priority.ALWAYS);

        VBox panel = new VBox(10, mensaje, invocarEnAtaque, invocarEnDefensa, ataqueDirecto, terminarTurno,
                tituloRegistro, registro, espacio, abandonar);
        panel.setPadding(new Insets(14));
        panel.setPrefWidth(300);
        panel.setMinWidth(300);
        panel.setStyle("-fx-background-color: rgba(0,0,0,0.30);");

        raiz.setCenter(tablero);
        raiz.setRight(panel);
        raiz.setStyle("-fx-background-color: #123524;");
    }

    /** Vuelve a dibujar el tablero con el estado actual del duelo. Complejidad: O(1) (5 zonas y 5 cartas por fila). */
    public void refrescar() {
        Duelista abajo = controlador.getAbajo();
        Duelista arriba = controlador.getArriba();
        boolean eligiendoObjetivo = controlador.getAtacanteSeleccionado() != -1;

        infoArriba.setText(resumen(arriba) + "   ·   Mano " + arriba.cartasEnMano());
        infoAbajo.setText(resumen(abajo));
        estado.setText(controlador.getEstado());

        campoArriba.getChildren().clear();
        for (int zona = 0; zona < Duelista.ZONAS_DE_MONSTRUO; zona++) {
            MonstruoEnCampo monstruo = arriba.getMonstruo(zona);
            VistaCarta vista;
            if (monstruo == null) {
                vista = VistaCarta.vacia();
            } else if (monstruo.estaBocaAbajo()) {
                vista = VistaCarta.reverso().objetivo(eligiendoObjetivo);
            } else {
                vista = VistaCarta.de(monstruo.getCarta(), monstruo.getPosicion() == Posicion.DEFENSA, false)
                        .objetivo(eligiendoObjetivo);
            }
            int z = zona;
            vista.setOnMouseClicked(e -> controlador.clicZonaRival(z));
            campoArriba.getChildren().add(vista);
        }

        campoAbajo.getChildren().clear();
        for (int zona = 0; zona < Duelista.ZONAS_DE_MONSTRUO; zona++) {
            MonstruoEnCampo monstruo = abajo.getMonstruo(zona);
            VistaCarta vista;
            if (monstruo == null) {
                vista = VistaCarta.vacia();
            } else {
                vista = VistaCarta.de(monstruo.getCarta(), monstruo.getPosicion() == Posicion.DEFENSA,
                                monstruo.estaBocaAbajo())
                        .gastada(monstruo.yaAtaco())
                        .seleccionada(zona == controlador.getAtacanteSeleccionado())
                        .objetivo(controlador.esperaZonaParaReemplazar());
            }
            int z = zona;
            vista.setOnMouseClicked(e -> controlador.clicZonaPropia(z));
            campoAbajo.getChildren().add(vista);
        }

        manoAbajo.getChildren().clear();
        for (int posicion = 0; posicion < Duelista.TAMANIO_MANO; posicion++) {
            Carta carta = abajo.getCartaEnMano(posicion);
            VistaCarta vista = carta == null
                    ? VistaCarta.vacia()
                    : VistaCarta.de(carta, false, false).seleccionada(posicion == controlador.getManoSeleccionada());
            int p = posicion;
            vista.setOnMouseClicked(e -> controlador.clicMano(p));
            manoAbajo.getChildren().add(vista);
        }

        mensaje.setText(controlador.getMensaje());
        invocarEnAtaque.setDisable(!controlador.puedeInvocar());
        invocarEnDefensa.setDisable(!controlador.puedeInvocar());
        ataqueDirecto.setDisable(!controlador.puedeAtacarDirecto());
        terminarTurno.setDisable(!controlador.puedeTerminarTurno());

        registro.getChildren().clear();
        for (String evento : controlador.getEventos()) {
            Label linea = etiqueta(12, false);
            linea.setText("• " + evento);
            linea.setWrapText(true);
            registro.getChildren().add(linea);
        }
    }

    /** Complejidad: O(1). */
    @Override
    public Parent getVista() {
        return raiz;
    }

    /** Dibuja el estado al mostrarse (también al volver del cambio de turno). Complejidad: O(1). */
    @Override
    public void alEntrar() {
        refrescar();
    }

    private static String resumen(Duelista duelista) {
        return duelista.getNombre() + "   ·   PV " + duelista.getPuntosDeVida() + "   ·   Mazo "
                + duelista.cartasEnMazo();
    }

    private static HBox filaDeCartas() {
        HBox fila = new HBox(10);
        fila.setAlignment(Pos.CENTER);
        return fila;
    }

    private static Label etiqueta(double tamanio, boolean negrita) {
        Label etiqueta = new Label();
        etiqueta.setStyle(TEXTO_CLARO + "-fx-font-size: " + tamanio + "px;" + (negrita ? "-fx-font-weight: bold;" : ""));
        return etiqueta;
    }

    private static Button boton(String texto) {
        Button boton = new Button(texto);
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setStyle("-fx-font-size: 14px;");
        return boton;
    }
}
