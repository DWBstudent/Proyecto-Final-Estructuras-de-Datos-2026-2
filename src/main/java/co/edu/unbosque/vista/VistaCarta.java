package co.edu.unbosque.vista;

import java.net.URL;

import co.edu.unbosque.modelo.entidades.Carta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * Una carta dibujada en código: marco de color según el tipo, imagen (si la carta tiene una
 * en los recursos), nombre y ATK/DEF. También dibuja una zona vacía y el reverso de una carta
 * boca abajo. Solo muestra datos: no sabe nada de las reglas.
 *
 * <p>Responsable: Santiago.
 */
public final class VistaCarta extends StackPane {

    public static final double ANCHO = 96;
    public static final double ALTO = 132;

    private static final String BORDE_NORMAL = "#f4e9cd";
    private static final String BORDE_SELECCIONADA = "#ffd60a";
    private static final String BORDE_OBJETIVO = "#ff6b6b";

    private final String fondo;
    private String borde = BORDE_NORMAL;
    private double grosor = 2;
    private String estiloBorde = "solid";

    private VistaCarta(String fondo) {
        this.fondo = fondo;
        setPrefSize(ANCHO, ALTO);
        setMinSize(ANCHO, ALTO);
        setMaxSize(ANCHO, ALTO);
        aplicarEstilo();
    }

    /**
     * Carta boca arriba. Si está en defensa, lo indica con una franja azul. Complejidad: O(1).
     *
     * @param bocaAbajo la carta es propia pero está oculta para el rival (se ve atenuada)
     */
    public static VistaCarta de(Carta carta, boolean enDefensa, boolean bocaAbajo) {
        VistaCarta vista = new VistaCarta(colorDelTipo(carta.getTipo().name()));
        vista.setOpacity(bocaAbajo ? 0.75 : 1);

        Label nombre = etiqueta(carta.getNombre(), 10.5, true);
        nombre.setWrapText(true);
        nombre.setTextAlignment(TextAlignment.CENTER);
        nombre.setMaxWidth(ANCHO - 8);
        nombre.setMaxHeight(30);

        StackPane centro = new StackPane();
        centro.setPrefSize(ANCHO - 12, 58);
        centro.setMaxSize(ANCHO - 12, 58);
        centro.setStyle("-fx-background-color: rgba(0,0,0,0.25); -fx-background-radius: 4;");
        Image imagen = cargarImagen(carta.getRutaImagen());
        if (imagen != null) {
            ImageView vistaImagen = new ImageView(imagen);
            vistaImagen.setFitWidth(ANCHO - 12);
            vistaImagen.setFitHeight(58);
            vistaImagen.setPreserveRatio(true);
            centro.getChildren().add(vistaImagen);
        } else {
            centro.getChildren().add(etiqueta(carta.getTipo().getNombreVisible(), 10, false));
        }

        Label valores = etiqueta("ATK " + carta.getAtaque() + "\nDEF " + carta.getDefensa(), 10.5, true);
        valores.setTextAlignment(TextAlignment.CENTER);

        VBox contenido = new VBox(3, nombre, centro, valores);
        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(4));
        vista.getChildren().add(contenido);

        if (enDefensa) {
            Label franja = etiqueta(bocaAbajo ? "DEFENSA · OCULTA" : "DEFENSA", 9, true);
            franja.setMaxWidth(Double.MAX_VALUE);
            franja.setAlignment(Pos.CENTER);
            franja.setStyle(franja.getStyle() + "-fx-background-color: rgba(29,78,137,0.9);");
            StackPane.setAlignment(franja, Pos.CENTER);
            vista.getChildren().add(franja);
        }
        return vista;
    }

    /** Reverso de una carta boca abajo del rival: no muestra nada de la carta. Complejidad: O(1). */
    public static VistaCarta reverso() {
        VistaCarta vista = new VistaCarta("#3d2a5c");
        Label signo = etiqueta("?", 34, true);
        Label texto = etiqueta("Boca abajo", 10, false);
        VBox contenido = new VBox(4, signo, texto);
        contenido.setAlignment(Pos.CENTER);
        vista.getChildren().add(contenido);
        return vista;
    }

    /** Zona o posición de la mano vacía. Complejidad: O(1). */
    public static VistaCarta vacia() {
        VistaCarta vista = new VistaCarta("rgba(255,255,255,0.06)");
        vista.borde = "rgba(255,255,255,0.35)";
        vista.estiloBorde = "dashed";
        vista.aplicarEstilo();
        return vista;
    }

    /** Resalta la carta elegida (atacante o carta de la mano). Complejidad: O(1). */
    public VistaCarta seleccionada(boolean si) {
        if (si) {
            borde = BORDE_SELECCIONADA;
            grosor = 4;
            aplicarEstilo();
        }
        return this;
    }

    /** Marca un monstruo rival que se puede atacar. Complejidad: O(1). */
    public VistaCarta objetivo(boolean si) {
        if (si) {
            borde = BORDE_OBJETIVO;
            grosor = 3;
            aplicarEstilo();
        }
        return this;
    }

    /** Atenúa un monstruo que ya atacó en este turno. Complejidad: O(1). */
    public VistaCarta gastada(boolean si) {
        if (si) {
            setOpacity(0.6);
        }
        return this;
    }

    private void aplicarEstilo() {
        setStyle("-fx-background-color: " + fondo + "; -fx-background-radius: 7; -fx-border-radius: 7;"
                + " -fx-border-color: " + borde + "; -fx-border-width: " + grosor + ";"
                + " -fx-border-style: " + estiloBorde + ";");
    }

    private static Label etiqueta(String texto, double tamanio, boolean negrita) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle("-fx-text-fill: white; -fx-font-size: " + tamanio + "px;"
                + (negrita ? " -fx-font-weight: bold;" : ""));
        return etiqueta;
    }

    /** Busca la imagen en los recursos; si no existe, devuelve {@code null}. */
    private static Image cargarImagen(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }
        URL recurso = VistaCarta.class.getResource(ruta.startsWith("/") ? ruta : "/" + ruta);
        if (recurso == null) {
            return null;
        }
        return new Image(recurso.toExternalForm(), ANCHO * 2, 116, true, true, false);
    }

    /**
     * Color del marco según el tipo. Se compara por nombre (no con constantes del enum)
     * para que compile aunque cambie la lista de tipos.
     */
    private static String colorDelTipo(String tipo) {
        switch (tipo) {
            case "DRAGON":
                return "#9b2c2c";
            case "GUERRERO":
                return "#8a5a2b";
            case "LANZADOR_DE_CONJUROS":
                return "#5b2a86";
            case "BESTIA":
            case "BESTIA_GUERRERA":
            case "BESTIA_ALADA":
                return "#6b5b16";
            case "BESTIA_DIVINA":
                return "#b08900";
            case "DEMONIO":
            case "ZOMBI":
                return "#3f2349";
            case "MAQUINA":
            case "ROCA":
                return "#4d5960";
            case "AGUA":
            case "SERPIENTE_MARINA":
                return "#1c5a85";
            case "PLANTA":
            case "INSECTO":
                return "#2d6a3e";
            case "TRUENO":
            case "PIROTECNIA":
                return "#a04a12";
            case "HADA":
                return "#a2457a";
            case "DINOSAURIO":
                return "#5e4b3c";
            default:
                return "#4a4a4a";
        }
    }
}
