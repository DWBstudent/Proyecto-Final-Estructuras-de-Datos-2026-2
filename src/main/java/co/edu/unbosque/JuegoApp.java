package co.edu.unbosque;

import co.edu.unbosque.controlador.Navegador;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import co.edu.unbosque.controlador.ControladorMenu;

/**
 * Aplicación JavaFX: crea la ventana, la escena y el navegador, y muestra la
 * primera pantalla.
 *
 * <p>
 * Responsable: Daniel.
 */
public class JuegoApp extends Application {

	/** Ancho inicial de la ventana. */
	public static final double ANCHO = 1920;

	/** Alto inicial de la ventana. */
	public static final double ALTO = 1080;

	/** Controlador que coordina las acciones principales del juego. */
	private ControladorMenu controlador;

	/**
	 * Construye los componentes y muestra la ventana principal.
	 *
	 * @param escenario ventana proporcionada por JavaFX
	 */
	@Override
	public void start(Stage escenario) {
		Scene escena = new Scene(new StackPane(), ANCHO, ALTO);
		Navegador navegador = new Navegador(escena);
		controlador = new ControladorMenu(navegador);
		navegador.ir(controlador.crearPantallaMenu());
		escenario.setTitle("Juego de cartas");
		escenario.setScene(escena);
		escenario.show();
	}

	/**
	 * Solicita guardar la partida al finalizar la aplicación.
	 *
	 * <p>
	 * También se ejecuta cuando se solicita salir mediante Platform.exit().
	 */
	@Override
	public void stop() {
		if (controlador != null) {
			controlador.guardarPartida();
		}
	}
}