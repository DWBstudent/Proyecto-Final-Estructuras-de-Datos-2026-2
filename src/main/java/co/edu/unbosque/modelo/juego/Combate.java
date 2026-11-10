package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;

/**
 * Tabla de resultados de un ataque (reglas de combate de Konami, sin efectos). No cambia
 * nada: solo calcula. El {@link Duelo} la usa para aplicar el ataque y la IA para puntuar
 * jugadas sin hacerlas.
 *
 * <pre>
 * Objetivo en ataque:   ATK &gt; ATK  destruye el objetivo; su dueño pierde la diferencia
 *                       ATK &lt; ATK  destruye el atacante; su dueño pierde la diferencia
 *                       ATK = ATK  destruye ambos; nadie pierde puntos
 * Objetivo en defensa:  ATK &gt; DEF  destruye el objetivo; nadie pierde puntos
 *                       ATK &lt; DEF  no destruye; el dueño del atacante pierde la diferencia
 *                       ATK = DEF  no pasa nada
 * Sin monstruos:        ataque directo; el rival pierde el ATK completo
 * </pre>
 *
 * <p>Responsable: Santiago.
 */
public final class Combate {

    private Combate() {
        // Solo tiene métodos estáticos.
    }

    /**
     * Calcula el ataque de una carta contra un monstruo del campo rival. Complejidad: O(1).
     *
     * @throws IllegalArgumentException si el atacante o el objetivo son {@code null}
     */
    public static ResultadoCombate resolver(Carta atacante, MonstruoEnCampo objetivo) {
        if (atacante == null) {
            throw new IllegalArgumentException("Falta la carta que ataca");
        }
        if (objetivo == null) {
            throw new IllegalArgumentException("Falta el monstruo atacado; para atacar directo usa ataqueDirecto");
        }
        int ataque = atacante.getAtaque();
        int valorObjetivo = objetivo.valorDeCombate();
        int diferencia = ataque - valorObjetivo;

        if (objetivo.getPosicion() == Posicion.ATAQUE) {
            if (diferencia > 0) {
                return new ResultadoCombate(true, false, 0, diferencia, false);
            }
            if (diferencia < 0) {
                return new ResultadoCombate(false, true, -diferencia, 0, false);
            }
            return new ResultadoCombate(true, true, 0, 0, false);
        }
        if (diferencia > 0) {
            return new ResultadoCombate(true, false, 0, 0, false);
        }
        if (diferencia < 0) {
            return new ResultadoCombate(false, false, -diferencia, 0, false);
        }
        return new ResultadoCombate(false, false, 0, 0, false);
    }

    /**
     * Calcula un ataque directo: el rival pierde el ATK completo. Complejidad: O(1).
     *
     * @throws IllegalArgumentException si el atacante es {@code null}
     */
    public static ResultadoCombate ataqueDirecto(Carta atacante) {
        if (atacante == null) {
            throw new IllegalArgumentException("Falta la carta que ataca");
        }
        return new ResultadoCombate(false, false, 0, atacante.getAtaque(), true);
    }
}
