package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.estructuras.MonticuloMax;

/**
 * IA del rival 2 (calcula cada ataque):
 * <ol>
 * <li>Invoca la carta de mayor ATK de su mano. La pone en defensa si es más débil que el
 *     mejor monstruo en ataque del rival (la destruiría); si no, en ataque.</li>
 * <li>Genera todas las jugadas posibles (hasta 5 atacantes por 5 objetivos = 25), puntúa
 *     cada una con {@link Combate} y las mete en un {@link MonticuloMax}:
 *     puntaje = daño causado − daño recibido, +1000 si destruye al objetivo y −1000 si
 *     pierde el suyo. Un ataque directo vale el ATK completo.</li>
 * <li>Saca la mejor. Si su puntaje es mayor que 0, la hace y vuelve al paso 2 con el campo
 *     nuevo; si no, se defiende (no ataca más).</li>
 * </ol>
 *
 * <p>Responsable: Santiago.
 */
public class IARival2 implements EstrategiaIA {

    static final int BONO_POR_DESTRUIR = 1000;

    /**
     * Complejidad: O(1): como máximo 5 rondas de ataque, y en cada una se insertan hasta
     * 25 jugadas en el montículo (O(log 25) cada una).
     */
    @Override
    public void jugarTurno(Duelo duelo) {
        if (duelo.estaTerminado()) {
            throw new IllegalStateException("El duelo ya terminó");
        }
        Duelista propio = duelo.getActual();
        int posicionMano = SeleccionIA.posicionDeMayorAtaque(propio);
        if (posicionMano != -1 && !duelo.yaInvocoEnEsteTurno()) {
            Carta carta = propio.getCartaEnMano(posicionMano);
            duelo.invocar(posicionMano, SeleccionIA.zonaParaInvocar(propio),
                    posicionParaInvocar(carta, duelo.getRival()));
        }
        while (duelo.sePuedeAtacarEnEsteTurno()) {
            Jugada mejor = mejorJugada(duelo);
            if (mejor == null || mejor.getPuntaje() <= 0) {
                return;
            }
            if (mejor.esDirecta()) {
                duelo.atacarDirecto(mejor.getZonaAtacante());
            } else {
                duelo.atacar(mejor.getZonaAtacante(), mejor.getZonaObjetivo());
            }
        }
    }

    /** Defensa si la carta es más débil que el mejor atacante rival; si no, ataque. */
    Posicion posicionParaInvocar(Carta carta, Duelista rival) {
        return carta.getAtaque() < SeleccionIA.mayorAtaqueEnCampo(rival) ? Posicion.DEFENSA : Posicion.ATAQUE;
    }

    /**
     * La jugada de mayor puntaje del duelista actual, o {@code null} si ningún monstruo
     * puede atacar. Arma el montículo con todas las jugadas y saca el máximo.
     */
    Jugada mejorJugada(Duelo duelo) {
        Duelista propio = duelo.getActual();
        Duelista rival = duelo.getRival();
        MonticuloMax<Jugada> jugadas = new MonticuloMax<>(Jugada.POR_PUNTAJE);
        for (int zona = 0; zona < Duelista.ZONAS_DE_MONSTRUO; zona++) {
            MonstruoEnCampo atacante = propio.getMonstruo(zona);
            if (atacante == null || !atacante.puedeAtacar()) {
                continue;
            }
            if (!rival.tieneMonstruos()) {
                ResultadoCombate directo = Combate.ataqueDirecto(atacante.getCarta());
                jugadas.insertar(new Jugada(zona, Jugada.DIRECTO, puntaje(directo, rival)));
                continue;
            }
            for (int objetivo = 0; objetivo < Duelista.ZONAS_DE_MONSTRUO; objetivo++) {
                MonstruoEnCampo defensor = rival.getMonstruo(objetivo);
                if (defensor != null) {
                    ResultadoCombate resultado = Combate.resolver(atacante.getCarta(), defensor);
                    jugadas.insertar(new Jugada(zona, objetivo, puntaje(resultado, rival)));
                }
            }
        }
        return jugadas.estaVacio() ? null : jugadas.extraerMax();
    }

    /** Puntaje de un ataque: daño causado − daño recibido, ±1000 por monstruo destruido. */
    int puntaje(ResultadoCombate resultado, Duelista rival) {
        int puntaje = resultado.getDanioAlDefensor() - resultado.getDanioAlAtacante();
        if (resultado.destruyeObjetivo()) {
            puntaje += BONO_POR_DESTRUIR;
        }
        if (resultado.destruyeAtacante()) {
            puntaje -= BONO_POR_DESTRUIR;
        }
        return puntaje;
    }
}
