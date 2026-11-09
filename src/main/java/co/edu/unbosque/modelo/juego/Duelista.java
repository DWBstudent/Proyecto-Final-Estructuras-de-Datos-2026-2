package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.estructuras.Pila;

/**
 * Uno de los dos lados de un duelo: puntos de vida, mazo, mano y campo. La mano y el campo
 * son arreglos fijos de 5 posiciones; una posición vacía es {@code null}, así las cartas no
 * cambian de lugar en la pantalla. Solo el {@link Duelo} cambia su estado; la vista y la IA
 * usan los métodos públicos de consulta.
 *
 * <p>Responsable: Santiago.
 */
public final class Duelista {

    public static final int PUNTOS_DE_VIDA_INICIALES = 8000;
    public static final int TAMANIO_MANO = 5;
    public static final int ZONAS_DE_MONSTRUO = 5;

    private final String nombre;
    private final Pila<Carta> mazo;
    private final Carta[] mano = new Carta[TAMANIO_MANO];
    private final MonstruoEnCampo[] campo = new MonstruoEnCampo[ZONAS_DE_MONSTRUO];
    private int puntosDeVida = PUNTOS_DE_VIDA_INICIALES;

    Duelista(String nombre, Pila<Carta> mazo) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El duelista necesita un nombre");
        }
        if (mazo == null) {
            throw new IllegalArgumentException("El duelista necesita un mazo");
        }
        this.nombre = nombre.trim();
        this.mazo = mazo;
    }

    /** Complejidad: O(1). */
    public String getNombre() {
        return nombre;
    }

    /** Complejidad: O(1). */
    public int getPuntosDeVida() {
        return puntosDeVida;
    }

    /** Cartas que quedan por robar. Complejidad: O(1). */
    public int cartasEnMazo() {
        return mazo.tamanio();
    }

    /**
     * Carta en una posición de la mano, o {@code null} si está vacía. Complejidad: O(1).
     *
     * @throws IllegalArgumentException si la posición no está entre 0 y 4
     */
    public Carta getCartaEnMano(int posicion) {
        validarIndice(posicion, TAMANIO_MANO, "de la mano");
        return mano[posicion];
    }

    /** Complejidad: O(1) (recorre 5 posiciones). */
    public int cartasEnMano() {
        return contarOcupadas(mano);
    }

    /**
     * Monstruo en una zona del campo, o {@code null} si está vacía. Complejidad: O(1).
     *
     * @throws IllegalArgumentException si la zona no está entre 0 y 4
     */
    public MonstruoEnCampo getMonstruo(int zona) {
        validarIndice(zona, ZONAS_DE_MONSTRUO, "del campo");
        return campo[zona];
    }

    /** Complejidad: O(1) (recorre 5 zonas). */
    public int monstruosEnCampo() {
        return contarOcupadas(campo);
    }

    /** Complejidad: O(1) (recorre 5 zonas). */
    public boolean tieneMonstruos() {
        return monstruosEnCampo() > 0;
    }

    /**
     * Primera zona libre del campo, o -1 si las 5 están ocupadas. Complejidad: O(1).
     */
    public int primeraZonaLibre() {
        for (int i = 0; i < campo.length; i++) {
            if (campo[i] == null) {
                return i;
            }
        }
        return -1;
    }

    /** Indica si se quedó sin puntos de vida. Complejidad: O(1). */
    public boolean estaDerrotado() {
        return puntosDeVida == 0;
    }

    /**
     * Roba hasta llenar las 5 posiciones de la mano. Devuelve {@code false} si necesitaba
     * robar y el mazo estaba vacío (eso hace perder el duelo).
     */
    boolean robarHastaCompletar() {
        for (int i = 0; i < mano.length; i++) {
            if (mano[i] == null) {
                if (mazo.estaVacia()) {
                    return false;
                }
                mano[i] = mazo.desapilar();
            }
        }
        return true;
    }

    Carta sacarDeMano(int posicion) {
        Carta carta = mano[posicion];
        mano[posicion] = null;
        return carta;
    }

    /** Coloca el monstruo y devuelve el que estaba en esa zona ({@code null} si estaba libre). */
    MonstruoEnCampo colocar(int zona, MonstruoEnCampo monstruo) {
        MonstruoEnCampo anterior = campo[zona];
        campo[zona] = monstruo;
        return anterior;
    }

    void retirar(int zona) {
        campo[zona] = null;
    }

    void reiniciarTurno() {
        for (MonstruoEnCampo monstruo : campo) {
            if (monstruo != null) {
                monstruo.reiniciarTurno();
            }
        }
    }

    /** Resta puntos de vida sin bajar de 0. */
    void perderPuntos(int cantidad) {
        puntosDeVida = Math.max(0, puntosDeVida - cantidad);
    }

    private static int contarOcupadas(Object[] posiciones) {
        int ocupadas = 0;
        for (Object posicion : posiciones) {
            if (posicion != null) {
                ocupadas++;
            }
        }
        return ocupadas;
    }

    private static void validarIndice(int indice, int limite, String donde) {
        if (indice < 0 || indice >= limite) {
            throw new IllegalArgumentException("La posición " + donde + " debe estar entre 0 y "
                    + (limite - 1) + ": " + indice);
        }
    }
}
