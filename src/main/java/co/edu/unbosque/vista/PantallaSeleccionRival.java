package co.edu.unbosque.vista;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Selección de rival del modo historia o del modo libre. Muestra los 3 rivales con su
 * estado ("Siguiente rival", "Vencido", "Bloqueado"…) y un botón para duelar contra los que
 * el controlador habilite. Cada vez que se muestra, le pide los datos al controlador, así
 * después de un duelo se ve el progreso nuevo.
 *
 * <p>Responsable: Santiago.
 */
public class PantallaSeleccionRival implements Pantalla {

    private final VBox raiz;
    private final VBox filas = new VBox(12);
    private final Label mensaje = new Label();
    private final Consumer<PantallaSeleccionRival> alMostrar;
    private final IntConsumer alElegir;

    /**
     * Complejidad: O(1).
     *
     * @param titulo nombre del modo
     * @param descripcion una frase que explica el modo
     * @param alMostrar acción que carga los rivales con {@link #mostrarRivales}
     * @param alElegir acción que recibe la posición (0, 1 o 2) del rival elegido
     * @param volver acción del botón "Volver"
     * @throws IllegalArgumentException si falta algún argumento
     */
    public PantallaSeleccionRival(String titulo, String descripcion, Consumer<PantallaSeleccionRival> alMostrar,
                                  IntConsumer alElegir, Runnable volver) {
        if (titulo == null || descripcion == null || alMostrar == null || alElegir == null || volver == null) {
            throw new IllegalArgumentException("Faltan datos para la selección de rival");
        }
        this.alMostrar = alMostrar;
        this.alElegir = alElegir;

        Label encabezado = new Label(titulo);
        encabezado.setStyle("-fx-text-fill: white; -fx-font-size: 34px; -fx-font-weight: bold;");
        Label explicacion = new Label(descripcion);
        explicacion.setWrapText(true);
        explicacion.setStyle("-fx-text-fill: #d8f3dc; -fx-font-size: 15px;");

        mensaje.setWrapText(true);
        mensaje.setMaxWidth(640);
        mensaje.setStyle("-fx-text-fill: #ffd60a; -fx-font-size: 15px;");

        Button botonVolver = new Button("Volver");
        botonVolver.setStyle("-fx-font-size: 15px;");
        botonVolver.setOnAction(evento -> volver.run());

        filas.setMaxWidth(640);
        raiz = new VBox(18, encabezado, explicacion, filas, mensaje, botonVolver);
        raiz.setAlignment(Pos.CENTER);
        raiz.setPadding(new Insets(32));
        raiz.setStyle("-fx-background-color: #0b2618;");
    }

    /**
     * Dibuja una fila por rival. Los tres arreglos van en el mismo orden.
     * Complejidad: O(n), con n = rivales.
     *
     * @param habilitados si se puede duelar contra cada rival
     * @param texto indicación debajo de la lista (por ejemplo, si falta completar el Deck)
     */
    public void mostrarRivales(String[] nombres, String[] estados, boolean[] habilitados, String texto) {
        filas.getChildren().clear();
        for (int i = 0; i < nombres.length; i++) {
            filas.getChildren().add(fila(i, nombres[i], estados[i], habilitados[i]));
        }
        mensaje.setText(texto == null ? "" : texto);
    }

    private HBox fila(int indice, String nombre, String estado, boolean habilitado) {
        Label etiquetaNombre = new Label(nombre);
        etiquetaNombre.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label etiquetaEstado = new Label(estado);
        etiquetaEstado.setStyle("-fx-text-fill: #b7e4c7; -fx-font-size: 14px;");
        VBox textos = new VBox(4, etiquetaNombre, etiquetaEstado);

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        Button duelar = new Button("Duelar");
        duelar.setStyle("-fx-font-size: 15px;");
        duelar.setPrefWidth(120);
        duelar.setDisable(!habilitado);
        duelar.setOnAction(evento -> alElegir.accept(indice));

        HBox fila = new HBox(16, textos, espacio, duelar);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(14, 18, 14, 18));
        fila.setStyle("-fx-background-color: " + (habilitado ? "#1b4332" : "#14301f")
                + "; -fx-background-radius: 10;");
        return fila;
    }

    /** Complejidad: O(1). */
    @Override
    public Parent getVista() {
        return raiz;
    }

    /** Pide al controlador los datos actualizados. Complejidad: la de {@code alMostrar}. */
    @Override
    public void alEntrar() {
        alMostrar.accept(this);
    }
}
