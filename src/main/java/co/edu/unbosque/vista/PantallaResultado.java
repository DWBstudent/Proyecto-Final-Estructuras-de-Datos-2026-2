package co.edu.unbosque.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * Resultado de un duelo: quién ganó, la recompensa (si hubo) y cómo terminó. El controlador
 * arma los textos; esta pantalla solo los muestra.
 *
 * <p>Responsable: Santiago.
 */
public class PantallaResultado implements Pantalla {

    private final VBox raiz;

    /**
     * Complejidad: O(n), con n = líneas de texto.
     *
     * @param titulo por ejemplo "¡Ganaste!" o "Perdiste"
     * @param lineas detalles, una por renglón
     * @param alContinuar qué hacer al presionar "Continuar"
     * @throws IllegalArgumentException si falta algún argumento
     */
    public PantallaResultado(String titulo, String[] lineas, Runnable alContinuar) {
        if (titulo == null || lineas == null || alContinuar == null) {
            throw new IllegalArgumentException("Faltan el título, las líneas o la acción de continuar");
        }
        Label encabezado = new Label(titulo);
        encabezado.setStyle("-fx-text-fill: #ffd60a; -fx-font-size: 40px; -fx-font-weight: bold;");

        VBox detalles = new VBox(10);
        detalles.setAlignment(Pos.CENTER);
        for (String linea : lineas) {
            Label etiqueta = new Label(linea);
            etiqueta.setWrapText(true);
            etiqueta.setTextAlignment(TextAlignment.CENTER);
            etiqueta.setMaxWidth(720);
            etiqueta.setStyle("-fx-text-fill: white; -fx-font-size: 17px;");
            detalles.getChildren().add(etiqueta);
        }

        Button continuar = new Button("Continuar");
        continuar.setStyle("-fx-font-size: 16px;");
        continuar.setPrefWidth(200);
        continuar.setOnAction(evento -> alContinuar.run());

        raiz = new VBox(28, encabezado, detalles, continuar);
        raiz.setAlignment(Pos.CENTER);
        raiz.setPadding(new Insets(32));
        raiz.setStyle("-fx-background-color: #0b2618;");
    }

    /** Complejidad: O(1). */
    @Override
    public Parent getVista() {
        return raiz;
    }
}
