package co.edu.unbosque.vista;

import java.net.URL;
import java.util.Locale;

import co.edu.unbosque.modelo.entidades.Carta;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * Una carta dibujada en código: marco de color según el tipo, imagen (si la
 * carta tiene una en los recursos), nombre y ATK/DEF. También dibuja una zona
 * vacía y el reverso de una carta boca abajo. Solo muestra datos: no sabe nada
 * de las reglas.
 *
 * <p>
 * Responsable: Santiago.
 */
public final class VistaCarta extends StackPane {

	/** Contenidos que puede representar una posición del tablero. */
	public enum Contenido {
		VACIA, REVERSO, CARTA
	}

	/**
	 * Datos de presentación preparados por el controlador. Para un reverso se
	 * entregan cadenas vacías y valores cero: los datos de la carta rival oculta no
	 * deben llegar a la vista.
	 *
	 * @param contenido    clase de representación
	 * @param nombre       nombre visible
	 * @param tipo         identificador textual del tipo para elegir su acabado
	 * @param rutaImagen   recurso de imagen; puede ser null
	 * @param ataque       valor de ATK que se muestra
	 * @param defensa      valor de DEF que se muestra
	 * @param enDefensa    indica posición defensiva
	 * @param oculta       carta propia que permanece boca abajo para el rival
	 * @param seleccionada indica selección actual
	 * @param objetivo     indica que se puede elegir como objetivo
	 * @param gastada      indica que ya atacó
	 * @param poderosa     activa el acabado platinado
	 */
	public record Datos(Contenido contenido, String nombre, String tipo, String rutaImagen, int ataque, int defensa,
			boolean enDefensa, boolean oculta, boolean seleccionada, boolean objetivo, boolean gastada,
			boolean poderosa) {

		/** Valida los campos de texto y el contenido obligatorio. */
		public Datos {
			if (contenido == null || nombre == null || tipo == null) {
				throw new IllegalArgumentException("Faltan datos de presentación de la carta");
			}
		}

		/** @return datos de una posición vacía */
		public static Datos vacia() {
			return new Datos(Contenido.VACIA, "", "", null, 0, 0, false, false, false, false, false, false);
		}

		/**
		 * Crea un reverso sin información de la carta oculta.
		 * 
		 * @param objetivo indica si se permite elegirlo como objetivo
		 * @return datos del reverso
		 */
		public static Datos reverso(boolean objetivo) {
			return new Datos(Contenido.REVERSO, "", "", null, 0, 0, false, false, false, objetivo, false, false);
		}
	}

	private final Label nombre = new Label();
	private final Label valores = new Label();
	private final Label posicion = new Label();
	private final Label alternativa = new Label();
	private final Label simbolo = new Label();
	private final ImageView imagen = new ImageView();
	private final StackPane ilustracion = new StackPane(imagen, alternativa, posicion);
	private final VBox contenido = new VBox(3, nombre, ilustracion, valores);
	private final Tooltip ayuda = new Tooltip();
	private String rutaActual;
	private String claseTipo;

	/** Construye una posición reutilizable, inicialmente vacía. */
	public VistaCarta() {
		getStyleClass().add("carta-duelo");
		contenido.getStyleClass().add("carta-contenido");
		contenido.setAlignment(Pos.CENTER);
		nombre.getStyleClass().add("carta-nombre");
		nombre.setWrapText(true);
		nombre.setTextAlignment(TextAlignment.CENTER);
		nombre.setAlignment(Pos.CENTER);
		nombre.setMinHeight(30);
		nombre.setMaxHeight(30);
		nombre.setMaxWidth(Double.MAX_VALUE);
		valores.getStyleClass().add("carta-valores");
		valores.setTextAlignment(TextAlignment.CENTER);
		valores.setMinHeight(28);
		ilustracion.getStyleClass().add("carta-ilustracion");
		alternativa.getStyleClass().add("carta-alternativa");
		alternativa.setWrapText(true);
		alternativa.setTextAlignment(TextAlignment.CENTER);
		posicion.getStyleClass().add("carta-posicion");
		posicion.setMaxWidth(Double.MAX_VALUE);
		posicion.setAlignment(Pos.CENTER);
		StackPane.setAlignment(posicion, Pos.BOTTOM_CENTER);
		simbolo.getStyleClass().add("carta-simbolo");
		simbolo.setWrapText(true);
		simbolo.setTextAlignment(TextAlignment.CENTER);
		imagen.setPreserveRatio(true);
		imagen.setSmooth(true);
		getChildren().addAll(contenido, simbolo);
		Tooltip.install(this, ayuda);
		ajustarTamanio(144);
		mostrar(Datos.vacia());
	}

	/**
	 * Ajusta el marco y la ilustración sin deformar la imagen.
	 * 
	 * @param ancho ancho positivo de la carta, en píxeles
	 */
	public void ajustarTamanio(double ancho) {
		if (!Double.isFinite(ancho) || ancho <= 0) {
			throw new IllegalArgumentException("El ancho debe ser positivo y finito");
		}
		double alto = ancho * 1.40;
		setMinSize(ancho, alto);
		setPrefSize(ancho, alto);
		setMaxSize(ancho, alto);
		ilustracion.setMinSize(ancho - 18, alto * 0.47);
		ilustracion.setPrefSize(ancho - 18, alto * 0.47);
		ilustracion.setMaxSize(ancho - 18, alto * 0.47);
		imagen.setFitWidth(ancho - 22);
		imagen.setFitHeight(alto * 0.47 - 4);
	}

	/**
	 * Actualiza el contenido y todos sus estados visuales. Reutiliza la imagen si
	 * su ruta no cambió entre actualizaciones.
	 * 
	 * @param datos información de presentación; no puede ser null
	 */
	public void mostrar(Datos datos) {
		if (datos == null) {
			throw new IllegalArgumentException("Los datos de la carta no pueden ser null");
		}
		boolean visible = datos.contenido() == Contenido.CARTA;
		contenido.setVisible(visible);
		contenido.setManaged(visible);
		simbolo.setVisible(!visible);
		simbolo.setManaged(!visible);
		estado("vacia", datos.contenido() == Contenido.VACIA);
		estado("reverso", datos.contenido() == Contenido.REVERSO);
		estado("seleccionada", datos.seleccionada());
		estado("objetivo", datos.objetivo());
		estado("gastada", datos.gastada());
		estado("oculta", datos.oculta());
		estado("poderosa", visible && datos.poderosa());
		if (claseTipo != null) {
			getStyleClass().remove(claseTipo);
			claseTipo = null;
		}
		if (!visible) {
			imagen.setImage(null);
			rutaActual = null;
			boolean reverso = datos.contenido() == Contenido.REVERSO;
			simbolo.setText(reverso ? "◆\nBOCA ABAJO" : "◇");
			ayuda.setText(reverso ? "Carta rival boca abajo" : "Zona vacía");
			setAccessibleText(ayuda.getText());
			return;
		}
		claseTipo = "tipo-" + datos.tipo().toLowerCase(Locale.ROOT).replace('_', '-');
		getStyleClass().add(claseTipo);
		nombre.setText(datos.nombre());
		valores.setText("ATK " + datos.ataque() + "\nDEF " + datos.defensa());
		alternativa.setText(datos.tipo().replace('_', ' '));
		boolean mostrarPosicion = datos.enDefensa() || datos.oculta();
		posicion.setText(datos.enDefensa() ? (datos.oculta() ? "DEFENSA · OCULTA" : "DEFENSA") : "OCULTA");
		posicion.setVisible(mostrarPosicion);
		posicion.setManaged(mostrarPosicion);
		cargarImagen(datos.rutaImagen());
		ayuda.setText(datos.nombre() + "\n" + valores.getText() + (mostrarPosicion ? "\n" + posicion.getText() : ""));
		setAccessibleText(ayuda.getText());
	}

	/** Actualiza una pseudoclase que la hoja CSS utiliza para dibujar el estado. */
	private void estado(String nombre, boolean activo) {
		pseudoClassStateChanged(PseudoClass.getPseudoClass(nombre), activo);
	}

	/** Carga un recurso gráfico; si falta, conserva el tipo como alternativa. */
	private void cargarImagen(String ruta) {
		if (ruta == null || ruta.isBlank()) {
			imagen.setImage(null);
			rutaActual = null;
		} else if (!ruta.equals(rutaActual)) {
			rutaActual = ruta;
			URL recurso = VistaCarta.class.getResource(ruta.startsWith("/") ? ruta : "/" + ruta);
			Image nueva = recurso == null ? null : new Image(recurso.toExternalForm(), 328, 220, true, true);
			imagen.setImage(nueva != null && !nueva.isError() ? nueva : null);
		}
		alternativa.setVisible(imagen.getImage() == null);
	}
}
