package co.edu.unbosque.vista;

import java.net.URL;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Selección de rival del modo historia o del modo libre. Muestra los 3 rivales
 * con su estado ("Siguiente rival", "Vencido", "Bloqueado"…) y un botón para
 * duelar contra los que el controlador habilite. Cada vez que se muestra, le
 * pide los datos al controlador, así después de un duelo se ve el progreso
 * nuevo.
 *
 * <p>
 * Responsable: Santiago.
 */
public class PantallaSeleccionRival implements Pantalla {

	/** Recurso que contiene el tema visual de esta pantalla. */
	private static final String RUTA_CSS = "/estilos/historia.css";

	/** Contenedor principal del fondo, la capa oscura y el contenido. */
	private final StackPane raiz;

	/** Contenedor de las filas de rivales. */
	private final VBox filas = new VBox();

	/** Mensaje informativo enviado por el controlador. */
	private final Label mensaje = new Label();

	/** Acción que solicita los datos actualizados de los rivales. */
	private final Consumer<PantallaSeleccionRival> alMostrar;

	/** Acción que comunica la posición del rival elegido. */
	private final IntConsumer alElegir;

	/**
	 * Construye la pantalla y conecta sus acciones de navegación.
	 *
	 * <p>
	 * Conserva el contrato utilizado por {@code ControladorPartida}. No carga
	 * rivales durante la construcción; solicita esa información cuando el navegador
	 * invoca {@link #alEntrar()}.
	 *
	 * @param titulo      nombre del modo de juego
	 * @param descripcion explicación breve del modo
	 * @param alMostrar   acción que actualiza la lista de rivales
	 * @param alElegir    acción que recibe el índice del rival seleccionado,
	 *                    comenzando en cero
	 * @param volver      acción que regresa a la pantalla anterior
	 * @throws IllegalArgumentException si algún argumento es {@code null}
	 * @throws IllegalStateException    si no se encuentra la hoja de estilos
	 */
	public PantallaSeleccionRival(String titulo, String descripcion, Consumer<PantallaSeleccionRival> alMostrar,
			IntConsumer alElegir, Runnable volver) {

		if (titulo == null || descripcion == null || alMostrar == null || alElegir == null || volver == null) {
			throw new IllegalArgumentException("Faltan datos para la selección de rival");
		}

		this.alMostrar = alMostrar;
		this.alElegir = alElegir;

		Label encabezado = new Label(titulo);
		encabezado.getStyleClass().add("historia-titulo");
		encabezado.setWrapText(true);
		encabezado.setMaxWidth(Double.MAX_VALUE);

		Label explicacion = new Label(descripcion);
		explicacion.getStyleClass().add("historia-descripcion");
		explicacion.setWrapText(true);
		explicacion.setMaxWidth(Double.MAX_VALUE);

		filas.getStyleClass().add("historia-filas");
		filas.setMaxWidth(Double.MAX_VALUE);
		mensaje.getStyleClass().add("historia-mensaje");
		mensaje.setWrapText(true);
		mensaje.setMaxWidth(Double.MAX_VALUE);

		Button botonVolver = new Button("Volver");
		botonVolver.getStyleClass().addAll("boton-piedra", "boton-volver");
		botonVolver.setOnAction(evento -> volver.run());
		VBox panel = new VBox(encabezado, explicacion, filas, mensaje, botonVolver);
		panel.getStyleClass().add("historia-panel");
		panel.setAlignment(Pos.CENTER);
		panel.setMaxWidth(820);
		panel.setMaxHeight(Region.USE_PREF_SIZE);

		/*
		 * Centra el panel cuando hay espacio. Su tamaño mínimo permite que el
		 * ScrollPane habilite el desplazamiento si la ventana resulta demasiado baja.
		 */
		StackPane contenido = new StackPane(panel);
		contenido.setPadding(new Insets(30));
		ScrollPane desplazamiento = new ScrollPane(contenido);
		desplazamiento.getStyleClass().add("historia-scroll");
		desplazamiento.setFitToWidth(true);
		desplazamiento.setFitToHeight(true);
		desplazamiento.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		desplazamiento.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		/*
		 * La capa oscura ocupa todo el fondo. No recibe eventos para permitir la
		 * interacción con los controles.
		 */
		Region velo = new Region();
		velo.getStyleClass().add("historia-velo");
		velo.setMouseTransparent(true);
		raiz = new StackPane(velo, desplazamiento);
		raiz.getStyleClass().add("historia-retro");
		cargarEstilos();
	}

	/**
	 * Actualiza las filas de rivales y el mensaje de la pantalla.
	 *
	 * <p>
	 * Las posiciones de los tres arreglos deben corresponder al mismo rival. La
	 * disponibilidad del botón depende exclusivamente del arreglo
	 * {@code habilitados}, calculado por el controlador.
	 *
	 * <p>
	 * Complejidad: O(n), donde n es la cantidad de rivales.
	 *
	 * @param nombres     nombres que se mostrarán en las filas
	 * @param estados     textos de estado de los rivales
	 * @param habilitados indica cuáles botones permiten iniciar un duelo
	 * @param texto       mensaje inferior; {@code null} equivale a un texto vacío
	 * @throws IllegalArgumentException si los arreglos son {@code null}, tienen
	 *                                  longitudes diferentes o contienen nombres o
	 *                                  estados nulos
	 */
	public void mostrarRivales(String[] nombres, String[] estados, boolean[] habilitados, String texto) {
		validarRivales(nombres, estados, habilitados);
		filas.getChildren().clear();
		for (int indice = 0; indice < nombres.length; indice++) {
			filas.getChildren().add(crearFila(indice, nombres[indice], estados[indice], habilitados[indice]));
		}

		String textoMensaje = texto == null ? "" : texto;
		mensaje.setText(textoMensaje);
		boolean tieneMensaje = !textoMensaje.isBlank();
		mensaje.setVisible(tieneMensaje);
		mensaje.setManaged(tieneMensaje);
	}

	/**
	 * Construye la representación visual de un rival.
	 *
	 * <p>
	 * El estado modifica su presentación. La habilitación del botón conserva el
	 * valor enviado por el controlador.
	 *
	 * @param indice     posición que se enviará al seleccionar el rival
	 * @param nombre     nombre visible del rival
	 * @param estado     descripción de su estado
	 * @param habilitado indica si se permite iniciar el duelo
	 * @return fila con insignia, información y botón de duelo
	 */
	private HBox crearFila(int indice, String nombre, String estado, boolean habilitado) {
		Label insignia = new Label(simboloDelEstado(estado));
		insignia.getStyleClass().add("rival-insignia");

		/*
		 * La insignia es decorativa. La información equivalente también aparece en la
		 * etiqueta de estado.
		 */
		insignia.setMouseTransparent(true);

		Label etiquetaNombre = new Label(nombre);
		etiquetaNombre.getStyleClass().add("rival-nombre");
		etiquetaNombre.setWrapText(true);
		etiquetaNombre.setMinWidth(0);
		etiquetaNombre.setMaxWidth(Double.MAX_VALUE);

		Label etiquetaEstado = new Label(estado);
		etiquetaEstado.getStyleClass().add("rival-estado");
		etiquetaEstado.setWrapText(true);

		VBox textos = new VBox(5, etiquetaNombre, etiquetaEstado);
		textos.setAlignment(Pos.CENTER_LEFT);
		textos.setMinWidth(0);
		textos.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(textos, Priority.ALWAYS);

		Button duelar = new Button("Duelar");
		duelar.getStyleClass().addAll("boton-piedra", "boton-duelar");
		duelar.setDisable(!habilitado);
		duelar.setAccessibleText("Duelar contra " + nombre);
		duelar.setOnAction(evento -> alElegir.accept(indice));

		HBox fila = new HBox(insignia, textos, duelar);
		fila.getStyleClass().addAll("fila-rival", claseDelEstado(estado));
		fila.setAlignment(Pos.CENTER_LEFT);

		return fila;
	}

	/**
	 * Traduce los estados de presentación del controlador a las clases definidas en
	 * la hoja de estilos.
	 *
	 * @param estado texto de estado recibido
	 * @return nombre de la clase CSS correspondiente
	 */
	private static String claseDelEstado(String estado) {
		return switch (estado) {
		case "Vencido" -> "rival-vencido";
		case "Bloqueado" -> "rival-bloqueado";
		case "Siguiente rival" -> "rival-siguiente";
		default -> "rival-disponible";
		};
	}

	/**
	 * Selecciona un símbolo decorativo según el estado.
	 *
	 * @param estado texto de estado recibido
	 * @return marca de victoria, candado o espadas
	 */
	private static String simboloDelEstado(String estado) {
		return switch (estado) {
		case "Vencido" -> "\u2713";
		case "Bloqueado" -> "\uD83D\uDD12";
		default -> "\u2694";
		};
	}

	/**
	 * Comprueba la coherencia de los arreglos antes de modificar la vista.
	 *
	 * <p>
	 * Complejidad: O(n), donde n es la cantidad de rivales.
	 *
	 * @param nombres     nombres de los rivales
	 * @param estados     estados de los rivales
	 * @param habilitados disponibilidad de los botones
	 * @throws IllegalArgumentException si los datos son inconsistentes
	 */
	private static void validarRivales(String[] nombres, String[] estados, boolean[] habilitados) {
		if (nombres == null || estados == null || habilitados == null) {
			throw new IllegalArgumentException("Los arreglos de rivales no pueden ser null");
		}

		if (nombres.length != estados.length || nombres.length != habilitados.length) {
			throw new IllegalArgumentException("Los arreglos de rivales deben tener la misma longitud");
		}

		for (int indice = 0; indice < nombres.length; indice++) {
			if (nombres[indice] == null || estados[indice] == null) {
				throw new IllegalArgumentException("El nombre y el estado no pueden ser null: " + indice);
			}
		}
	}

	/**
	 * Carga la hoja de estilos desde los recursos de la aplicación.
	 *
	 * @throws IllegalStateException si no existe el recurso CSS
	 */
	private void cargarEstilos() {
		URL recurso = PantallaSeleccionRival.class.getResource(RUTA_CSS);
		if (recurso == null) {
			throw new IllegalStateException("No se encontró la hoja de estilos: " + RUTA_CSS);
		}

		raiz.getStylesheets().add(recurso.toExternalForm());
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
	 * Solicita al controlador los datos actualizados.
	 *
	 * <p>
	 * Permite reflejar el progreso después de regresar de un duelo. Su complejidad
	 * depende de la acción registrada.
	 */
	@Override
	public void alEntrar() {
		alMostrar.accept(this);
	}
}