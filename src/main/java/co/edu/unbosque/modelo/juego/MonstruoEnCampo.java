package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;

/**
 * Una carta colocada en una de las 5 zonas de monstruo del campo, con su posición y con lo
 * que hizo en el turno actual. Solo el {@link Duelo} cambia su estado; la vista y la IA lo leen.
 *
 * <p>Responsable: Santiago.
 */
public final class MonstruoEnCampo {

    private final Carta carta;
    private final Posicion posicion;
    private boolean bocaAbajo;
    private boolean yaAtaco;

    /**
     * Coloca la carta en la posición indicada. En defensa queda boca abajo.
     * Complejidad: O(1).
     *
     * @throws IllegalArgumentException si la carta o la posición son {@code null}
     */
    public MonstruoEnCampo(Carta carta, Posicion posicion) {
        if (carta == null) {
            throw new IllegalArgumentException("El monstruo necesita una carta");
        }
        if (posicion == null) {
            throw new IllegalArgumentException("El monstruo necesita una posición");
        }
        this.carta = carta;
        this.posicion = posicion;
        this.bocaAbajo = posicion == Posicion.DEFENSA;
    }

    /** Complejidad: O(1). */
    public Carta getCarta() {
        return carta;
    }

    /** Complejidad: O(1). */
    public Posicion getPosicion() {
        return posicion;
    }

    /** Indica si la carta sigue oculta para el rival. Complejidad: O(1). */
    public boolean estaBocaAbajo() {
        return bocaAbajo;
    }

    /** Indica si este monstruo ya atacó en el turno actual. Complejidad: O(1). */
    public boolean yaAtaco() {
        return yaAtaco;
    }

    /**
     * Indica si puede atacar ahora: está en ataque y no ha atacado en este turno.
     * No tiene en cuenta la regla del primer turno, que revisa el {@link Duelo}.
     * Complejidad: O(1).
     */
    public boolean puedeAtacar() {
        return posicion == Posicion.ATAQUE && !yaAtaco;
    }

    /** Valor con el que se defiende si lo atacan: ATK en ataque, DEF en defensa. Complejidad: O(1). */
    public int valorDeCombate() {
        return posicion == Posicion.ATAQUE ? carta.getAtaque() : carta.getDefensa();
    }

    void marcarAtaque() {
        yaAtaco = true;
    }

    void reiniciarTurno() {
        yaAtaco = false;
    }

    void voltear() {
        bocaAbajo = false;
    }
}
