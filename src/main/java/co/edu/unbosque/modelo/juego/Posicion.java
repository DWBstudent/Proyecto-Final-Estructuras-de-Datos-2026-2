package co.edu.unbosque.modelo.juego;

/**
 * Posición de un monstruo en el campo. En {@link #ATAQUE} está boca arriba y puede atacar;
 * en {@link #DEFENSA} está boca abajo, no ataca y, si lo atacan, se compara con su defensa.
 *
 * <p>Responsable: Santiago.
 */
public enum Posicion {
    ATAQUE,
    DEFENSA
}
