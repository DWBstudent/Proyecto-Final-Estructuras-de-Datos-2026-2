package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;

/**
 * El Chest del jugador: hasta 100 cartas distintas, con máximo 3 copias de cada una
 * sumando las que están en el Deck.
 *
 * <p>Responsable: Brayan (hito H2). Contrato del hito H0: los métodos ya están
 * declarados para que los demás puedan compilar; Brayan decide la implementación
 * (ver "Estructuras de datos por requisito" en la ruta de desarrollo).
 */
public class Chest {

    /** Cantidad máxima de copias de una misma carta entre Chest y Deck. */
    public static final int MAXIMO_COPIAS = 3;

    /**
     * Agrega una copia de la carta.
     *
     * @return {@code false} si ya hay 3 copias entre Chest y Deck
     * @throws IllegalArgumentException si la carta es {@code null}
     */
    public boolean agregar(Carta carta) {
        throw new UnsupportedOperationException("Pendiente: Brayan (H2)");
    }

    /**
     * Quita una copia de la carta con ese id.
     *
     * @return {@code false} si no había ninguna copia en el Chest
     */
    public boolean quitar(int idCarta) {
        throw new UnsupportedOperationException("Pendiente: Brayan (H2)");
    }

    /** Copias de esa carta que hay en el Chest. */
    public int copiasDe(int idCarta) {
        throw new UnsupportedOperationException("Pendiente: Brayan (H2)");
    }

    /** Cantidad de cartas distintas en el Chest. */
    public int cartasDistintas() {
        throw new UnsupportedOperationException("Pendiente: Brayan (H2)");
    }
}
