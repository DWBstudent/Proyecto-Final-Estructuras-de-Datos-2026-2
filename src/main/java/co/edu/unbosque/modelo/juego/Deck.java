package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;

/**
 * El Deck del jugador: exactamente 40 cartas para poder duelar.
 *
 * <p>Responsable: Brayan (hito H2). Contrato del hito H0: Santiago usa
 * {@link #aArreglo()} para armar el mazo del duelo.
 */
public class Deck {

    /** Cartas que debe tener el Deck para duelar. */
    public static final int TAMANIO = 40;

    /**
     * Agrega una copia de la carta.
     *
     * @return {@code false} si el Deck ya tiene 40 cartas o se superarían 3 copias
     * @throws IllegalArgumentException si la carta es {@code null}
     */
    public boolean agregar(Carta carta) {
        throw new UnsupportedOperationException("Pendiente: Brayan (H2)");
    }

    /**
     * Quita una copia de la carta con ese id.
     *
     * @return {@code false} si no había ninguna copia en el Deck
     */
    public boolean quitar(int idCarta) {
        throw new UnsupportedOperationException("Pendiente: Brayan (H2)");
    }

    /** Cartas que tiene el Deck ahora. */
    public int tamanio() {
        throw new UnsupportedOperationException("Pendiente: Brayan (H2)");
    }

    /** {@code true} si tiene exactamente 40 cartas. */
    public boolean estaCompleto() {
        throw new UnsupportedOperationException("Pendiente: Brayan (H2)");
    }

    /**
     * Copia de las cartas del Deck en un arreglo nuevo, para que el duelo las baraje
     * sin modificar el Deck. Complejidad esperada: O(n).
     *
     * @throws IllegalStateException si el Deck no tiene 40 cartas
     */
    public Carta[] aArreglo() {
        throw new UnsupportedOperationException("Pendiente: Brayan (H2)");
    }
}
