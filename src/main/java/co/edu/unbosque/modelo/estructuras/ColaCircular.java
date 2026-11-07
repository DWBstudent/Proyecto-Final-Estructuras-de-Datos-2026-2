package co.edu.unbosque.modelo.estructuras;

/**
 * Cola genérica de capacidad fija implementada con un arreglo circular: cuando el
 * frente o el fin llegan al final del arreglo, siguen desde la posición 0. Así
 * encolar y desencolar nunca corren elementos.
 *
 * <p>En el duelo guarda los turnos: {@link #frente()} es el jugador que tiene el turno
 * y {@link #rotar()} se lo pasa al siguiente.
 *
 * <p>Responsable: Santiago.
 *
 * @param <T> tipo de los elementos
 */
public class ColaCircular<T> implements ColaTAD<T> {

    private final T[] elementos;
    private int frente;
    private int tamanio;

    /**
     * Crea una cola vacía con espacio para {@code capacidad} elementos. Complejidad: O(n),
     * con n = capacidad, porque se reserva el arreglo.
     *
     * @throws IllegalArgumentException si la capacidad es menor que 1
     */
    @SuppressWarnings("unchecked")
    public ColaCircular(int capacidad) {
        if (capacidad < 1) {
            throw new IllegalArgumentException("La capacidad debe ser al menos 1: " + capacidad);
        }
        elementos = (T[]) new Object[capacidad];
    }

    /**
     * Complejidad: O(1). El elemento queda en la posición siguiente al último,
     * dando la vuelta al arreglo si hace falta.
     *
     * @throws IllegalArgumentException si el elemento es {@code null}
     * @throws IllegalStateException si la cola está llena
     */
    @Override
    public void encolar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se puede encolar null");
        }
        if (estaLlena()) {
            throw new IllegalStateException("La cola circular está llena (capacidad " + capacidad() + ")");
        }
        elementos[siguientePosicion(frente, tamanio)] = elemento;
        tamanio++;
    }

    /** Complejidad: O(1). La casilla liberada se deja en {@code null} para no retener el objeto. */
    @Override
    public T desencolar() {
        verificarNoVacia();
        T dato = elementos[frente];
        elementos[frente] = null;
        frente = siguientePosicion(frente, 1);
        tamanio--;
        return dato;
    }

    /** Complejidad: O(1). */
    @Override
    public T frente() {
        verificarNoVacia();
        return elementos[frente];
    }

    /**
     * Pasa el elemento del frente al final y devuelve el nuevo frente. En el duelo
     * equivale a pasar el turno: devuelve el jugador al que le toca. Con un solo
     * elemento devuelve ese mismo. Complejidad: O(1).
     *
     * @throws EstructuraVaciaException si la cola está vacía
     */
    public T rotar() {
        verificarNoVacia();
        T dato = elementos[frente];
        elementos[frente] = null;
        elementos[siguientePosicion(frente, tamanio)] = dato;
        frente = siguientePosicion(frente, 1);
        return elementos[frente];
    }

    /** Complejidad: O(1). */
    @Override
    public boolean estaVacia() {
        return tamanio == 0;
    }

    /** Complejidad: O(1). */
    public boolean estaLlena() {
        return tamanio == elementos.length;
    }

    /** Complejidad: O(1). */
    @Override
    public int tamanio() {
        return tamanio;
    }

    /** Cantidad máxima de elementos que admite. Complejidad: O(1). */
    public int capacidad() {
        return elementos.length;
    }

    /**
     * Complejidad: O(n). A diferencia de la cola enlazada, hay que limpiar las
     * casillas ocupadas para que el recolector de basura libere los objetos.
     */
    @Override
    public void vaciar() {
        for (int i = 0; i < tamanio; i++) {
            elementos[siguientePosicion(frente, i)] = null;
        }
        frente = 0;
        tamanio = 0;
    }

    /** Posición que está {@code pasos} casillas después de {@code desde}, dando la vuelta. */
    private int siguientePosicion(int desde, int pasos) {
        return (desde + pasos) % elementos.length;
    }

    private void verificarNoVacia() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La cola circular está vacía");
        }
    }
}
