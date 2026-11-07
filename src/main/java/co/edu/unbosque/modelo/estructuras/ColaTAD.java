package co.edu.unbosque.modelo.estructuras;

/**
 * Contrato de una cola (FIFO): el primer elemento que entra es el primero que sale.
 * Usos en el juego: rivales del modo historia, registro del duelo, turnos (cola circular).
 *
 * <p>Ningún método acepta {@code null} como elemento.
 *
 * @param <T> tipo de los elementos
 */
public interface ColaTAD<T> {

    /**
     * Agrega un elemento al final. Complejidad esperada: O(1).
     *
     * @throws IllegalArgumentException si el elemento es {@code null}
     */
    void encolar(T elemento);

    /**
     * Saca y devuelve el elemento del frente. Complejidad esperada: O(1).
     *
     * @throws EstructuraVaciaException si la cola está vacía
     */
    T desencolar();

    /**
     * Devuelve el elemento del frente sin sacarlo. Complejidad esperada: O(1).
     *
     * @throws EstructuraVaciaException si la cola está vacía
     */
    T frente();

    /** Complejidad esperada: O(1). */
    boolean estaVacia();

    /** Complejidad esperada: O(1). */
    int tamanio();

    /** Deja la cola sin elementos. Complejidad esperada: O(1). */
    void vaciar();
}
