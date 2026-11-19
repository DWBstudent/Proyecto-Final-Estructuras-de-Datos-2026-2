package co.edu.unbosque.vista;

import co.edu.unbosque.controlador.Navegador;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import co.edu.unbosque.controlador.ControladorMenu;

/**
 * Aplicación JavaFX: crea la ventana, la escena y el navegador, y muestra la primera pantalla.
 *
 * <p>Responsable: Daniel.
 */
public class JuegoApp extends Application {

    public static final double ANCHO = 1024;
    public static final double ALTO = 720;
    

    private ControladorMenu controlador;

    @Override
    public void start(Stage escenario) {
        Scene escena = new Scene(new StackPane(), ANCHO, ALTO);
        Navegador navegador = new Navegador(escena);
        controlador = new ControladorMenu(navegador);
        navegador.ir(new PantallaMenu(controlador));
        escenario.setTitle("Juego de cartas");
        escenario.setScene(escena);
        escenario.show();
    }

    /** JavaFX lo llama al cerrar la ventana o al pulsar Salir: se guarda la partida. */
    @Override
    public void stop() {
        if (controlador != null) {
            controlador.guardarPartida();
        }
    }
}
