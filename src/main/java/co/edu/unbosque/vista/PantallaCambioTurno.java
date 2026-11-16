package co.edu.unbosque.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Pantalla entre turnos del modo jugador contra jugador: tapa el tablero para que el
 * siguiente jugador no vea la mano del anterior. Al presionar el botón se vuelve al duelo.
 *
 * <p>Responsable: Santiago.
 */
public class PantallaCambioTurno implements Pantalla {

    private final VBox raiz;

    /**
     * Complejidad: O(1).
     *
     * @param nombreDelSiguiente jugador que va a jugar ahora
     * @param alContinuar qué hacer cuando el jugador está listo (volver al duelo)
     * @throws IllegalArgumentException si falta algún argumento
     */
    public PantallaCambioTurno(String nombreDelSiguiente, Runnable alContinuar) {
        if (nombreDelSiguiente == null || alContinuar == null) {
            throw new IllegalArgumentException("Falta el nombre del jugador o la acción de continuar");
        }
        Label titulo = new Label("Turno de " + nombreDelSiguiente);
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 34px; -fx-font-weight: bold;");
        Label aviso = new Label("Pásale el control a " + nombreDelSiguiente
                + ". El otro jugador no debe mirar la pantalla.");
        aviso.setStyle("-fx-text-fill: #d8f3dc; -fx-font-size: 16px;");
        Button listo = new Button("Estoy listo");
        listo.setStyle("-fx-font-size: 16px;");
        listo.setPrefWidth(200);
        listo.setOnAction(evento -> alContinuar.run());

        raiz = new VBox(22, titulo, aviso, listo);
        raiz.setAlignment(Pos.CENTER);
        raiz.setPadding(new Insets(24));
        raiz.setStyle("-fx-background-color: #0b2618;");
    }

    /** Complejidad: O(1). */
    @Override
    public Parent getVista() {
        return raiz;
    }
}
