package co.edu.unbosque.vista;

import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

/**
 * Muestra la baraja, su capacidad ocupada y la opción de devolver cartas al cofre.
 *
 * <p>Responsable: Brayan.
 */
public final class PantallaDeck extends PantallaColeccion {

    /**
     * Configura los textos y las acciones de la baraja. Complejidad O(1).
     *
     * @param alMostrar acción que carga el contenido de la baraja
     * @param alSeleccionar acción que muestra el detalle de la fila seleccionada
     * @param alTrasladar acción que solicita devolver una copia al Chest
     * @param abrirChest acción que abre el cofre
     * @param volver acción que regresa al menú
     */
    public PantallaDeck(
            Consumer<PantallaColeccion> alMostrar,
            ObjIntConsumer<PantallaColeccion> alSeleccionar,
            ObjIntConsumer<PantallaColeccion> alTrasladar,
            Runnable abrirChest,
            Runnable volver) {
        super("DECK", "La baraja está vacía", "DEVOLVER AL CHEST", "VER CHEST",
                alMostrar, alSeleccionar, alTrasladar, abrirChest, volver);
    }
}