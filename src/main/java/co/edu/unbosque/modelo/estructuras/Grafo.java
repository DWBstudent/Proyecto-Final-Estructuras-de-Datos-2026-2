package co.edu.unbosque.modelo.estructuras;

/**
 * Grafo dirigido con los vértices numerados de {@code 0} a {@code n - 1}, guardado como
 * lista de adyacencia: cada vértice tiene una lista enlazada de arcos propia. Cada arco
 * lleva una etiqueta entera que lo identifica entre los arcos que salen del mismo vértice,
 * así que se puede preguntar "¿a qué vértice llego por la etiqueta e?".
 *
 * <p>Una arista no dirigida se representa con dos arcos, uno en cada sentido.
 *
 * <p>Lo usa el {@code Inventario}: cada casilla de la cuadrícula es un vértice y la
 * etiqueta de cada arco es la dirección en la que se mueve el cursor.
 *
 * <p>Responsable: Santiago.
 */
public class Grafo {

    private static final class Arco {
        private final int destino;
        private final int etiqueta;
        private Arco siguiente;

        private Arco(int destino, int etiqueta) {
            this.destino = destino;
            this.etiqueta = etiqueta;
        }
    }

    /** {@code adyacencia[v]} es el primer arco que sale de v, o {@code null} si no tiene. */
    private final Arco[] adyacencia;
    private final int[] grados;
    private int cantidadArcos;

    /**
     * Crea un grafo con esa cantidad de vértices y sin arcos. Complejidad: O(n).
     *
     * @throws IllegalArgumentException si la cantidad de vértices es menor que 1
     */
    public Grafo(int cantidadVertices) {
        if (cantidadVertices < 1) {
            throw new IllegalArgumentException("El grafo necesita al menos 1 vértice: " + cantidadVertices);
        }
        adyacencia = new Arco[cantidadVertices];
        grados = new int[cantidadVertices];
    }

    /**
     * Agrega un arco de {@code origen} a {@code destino}. Se agrega al final de la lista
     * del origen, así el recorrido en anchura visita los vecinos en el orden en que se
     * agregaron. Complejidad: O(grado del origen), porque revisa que la etiqueta no esté repetida.
     *
     * @throws IllegalArgumentException si algún vértice está fuera de rango, si origen y
     *         destino son el mismo vértice o si el origen ya tiene un arco con esa etiqueta
     */
    public void agregarArco(int origen, int destino, int etiqueta) {
        validarVertice(origen);
        validarVertice(destino);
        if (origen == destino) {
            throw new IllegalArgumentException("No se permiten arcos de un vértice a sí mismo: " + origen);
        }
        Arco nuevo = new Arco(destino, etiqueta);
        if (adyacencia[origen] == null) {
            adyacencia[origen] = nuevo;
        } else {
            Arco actual = adyacencia[origen];
            while (true) {
                if (actual.etiqueta == etiqueta) {
                    throw new IllegalArgumentException("El vértice " + origen
                            + " ya tiene un arco con la etiqueta " + etiqueta);
                }
                if (actual.siguiente == null) {
                    break;
                }
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
        grados[origen]++;
        cantidadArcos++;
    }

    /**
     * Vértice al que se llega desde {@code vertice} por el arco con esa etiqueta,
     * o -1 si no sale ningún arco con esa etiqueta. Complejidad: O(grado del vértice).
     *
     * @throws IllegalArgumentException si el vértice está fuera de rango
     */
    public int vecino(int vertice, int etiqueta) {
        validarVertice(vertice);
        for (Arco arco = adyacencia[vertice]; arco != null; arco = arco.siguiente) {
            if (arco.etiqueta == etiqueta) {
                return arco.destino;
            }
        }
        return -1;
    }

    /**
     * Cantidad de arcos que salen del vértice. Complejidad: O(1).
     *
     * @throws IllegalArgumentException si el vértice está fuera de rango
     */
    public int grado(int vertice) {
        validarVertice(vertice);
        return grados[vertice];
    }

    /** Complejidad: O(1). */
    public int cantidadVertices() {
        return adyacencia.length;
    }

    /** Total de arcos del grafo. Complejidad: O(1). */
    public int cantidadArcos() {
        return cantidadArcos;
    }

    /**
     * Recorrido en anchura (BFS) desde {@code origen}: primero el origen, luego sus vecinos,
     * luego los vecinos de esos, y así. Usa una {@link Cola} y un arreglo de visitados para
     * no pasar dos veces por el mismo vértice. Como se visita por niveles, los vértices
     * salen ordenados de menor a mayor distancia al origen.
     * Complejidad: O(V + E).
     *
     * @return los vértices alcanzables desde el origen, en el orden en que se visitaron;
     *         el primero siempre es el origen
     * @throws IllegalArgumentException si el origen está fuera de rango
     */
    public int[] recorridoEnAnchura(int origen) {
        validarVertice(origen);
        boolean[] visitado = new boolean[cantidadVertices()];
        int[] orden = new int[cantidadVertices()];
        int visitados = 0;

        Cola<Integer> pendientes = new Cola<>();
        visitado[origen] = true;
        pendientes.encolar(origen);
        while (!pendientes.estaVacia()) {
            int actual = pendientes.desencolar();
            orden[visitados++] = actual;
            for (Arco arco = adyacencia[actual]; arco != null; arco = arco.siguiente) {
                if (!visitado[arco.destino]) {
                    visitado[arco.destino] = true;
                    pendientes.encolar(arco.destino);
                }
            }
        }

        int[] resultado = new int[visitados];
        System.arraycopy(orden, 0, resultado, 0, visitados);
        return resultado;
    }

    private void validarVertice(int vertice) {
        if (vertice < 0 || vertice >= cantidadVertices()) {
            throw new IllegalArgumentException("El vértice debe estar entre 0 y "
                    + (cantidadVertices() - 1) + ": " + vertice);
        }
    }
}
