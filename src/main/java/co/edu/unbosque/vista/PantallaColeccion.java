package co.edu.unbosque.vista;

import java.net.URL;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.css.PseudoClass;
import javafx.scene.control.TextField;

/**
 * Vista base del cofre y la baraja. Muestra las filas y el detalle que entrega
 * el controlador, y le comunica las acciones del usuario.
 *
 * <p>
 * Responsable: Brayan.
 */
public abstract class PantallaColeccion implements Pantalla {

	/** Contenedor del buscador, utilizado por la pantalla del cofre. */
	private final HBox barraBusqueda = new HBox(8);

	/** Contenedor principal de la pantalla. */
	private final BorderPane raiz = new BorderPane();

	/** Texto visible de cada copia de carta. */
	private final ListView<String> lista = new ListView<>();

	/** Imagen de la carta seleccionada. */
	private final ImageView imagen = new ImageView();

	/** Nombre visible de la carta seleccionada. */
	private final Label nombre = new Label();

	/** Tipo, ataque y defensa de la carta seleccionada. */
	private final Label estadisticas = new Label();

	/** Descripción visible de la carta seleccionada. */
	private final Label descripcion = new Label();

	/** Resumen del contenido del cofre o de la baraja. */
	private final Label cantidad = new Label();

	/** Resultado de la última acción. */
	private final Label mensaje = new Label();

	/** Acción que solicita los datos al entrar en la pantalla. */
	private final Consumer<PantallaColeccion> alMostrar;

	/** Estado visual que aplica el acabado iridiscente al detalle. */
	private static final PseudoClass CARTA_PODEROSA = PseudoClass.getPseudoClass("carta-poderosa");

	/** Contenedor del detalle de la carta seleccionada. */
	private final VBox panelDetalle;

	/** Selector con los nombres de los criterios que entrega el controlador. */
	private final ComboBox<String> selectorOrden = new ComboBox<>();

	/** Botón que solicita revertir el último traslado. */
	private final Button botonDeshacer = new Button("DESHACER");

	/** Botón que solicita repetir el último traslado deshecho. */
	private final Button botonRehacer = new Button("REHACER");

	/**
	 * Construye los controles y conecta sus eventos. Complejidad O(1) respecto al
	 * número de cartas.
	 *
	 * @param titulo          encabezado de la pantalla
	 * @param textoVacio      mensaje que aparece cuando la lista está vacía
	 * @param textoTraslado   texto del botón para mover una copia
	 * @param textoCambio     texto del botón para abrir la otra pantalla
	 * @param alMostrar       acción que carga las filas y el contador
	 * @param alSeleccionar   acción que recibe el índice de la fila seleccionada
	 * @param alTrasladar     acción que recibe el índice de la fila que se quiere
	 *                        mover
	 * @param cambiarPantalla acción que abre la otra pantalla de la colección
	 * @param volver          acción que regresa al menú
	 */
	protected PantallaColeccion(String titulo, String textoVacio, String textoTraslado, String textoCambio,
			Consumer<PantallaColeccion> alMostrar, ObjIntConsumer<PantallaColeccion> alSeleccionar,
			ObjIntConsumer<PantallaColeccion> alTrasladar, Runnable cambiarPantalla, Runnable volver) {
		this.alMostrar = alMostrar;

		lista.setPlaceholder(new Label(textoVacio));
		lista.getSelectionModel().selectedIndexProperty().addListener((observable, anterior, seleccion) -> {
			int fila = seleccion.intValue();
			if (fila < 0) {
				limpiarDetalle();
			} else {
				alSeleccionar.accept(this, fila);
			}
		});

		Label encabezado = new Label(titulo);
		encabezado.getStyleClass().add("titulo");
		cantidad.getStyleClass().add("contador");
		selectorOrden.setPromptText("Selecciona un orden");
		selectorOrden.setMaxWidth(Double.MAX_VALUE);
		HBox opcionesOrden = new HBox(10, new Label("ORDENAR POR"), selectorOrden);
		HBox.setHgrow(selectorOrden, Priority.ALWAYS);
		VBox cabecera = new VBox(8, encabezado, cantidad, opcionesOrden);
		barraBusqueda.setVisible(false);
		barraBusqueda.setManaged(false);
		cabecera.getChildren().add(barraBusqueda);
		BorderPane.setMargin(cabecera, new Insets(0, 0, 16, 0));
		raiz.setTop(cabecera);
		VBox panelLista = new VBox(8, new Label("CARTAS"), lista);
		panelLista.getStyleClass().add("panel");
		VBox.setVgrow(lista, Priority.ALWAYS);
		HBox.setHgrow(panelLista, Priority.ALWAYS);
		panelDetalle = crearDetalle();
		raiz.setCenter(new HBox(16, panelLista, panelDetalle));
		Button trasladar = new Button(textoTraslado);
		trasladar.disableProperty().bind(lista.getSelectionModel().selectedIndexProperty().lessThan(0));
		trasladar.setOnAction(evento -> {
			int fila = lista.getSelectionModel().getSelectedIndex();
			if (fila >= 0) {
				alTrasladar.accept(this, fila);
			}
		});

		Button cambiar = new Button(textoCambio);
		cambiar.setOnAction(evento -> cambiarPantalla.run());
		Button regresar = new Button("VOLVER");
		regresar.setOnAction(evento -> volver.run());
		mensaje.setWrapText(true);
		mensaje.getStyleClass().add("mensaje");
		botonDeshacer.setDisable(true);
		botonRehacer.setDisable(true);
		HBox acciones = new HBox(10, trasladar, cambiar, botonDeshacer, botonRehacer, regresar);
		VBox pie = new VBox(8, mensaje, acciones);
		BorderPane.setMargin(pie, new Insets(12, 0, 0, 0));
		raiz.setBottom(pie);
		raiz.getStyleClass().add("coleccion-retro");
		URL estilo = PantallaColeccion.class.getResource("/estilos/coleccion.css");
		if (estilo != null) {
			raiz.getStylesheets().add(estilo.toExternalForm());
		}
	}

	/**
	 * Devuelve el contenedor que coloca el navegador en la escena. Complejidad
	 * O(1).
	 *
	 * @return nodo raíz de la pantalla
	 */
	@Override
	public final Parent getVista() {
		return raiz;
	}

	/**
	 * Limpia el mensaje anterior y solicita los datos actualizados. Su costo
	 * depende de la acción de carga conectada por el controlador.
	 */
	@Override
	public final void alEntrar() {
		mostrarMensaje("");
		alMostrar.accept(this);
	}

	/**
	 * Actualiza las filas, el contador y la selección. Complejidad O(n) para copiar
	 * los textos, más el costo de actualizar el detalle.
	 *
	 * @param filas         textos de las cartas en el orden que entrega el
	 *                      controlador
	 * @param resumen       texto que se muestra debajo del encabezado
	 * @param filaPreferida fila que se quiere seleccionar después de actualizar
	 */
	public final void mostrarCartas(String[] filas, String resumen, int filaPreferida) {
		lista.getSelectionModel().clearSelection();
		lista.getItems().setAll(filas);
		cantidad.setText(resumen);

		if (filas.length == 0) {
			limpiarDetalle();
			return;
		}

		int fila = Math.max(0, Math.min(filaPreferida, filas.length - 1));
		lista.getSelectionModel().select(fila);
	}

	/**
	 * Muestra los datos de la carta seleccionada. Actualizar los controles tiene
	 * costo O(1) respecto al número de cartas. La imagen se carga en segundo plano.
	 *
	 * @param nombreCarta       nombre visible de la carta
	 * @param textoEstadisticas tipo, ataque y defensa formateados
	 * @param textoDescripcion  descripción visible
	 * @param rutaImagen        ruta de la imagen en los recursos del proyecto
	 */
	public final void mostrarDetalle(String nombreCarta, String textoEstadisticas, String textoDescripcion,
			String rutaImagen) {
		nombre.setText(nombreCarta);
		estadisticas.setText(textoEstadisticas);
		descripcion.setText(textoDescripcion);
		imagen.setImage(null);

		if (rutaImagen == null || rutaImagen.isBlank()) {
			return;
		}

		String ruta = rutaImagen.startsWith("/") ? rutaImagen : "/" + rutaImagen;
		URL recurso = PantallaColeccion.class.getResource(ruta);
		if (recurso != null) {
			imagen.setImage(new Image(recurso.toExternalForm(), 210, 260, true, true, true));
		}
	}

	/**
	 * Muestra el resultado de una acción. Complejidad O(1) respecto al número de
	 * cartas.
	 *
	 * @param texto mensaje que se quiere mostrar
	 */
	public final void mostrarMensaje(String texto) {
		mensaje.setText(texto);
	}

	/** Restablece el panel cuando la selección está vacía. */
	private void limpiarDetalle() {
		mostrarAcabadoEspecial(false);
		mostrarDetalle("Selecciona una carta", "", "", null);
	}

	/**
	 * Construye el panel de detalle y le da espacio a descripciones largas.
	 *
	 * @return panel con la imagen, las estadísticas y la descripción
	 */
	private VBox crearDetalle() {
		imagen.setFitWidth(210);
		imagen.setFitHeight(260);
		imagen.setPreserveRatio(true);
		StackPane marco = new StackPane(imagen);
		marco.setMinHeight(265);
		marco.getStyleClass().add("marco-imagen");

		nombre.setWrapText(true);
		nombre.getStyleClass().add("nombre-carta");
		estadisticas.setWrapText(true);
		descripcion.setWrapText(true);
		descripcion.setMaxWidth(Double.MAX_VALUE);

		ScrollPane texto = new ScrollPane(descripcion);
		texto.setFitToWidth(true);
		texto.setPannable(true);
		texto.setPrefViewportHeight(100);
		VBox.setVgrow(texto, Priority.ALWAYS);
		VBox detalle = new VBox(10, marco, nombre, estadisticas, texto);
		detalle.setPrefWidth(330);
		detalle.getStyleClass().add("panel");
		return detalle;
	}

	/**
	 * Aplica o retira el acabado iridiscente del panel de detalle. Complejidad:
	 * O(1).
	 *
	 * @param activa indica si se debe mostrar el acabado especial
	 */
	public final void mostrarAcabadoEspecial(boolean activa) {
		panelDetalle.pseudoClassStateChanged(CARTA_PODEROSA, activa);
	}

	/**
	 * Carga los nombres de los criterios y conecta la selección con el controlador.
	 * Configurar el selector conserva el orden actual de las filas. Complejidad:
	 * O(k), con k criterios disponibles.
	 *
	 * @param criterios nombres visibles de los criterios
	 * @param alOrdenar acción que recibe la pantalla y el índice del criterio
	 *                  elegido
	 */
	public final void configurarOrden(String[] criterios, ObjIntConsumer<PantallaColeccion> alOrdenar) {
		selectorOrden.setOnAction(null);
		selectorOrden.getItems().setAll(criterios);
		selectorOrden.getSelectionModel().clearSelection();

		selectorOrden.setOnAction(evento -> {
			int indice = selectorOrden.getSelectionModel().getSelectedIndex();

			if (indice >= 0) {
				alOrdenar.accept(this, indice);
			}
		});
	}

	/**
	 * Consulta la fila seleccionada. Complejidad: O(1).
	 *
	 * @return índice seleccionado, o -1 si la selección está vacía
	 */
	public final int getFilaSeleccionada() {
		return lista.getSelectionModel().getSelectedIndex();
	}

	/**
	 * Selecciona una fila existente y permite que se actualice su detalle. El costo
	 * incluye la acción de selección conectada por el controlador.
	 *
	 * @param fila índice de la fila que se quiere seleccionar
	 */
	public final void seleccionarFila(int fila) {
		if (fila >= 0 && fila < lista.getItems().size()) {
			lista.getSelectionModel().select(fila);
		}
	}

	/**
	 * Conecta los botones del historial con sus acciones del controlador.
	 * Complejidad: O(1).
	 *
	 * @param alDeshacer acción que recibe la pantalla al solicitar deshacer
	 * @param alRehacer  acción que recibe la pantalla al solicitar rehacer
	 */
	public final void configurarHistorial(Consumer<PantallaColeccion> alDeshacer,
			Consumer<PantallaColeccion> alRehacer) {
		botonDeshacer.setOnAction(evento -> alDeshacer.accept(this));
		botonRehacer.setOnAction(evento -> alRehacer.accept(this));
	}

	/**
	 * Actualiza la disponibilidad de los botones del historial. Complejidad: O(1).
	 *
	 * @param puedeDeshacer indica si hay un traslado que se puede deshacer
	 * @param puedeRehacer  indica si hay un traslado que se puede repetir
	 */
	public final void mostrarEstadoHistorial(boolean puedeDeshacer, boolean puedeRehacer) {
		botonDeshacer.setDisable(!puedeDeshacer);
		botonRehacer.setDisable(!puedeRehacer);
	}

	/**
	 * Muestra el buscador y comunica el texto escrito al controlador. Permite
	 * buscar con el botón o pulsando Enter. Complejidad: O(1) respecto a la
	 * cantidad de cartas.
	 *
	 * @param alBuscar acción que recibe la pantalla y el texto escrito
	 */
	public final void configurarBusqueda(BiConsumer<PantallaColeccion, String> alBuscar) {
		TextField campo = new TextField();
		campo.setPromptText("Nombre completo de la carta");
		HBox.setHgrow(campo, Priority.ALWAYS);
		Button buscar = new Button("BUSCAR");
		Runnable solicitarBusqueda = () -> alBuscar.accept(this, campo.getText());
		buscar.setOnAction(evento -> solicitarBusqueda.run());
		campo.setOnAction(evento -> solicitarBusqueda.run());
		barraBusqueda.getChildren().setAll(campo, buscar);
		barraBusqueda.setVisible(true);
		barraBusqueda.setManaged(true);
	}
}