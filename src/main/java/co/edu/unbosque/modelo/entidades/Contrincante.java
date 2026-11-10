package co.edu.unbosque.modelo.entidades;

import java.util.Random;

import co.edu.unbosque.modelo.juego.Catalogo;
import co.edu.unbosque.modelo.juego.Deck;

/**
 * Uno de los 3 rivales del modo historia: su nombre, su Deck de 40 cartas y su lista de
 * recompensas. Al ganarle, el jugador recibe 1 carta al azar de esa lista.
 *
 * <p>Los Decks se arman por rangos de id del catálogo de 100 cartas, que va de las más
 * débiles (ids 1 a 40) a las más fuertes (ids 71 a 100):
 * <pre>
 * Rival  Deck                      Recompensas
 *   1    ids 1 a 40, 1 copia       ids 41 a 50
 *   2    ids 41 a 60, 2 copias     ids 81 a 90
 *   3    ids 71 a 90, 2 copias     ids 91 a 100
 * </pre>
 *
 * <p>Responsable: Santiago.
 */
public final class Contrincante {

    private static final String[] NOMBRES = {"Tomás el Aprendiz", "Capitana Brisa", "Rey Sombrío"};

    // Por rival: primer id del Deck, último id del Deck, copias de cada id,
    // primer id de las recompensas, último id de las recompensas.
    private static final int[][] RANGOS = {
        {1, 40, 1, 41, 50},
        {41, 60, 2, 81, 90},
        {71, 90, 2, 91, 100},
    };

    private final int numero;
    private final String nombre;
    private final Carta[] deck;
    private final Carta[] recompensas;

    private Contrincante(int numero, String nombre, Carta[] deck, Carta[] recompensas) {
        this.numero = numero;
        this.nombre = nombre;
        this.deck = deck;
        this.recompensas = recompensas;
    }

    /**
     * Crea el rival {@code numero} (1, 2 o 3) con las cartas del catálogo.
     * Complejidad: O(n), con n = 40 cartas del Deck + las recompensas.
     *
     * @throws IllegalArgumentException si el número no está entre 1 y {@value Jugador#TOTAL_RIVALES}
     *         o el catálogo es {@code null}
     * @throws IllegalStateException si el catálogo no tiene alguna de las cartas
     */
    public static Contrincante crear(int numero, Catalogo catalogo) {
        if (numero < 1 || numero > Jugador.TOTAL_RIVALES) {
            throw new IllegalArgumentException("El rival debe estar entre 1 y " + Jugador.TOTAL_RIVALES + ": " + numero);
        }
        if (catalogo == null) {
            throw new IllegalArgumentException("Se necesita el catálogo para armar el rival");
        }
        int[] rango = RANGOS[numero - 1];
        Carta[] deck = cartasDelRango(catalogo, rango[0], rango[1], rango[2]);
        if (deck.length != Deck.TAMANIO) {
            throw new IllegalStateException("El Deck del rival " + numero + " quedó con " + deck.length + " cartas");
        }
        Carta[] recompensas = cartasDelRango(catalogo, rango[3], rango[4], 1);
        return new Contrincante(numero, NOMBRES[numero - 1], deck, recompensas);
    }

    private static Carta[] cartasDelRango(Catalogo catalogo, int desde, int hasta, int copias) {
        Carta[] cartas = new Carta[(hasta - desde + 1) * copias];
        int posicion = 0;
        for (int id = desde; id <= hasta; id++) {
            Carta carta = catalogo.buscarPorId(id);
            if (carta == null) {
                throw new IllegalStateException("El catálogo no tiene la carta " + id);
            }
            for (int copia = 0; copia < copias; copia++) {
                cartas[posicion++] = carta;
            }
        }
        return cartas;
    }

    /** Número del rival en el modo historia (1, 2 o 3). Complejidad: O(1). */
    public int getNumero() {
        return numero;
    }

    /** Complejidad: O(1). */
    public String getNombre() {
        return nombre;
    }

    /** Copia de las 40 cartas del Deck, lista para pasarla al duelo. Complejidad: O(n). */
    public Carta[] getDeck() {
        return copiar(deck);
    }

    /** Copia de la lista de recompensas. Complejidad: O(n). */
    public Carta[] getRecompensas() {
        return copiar(recompensas);
    }

    /**
     * Elige 1 carta al azar de la lista de recompensas. Complejidad: O(1).
     *
     * @throws IllegalArgumentException si el {@code Random} es {@code null}
     */
    public Carta elegirRecompensa(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Se necesita un Random para elegir la recompensa");
        }
        return recompensas[random.nextInt(recompensas.length)];
    }

    private static Carta[] copiar(Carta[] original) {
        Carta[] copia = new Carta[original.length];
        System.arraycopy(original, 0, copia, 0, original.length);
        return copia;
    }

    /** Complejidad: O(1). */
    @Override
    public String toString() {
        return "Rival " + numero + ": " + nombre;
    }
}
