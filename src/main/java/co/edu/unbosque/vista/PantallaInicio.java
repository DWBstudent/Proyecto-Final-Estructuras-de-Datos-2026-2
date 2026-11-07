package co.edu.unbosque.vista;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Pantalla provisional del hito H0. Daniel la reemplaza por el menú principal en H3.
 */
public class PantallaInicio implements Pantalla {

    private final VBox raiz;

    public PantallaInicio() {
        Label titulo = new Label("Juego de cartas");
        titulo.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");
        Label estado = new Label("Base del proyecto lista (H0). El menú llega en H3.");
        raiz = new VBox(16, titulo, estado);
        raiz.setAlignment(Pos.CENTER);
    }

    @Override
    public Parent getVista() {
        return raiz;
    }
}
