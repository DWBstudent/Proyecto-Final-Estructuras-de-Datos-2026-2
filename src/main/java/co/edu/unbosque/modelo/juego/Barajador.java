package co.edu.unbosque.modelo.juego;

import java.util.Random;

import co.edu.unbosque.modelo.estructuras.Pila;

/**
 * Baraja cartas con el algoritmo de Fisher-Yates (barajado de Knuth) y las apila para
 * el robo del duelo. El duelo lo usa así:
 * <pre>{@code
 * Carta[] cartas = deck.aArreglo();          // copia: el Deck no cambia
 * Barajador.barajar(cartas, random);
 * Pila<Carta> mazo = Barajador.aPila(cartas); // robar = mazo.desapilar()
 * }</pre>
 *
 * <p>El {@link Random} se recibe desde afuera: en las pruebas se usa {@code new Random(42)}
 * y el resultado siempre es el mismo.
 *
 * <p>Responsable: Santiago.
 */
public final class Barajador {

    private Barajador() {
        // Solo tiene métodos estáticos.
    }

    /**
     * Desordena el arreglo en el mismo lugar. Recorre las posiciones de la última a la
     * segunda y cambia cada una con una posición al azar entre 0 y ella misma. Así las
     * n! permutaciones tienen la misma probabilidad. Complejidad: O(n).
     *
     * @throws IllegalArgumentException si el arreglo o el {@code Random} son {@code null}
     */
    public static <T> void barajar(T[] arreglo, Random random) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo a barajar no puede ser null");
        }
        if (random == null) {
            throw new IllegalArgumentException("Se necesita un Random para barajar");
        }
        for (int i = arreglo.length - 1; i > 0; i--) {
            // i + 1 incluye a la propia posición i: si se usa nextInt(arreglo.length),
            // algunas permutaciones salen más que otras.
            int j = random.nextInt(i + 1);
            T temporal = arreglo[i];
            arreglo[i] = arreglo[j];
            arreglo[j] = temporal;
        }
    }

    /**
     * Apila los elementos de modo que la posición 0 del arreglo quede en la cima: el
     * primer elemento del arreglo es el primero que se roba. El arreglo no se modifica.
     * Complejidad: O(n).
     *
     * @throws IllegalArgumentException si el arreglo es {@code null} o contiene algún {@code null};
     *         en ese caso no se crea ninguna pila
     */
    public static <T> Pila<T> aPila(T[] arreglo) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo a apilar no puede ser null");
        }
        for (int i = 0; i < arreglo.length; i++) {
            if (arreglo[i] == null) {
                throw new IllegalArgumentException("El arreglo tiene un null en la posición " + i);
            }
        }
        Pila<T> pila = new Pila<>();
        for (int i = arreglo.length - 1; i >= 0; i--) {
            pila.apilar(arreglo[i]);
        }
        return pila;
    }
}
