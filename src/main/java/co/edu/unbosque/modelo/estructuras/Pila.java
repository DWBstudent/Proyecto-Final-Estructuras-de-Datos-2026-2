package co.edu.unbosque.modelo.estructuras;

/**
 * Pila genérica implementada con nodos enlazados. La cima es el primer nodo,
 * así que apilar y desapilar nunca recorren la estructura.
 *
 * <p>Responsable: Daniel.
 *
 * @param <T> tipo de los elementos
 */
public class Pila<T> implements PilaTAD<T> {

    private static final class Nodo<T> {
        private final T dato;
        private final Nodo<T> siguiente;

        private Nodo(T dato, Nodo<T> siguiente) {
            this.dato = dato;
            this.siguiente = siguiente;
        }
    }

    private Nodo<T> cima;
    private int tamanio;

    /** Complejidad: O(1). Crea un nodo nuevo que apunta a la cima anterior. */
    @Override
    public void apilar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se puede apilar null");
        }
        cima = new Nodo<>(elemento, cima);
        tamanio++;
    }

    /** Complejidad: O(1). */
    @Override
    public T desapilar() {
        verificarNoVacia();
        T dato = cima.dato;
        cima = cima.siguiente;
        tamanio--;
        return dato;
    }

    /** Complejidad: O(1). */
    @Override
    public T cima() {
        verificarNoVacia();
        return cima.dato;
    }

    /** Complejidad: O(1). */
    @Override
    public boolean estaVacia() {
        return cima == null;
    }

    /** Complejidad: O(1). */
    @Override
    public int tamanio() {
        return tamanio;
    }

    /** Complejidad: O(1). Los nodos sueltos los recoge el recolector de basura. */
    @Override
    public void vaciar() {
        cima = null;
        tamanio = 0;
    }

    private void verificarNoVacia() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La pila está vacía");
        }
    }
}
