package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.Direccion;
import co.edu.unbosque.modelo.entidades.Jugador;
import co.edu.unbosque.modelo.estructuras.Grafo;

/**
 * El inventario: cuadrícula de 10 × 10 con las 100 cartas, modelada como un grafo
 * donde cada casilla se une con sus vecinas en las 8 direcciones. La carta de la
 * fila f y la columna c es la de id {@code f * 10 + c + 1}. El cursor empieza en (0, 0).
 *
 * <p>Cada casilla es el vértice {@code f * 10 + c} del {@link Grafo}, y cada arco lleva como
 * etiqueta el {@code ordinal()} de la {@link Direccion} en la que se mueve el cursor. Las
 * esquinas tienen 3 vecinas, los bordes 5 y el interior 8: en total 342 aristas, que son
 * 684 arcos porque cada arista va en los dos sentidos.
 *
 * <p>Responsable: Santiago (hito H1). Contrato del hito H0: Brayan lo usa en la pantalla
 * del inventario; si la carta está descubierta lo pregunta a {@code Jugador.estaDescubierta}.
 */
public class Inventario {

    public static final int FILAS = 10;
    public static final int COLUMNAS = 10;

    private final Catalogo catalogo;
    private final Grafo cuadricula;
    /** Vértice donde está el cursor: {@code fila * COLUMNAS + columna}. */
    private int cursor;

    /**
     * Construye el grafo de la cuadrícula: para cada casilla y cada dirección, si la casilla
     * vecina está dentro de la cuadrícula, agrega el arco. Complejidad: O(V · 8) = O(V),
     * con V = 100 casillas.
     *
     * @throws IllegalArgumentException si el catálogo es {@code null}
     */
    public Inventario(Catalogo catalogo) {
        if (catalogo == null) {
            throw new IllegalArgumentException("El inventario necesita el catálogo");
        }
        this.catalogo = catalogo;
        this.cuadricula = new Grafo(FILAS * COLUMNAS);
        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                for (Direccion direccion : Direccion.values()) {
                    int filaVecina = fila + direccion.getDeltaFila();
                    int columnaVecina = columna + direccion.getDeltaColumna();
                    if (estaDentro(filaVecina, columnaVecina)) {
                        cuadricula.agregarArco(vertice(fila, columna), vertice(filaVecina, columnaVecina),
                                direccion.ordinal());
                    }
                }
            }
        }
    }

    /**
     * Mueve el cursor a la casilla vecina en esa dirección. Complejidad: O(1), porque
     * busca entre los arcos de la casilla actual y nunca son más de 8.
     *
     * @return {@code false} si la casilla vecina queda fuera de la cuadrícula; el cursor no se mueve
     * @throws IllegalArgumentException si la dirección es {@code null}
     */
    public boolean mover(Direccion direccion) {
        if (direccion == null) {
            throw new IllegalArgumentException("La dirección no puede ser null");
        }
        int destino = cuadricula.vecino(cursor, direccion.ordinal());
        if (destino == -1) {
            return false;
        }
        cursor = destino;
        return true;
    }

    /** Fila del cursor, de 0 a 9 (la 0 es la de arriba). Complejidad: O(1). */
    public int getFila() {
        return cursor / COLUMNAS;
    }

    /** Columna del cursor, de 0 a 9. Complejidad: O(1). */
    public int getColumna() {
        return cursor % COLUMNAS;
    }

    /**
     * Carta que está bajo el cursor (aunque no esté descubierta).
     * Complejidad: la de {@link Catalogo#buscarPorId(int)}, O(1) promedio.
     */
    public Carta getCartaEnCursor() {
        return catalogo.buscarPorId(cursor + 1);
    }

    /**
     * Lleva el cursor a la carta sin descubrir más cercana, contando los pasos en las
     * 8 direcciones. Hace un recorrido en anchura (BFS) desde el cursor: como visita por
     * distancia, la primera casilla sin descubrir que encuentra es la más cercana. Si la
     * carta bajo el cursor no está descubierta, el cursor se queda donde está.
     * Complejidad: O(V + E), con V = 100 casillas y E = 684 arcos.
     *
     * @return {@code false} si el jugador ya descubrió todas las cartas; el cursor no se mueve
     * @throws IllegalArgumentException si el jugador es {@code null}
     */
    public boolean irALaMasCercanaSinDescubrir(Jugador jugador) {
        if (jugador == null) {
            throw new IllegalArgumentException("Se necesita el jugador para saber qué cartas descubrió");
        }
        int[] porDistancia = cuadricula.recorridoEnAnchura(cursor);
        for (int casilla : porDistancia) {
            if (!jugador.estaDescubierta(casilla + 1)) {
                cursor = casilla;
                return true;
            }
        }
        return false;
    }

    /**
     * Cantidad de casillas vecinas de esa casilla: 3 en las esquinas, 5 en los bordes
     * y 8 en el interior. Complejidad: O(1).
     *
     * @throws IllegalArgumentException si la casilla está fuera de la cuadrícula
     */
    public int cantidadVecinas(int fila, int columna) {
        if (!estaDentro(fila, columna)) {
            throw new IllegalArgumentException("La casilla (" + fila + ", " + columna + ") está fuera de la cuadrícula");
        }
        return cuadricula.grado(vertice(fila, columna));
    }

    /** Aristas de la cuadrícula: debe dar 342. Complejidad: O(1). */
    public int cantidadAristas() {
        return cuadricula.cantidadArcos() / 2;
    }

    private static boolean estaDentro(int fila, int columna) {
        return fila >= 0 && fila < FILAS && columna >= 0 && columna < COLUMNAS;
    }

    private static int vertice(int fila, int columna) {
        return fila * COLUMNAS + columna;
    }
}
