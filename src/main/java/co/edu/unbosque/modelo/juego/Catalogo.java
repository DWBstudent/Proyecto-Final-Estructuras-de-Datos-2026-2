package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;

/**
 * Las 100 cartas del juego, cargadas desde {@code cartas.csv}. Permite buscar una carta
 * por id y traducir una contraseña al id de su carta (ambas con {@code TablaHash}).
 *
 * <p>Responsable: Daniel (hito H2). Contrato del hito H0.
 */
public class Catalogo {

    /**
     * Carta con ese id. Complejidad esperada: O(1) promedio.
     *
     * @throws IllegalArgumentException si el id no existe en el catálogo
     */
    public Carta buscarPorId(int id) {
        throw new UnsupportedOperationException("Pendiente: Daniel (H2)");
    }

    /**
     * Id de la carta que corresponde a la contraseña, o -1 si no corresponde a ninguna.
     * Complejidad esperada: O(1) promedio.
     */
    public int idPorContrasena(String contrasena) {
        throw new UnsupportedOperationException("Pendiente: Daniel (H2)");
    }

    /** Cantidad de cartas cargadas. */
    public int tamanio() {
        throw new UnsupportedOperationException("Pendiente: Daniel (H2)");
    }
}
