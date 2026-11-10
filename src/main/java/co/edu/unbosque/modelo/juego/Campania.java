package co.edu.unbosque.modelo.juego;

import java.util.Random;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.Contrincante;
import co.edu.unbosque.modelo.entidades.Jugador;
import co.edu.unbosque.modelo.estructuras.Cola;

/**
 * Modo historia: el jugador enfrenta a los 3 rivales en orden. Los rivales que faltan van en
 * una {@link Cola}: el frente es el rival actual; al ganarle sale de la cola y el jugador
 * recibe 1 carta de su lista de recompensas; al perder, se repite el mismo rival. La campaña
 * termina cuando la cola queda vacía.
 *
 * <p>Si el jugador ya venció rivales (por ejemplo, al cargar una partida guardada), la cola
 * empieza en el siguiente rival.
 *
 * <p>Responsable: Santiago.
 */
public class Campania {

    private final Jugador jugador;
    private final Cola<Contrincante> rivales = new Cola<>();

    /**
     * Arma la cola con los rivales que el jugador todavía no ha vencido.
     * Complejidad: O(n), con n = cartas de los Decks de los rivales (40 por rival).
     *
     * @throws IllegalArgumentException si el jugador o el catálogo son {@code null}
     */
    public Campania(Jugador jugador, Catalogo catalogo) {
        if (jugador == null) {
            throw new IllegalArgumentException("La campaña necesita un jugador");
        }
        if (catalogo == null) {
            throw new IllegalArgumentException("La campaña necesita el catálogo");
        }
        this.jugador = jugador;
        for (int numero = jugador.getRivalesVencidos() + 1; numero <= Jugador.TOTAL_RIVALES; numero++) {
            rivales.encolar(Contrincante.crear(numero, catalogo));
        }
    }

    /** Complejidad: O(1). */
    public Jugador getJugador() {
        return jugador;
    }

    /** Indica si ya venció a los 3 rivales. Complejidad: O(1). */
    public boolean estaCompleta() {
        return rivales.estaVacia();
    }

    /** Rivales que faltan por vencer, contando el actual. Complejidad: O(1). */
    public int rivalesRestantes() {
        return rivales.tamanio();
    }

    /**
     * El rival que toca enfrentar. Complejidad: O(1).
     *
     * @throws IllegalStateException si la campaña está completa
     */
    public Contrincante getRivalActual() {
        verificarIncompleta();
        return rivales.frente();
    }

    /**
     * La IA del rival actual. Complejidad: O(1).
     *
     * @throws IllegalStateException si la campaña está completa
     */
    public EstrategiaIA estrategiaDelRivalActual() {
        return estrategiaPara(getRivalActual().getNumero());
    }

    /**
     * La IA que corresponde a cada rival: 1, 2 o 3. Complejidad: O(1).
     *
     * @throws IllegalArgumentException si el número no está entre 1 y 3
     */
    public static EstrategiaIA estrategiaPara(int numeroDeRival) {
        switch (numeroDeRival) {
            case 1:
                return new IARival1();
            case 2:
                return new IARival2();
            case 3:
                return new IARival3();
            default:
                throw new IllegalArgumentException("El rival debe estar entre 1 y " + Jugador.TOTAL_RIVALES
                        + ": " + numeroDeRival);
        }
    }

    /**
     * Crea el duelo contra el rival actual. El jugador es el duelista uno y empieza.
     * Complejidad: O(n), con n = 40.
     *
     * @throws IllegalStateException si la campaña está completa
     * @throws IllegalArgumentException si el Deck no tiene 40 cartas o el {@code Random} es {@code null}
     */
    public Duelo crearDuelo(Carta[] deckDelJugador, Random random) {
        Contrincante rival = getRivalActual();
        return new Duelo(jugador.getNombre(), deckDelJugador, rival.getNombre(), rival.getDeck(), random);
    }

    /**
     * Aplica el resultado de un duelo terminado contra el rival actual. Si ganó el jugador
     * (duelista uno), el rival sale de la cola, se registra como vencido y se entrega 1
     * recompensa al azar. Si perdió, no cambia nada y se repite el mismo rival.
     * Complejidad: O(1) más lo que tarde {@link Jugador#recibirCarta(Carta)}.
     *
     * @return la recompensa si ganó, o {@code null} si perdió
     * @throws IllegalStateException si la campaña está completa o el duelo no ha terminado
     * @throws IllegalArgumentException si el duelo o el {@code Random} son {@code null}
     */
    public Recompensa registrarResultado(Duelo duelo, Random random) {
        verificarIncompleta();
        if (duelo == null) {
            throw new IllegalArgumentException("Falta el duelo");
        }
        if (random == null) {
            throw new IllegalArgumentException("Se necesita un Random para la recompensa");
        }
        if (!duelo.estaTerminado()) {
            throw new IllegalStateException("El duelo todavía no ha terminado");
        }
        if (duelo.getGanador() != duelo.getDuelistaUno()) {
            return null;
        }
        Contrincante vencido = rivales.desencolar();
        jugador.registrarRivalVencido();
        Carta carta = vencido.elegirRecompensa(random);
        return new Recompensa(carta, jugador.recibirCarta(carta));
    }

    private void verificarIncompleta() {
        if (estaCompleta()) {
            throw new IllegalStateException("La campaña ya terminó: venciste a los " + Jugador.TOTAL_RIVALES
                    + " rivales");
        }
    }
}
