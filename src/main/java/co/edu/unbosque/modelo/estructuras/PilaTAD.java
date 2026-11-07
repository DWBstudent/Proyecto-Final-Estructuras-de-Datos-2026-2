package co.edu.unbosque.modelo.estructuras;

/**
 * Contrato de una pila (LIFO): el último elemento que entra es el primero que sale.
 * Usos en el juego: navegación entre pantallas, deshacer y rehacer, mazo del duelo.
 *
 * <p>Ningún método acepta {@code null} como elemento.
 *
 * @param <T> tipo de los elementos
 */
public interface PilaTAD<T> {

    /**
     * Pone un elemento en la cima. Complejidad esperada: O(1).
     *
     * @throws IllegalArgumentException si el elemento es {@code null}
     */
    void apilar(T elemento);

    /**
     * Saca y devuelve el elemento de la cima. Complejidad esperada: O(1).
     *
     * @throws EstructuraVaciaException si la pila está vacía
     */
    T desapilar();

    /**
     * Devuelve el elemento de la cima sin sacarlo. Complejidad esperada: O(1).
     *
     * @throws EstructuraVaciaException si la pila está vacía
     */
    T cima();

    /** Complejidad esperada: O(1). */
    boolean estaVacia();

    /** Complejidad esperada: O(1). */
    int tamanio();

    /** Deja la pila sin elementos. Complejidad esperada: O(1). */
    void vaciar();
}
