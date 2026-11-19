package co.edu.unbosque.vista;

import java.net.URL;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Muestra la cuadrícula del inventario y el detalle de la posición
 * seleccionada. Comunica al controlador los clics y las solicitudes de
 * movimiento.
 *
 * <p>
 * Responsable: Brayan.
 */
public final class PantallaInventario implements Pantalla {

	/** Estado visual de una posición seleccionada. */
	private static final PseudoClass SELECCIONADA = PseudoClass.getPseudoClass("seleccionada");

	/** Estado visual de una carta que el jugador ya descubrió. */
	private static final PseudoClass DESCUBIERTA = PseudoClass.getPseudoClass("descubierta");

	/** Acabado visual especial del detalle. */
	private static final PseudoClass CARTA_PODEROSA = PseudoClass.getPseudoClass("carta-poderosa");

	/** Contenedor principal de la pantalla. */
	private final BorderPane raiz = new BorderPane();

	/** Botones que representan las posiciones del inventario. */
	private final Button[] casillas;

	/** Cantidad de columnas recibida del controlador. */
	private final int columnas;

	/** Resumen de cartas descubiertas. */
	private final Label resumen = new Label();

	/** Posición que ocupa actualmente el cursor. */
	private final Label posicion = new Label();

	/** Resultado de la última acción. */
	private final Label mensaje = new Label();

	/** Imagen de la carta seleccionada. */
	private final ImageView imagen = new ImageView();

	/** Nombre visible de la selección. */
	private final Label nombre = new Label();

	/** Tipo y estadísticas que entrega el controlador. */
	private final Label estadisticas = new Label();

	/** Descripción o indicación de carta pendiente de descubrir. */
	private final Label descripcion = new Label();

	/** Contenedor al que se aplica el acabado especial. */
	private final VBox panelDetalle;

	/** Acción que solicita actualizar la pantalla al entrar. */
	private final Runnable alMostrar;

	/** Acción que solicita un movimiento de fila y columna. */
	private final BiConsumer<Integer, Integer> alMover;

	/** Índice de la casilla resaltada, o -1 antes de la primera selección. */
	private int seleccionada = -1;

	/**
	 * Construye la cuadrícula y conecta sus acciones. Complejidad: O(f · c), con f
	 * filas y c columnas.
	 *
	 * @param filas         cantidad de filas
	 * @param columnas      cantidad de columnas
	 * @param alMostrar     acción que carga el contenido
	 * @param alSeleccionar acción que recibe el índice de una casilla pulsada
	 * @param alMover       acción que recibe el desplazamiento solicitado
	 * @param alBuscar      acción que busca una carta sin descubrir mediante BFS
	 * @param volver        acción que regresa al menú
	 * @throws IllegalArgumentException si las dimensiones son inválidas o falta
	 *                                  alguna acción
	 */
	public PantallaInventario(int filas, int columnas, Runnable alMostrar, IntConsumer alSeleccionar,
			BiConsumer<Integer, Integer> alMover, Runnable alBuscar, Runnable volver) {
		if (filas <= 0 || columnas <= 0) {
			throw new IllegalArgumentException("La cuadrícula necesita dimensiones positivas");
		}
		if (alMostrar == null || alSeleccionar == null || alMover == null || alBuscar == null || volver == null) {
			throw new IllegalArgumentException("El inventario necesita todas sus acciones");
		}

		this.columnas = columnas;
		this.alMostrar = alMostrar;
		this.alMover = alMover;
		casillas = new Button[filas * columnas];
		Label titulo = new Label("INVENTARIO");
		titulo.getStyleClass().add("titulo");
		resumen.getStyleClass().add("contador");
		VBox cabecera = new VBox(6, titulo, resumen);
		BorderPane.setMargin(cabecera, new Insets(0, 0, 14, 0));
		raiz.setTop(cabecera);
		GridPane cuadricula = crearCuadricula(filas, alSeleccionar);
		ScrollPane zonaCuadricula = new ScrollPane(cuadricula);
		zonaCuadricula.setFitToWidth(true);
		zonaCuadricula.setPrefViewportHeight(430);
		VBox.setVgrow(zonaCuadricula, Priority.ALWAYS);
		posicion.getStyleClass().add("contador");
		VBox panelInventario = new VBox(10, posicion, zonaCuadricula, crearControles(),
				new Label("Flechas: mover · Teclado numérico: ocho direcciones"));
		panelInventario.getStyleClass().add("panel");
		HBox.setHgrow(panelInventario, Priority.ALWAYS);
		panelDetalle = crearDetalle();
		raiz.setCenter(new HBox(16, panelInventario, panelDetalle));
		Button buscar = new Button("BUSCAR SIN DESCUBRIR");
		buscar.setOnAction(evento -> alBuscar.run());
		Button regresar = new Button("VOLVER");
		regresar.setOnAction(evento -> volver.run());
		mensaje.setWrapText(true);
		mensaje.getStyleClass().add("mensaje");
		VBox pie = new VBox(8, mensaje, new HBox(10, buscar, regresar));
		BorderPane.setMargin(pie, new Insets(12, 0, 0, 0));
		raiz.setBottom(pie);
		raiz.getStyleClass().add("coleccion-retro");
		URL estilo = PantallaInventario.class.getResource("/estilos/coleccion.css");
		if (estilo != null) {
			raiz.getStylesheets().add(estilo.toExternalForm());
		}

		raiz.addEventFilter(KeyEvent.KEY_PRESSED, this::atenderTecla);
	}

	/**
	 * Devuelve el contenedor que muestra el navegador. Complejidad: O(1).
	 *
	 * @return raíz de la pantalla
	 */
	@Override
	public Parent getVista() {
		return raiz;
	}

	/**
	 * Solicita los datos actualizados del inventario. El costo depende de la
	 * consulta conectada por el controlador.
	 */
	@Override
	public void alEntrar() {
		mostrarMensaje("");
		alMostrar.run();
	}

	/**
	 * Actualiza los textos y el estado de descubrimiento de las casillas.
	 * Complejidad: O(n), con n posiciones.
	 *
	 * @param textos       texto visible de cada posición
	 * @param descubiertas estado de descubrimiento de cada posición
	 * @param textoResumen resumen que aparece bajo el título
	 * @throws IllegalArgumentException si los arreglos no coinciden con la
	 *                                  cuadrícula
	 */
	public void mostrarCuadricula(String[] textos, boolean[] descubiertas, String textoResumen) {
		if (textos == null || descubiertas == null || textos.length != casillas.length
				|| descubiertas.length != casillas.length) {
			throw new IllegalArgumentException("Los datos deben coincidir con la cuadrícula");
		}

		for (int indice = 0; indice < casillas.length; indice++) {
			casillas[indice].setText(textos[indice]);
			casillas[indice].pseudoClassStateChanged(DESCUBIERTA, descubiertas[indice]);
		}

		resumen.setText(textoResumen);
	}

	/**
	 * Cambia la casilla resaltada y el detalle visible. Complejidad: O(1) respecto
	 * a la cantidad de posiciones. La imagen se carga en segundo plano.
	 *
	 * @param fila              fila del cursor, empezando desde 0
	 * @param columna           columna del cursor, empezando desde 0
	 * @param nombreCarta       nombre o texto de carta desconocida
	 * @param textoEstadisticas tipo y estadísticas visibles
	 * @param textoDescripcion  descripción visible
	 * @param rutaImagen        ruta de imagen, o null para dejar el marco vacío
	 * @param acabadoEspecial   indica si se aplica el marco iridiscente
	 */
	public void mostrarSeleccion(int fila, int columna, String nombreCarta, String textoEstadisticas,
			String textoDescripcion, String rutaImagen, boolean acabadoEspecial) {
		if (seleccionada >= 0) {
			casillas[seleccionada].pseudoClassStateChanged(SELECCIONADA, false);
		}

		seleccionada = fila * columnas + columna;
		casillas[seleccionada].pseudoClassStateChanged(SELECCIONADA, true);
		posicion.setText("Fila " + (fila + 1) + " · Columna " + (columna + 1));
		nombre.setText(nombreCarta);
		estadisticas.setText(textoEstadisticas);
		descripcion.setText(textoDescripcion);
		panelDetalle.pseudoClassStateChanged(CARTA_PODEROSA, acabadoEspecial);
		imagen.setImage(null);
		if (rutaImagen != null && !rutaImagen.isBlank()) {
			String ruta = rutaImagen.startsWith("/") ? rutaImagen : "/" + rutaImagen;
			URL recurso = PantallaInventario.class.getResource(ruta);

			if (recurso != null) {
				imagen.setImage(new Image(recurso.toExternalForm(), 210, 260, true, true, true));
			}
		}
	}

	/**
	 * Muestra el resultado de la última acción. Complejidad: O(1) respecto a la
	 * cantidad de posiciones.
	 *
	 * @param texto mensaje que se quiere presentar
	 */
	public void mostrarMensaje(String texto) {
		mensaje.setText(texto);
	}

	/**
	 * Construye las casillas y comunica al controlador el índice pulsado.
	 *
	 * @param filas         cantidad de filas
	 * @param alSeleccionar acción que atiende una casilla pulsada
	 * @return cuadrícula de botones
	 */
	private GridPane crearCuadricula(int filas, IntConsumer alSeleccionar) {
		GridPane cuadricula = new GridPane();
		cuadricula.setHgap(4);
		cuadricula.setVgap(4);
		cuadricula.setPadding(new Insets(6));

		for (int columna = 0; columna < columnas; columna++) {
			ColumnConstraints ancho = new ColumnConstraints();
			ancho.setPercentWidth(100.0 / columnas);
			cuadricula.getColumnConstraints().add(ancho);
		}

		for (int fila = 0; fila < filas; fila++) {
			for (int columna = 0; columna < columnas; columna++) {
				int indice = fila * columnas + columna;
				Button casilla = new Button();
				casilla.getStyleClass().add("casilla-inventario");
				casilla.setMinWidth(0);
				casilla.setMaxWidth(Double.MAX_VALUE);
				casilla.setPrefHeight(40);
				casilla.setOnAction(evento -> alSeleccionar.accept(indice));

				casillas[indice] = casilla;
				cuadricula.add(casilla, columna, fila);
			}
		}

		return cuadricula;
	}

	/**
	 * Construye los ocho botones de movimiento alrededor del centro.
	 *
	 * @return panel de navegación
	 */
	private GridPane crearControles() {
		GridPane controles = new GridPane();
		controles.setHgap(4);
		controles.setVgap(4);

		agregarDireccion(controles, "↖", -1, -1, 0, 0);
		agregarDireccion(controles, "↑", -1, 0, 1, 0);
		agregarDireccion(controles, "↗", -1, 1, 2, 0);
		agregarDireccion(controles, "←", 0, -1, 0, 1);
		agregarDireccion(controles, "→", 0, 1, 2, 1);
		agregarDireccion(controles, "↙", 1, -1, 0, 2);
		agregarDireccion(controles, "↓", 1, 0, 1, 2);
		agregarDireccion(controles, "↘", 1, 1, 2, 2);
		return controles;
	}

	/**
	 * Agrega un botón que comunica un desplazamiento al controlador.
	 *
	 * @param controles     panel de botones
	 * @param texto         símbolo de dirección
	 * @param cambioFila    desplazamiento solicitado en filas
	 * @param cambioColumna desplazamiento solicitado en columnas
	 * @param columna       columna visual del botón
	 * @param fila          fila visual del botón
	 */
	private void agregarDireccion(GridPane controles, String texto, int cambioFila, int cambioColumna, int columna,
			int fila) {
		Button boton = new Button(texto);
		boton.setPrefWidth(44);
		boton.setOnAction(evento -> alMover.accept(cambioFila, cambioColumna));
		controles.add(boton, columna, fila);
	}

	/**
	 * Convierte las teclas de navegación en solicitudes de movimiento.
	 *
	 * @param evento tecla pulsada dentro de la pantalla
	 */
	private void atenderTecla(KeyEvent evento) {
		switch (evento.getCode()) {
		case UP, NUMPAD8 -> alMover.accept(-1, 0);
		case DOWN, NUMPAD2 -> alMover.accept(1, 0);
		case LEFT, NUMPAD4 -> alMover.accept(0, -1);
		case RIGHT, NUMPAD6 -> alMover.accept(0, 1);
		case NUMPAD7 -> alMover.accept(-1, -1);
		case NUMPAD9 -> alMover.accept(-1, 1);
		case NUMPAD1 -> alMover.accept(1, -1);
		case NUMPAD3 -> alMover.accept(1, 1);
		default -> {
			return;
		}
		}

		evento.consume();
	}

	/**
	 * Construye el detalle de la carta seleccionada.
	 *
	 * @return panel con imagen, estadísticas y descripción desplazable
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
		texto.setPrefViewportHeight(100);
		VBox.setVgrow(texto, Priority.ALWAYS);

		VBox detalle = new VBox(10, marco, nombre, estadisticas, texto);
		detalle.setPrefWidth(300);
		detalle.getStyleClass().add("panel");
		return detalle;
	}
}