package co.edu.unbosque.modelo.juego;

import java.util.Random;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.estructuras.Cola;
import co.edu.unbosque.modelo.estructuras.ColaCircular;
import co.edu.unbosque.modelo.estructuras.Pila;

/**
 * Un duelo entre dos duelistas con las reglas del proyecto (solo monstruos, sin fusiones,
 * magias ni trampas). Lo usan la pantalla del duelo, la IA y la campaña.
 *
 * <p>Reglas:
 * <ol>
 * <li>Preparación: cada uno baraja su Deck de 40 cartas, lo apila y roba 5. Empieza el
 *     duelista uno.</li>
 * <li>Robo: al iniciar su turno, el duelista roba hasta tener 5 cartas en la mano. Si debe
 *     robar y su mazo está vacío, pierde.</li>
 * <li>Invocación: 1 monstruo por turno, en ataque (boca arriba) o en defensa (boca abajo).
 *     Si la zona elegida está ocupada, el monstruo que estaba ahí se destruye.</li>
 * <li>Batalla: cada monstruo en ataque ataca una vez por turno. Quien empieza no ataca en su
 *     primer turno. Solo se ataca directo si el rival no tiene monstruos. El resultado sale
 *     de {@link Combate}.</li>
 * <li>Fin: gana quien deja al rival en 0 puntos de vida.</li>
 * </ol>
 *
 * <p>Estructuras: los turnos van en una {@link ColaCircular} de 2 duelistas ({@code rotar()}
 * pasa el turno en O(1)); el registro de lo que pasa va en una {@link Cola} de textos; el
 * mazo de cada uno es una {@code Pila} armada con {@link Barajador}.
 *
 * <p>Responsable: Santiago.
 */
public class Duelo {

    private final Duelista duelistaUno;
    private final Duelista duelistaDos;
    private final ColaCircular<Duelista> turnos = new ColaCircular<>(2);
    private final Cola<String> registro = new Cola<>();
    private int numeroDeTurno = 1;
    private boolean invocoEnEsteTurno;
    private Duelista ganador;

    /**
     * Prepara el duelo: copia y baraja los dos Decks, los apila y cada duelista roba 5
     * cartas. Los arreglos recibidos no se modifican. Complejidad: O(n), con n = 40.
     *
     * @throws IllegalArgumentException si falta un nombre o el {@code Random}, o si un Deck
     *         es {@code null}, no tiene exactamente {@value Deck#TAMANIO} cartas o tiene un {@code null}
     */
    public Duelo(String nombreUno, Carta[] deckUno, String nombreDos, Carta[] deckDos, Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Se necesita un Random para barajar");
        }
        duelistaUno = new Duelista(nombreUno, mazoBarajado(deckUno, nombreUno, random));
        duelistaDos = new Duelista(nombreDos, mazoBarajado(deckDos, nombreDos, random));
        duelistaUno.robarHastaCompletar();
        duelistaDos.robarHastaCompletar();
        turnos.encolar(duelistaUno);
        turnos.encolar(duelistaDos);
        registrar("Empieza el duelo: " + duelistaUno.getNombre() + " contra " + duelistaDos.getNombre());
        registrar("Turno 1: " + duelistaUno.getNombre());
    }

    private static Pila<Carta> mazoBarajado(Carta[] deck, String nombre, Random random) {
        if (deck == null) {
            throw new IllegalArgumentException("El Deck de " + nombre + " no puede ser null");
        }
        if (deck.length != Deck.TAMANIO) {
            throw new IllegalArgumentException("El Deck de " + nombre + " debe tener " + Deck.TAMANIO
                    + " cartas y tiene " + deck.length);
        }
        Carta[] copia = new Carta[deck.length];
        System.arraycopy(deck, 0, copia, 0, deck.length);
        Barajador.barajar(copia, random);
        return Barajador.aPila(copia);
    }

    // Consultas

    /** El duelista que tiene el turno. Complejidad: O(1). */
    public Duelista getActual() {
        return turnos.frente();
    }

    /** El duelista que espera su turno. Complejidad: O(1). */
    public Duelista getRival() {
        return getActual() == duelistaUno ? duelistaDos : duelistaUno;
    }

    /** El duelista que empezó (el del primer Deck). Complejidad: O(1). */
    public Duelista getDuelistaUno() {
        return duelistaUno;
    }

    /** Complejidad: O(1). */
    public Duelista getDuelistaDos() {
        return duelistaDos;
    }

    /** Número del turno actual; el primero es 1. Complejidad: O(1). */
    public int getNumeroDeTurno() {
        return numeroDeTurno;
    }

    /** Indica si el duelista actual ya invocó en este turno. Complejidad: O(1). */
    public boolean yaInvocoEnEsteTurno() {
        return invocoEnEsteTurno;
    }

    /** Indica si en este turno se puede atacar (en el turno 1 no). Complejidad: O(1). */
    public boolean sePuedeAtacarEnEsteTurno() {
        return numeroDeTurno > 1 && !estaTerminado();
    }

    /** Complejidad: O(1). */
    public boolean estaTerminado() {
        return ganador != null;
    }

    /** El ganador, o {@code null} si el duelo sigue. Complejidad: O(1). */
    public Duelista getGanador() {
        return ganador;
    }

    /** Indica si hay eventos del registro sin leer. Complejidad: O(1). */
    public boolean hayEventos() {
        return !registro.estaVacia();
    }

    /**
     * Saca el evento más antiguo del registro (lo que pasó, en orden). Complejidad: O(1).
     *
     * @throws co.edu.unbosque.modelo.estructuras.EstructuraVaciaException si no hay eventos
     */
    public String siguienteEvento() {
        return registro.desencolar();
    }

    // Acciones del duelista actual

    /**
     * Invoca la carta de una posición de la mano en una zona del campo. Si la zona está
     * ocupada, el monstruo que estaba ahí se destruye. Complejidad: O(1).
     *
     * @throws IllegalStateException si el duelo terminó o ya se invocó en este turno
     * @throws IllegalArgumentException si las posiciones no están entre 0 y 4, la posición de
     *         la mano está vacía o la {@code posicion} es {@code null}
     */
    public void invocar(int posicionMano, int zona, Posicion posicion) {
        verificarEnCurso();
        if (invocoEnEsteTurno) {
            throw new IllegalStateException("Solo se puede invocar 1 monstruo por turno");
        }
        Duelista actual = getActual();
        Carta carta = actual.getCartaEnMano(posicionMano);
        actual.getMonstruo(zona); // solo valida que la zona esté entre 0 y 4 antes de cambiar nada
        if (carta == null) {
            throw new IllegalArgumentException("No hay carta en la posición " + posicionMano + " de la mano");
        }
        if (posicion == null) {
            throw new IllegalArgumentException("Falta la posición del monstruo");
        }
        actual.sacarDeMano(posicionMano);
        MonstruoEnCampo anterior = actual.colocar(zona, new MonstruoEnCampo(carta, posicion));
        invocoEnEsteTurno = true;
        if (anterior != null) {
            registrar(actual.getNombre() + " reemplaza a " + anterior.getCarta().getNombre()
                    + ", que se destruye");
        }
        if (posicion == Posicion.ATAQUE) {
            registrar(actual.getNombre() + " invoca a " + carta.getNombre() + " en ataque");
        } else {
            registrar(actual.getNombre() + " coloca un monstruo boca abajo en defensa");
        }
    }

    /**
     * Ataca con el monstruo de {@code zonaAtacante} al monstruo rival de {@code zonaObjetivo}
     * y aplica la tabla de {@link Combate}. Complejidad: O(1).
     *
     * @return lo que pasó en el ataque
     * @throws IllegalStateException si el duelo terminó, es el turno 1 o el monstruo no puede
     *         atacar (está en defensa o ya atacó)
     * @throws IllegalArgumentException si las zonas no están entre 0 y 4 o alguna está vacía
     */
    public ResultadoCombate atacar(int zonaAtacante, int zonaObjetivo) {
        MonstruoEnCampo atacante = validarAtacante(zonaAtacante);
        Duelista actual = getActual();
        Duelista rival = getRival();
        MonstruoEnCampo objetivo = rival.getMonstruo(zonaObjetivo);
        if (objetivo == null) {
            throw new IllegalArgumentException("No hay monstruo rival en la zona " + zonaObjetivo);
        }
        ResultadoCombate resultado = Combate.resolver(atacante.getCarta(), objetivo);
        atacante.marcarAtaque();
        objetivo.voltear();
        registrar(atacante.getCarta().getNombre() + " ataca a " + objetivo.getCarta().getNombre());
        if (resultado.destruyeObjetivo()) {
            rival.retirar(zonaObjetivo);
            registrar(objetivo.getCarta().getNombre() + " es destruido");
        }
        if (resultado.destruyeAtacante()) {
            actual.retirar(zonaAtacante);
            registrar(atacante.getCarta().getNombre() + " es destruido");
        }
        aplicarDanio(resultado, actual, rival);
        return resultado;
    }

    /**
     * Ataca directo a los puntos de vida del rival con el ATK completo. Complejidad: O(1).
     *
     * @return lo que pasó en el ataque
     * @throws IllegalStateException si el duelo terminó, es el turno 1, el monstruo no puede
     *         atacar o el rival todavía tiene monstruos
     * @throws IllegalArgumentException si la zona no está entre 0 y 4 o está vacía
     */
    public ResultadoCombate atacarDirecto(int zonaAtacante) {
        MonstruoEnCampo atacante = validarAtacante(zonaAtacante);
        Duelista rival = getRival();
        if (rival.tieneMonstruos()) {
            throw new IllegalStateException("Solo se ataca directo si el rival no tiene monstruos");
        }
        ResultadoCombate resultado = Combate.ataqueDirecto(atacante.getCarta());
        atacante.marcarAtaque();
        registrar(atacante.getCarta().getNombre() + " ataca directo a " + rival.getNombre());
        aplicarDanio(resultado, getActual(), rival);
        return resultado;
    }

    /**
     * Termina el turno: pasa el turno al rival con la cola circular y el nuevo duelista roba
     * hasta tener 5 cartas. Si debía robar y no tenía cartas en el mazo, pierde.
     * Complejidad: O(1).
     *
     * @throws IllegalStateException si el duelo terminó
     */
    public void terminarTurno() {
        verificarEnCurso();
        turnos.rotar();
        numeroDeTurno++;
        invocoEnEsteTurno = false;
        Duelista actual = getActual();
        actual.reiniciarTurno();
        registrar("Turno " + numeroDeTurno + ": " + actual.getNombre());
        if (!actual.robarHastaCompletar()) {
            registrar(actual.getNombre() + " debe robar y no le quedan cartas en el mazo");
            terminar(getRival());
        }
    }

    private MonstruoEnCampo validarAtacante(int zonaAtacante) {
        verificarEnCurso();
        MonstruoEnCampo atacante = getActual().getMonstruo(zonaAtacante);
        if (numeroDeTurno == 1) {
            throw new IllegalStateException("Quien empieza el duelo no ataca en su primer turno");
        }
        if (atacante == null) {
            throw new IllegalArgumentException("No hay monstruo propio en la zona " + zonaAtacante);
        }
        if (atacante.getPosicion() != Posicion.ATAQUE) {
            throw new IllegalStateException("Un monstruo en defensa no puede atacar");
        }
        if (atacante.yaAtaco()) {
            throw new IllegalStateException(atacante.getCarta().getNombre() + " ya atacó en este turno");
        }
        return atacante;
    }

    private void aplicarDanio(ResultadoCombate resultado, Duelista actual, Duelista rival) {
        if (resultado.getDanioAlDefensor() > 0) {
            rival.perderPuntos(resultado.getDanioAlDefensor());
            registrar(rival.getNombre() + " pierde " + resultado.getDanioAlDefensor()
                    + " puntos de vida (le quedan " + rival.getPuntosDeVida() + ")");
        }
        if (resultado.getDanioAlAtacante() > 0) {
            actual.perderPuntos(resultado.getDanioAlAtacante());
            registrar(actual.getNombre() + " pierde " + resultado.getDanioAlAtacante()
                    + " puntos de vida (le quedan " + actual.getPuntosDeVida() + ")");
        }
        if (rival.estaDerrotado()) {
            terminar(actual);
        } else if (actual.estaDerrotado()) {
            terminar(rival);
        }
    }

    private void terminar(Duelista elGanador) {
        ganador = elGanador;
        registrar("Gana " + elGanador.getNombre());
    }

    private void verificarEnCurso() {
        if (estaTerminado()) {
            throw new IllegalStateException("El duelo ya terminó; ganó " + ganador.getNombre());
        }
    }

    private void registrar(String evento) {
        registro.encolar(evento);
    }
}
