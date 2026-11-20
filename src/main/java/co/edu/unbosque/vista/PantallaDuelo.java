package co.edu.unbosque.vista;

import java.net.URL;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Tablero del duelo. Arriba, el rival (sus puntos, su mazo y su campo; la mano
 * no se ve). Abajo, quien juega: su campo y su mano. A la derecha, la
 * indicación de qué hacer, los botones y el registro de lo que pasó.
 *
 * <p>
 * Cómo se juega: clic en una carta de la mano y luego "Invocar en ataque" o "en
 * defensa"; clic en un monstruo propio en ataque y luego en un monstruo rival
 * (o "Ataque directo"); "Terminar turno" al acabar.
 *
 * <p>
 * No tiene reglas: cada clic se lo pasa al {@link ControladorDuelo} y
 * {@link #refrescar()} vuelve a dibujar todo con lo que el controlador indica.
 *
 * <p>
 * Responsable: Santiago.
 */
public class PantallaDuelo implements Pantalla {

	/** Cantidad de posiciones que muestra cada fila de esta interfaz. */
	private static final int POSICIONES = 5;

	/**
	 * Eventos que el controlador conecta con el tablero.
	 * 
	 * @param alMostrar      acción que entrega los datos de presentación
	 * @param mano           acción al pulsar una posición de la mano
	 * @param zonaPropia     acción al pulsar una zona del campo propio
	 * @param zonaRival      acción al pulsar una zona del campo rival
	 * @param invocarAtaque  acción para invocar en ataque
	 * @param invocarDefensa acción para invocar en defensa
	 * @param atacarDirecto  acción de ataque directo
	 * @param terminarTurno  acción para terminar el turno
	 * @param abandonar      acción para salir del duelo
	 */
	public record Acciones(Consumer<PantallaDuelo> alMostrar, IntConsumer mano, IntConsumer zonaPropia,
			IntConsumer zonaRival, Runnable invocarAtaque, Runnable invocarDefensa, Runnable atacarDirecto,
			Runnable terminarTurno, Runnable abandonar) {
		/** Valida que todos los eventos tengan una acción asociada. */
		public Acciones {
			if (alMostrar == null || mano == null || zonaPropia == null || zonaRival == null || invocarAtaque == null
					|| invocarDefensa == null || atacarDirecto == null || terminarTurno == null || abandonar == null) {
				throw new IllegalArgumentException("Faltan acciones para el tablero");
			}
		}
	}

	private final Acciones acciones;
	private final StackPane raiz = new StackPane();
	private final VBox tablero = new VBox(10);
	private final Label infoArriba = etiqueta("duelista-info");
	private final Label infoAbajo = etiqueta("duelista-info");
	private final Label estado = etiqueta("duelo-turno");
	private final Label mensaje = etiqueta("duelo-mensaje");
	private final VBox registro = new VBox(8);
	private final VistaCarta[] rivales = new VistaCarta[POSICIONES];
	private final VistaCarta[] propias = new VistaCarta[POSICIONES];
	private final VistaCarta[] mano = new VistaCarta[POSICIONES];
	private final Button invocarAtaque;
	private final Button invocarDefensa;
	private final Button atacarDirecto;
	private final Button terminarTurno;

	/**
	 * Construye el tablero y el panel de acciones.
	 * 
	 * @param acciones funciones que reciben los eventos del usuario
	 * @throws IllegalArgumentException si las acciones son null
	 * @throws IllegalStateException    si no existe duelo.css
	 */
	public PantallaDuelo(Acciones acciones) {
		if (acciones == null) {
			throw new IllegalArgumentException("El duelo necesita sus acciones");
		}
		this.acciones = acciones;
		raiz.getStyleClass().add("duelo-retro");
		Region velo = new Region();
		velo.getStyleClass().add("duelo-velo");
		velo.setMouseTransparent(true);

		HBox campoRival = crearFila(rivales, acciones.zonaRival());
		HBox campoPropio = crearFila(propias, acciones.zonaPropia());
		HBox filaMano = crearFila(mano, acciones.mano());
		Label tituloMano = etiqueta("duelo-titulo-mano");
		tituloMano.setText("TU MANO");
		tablero.getStyleClass().add("duelo-tablero");
		tablero.setAlignment(Pos.CENTER);
		tablero.setMinSize(652, 640);
		tablero.getChildren().addAll(infoArriba, campoRival, estado, campoPropio, tituloMano, filaMano, infoAbajo);
		tablero.widthProperty().addListener((o, antes, ahora) -> ajustarCartas());
		tablero.heightProperty().addListener((o, antes, ahora) -> ajustarCartas());

		ScrollPane areaTablero = scroll(tablero);
		areaTablero.setFitToHeight(true);

		invocarAtaque = boton("Invocar en ataque", acciones.invocarAtaque());
		invocarDefensa = boton("Invocar en defensa", acciones.invocarDefensa());
		atacarDirecto = boton("Ataque directo", acciones.atacarDirecto());
		terminarTurno = boton("Terminar turno", acciones.terminarTurno());
		terminarTurno.getStyleClass().add("boton-principal");
		Button abandonar = boton("Abandonar duelo", acciones.abandonar());

		Label tituloRegistro = etiqueta("duelo-titulo-registro");
		tituloRegistro.setText("LO QUE PASÓ");
		registro.getStyleClass().add("duelo-registro");
		ScrollPane areaRegistro = scroll(registro);
		VBox.setVgrow(areaRegistro, Priority.ALWAYS);
		areaRegistro.setMinHeight(60);
		mensaje.setMinHeight(65);

		VBox panel = new VBox(12, mensaje, invocarAtaque, invocarDefensa, atacarDirecto, terminarTurno, tituloRegistro,
				areaRegistro, abandonar);
		panel.getStyleClass().add("duelo-acciones");
		panel.setMinWidth(300);
		panel.setPrefWidth(300);

		BorderPane distribucion = new BorderPane();
		distribucion.setCenter(areaTablero);
		distribucion.setRight(panel);
		raiz.getChildren().addAll(velo, distribucion);

		URL css = PantallaDuelo.class.getResource("/estilos/duelo.css");
		if (css == null) {
			throw new IllegalStateException("No se encontró /estilos/duelo.css");
		}
		raiz.getStylesheets().add(css.toExternalForm());
		mostrarAcciones(false, false, false);
	}

	/**
	 * Entrega los textos del duelo sin consultar el modelo.
	 * 
	 * @param rival      resumen del duelista superior
	 * @param jugador    resumen del duelista inferior
	 * @param turno      información del turno o del ganador
	 * @param indicacion instrucción de la acción que se espera
	 */
	public void mostrarResumen(String rival, String jugador, String turno, String indicacion) {
		infoArriba.setText(rival);
		infoAbajo.setText(jugador);
		estado.setText(turno);
		mensaje.setText(indicacion);
	}

	/**
	 * Actualiza las cartas de las tres filas. Los arreglos tienen cinco posiciones.
	 * 
	 * @param campoRival  datos públicos del campo rival
	 * @param campoPropio datos del campo propio
	 * @param cartasMano  datos de la mano del jugador que controla el tablero
	 * @throws IllegalArgumentException si falta una fila o alguno de sus datos
	 */
	public void mostrarCartas(VistaCarta.Datos[] campoRival, VistaCarta.Datos[] campoPropio,
			VistaCarta.Datos[] cartasMano) {
		validarFila(campoRival);
		validarFila(campoPropio);
		validarFila(cartasMano);
		for (int i = 0; i < POSICIONES; i++) {
			rivales[i].mostrar(campoRival[i]);
			propias[i].mostrar(campoPropio[i]);
			mano[i].mostrar(cartasMano[i]);
		}
	}

	/**
	 * Aplica la disponibilidad calculada por el controlador.
	 * 
	 * @param invocar  permite invocar en ataque o defensa
	 * @param directo  permite atacar directamente
	 * @param terminar permite terminar el turno
	 */
	public void mostrarAcciones(boolean invocar, boolean directo, boolean terminar) {
		invocarAtaque.setDisable(!invocar);
		invocarDefensa.setDisable(!invocar);
		atacarDirecto.setDisable(!directo);
		terminarTurno.setDisable(!terminar);
	}

	/**
	 * Sustituye el registro visible del duelo.
	 * 
	 * @param eventos mensajes preparados por el controlador
	 */
	public void mostrarRegistro(String[] eventos) {
		registro.getChildren().clear();
		for (String evento : eventos) {
			Label linea = etiqueta("duelo-evento");
			linea.setText("• " + evento);
			registro.getChildren().add(linea);
		}
	}

	/** Solicita una actualización mediante la acción registrada. */
	public void refrescar() {
		acciones.alMostrar().accept(this);
	}

	/** @return raíz del tablero */
	@Override
	public Parent getVista() {
		return raiz;
	}

	/** Solicita los datos al mostrarse, también al volver del cambio de turno. */
	@Override
	public void alEntrar() {
		refrescar();
	}

	/** Distribuye cinco posiciones uniformemente a lo ancho de cada fila. */
	private HBox crearFila(VistaCarta[] cartas, IntConsumer alElegir) {
		HBox fila = new HBox(18);
		fila.getStyleClass().add("duelo-fila");
		fila.setAlignment(Pos.CENTER);
		VBox.setVgrow(fila, Priority.ALWAYS);
		for (int i = 0; i < cartas.length; i++) {
			VistaCarta carta = new VistaCarta();
			int indice = i;
			carta.setOnMouseClicked(evento -> alElegir.accept(indice));
			StackPane casilla = new StackPane(carta);
			casilla.setMinSize(0, 0);
			casilla.setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(casilla, Priority.ALWAYS);
			fila.getChildren().add(casilla);
			cartas[i] = carta;
		}
		return fila;
	}

	/**
	 * Ajusta las cartas a ambas dimensiones; en ventanas pequeñas permite desplazar
	 * el tablero.
	 */
	private void ajustarCartas() {
		double porAncho = (tablero.getWidth() - 40 - 4 * 18) / POSICIONES;
		double porAlto = (tablero.getHeight() - 180) / (3 * 1.40);
		double ancho = Math.max(108, Math.min(164, Math.min(porAncho, porAlto)));
		for (int i = 0; i < POSICIONES; i++) {
			rivales[i].ajustarTamanio(ancho);
			propias[i].ajustarTamanio(ancho);
			mano[i].ajustarTamanio(ancho);
		}
	}

	private static void validarFila(VistaCarta.Datos[] datos) {
		if (datos == null || datos.length != POSICIONES) {
			throw new IllegalArgumentException("Cada fila debe tener cinco posiciones");
		}
		for (VistaCarta.Datos dato : datos) {
			if (dato == null) {
				throw new IllegalArgumentException("Las posiciones no pueden ser null");
			}
		}
	}

	private static Label etiqueta(String clase) {
		Label etiqueta = new Label();
		etiqueta.getStyleClass().add(clase);
		etiqueta.setWrapText(true);
		etiqueta.setMaxWidth(Double.MAX_VALUE);
		etiqueta.setAlignment(Pos.CENTER);
		return etiqueta;
	}

	private static Button boton(String texto, Runnable accion) {
		Button boton = new Button(texto);
		boton.getStyleClass().add("boton-piedra");
		boton.setMaxWidth(Double.MAX_VALUE);
		boton.setOnAction(evento -> accion.run());
		return boton;
	}

	private static ScrollPane scroll(Region contenido) {
		ScrollPane scroll = new ScrollPane(contenido);
		scroll.getStyleClass().add("duelo-scroll");
		scroll.setFitToWidth(true);
		return scroll;
	}
}
