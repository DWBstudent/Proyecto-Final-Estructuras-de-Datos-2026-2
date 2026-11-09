package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;

/**
 * La carta que se ganó al vencer a un rival y si entró al Chest. Si el jugador ya tenía
 * {@value Chest#MAXIMO_COPIAS} copias, no se agrega y la pantalla lo informa. Es inmutable.
 *
 * <p>Responsable: Santiago.
 */
public final class Recompensa {

    private final Carta carta;
    private final boolean agregada;

    Recompensa(Carta carta, boolean agregada) {
        this.carta = carta;
        this.agregada = agregada;
    }

    /** Complejidad: O(1). */
    public Carta getCarta() {
        return carta;
    }

    /** {@code false} si el jugador ya tenía 3 copias y no se agregó. Complejidad: O(1). */
    public boolean fueAgregada() {
        return agregada;
    }

    /** Texto para la pantalla de resultado. Complejidad: O(1). */
    public String mensaje() {
        if (agregada) {
            return "Ganaste " + carta.getNombre();
        }
        return "Ganaste " + carta.getNombre() + ", pero ya tienes " + Chest.MAXIMO_COPIAS
                + " copias y no se agrega";
    }
}
