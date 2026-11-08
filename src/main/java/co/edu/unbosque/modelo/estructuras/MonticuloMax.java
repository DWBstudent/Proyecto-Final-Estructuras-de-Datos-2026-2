package co.edu.unbosque.modelo.estructuras;

import co.edu.unbosque.modelo.ordenamiento.Comparador;

/**
 * Montículo binario de máximos (max-heap) genérico, guardado en un arreglo. El elemento
 * de la posición {@code i} tiene sus hijos en {@code 2i + 1} y {@code 2i + 2} y su padre
 * en {@code (i - 1) / 2}. Cada padre es mayor o igual que sus hijos según el comparador,
 * así que el máximo siempre está en la posición 0.
 *
 * <p>Lo usa la IA de los rivales: genera sus jugadas posibles, las mete al montículo
 * ordenadas por puntaje y saca la mejor. Ejemplo:
 * <pre>{@code
 * MonticuloMax<Jugada> jugadas = new MonticuloMax<>(posibles, (a, b) -> Integer.compare(a.getPuntaje(), b.getPuntaje()));
 * Jugada mejor = jugadas.extraerMax();
 * }</pre>
 *
 * <p>El arreglo crece al doble cuando se llena. Con elementos iguales según el comparador,
 * el orden en que salen no está definido.
 *
 * <p>Responsable: Santiago.
 *
 * @param <T> tipo de los elementos
 */
public class MonticuloMax<T> {

    private static final int CAPACIDAD_INICIAL = 8;

    private final Comparador<T> comparador;
    private T[] elementos;
    private int tamanio;

    /**
     * Crea un montículo vacío. Complejidad: O(1).
     *
     * @param comparador decide cuál elemento es mayor
     * @throws IllegalArgumentException si el comparador es {@code null}
     */
    public MonticuloMax(Comparador<T> comparador) {
        this.comparador = validarComparador(comparador);
        this.elementos = nuevoArreglo(CAPACIDAD_INICIAL);
    }

    /**
     * Crea un montículo con todos los elementos del arreglo, usando el algoritmo de
     * construcción de abajo hacia arriba (heapify). Complejidad: O(n), más rápido que
     * insertar uno por uno, que sería O(n log n). El arreglo recibido no se modifica.
     *
     * @throws IllegalArgumentException si el comparador o el arreglo son {@code null},
     *         o si el arreglo contiene algún {@code null}
     */
    public MonticuloMax(T[] iniciales, Comparador<T> comparador) {
        this.comparador = validarComparador(comparador);
        if (iniciales == null) {
            throw new IllegalArgumentException("El arreglo de elementos no puede ser null");
        }
        this.elementos = nuevoArreglo(iniciales.length > 0 ? iniciales.length : CAPACIDAD_INICIAL);
        for (int i = 0; i < iniciales.length; i++) {
            if (iniciales[i] == null) {
                throw new IllegalArgumentException("El arreglo tiene un null en la posición " + i);
            }
            elementos[i] = iniciales[i];
        }
        this.tamanio = iniciales.length;
        // Las hojas ya son montículos de un elemento; se hunden solo los nodos internos.
        for (int i = tamanio / 2 - 1; i >= 0; i--) {
            hundir(i);
        }
    }

    /**
     * Agrega un elemento y lo hace subir hasta su lugar. Complejidad: O(log n);
     * O(n) solo cuando el arreglo se llena y hay que duplicarlo (O(log n) amortizado).
     *
     * @throws IllegalArgumentException si el elemento es {@code null}
     */
    public void insertar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se puede insertar null");
        }
        if (tamanio == elementos.length) {
            crecer();
        }
        elementos[tamanio] = elemento;
        flotar(tamanio);
        tamanio++;
    }

    /**
     * Saca y devuelve el mayor elemento. El último elemento pasa a la raíz y se hunde
     * hasta su lugar. Complejidad: O(log n).
     *
     * @throws EstructuraVaciaException si el montículo está vacío
     */
    public T extraerMax() {
        verificarNoVacio();
        T maximo = elementos[0];
        tamanio--;
        elementos[0] = elementos[tamanio];
        elementos[tamanio] = null;
        if (tamanio > 0) {
            hundir(0);
        }
        return maximo;
    }

    /**
     * Devuelve el mayor elemento sin sacarlo. Complejidad: O(1).
     *
     * @throws EstructuraVaciaException si el montículo está vacío
     */
    public T verMax() {
        verificarNoVacio();
        return elementos[0];
    }

    /** Complejidad: O(1). */
    public int tamanio() {
        return tamanio;
    }

    /** Complejidad: O(1). */
    public boolean estaVacio() {
        return tamanio == 0;
    }

    /**
     * Deja el montículo sin elementos, por ejemplo para reutilizarlo en el siguiente turno.
     * Complejidad: O(n), porque limpia las casillas para que el recolector de basura
     * libere los objetos.
     */
    public void vaciar() {
        for (int i = 0; i < tamanio; i++) {
            elementos[i] = null;
        }
        tamanio = 0;
    }

    /** Sube el elemento de la posición i mientras sea mayor que su padre. O(log n). */
    private void flotar(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (comparador.comparar(elementos[i], elementos[padre]) <= 0) {
                return;
            }
            intercambiar(i, padre);
            i = padre;
        }
    }

    /** Baja el elemento de la posición i mientras algún hijo sea mayor. O(log n). */
    private void hundir(int i) {
        while (true) {
            int izquierdo = 2 * i + 1;
            int derecho = izquierdo + 1;
            int mayor = i;
            if (izquierdo < tamanio && comparador.comparar(elementos[izquierdo], elementos[mayor]) > 0) {
                mayor = izquierdo;
            }
            if (derecho < tamanio && comparador.comparar(elementos[derecho], elementos[mayor]) > 0) {
                mayor = derecho;
            }
            if (mayor == i) {
                return;
            }
            intercambiar(i, mayor);
            i = mayor;
        }
    }

    private void intercambiar(int a, int b) {
        T temporal = elementos[a];
        elementos[a] = elementos[b];
        elementos[b] = temporal;
    }

    /** Duplica la capacidad del arreglo. O(n). */
    private void crecer() {
        T[] nuevo = nuevoArreglo(elementos.length * 2);
        System.arraycopy(elementos, 0, nuevo, 0, tamanio);
        elementos = nuevo;
    }

    @SuppressWarnings("unchecked")
    private T[] nuevoArreglo(int capacidad) {
        return (T[]) new Object[capacidad];
    }

    private static <T> Comparador<T> validarComparador(Comparador<T> comparador) {
        if (comparador == null) {
            throw new IllegalArgumentException("El montículo necesita un comparador");
        }
        return comparador;
    }

    private void verificarNoVacio() {
        if (estaVacio()) {
            throw new EstructuraVaciaException("El montículo está vacío");
        }
    }
}
