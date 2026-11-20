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

/**
 * Pantalla entre turnos del modo jugador contra jugador: tapa el tablero para
 * que el siguiente jugador no vea la mano del anterior. Al presionar el botón
 * se vuelve al duelo.
 *
 * <p>
 * Responsable: Santiago.
 */
public class PantallaCambioTurno implements Pantalla {
	private final StackPane raiz = new StackPane();

	/**
	 * Construye el aviso de cambio de turno.
	 * 
	 * @param nombreDelSiguiente nombre del jugador que recibirá el control
	 * @param alContinuar        acción que muestra el tablero cuando esté listo
	 * @throws IllegalArgumentException si falta algún argumento
	 * @throws IllegalStateException    si no existe duelo.css
	 */
	public PantallaCambioTurno(String nombreDelSiguiente, Runnable alContinuar) {
		if (nombreDelSiguiente == null || alContinuar == null) {
			throw new IllegalArgumentException("Falta el jugador o la acción de continuar");
		}
		raiz.getStyleClass().addAll("duelo-retro", "cambio-turno-retro");
		Region velo = new Region();
		velo.getStyleClass().add("duelo-velo");
		velo.setMouseTransparent(true);

		Label titulo = new Label("Turno de " + nombreDelSiguiente);
		titulo.getStyleClass().add("cambio-titulo");
		titulo.setWrapText(true);
		titulo.setMaxWidth(Double.MAX_VALUE);

		Label aviso = new Label(
				"Pásale el control a " + nombreDelSiguiente + ". El otro jugador no debe mirar la pantalla.");
		aviso.getStyleClass().add("cambio-aviso");
		aviso.setWrapText(true);
		aviso.setMaxWidth(Double.MAX_VALUE);

		Button listo = new Button("Estoy listo");
		listo.getStyleClass().addAll("boton-piedra", "boton-principal");
		listo.setPrefWidth(240);
		listo.setOnAction(evento -> alContinuar.run());

		VBox panel = new VBox(24, titulo, aviso, listo);
		panel.getStyleClass().add("cambio-panel");
		panel.setAlignment(Pos.CENTER);
		panel.setMaxWidth(760);
		panel.setMaxHeight(Region.USE_PREF_SIZE);
		StackPane centro = new StackPane(panel);
		centro.setPadding(new Insets(28));
		ScrollPane scroll = new ScrollPane(centro);
		scroll.getStyleClass().add("duelo-scroll");
		scroll.setFitToWidth(true);
		scroll.setFitToHeight(true);
		raiz.getChildren().addAll(velo, scroll);

		URL css = PantallaCambioTurno.class.getResource("/estilos/duelo.css");
		if (css == null) {
			throw new IllegalStateException("No se encontró /estilos/duelo.css");
		}
		raiz.getStylesheets().add(css.toExternalForm());
	}

	/** @return contenedor del aviso; nunca contiene cartas del duelo */
	@Override
	public Parent getVista() {
		return raiz;
	}
}