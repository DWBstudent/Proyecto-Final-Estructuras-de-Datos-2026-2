package co.edu.unbosque.vista;

import javafx.scene.Parent;

/**
 * Una pantalla del juego (menú, Chest, Deck, duelo, inventario…). Cada pantalla
 * construye sus controles una vez y el {@code Navegador} la muestra.
 *
 * <p>Contrato del hito H0: todas las pantallas implementan esta interfaz.
 */
public interface Pantalla {

    /** Nodo raíz que se pone en la escena. */
    Parent getVista();

    /**
     * Se llama cada vez que la pantalla se muestra, también al volver a ella.
     * Sirve para refrescar datos (por ejemplo, el Chest después de un duelo).
     */
    default void alEntrar() {
    }
}
