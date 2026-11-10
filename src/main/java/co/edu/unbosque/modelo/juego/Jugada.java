package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.ordenamiento.Comparador;

/**
 * Un ataque posible que la IA evalúa: con qué monstruo propio, contra qué zona rival (o
 * directo) y qué tan bueno es. Es inmutable. La IA mete las jugadas en un
 * {@code MonticuloMax<Jugada>} ordenado por {@link #POR_PUNTAJE} y saca la mejor.
 *
 * <p>Responsable: Santiago.
 */
public final class Jugada {

    /** Valor de {@link #getZonaObjetivo()} cuando el ataque es directo. */
    public static final int DIRECTO = -1;

    /** Ordena las jugadas por puntaje: la de mayor puntaje es la mayor. */
    public static final Comparador<Jugada> POR_PUNTAJE =
            (a, b) -> Integer.compare(a.puntaje, b.puntaje);

    private final int zonaAtacante;
    private final int zonaObjetivo;
    private final int puntaje;

    /**
     * Complejidad: O(1).
     *
     * @param zonaObjetivo zona del monstruo rival, o {@link #DIRECTO}
     */
    public Jugada(int zonaAtacante, int zonaObjetivo, int puntaje) {
        this.zonaAtacante = zonaAtacante;
        this.zonaObjetivo = zonaObjetivo;
        this.puntaje = puntaje;
    }

    /** Complejidad: O(1). */
    public int getZonaAtacante() {
        return zonaAtacante;
    }

    /** Zona del monstruo rival, o {@link #DIRECTO}. Complejidad: O(1). */
    public int getZonaObjetivo() {
        return zonaObjetivo;
    }

    /** Complejidad: O(1). */
    public int getPuntaje() {
        return puntaje;
    }

    /** Complejidad: O(1). */
    public boolean esDirecta() {
        return zonaObjetivo == DIRECTO;
    }

    /** Complejidad: O(1). */
    @Override
    public String toString() {
        String objetivo = esDirecta() ? "directo" : "zona " + zonaObjetivo;
        return "Zona " + zonaAtacante + " ataca " + objetivo + " (puntaje " + puntaje + ")";
    }
}
