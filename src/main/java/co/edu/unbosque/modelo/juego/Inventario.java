package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.Direccion;

/**
 * El inventario: cuadrícula de 10 × 10 con las 100 cartas, modelada como un grafo
 * donde cada casilla se une con sus vecinas en las 8 direcciones. La carta de la
 * fila f y la columna c es la de id {@code f * 10 + c + 1}. El cursor empieza en (0, 0).
 *
 * <p>Responsable: Santiago (hito H1). Contrato del hito H0: Brayan lo usa en la pantalla
 * del inventario; si la carta está descubierta lo pregunta a {@code Jugador.estaDescubierta}.
 */
public class Inventario {

    public static final int FILAS = 10;
    public static final int COLUMNAS = 10;

    /**
     * @throws IllegalArgumentException si el catálogo es {@code null}
     */
    public Inventario(Catalogo catalogo) {
        if (catalogo == null) {
            throw new IllegalArgumentException("El inventario necesita el catálogo");
        }
        // Santiago: construir aquí el grafo (100 vértices, 342 aristas).
    }

    /**
     * Mueve el cursor a la casilla vecina en esa dirección. Complejidad esperada: O(1).
     *
     * @return {@code false} si la casilla vecina queda fuera de la cuadrícula; el cursor no se mueve
     */
    public boolean mover(Direccion direccion) {
        throw new UnsupportedOperationException("Pendiente: Santiago (H1)");
    }

    public int getFila() {
        throw new UnsupportedOperationException("Pendiente: Santiago (H1)");
    }

    public int getColumna() {
        throw new UnsupportedOperationException("Pendiente: Santiago (H1)");
    }

    /** Carta que está bajo el cursor (aunque no esté descubierta). */
    public Carta getCartaEnCursor() {
        throw new UnsupportedOperationException("Pendiente: Santiago (H1)");
    }
}
