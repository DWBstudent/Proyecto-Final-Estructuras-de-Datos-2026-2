package co.edu.unbosque;

import co.edu.unbosque.vista.JuegoApp;
import javafx.application.Application;

/**
 * Punto de entrada. Solo arranca la aplicación; no tiene lógica.
 * Existe aparte de {@link JuegoApp} para que el proyecto corra sin configurar módulos de JavaFX.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Application.launch(JuegoApp.class, args);
    }
}
