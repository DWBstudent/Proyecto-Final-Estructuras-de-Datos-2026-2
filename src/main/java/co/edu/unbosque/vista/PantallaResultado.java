package co.edu.unbosque.vista;

import java.net.URL;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * Resultado de un duelo: quién ganó, la recompensa (si hubo) y cómo terminó. El
 * controlador arma los textos; esta pantalla solo los muestra.
 *
 * <p>
 * Responsable: Santiago.
 */
public class PantallaResultado implements Pantalla {

	/** Hoja de estilos de la pantalla. */
	private static final String RUTA_CSS = "/estilos/resultado.css";

	/** Ancho máximo del recuadro de resultado. */
	private static final double ANCHO_PANEL = 780;

	/** Contenedor principal del fondo y los controles. */
	private final StackPane raiz = new StackPane();

	/**
	 * Construye la pantalla de resultado.
	 *
	 * <p>
	 * Conserva el contrato utilizado por los controladores existentes. El contenido
	 * puede representar una victoria o una derrota.
	 *
	 * @param titulo      encabezado, por ejemplo "¡Ganaste!" o "Perdiste"
	 * @param lineas      detalles del resultado, uno por renglón
	 * @param alContinuar acción del botón Continuar
	 * @throws IllegalArgumentException si algún argumento es null o si alguna línea
	 *                                  es null
	 * @throws IllegalStateException    si no existe la hoja de estilos
	 */
	public PantallaResultado(String titulo, String[] lineas, Runnable alContinuar) {
		validarArgumentos(titulo, lineas, alContinuar);
		raiz.getStyleClass().add("resultado-retro");

		/*
		 * La sombra ocupa el fondo, pero no recibe eventos ni modifica la opacidad del
		 * recuadro.
		 */
		Region velo = new Region();
		velo.getStyleClass().add("resultado-velo");
		velo.setMouseTransparent(true);
		Label encabezado = new Label(titulo);
		encabezado.getStyleClass().add("resultado-titulo");
		encabezado.setWrapText(true);
		encabezado.setMaxWidth(Double.MAX_VALUE);
		encabezado.setAlignment(Pos.CENTER);
		encabezado.setTextAlignment(TextAlignment.CENTER);
		VBox detalles = crearDetalles(lineas);
		Button continuar = new Button("Continuar");
		continuar.getStyleClass().add("boton-piedra");
		continuar.setPrefWidth(240);
		continuar.setMaxWidth(Double.MAX_VALUE);
		continuar.setOnAction(evento -> alContinuar.run());
		VBox panel = new VBox(encabezado, detalles, continuar);
		panel.getStyleClass().add("resultado-panel");
		panel.setAlignment(Pos.CENTER);
		panel.setMaxWidth(ANCHO_PANEL);
		panel.setMaxHeight(Region.USE_PREF_SIZE);

		/*
		 * Centra el recuadro y deja visible la imagen alrededor de sus bordes.
		 */
		StackPane centro = new StackPane(panel);
		centro.setPadding(new Insets(28));
		ScrollPane desplazamiento = new ScrollPane(centro);
		desplazamiento.getStyleClass().add("resultado-scroll");
		desplazamiento.setFitToWidth(true);
		desplazamiento.setFitToHeight(true);
		desplazamiento.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		desplazamiento.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		raiz.getChildren().addAll(velo, desplazamiento);
		cargarEstilos();
	}

	/**
	 * Construye las etiquetas de las estadísticas y los mensajes.
	 *
	 * <p>
	 * Complejidad: O(n), donde n es la cantidad de líneas.
	 *
	 * @param lineas textos preparados para mostrar
	 * @return contenedor de las etiquetas
	 */
	private static VBox crearDetalles(String[] lineas) {
		VBox detalles = new VBox();
		detalles.getStyleClass().add("resultado-detalles");
		detalles.setAlignment(Pos.CENTER);
		detalles.setMaxWidth(Double.MAX_VALUE);
		for (String linea : lineas) {
			Label etiqueta = new Label(linea);
			etiqueta.getStyleClass().add("resultado-linea");
			etiqueta.setWrapText(true);
			etiqueta.setMinWidth(0);
			etiqueta.setMaxWidth(Double.MAX_VALUE);
			etiqueta.setAlignment(Pos.CENTER);
			etiqueta.setTextAlignment(TextAlignment.CENTER);
			detalles.getChildren().add(etiqueta);
		}

		return detalles;
	}

	/**
	 * Valida los datos antes de construir los controles.
	 *
	 * @param titulo      encabezado de la pantalla
	 * @param lineas      detalles del resultado
	 * @param alContinuar acción de navegación
	 * @throws IllegalArgumentException si falta algún argumento o alguna línea es
	 *                                  null
	 */
	private static void validarArgumentos(String titulo, String[] lineas, Runnable alContinuar) {

		if (titulo == null || lineas == null || alContinuar == null) {
			throw new IllegalArgumentException("Faltan el título, las líneas o la acción de continuar");
		}

		for (int indice = 0; indice < lineas.length; indice++) {
			if (lineas[indice] == null) {
				throw new IllegalArgumentException("La línea " + indice + " no puede ser null");
			}
		}
	}

	/**
	 * Carga la hoja de estilos desde los recursos.
	 *
	 * @throws IllegalStateException si no existe resultado.css
	 */
	private void cargarEstilos() {
		URL recurso = PantallaResultado.class.getResource(RUTA_CSS);
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
}
