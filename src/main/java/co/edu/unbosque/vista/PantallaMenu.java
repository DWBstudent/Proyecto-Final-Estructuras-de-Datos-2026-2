package co.edu.unbosque.vista;

import java.net.URL;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/**
 * Esta es la pantalla del menu principal que solo muestra los botones va con el
 * ControladorMenu para decirle hacia a donde se mueve
 * 
 * responsable dANIEL
 */

public class PantallaMenu implements Pantalla {

	/** Ruta de la hoja de estilos dentro de los recursos. */
	private static final String RUTA_CSS = "/estilos/incio.css";

	/** Ruta del GIF animado dentro de los recursos. */
	private static final String RUTA_FONDO = "/imagenes/fondo-inicio.gif";

	/** Ancho máximo del contenedor de botones y mensajes. */
	private static final double ANCHO_MENU = 380;

	/** Contenedor principal del fondo, la sombra y los controles. */
	private final StackPane raiz = new StackPane();

	/** Capa que delimita el área visible del GIF. */
	private final Pane capaFondo = new Pane();

	/** Nodo que presenta la imagen animada. */
	private final ImageView vistaFondo = new ImageView();

	/** Información sobre la carga o el guardado de la partida. */
	private final Label estado = new Label();

	/** Aviso independiente para problemas de carga del fondo. */
	private final Label avisoFondo = new Label();

	/** Funciones que atienden los eventos de esta pantalla. */
	private final Acciones acciones;

	/**
	 * Agrupa las acciones que se conectan con los eventos del menú.
	 *
	 * <p>
	 * La vista conoce estas funciones, pero desconoce la clase que las implementa.
	 * El registro conserva sus referencias sin permitir que sean reemplazadas
	 * después de su construcción.
	 *
	 * @param alMostrar            acción que actualiza la pantalla al entrar
	 * @param modoHistoria         acción del botón Modo historia
	 * @param modoLibre            acción del botón Modo libre
	 * @param jugadorContraJugador acción del botón Jugador contra jugador
	 * @param coleccion            acción del botón Chest y Deck
	 * @param inventario           acción del botón Inventario
	 * @param contrasenas          acción del botón Contraseñas
	 * @param alternarMusica       acción que devuelve si la música quedó encendida
	 * @param salir                acción del botón Salir
	 */
	public record Acciones(Consumer<PantallaMenu> alMostrar, Runnable modoHistoria, Runnable modoLibre,
			Runnable jugadorContraJugador, Runnable coleccion, Runnable inventario, Runnable contrasenas,
			BooleanSupplier alternarMusica, Runnable salir) {

		/**
		 * Comprueba que todos los eventos tengan una acción asociada.
		 *
		 * @throws IllegalArgumentException si alguna acción es null
		 */
		public Acciones {
			if (alMostrar == null || modoHistoria == null || modoLibre == null || jugadorContraJugador == null
					|| coleccion == null || inventario == null || contrasenas == null || alternarMusica == null
					|| salir == null) {

				throw new IllegalArgumentException("Todas las acciones del menú son obligatorias");
			}
		}
	}

	/**
	 * Construye el menú y conecta sus eventos.
	 *
	 * <p>
	 * El fondo se carga en segundo plano. Su ausencia no impide utilizar los
	 * botones de la pantalla.
	 *
	 * @param acciones funciones asociadas a los eventos del menú
	 * @throws IllegalArgumentException si las acciones son null
	 * @throws IllegalStateException    si no existe la hoja de estilos
	 */
	public PantallaMenu(Acciones acciones) {
		if (acciones == null) {
			throw new IllegalArgumentException("El menú necesita sus acciones");
		}

		this.acciones = acciones;
		raiz.getStyleClass().add("inicio-retro");
		configurarCapaFondo();
		Region velo = new Region();
		velo.getStyleClass().add("inicio-velo");
		velo.setMouseTransparent(true);
		VBox menu = crearContenido();
		StackPane contenido = new StackPane(menu);
		contenido.setPadding(new Insets(400, 24, 24, 24));
		ScrollPane desplazamiento = new ScrollPane(contenido);
		desplazamiento.getStyleClass().add("inicio-scroll");
		desplazamiento.setFitToWidth(true);
		desplazamiento.setFitToHeight(true);
		desplazamiento.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		desplazamiento.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		/*
		 * Los nodos se dibujan en este orden: fondo animado, sombra negra y controles.
		 */
		raiz.getChildren().addAll(capaFondo, velo, desplazamiento);
		cargarEstilos();
		cargarFondo();
	}

	/**
	 * Construye el título, los botones y los mensajes del menú.
	 *
	 * <p>
	 * Los botones notifican las acciones recibidas durante la construcción de la
	 * pantalla.
	 *
	 * @return contenedor de los controles del menú
	 */
	private VBox crearContenido() {
		Label titulo = new Label("Acepta el duelo Desafía tu destino");
		titulo.getStyleClass().add("inicio-titulo");
		titulo.setWrapText(true);
		titulo.setMaxWidth(Double.MAX_VALUE);
		estado.getStyleClass().add("inicio-estado");
		estado.setWrapText(true);
		estado.setMaxWidth(Double.MAX_VALUE);
		avisoFondo.getStyleClass().add("inicio-estado");
		avisoFondo.setWrapText(true);
		avisoFondo.setMaxWidth(Double.MAX_VALUE);
		avisoFondo.setVisible(false);
		avisoFondo.setManaged(false);
		Button musica = crearBoton("Música: sí", () -> {
		});

		musica.setOnAction(evento -> {
			boolean encendida = acciones.alternarMusica().getAsBoolean();
			musica.setText(encendida ? "Música: sí" : "Música: no");
		});

		VBox contenido = new VBox(titulo, crearBoton("Modo historia", acciones.modoHistoria()),
				crearBoton("Modo libre", acciones.modoLibre()),
				crearBoton("Jugador contra jugador", acciones.jugadorContraJugador()),
				crearBoton("Chest y Deck", acciones.coleccion()), crearBoton("Inventario", acciones.inventario()),
				crearBoton("Contraseñas", acciones.contrasenas()), musica, crearBoton("Salir", acciones.salir()),
				estado, avisoFondo);
		contenido.getStyleClass().add("inicio-contenido");
		contenido.setAlignment(Pos.CENTER);
		contenido.setMaxWidth(ANCHO_MENU);
		contenido.setMaxHeight(Region.USE_PREF_SIZE);
		return contenido;
	}

	/**
	 * Crea un botón con el estilo de piedra compartido.
	 *
	 * @param texto  contenido visible del botón
	 * @param accion función que se ejecuta al activarlo
	 * @return botón configurado
	 */
	private static Button crearBoton(String texto, Runnable accion) {
		Button boton = new Button(texto);
		boton.getStyleClass().add("boton-piedra");
		boton.setMaxWidth(Double.MAX_VALUE);
		boton.setOnAction(evento -> accion.run());
		return boton;
	}

	/**
	 * Configura la capa del GIF y su área de recorte.
	 *
	 * <p>
	 * La imagen no participa en el cálculo del tamaño del menú ni recibe eventos de
	 * ratón.
	 *
	 * <p>
	 * El recorte oculta las partes que sobresalen cuando la imagen se amplía para
	 * cubrir el espacio disponible.
	 */
	private void configurarCapaFondo() {
		capaFondo.setMinSize(0, 0);
		capaFondo.setMouseTransparent(true);
		vistaFondo.setManaged(false);
		vistaFondo.setMouseTransparent(true);
		vistaFondo.setPreserveRatio(true);
		vistaFondo.setSmooth(true);
		capaFondo.getChildren().add(vistaFondo);
		Rectangle recorte = new Rectangle();
		recorte.widthProperty().bind(capaFondo.widthProperty());
		recorte.heightProperty().bind(capaFondo.heightProperty());
		capaFondo.setClip(recorte);
		capaFondo.widthProperty().addListener((observable, anterior, actual) -> ajustarFondo());
		capaFondo.heightProperty().addListener((observable, anterior, actual) -> ajustarFondo());
	}

	/**
	 * Carga la hoja de estilos desde los recursos de la aplicación.
	 *
	 * @throws IllegalStateException si no existe el archivo CSS
	 */
	private void cargarEstilos() {
		URL recurso = PantallaMenu.class.getResource(RUTA_CSS);
		if (recurso == null) {
			throw new IllegalStateException("No se encontró la hoja de estilos: " + RUTA_CSS);
		}

		raiz.getStylesheets().add(recurso.toExternalForm());
	}

	/**
	 * Carga el GIF en segundo plano y observa su resultado.
	 *
	 * <p>
	 * La repetición de la animación depende de la configuración incluida en el
	 * propio archivo GIF.
	 *
	 * <p>
	 * Si la carga falla, informa del problema y conserva disponibles los controles
	 * de la pantalla.
	 */
	private void cargarFondo() {
		URL recurso = PantallaMenu.class.getResource(RUTA_FONDO);
		if (recurso == null) {
			mostrarErrorFondo("No existe el recurso " + RUTA_FONDO);
			return;
		}

		try {
			Image imagen = new Image(recurso.toExternalForm(), true);
			vistaFondo.setImage(imagen);

			/*
			 * Durante la carga asíncrona las dimensiones pueden ser cero. Cuando se
			 * conocen, se ajusta el fondo.
			 */
			imagen.widthProperty().addListener((observable, anterior, actual) -> ajustarFondo());
			imagen.heightProperty().addListener((observable, anterior, actual) -> ajustarFondo());
			imagen.errorProperty().addListener((observable, anterior, hayError) -> {
				if (hayError) {
					mostrarErrorFondo(String.valueOf(imagen.getException()));
				}
			});

			/*
			 * Comprueba también el estado actual por si la carga terminó antes de registrar
			 * los observadores.
			 */
			if (imagen.isError()) {
				mostrarErrorFondo(String.valueOf(imagen.getException()));
			} else {
				ajustarFondo();
			}

		} catch (IllegalArgumentException excepcion) {
			mostrarErrorFondo(excepcion.getMessage());
		}
	}

	/**
	 * Escala el fondo para cubrir su contenedor sin deformarse.
	 *
	 * <p>
	 * Utiliza la mayor escala necesaria entre ancho y alto. El contenido sobrante
	 * queda centrado y recortado.
	 *
	 * <p>
	 * No realiza cambios mientras la imagen o el contenedor no tengan dimensiones
	 * válidas.
	 */
	private void ajustarFondo() {
		Image imagen = vistaFondo.getImage();
		if (imagen == null || imagen.isError()) {
			return;
		}

		double anchoImagen = imagen.getWidth();
		double altoImagen = imagen.getHeight();
		double anchoDisponible = capaFondo.getWidth();
		double altoDisponible = capaFondo.getHeight();
		if (anchoImagen <= 0 || altoImagen <= 0 || anchoDisponible <= 0 || altoDisponible <= 0) {
			return;
		}

		double escala = Math.max(anchoDisponible / anchoImagen, altoDisponible / altoImagen);
		double anchoFinal = anchoImagen * escala;
		double altoFinal = altoImagen * escala;
		vistaFondo.setFitWidth(anchoFinal);
		vistaFondo.setFitHeight(altoFinal);
		vistaFondo.setLayoutX((anchoDisponible - anchoFinal) / 2.0);
		vistaFondo.setLayoutY((altoDisponible - altoFinal) / 2.0);
	}

	/**
	 * Informa un problema del fondo sin impedir el uso del menú.
	 *
	 * @param detalle información técnica enviada a la consola
	 */
	private void mostrarErrorFondo(String detalle) {
		avisoFondo.setText("No se pudo cargar el fondo animado.");
		avisoFondo.setVisible(true);
		avisoFondo.setManaged(true);
		System.err.println("El menú continúa sin GIF: " + detalle);
	}

	/**
	 * Devuelve el contenedor principal de la pantalla.
	 *
	 * @return raíz que debe mostrar el navegador
	 */
	@Override
	public Parent getVista() {
		return raiz;
	}

	/**
	 * Notifica que la pantalla se ha mostrado.
	 *
	 * <p>
	 * La actualización del menú y el guardado de la partida corresponden a la
	 * acción registrada.
	 */
	@Override
	public void alEntrar() {
		acciones.alMostrar().accept(this);
	}

	/**
	 * Actualiza la información de carga o guardado de la partida.
	 *
	 * @param texto mensaje que se mostrará; null equivale a vacío
	 */
	public void mostrarEstado(String texto) {
		estado.setText(texto == null ? "" : texto);
	}
}