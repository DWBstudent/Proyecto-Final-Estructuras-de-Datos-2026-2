package co.edu.unbosque.modelo.juego;

/**
 * Lo que pasa en un ataque según la tabla de combate: qué monstruos se destruyen y cuántos
 * puntos de vida pierde cada dueño. Es inmutable. Lo crea {@link Combate}.
 *
 * <p>Responsable: Santiago.
 */
public final class ResultadoCombate {

    private final boolean destruyeObjetivo;
    private final boolean destruyeAtacante;
    private final int danioAlAtacante;
    private final int danioAlDefensor;
    private final boolean ataqueDirecto;

    ResultadoCombate(boolean destruyeObjetivo, boolean destruyeAtacante, int danioAlAtacante,
                     int danioAlDefensor, boolean ataqueDirecto) {
        this.destruyeObjetivo = destruyeObjetivo;
        this.destruyeAtacante = destruyeAtacante;
        this.danioAlAtacante = danioAlAtacante;
        this.danioAlDefensor = danioAlDefensor;
        this.ataqueDirecto = ataqueDirecto;
    }

    /** Complejidad: O(1). */
    public boolean destruyeObjetivo() {
        return destruyeObjetivo;
    }

    /** Complejidad: O(1). */
    public boolean destruyeAtacante() {
        return destruyeAtacante;
    }

    /** Puntos de vida que pierde el dueño del atacante. Complejidad: O(1). */
    public int getDanioAlAtacante() {
        return danioAlAtacante;
    }

    /** Puntos de vida que pierde el dueño del objetivo (o el rival, si es directo). Complejidad: O(1). */
    public int getDanioAlDefensor() {
        return danioAlDefensor;
    }

    /** Complejidad: O(1). */
    public boolean esAtaqueDirecto() {
        return ataqueDirecto;
    }
}
