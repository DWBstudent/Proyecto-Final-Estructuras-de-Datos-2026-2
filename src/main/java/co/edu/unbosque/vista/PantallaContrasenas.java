package co.edu.unbosque.vista;

import java.net.URL;
import java.util.function.BiConsumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Pantalla para escribir una contraseña de 8 dígitos y canjearla por su carta.
 * Solo muestra textos e imágenes; el canje lo decide el controlador.
 *
 * <p>
 * Responsable: Daniel.
 */
public final class PantallaContrasenas implements Pantalla {

	private final BorderPane raiz = new BorderPane();
	private final TextField campo = new TextField();
	private final Label mensaje = new Label();
	private final ImageView imagen = new ImageView();
	private final Label nombre = new Label();
	private final Label estadisticas = new Label();

	/**
	 * Construye la pantalla. Complejidad: O(1).
	 *
	 * @param alCanjear acción que recibe la pantalla y el texto escrito
	 * @param volver    acción para regresar al menú
	 * @throws IllegalArgumentException si falta alguna acción
	 */
	public PantallaContrasenas(BiConsumer<PantallaContrasenas, String> alCanjear, Runnable volver) {
		if (alCanjear == null || volver == null) {
			throw new IllegalArgumentException("La pantalla de contraseñas necesita sus acciones");
		}

		Label titulo = new Label("CONTRASEÑAS");
		titulo.getStyleClass().add("titulo");
		Label ayuda = new Label("Escribe la contraseña de 8 dígitos de una carta. Cada contraseña sirve una sola vez.");
		ayuda.setWrapText(true);
		VBox cabecera = new VBox(6, titulo, ayuda);
		BorderPane.setMargin(cabecera, new Insets(0, 0, 16, 0));
		raiz.setTop(cabecera);

		campo.setPromptText("Ejemplo: 12345678");
		Button canjear = new Button("CANJEAR");
		Runnable solicitarCanje = () -> alCanjear.accept(this, campo.getText());
		canjear.setOnAction(evento -> solicitarCanje.run());
		campo.setOnAction(evento -> solicitarCanje.run());
		HBox fila = new HBox(10, campo, canjear);
		fila.setAlignment(Pos.CENTER_LEFT);

		imagen.setFitWidth(210);
		imagen.setFitHeight(260);
		imagen.setPreserveRatio(true);
		StackPane marco = new StackPane(imagen);
		marco.setMinHeight(265);
		marco.getStyleClass().add("marco-imagen");
		nombre.getStyleClass().add("nombre-carta");
		nombre.setWrapText(true);
		estadisticas.setWrapText(true);
		mensaje.getStyleClass().add("mensaje");
		mensaje.setWrapText(true);

		VBox centro = new VBox(12, fila, mensaje, marco, nombre, estadisticas);
		centro.getStyleClass().add("panel");
		raiz.setCenter(centro);

		Button regresar = new Button("VOLVER");
		regresar.setOnAction(evento -> volver.run());
		BorderPane.setMargin(regresar, new Insets(12, 0, 0, 0));
		raiz.setBottom(regresar);

		raiz.getStyleClass().add("coleccion-retro");
		URL estilo = PantallaContrasenas.class.getResource("/estilos/coleccion.css");
		if (estilo != null) {
			raiz.getStylesheets().add(estilo.toExternalForm());
		}
	}

	@Override
	public Parent getVista() {
		return raiz;
	}

	/** Al entrar, la pantalla queda limpia. Complejidad: O(1). */
	@Override
	public void alEntrar() {
		campo.setText("");
		mostrarMensaje("");
		mostrarCarta("", "", null);
	}

	/** Muestra un texto debajo del campo. Complejidad: O(1). */
	public void mostrarMensaje(String texto) {
		mensaje.setText(texto);
	}

	/** Borra lo escrito en el campo. Complejidad: O(1). */
	public void limpiarCampo() {
		campo.setText("");
	}

	/**
	 * Muestra la carta obtenida. Con {@code rutaImagen} nula o vacía no muestra imagen.
	 * Complejidad: O(1).
	 */
	public void mostrarCarta(String nombreCarta, String textoEstadisticas, String rutaImagen) {
		nombre.setText(nombreCarta);
		estadisticas.setText(textoEstadisticas);
		imagen.setImage(null);
		if (rutaImagen != null && !rutaImagen.isBlank()) {
			String ruta = rutaImagen.startsWith("/") ? rutaImagen : "/" + rutaImagen;
			URL recurso = PantallaContrasenas.class.getResource(ruta);
			if (recurso != null) {
				imagen.setImage(new Image(recurso.toExternalForm(), 210, 260, true, true, true));
			}
		}
	}
}