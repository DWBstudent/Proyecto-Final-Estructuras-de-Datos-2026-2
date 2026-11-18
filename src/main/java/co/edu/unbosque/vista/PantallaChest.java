package co.edu.unbosque.vista;

import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

/**
 * Muestra el cofre y permite solicitar que una copia pase a la baraja.
 *
 * <p>Responsable: Brayan.
 */
public final class PantallaChest extends PantallaColeccion {

    /**
     * Configura los textos y las acciones del cofre. Complejidad O(1).
     *
     * @param alMostrar acción que carga el contenido del cofre
     * @param alSeleccionar acción que muestra el detalle de la fila seleccionada
     * @param alTrasladar acción que solicita pasar una copia al Deck
     * @param abrirDeck acción que abre la baraja
     * @param volver acción que regresa al menú
     */
    public PantallaChest(
            Consumer<PantallaColeccion> alMostrar,
            ObjIntConsumer<PantallaColeccion> alSeleccionar,
            ObjIntConsumer<PantallaColeccion> alTrasladar,
            Runnable abrirDeck,
            Runnable volver) {
        super("CHEST", "El cofre está vacío", "PASAR AL DECK", "VER DECK",
                alMostrar, alSeleccionar, alTrasladar, abrirDeck, volver);
    }
}