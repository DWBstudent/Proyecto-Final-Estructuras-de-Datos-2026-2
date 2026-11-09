package co.edu.unbosque.modelo.juego;

/**
 * Forma de jugar de un rival manejado por la máquina. Cada uno de los 3 rivales del modo
 * historia tiene la suya: {@link IARival1}, {@link IARival2} e {@link IARival3}.
 *
 * <p>Responsable: Santiago.
 */
public interface EstrategiaIA {

    /**
     * Juega el turno del duelista actual del duelo: invoca 1 monstruo y ataca según la
     * estrategia. No termina el turno: eso lo hace quien la llama con
     * {@link Duelo#terminarTurno()}, así la pantalla puede mostrar lo que pasó antes.
     * Si el duelo termina en medio del turno, deja de jugar.
     *
     * @throws IllegalStateException si el duelo ya terminó
     */
    void jugarTurno(Duelo duelo);
}
