package co.edu.unbosque.modelo.estructuras;

import co.edu.unbosque.modelo.ordenamiento.Comparador;

/**
 * Contrato de una lista indexada desde 0. La implementa {@code ListaDoble} (Brayan),
 * que se usa en el Chest y en el Deck.
 *
 * <p>Ningún método acepta {@code null} como elemento.
 *
 * @param <T> tipo de los elementos
 */
public interface Lista<T> {

    /**
     * Agrega un elemento al inicio. Complejidad esperada: O(1).
     *
     * @throws IllegalArgumentException si el elemento es {@code null}
     */
    void agregarAlInicio(T elemento);

    /**
     * Agrega un elemento al final. Complejidad esperada: O(1).
     *
     * @throws IllegalArgumentException si el elemento es {@code null}
     */
    void agregarAlFinal(T elemento);

    /**
     * Devuelve el elemento en la posición indicada. Complejidad esperada: O(n).
     *
     * @throws IndexOutOfBoundsException si el índice no está entre 0 y tamanio() - 1
     */
    T obtener(int indice);

    /**
     * Elimina y devuelve el elemento en la posición indicada. Complejidad esperada: O(n).
     *
     * @throws IndexOutOfBoundsException si el índice no está entre 0 y tamanio() - 1
     */
    T eliminar(int indice);

    /**
     * Posición de la primera aparición del elemento (según {@code equals}), o -1 si no está.
     * Complejidad esperada: O(n).
     */
    int indiceDe(T elemento);

    /** Complejidad esperada: O(n). */
    boolean contiene(T elemento);

    /** Complejidad esperada: O(1). */
    int tamanio();

    /** Complejidad esperada: O(1). */
    boolean estaVacia();

    /** Deja la lista sin elementos. Complejidad esperada: O(1). */
    void vaciar();

    /**
     * Ordena la lista de forma estable según el comparador.
     * Complejidad esperada: O(n log n) con Merge Sort.
     *
     * @throws IllegalArgumentException si el comparador es {@code null}
     */
    void ordenar(Comparador<T> comparador);
}
