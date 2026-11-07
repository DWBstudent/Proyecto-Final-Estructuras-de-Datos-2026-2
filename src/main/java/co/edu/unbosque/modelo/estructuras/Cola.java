package co.edu.unbosque.modelo.estructuras;

/**
 * Cola genérica implementada con nodos enlazados y referencias al frente y al fin,
 * así que encolar y desencolar nunca recorren la estructura.
 *
 * <p>Responsable: Daniel.
 *
 * @param <T> tipo de los elementos
 */
public class Cola<T> implements ColaTAD<T> {

    private static final class Nodo<T> {
        private final T dato;
        private Nodo<T> siguiente;

        private Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamanio;

    /** Complejidad: O(1). El nuevo nodo se enlaza después del fin. */
    @Override
    public void encolar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se puede encolar null");
        }
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.siguiente = nuevo;
        }
        fin = nuevo;
        tamanio++;
    }

    /** Complejidad: O(1). Si la cola queda vacía, el fin también se reinicia. */
    @Override
    public T desencolar() {
        verificarNoVacia();
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        tamanio--;
        return dato;
    }

    /** Complejidad: O(1). */
    @Override
    public T frente() {
        verificarNoVacia();
        return frente.dato;
    }

    /** Complejidad: O(1). */
    @Override
    public boolean estaVacia() {
        return frente == null;
    }

    /** Complejidad: O(1). */
    @Override
    public int tamanio() {
        return tamanio;
    }

    /** Complejidad: O(1). */
    @Override
    public void vaciar() {
        frente = null;
        fin = null;
        tamanio = 0;
    }

    private void verificarNoVacia() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La cola está vacía");
        }
    }
}
