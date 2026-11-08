package co.edu.unbosque.modelo.estructuras;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GrafoTest {

    private static final int IDA = 1;
    private static final int VUELTA = 2;

    /** Arista no dirigida entre a y b: un arco en cada sentido. */
    private static void unir(Grafo grafo, int a, int b, int etiquetaAB, int etiquetaBA) {
        grafo.agregarArco(a, b, etiquetaAB);
        grafo.agregarArco(b, a, etiquetaBA);
    }

    // Caso normal

    @Test
    void vecinoDevuelveElDestinoDeCadaEtiqueta() {
        Grafo grafo = new Grafo(3);
        grafo.agregarArco(0, 1, 10);
        grafo.agregarArco(0, 2, 20);

        assertEquals(1, grafo.vecino(0, 10));
        assertEquals(2, grafo.vecino(0, 20));
        assertEquals(2, grafo.grado(0));
        assertEquals(0, grafo.grado(1));
        assertEquals(2, grafo.cantidadArcos());
        assertEquals(3, grafo.cantidadVertices());
    }

    @Test
    void recorridoEnAnchuraVisitaPorNiveles() {
        //      0
        //     / \
        //    1   2
        //     \ /
        //      3
        //      |
        //      4
        Grafo grafo = new Grafo(5);
        unir(grafo, 0, 1, IDA, VUELTA);
        unir(grafo, 0, 2, IDA + 10, VUELTA);
        unir(grafo, 1, 3, IDA, VUELTA + 10);
        unir(grafo, 2, 3, IDA, VUELTA + 20);
        unir(grafo, 3, 4, IDA, VUELTA);

        assertArrayEquals(new int[] {0, 1, 2, 3, 4}, grafo.recorridoEnAnchura(0));
        assertArrayEquals(new int[] {4, 3, 1, 2, 0}, grafo.recorridoEnAnchura(4));
    }

    @Test
    void elRecorridoRespetaElOrdenEnQueSeAgregaronLosVecinos() {
        Grafo grafo = new Grafo(4);
        grafo.agregarArco(0, 3, 1);
        grafo.agregarArco(0, 1, 2);
        grafo.agregarArco(0, 2, 3);

        assertArrayEquals(new int[] {0, 3, 1, 2}, grafo.recorridoEnAnchura(0));
    }

    // Caso límite

    @Test
    void vecinoSinEseArcoDevuelveMenosUno() {
        Grafo grafo = new Grafo(2);
        grafo.agregarArco(0, 1, IDA);

        assertEquals(-1, grafo.vecino(0, VUELTA));
        assertEquals(-1, grafo.vecino(1, IDA));
    }

    @Test
    void verticeAisladoSoloSeVisitaASiMismo() {
        Grafo grafo = new Grafo(3);
        grafo.agregarArco(0, 1, IDA);

        assertArrayEquals(new int[] {2}, grafo.recorridoEnAnchura(2));
    }

    @Test
    void grafoDeUnSoloVertice() {
        Grafo grafo = new Grafo(1);

        assertArrayEquals(new int[] {0}, grafo.recorridoEnAnchura(0));
        assertEquals(0, grafo.cantidadArcos());
    }

    @Test
    void conCiclosNoVisitaDosVecesElMismoVertice() {
        // Triángulo 0-1-2-0: sin el arreglo de visitados el recorrido nunca terminaría.
        Grafo grafo = new Grafo(3);
        unir(grafo, 0, 1, 1, 2);
        unir(grafo, 1, 2, 3, 4);
        unir(grafo, 2, 0, 5, 6);

        assertArrayEquals(new int[] {0, 1, 2}, grafo.recorridoEnAnchura(0));
    }

    @Test
    void losArcosSonDirigidos() {
        Grafo grafo = new Grafo(2);
        grafo.agregarArco(0, 1, IDA);

        assertArrayEquals(new int[] {0, 1}, grafo.recorridoEnAnchura(0));
        assertArrayEquals(new int[] {1}, grafo.recorridoEnAnchura(1));
    }

    @Test
    void laMismaEtiquetaPuedeUsarseEnVerticesDistintos() {
        Grafo grafo = new Grafo(3);
        grafo.agregarArco(0, 1, IDA);
        grafo.agregarArco(1, 2, IDA);

        assertEquals(1, grafo.vecino(0, IDA));
        assertEquals(2, grafo.vecino(1, IDA));
    }

    // Caso de error

    @Test
    void menosDeUnVerticeLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Grafo(0));
        assertThrows(IllegalArgumentException.class, () -> new Grafo(-5));
    }

    @Test
    void verticeFueraDeRangoLanzaExcepcion() {
        Grafo grafo = new Grafo(3);

        assertThrows(IllegalArgumentException.class, () -> grafo.agregarArco(0, 3, IDA));
        assertThrows(IllegalArgumentException.class, () -> grafo.agregarArco(-1, 0, IDA));
        assertThrows(IllegalArgumentException.class, () -> grafo.vecino(3, IDA));
        assertThrows(IllegalArgumentException.class, () -> grafo.grado(-1));
        assertThrows(IllegalArgumentException.class, () -> grafo.recorridoEnAnchura(3));
    }

    @Test
    void etiquetaRepetidaEnElMismoOrigenLanzaExcepcionYNoAgregaNada() {
        Grafo grafo = new Grafo(3);
        grafo.agregarArco(0, 1, IDA);

        assertThrows(IllegalArgumentException.class, () -> grafo.agregarArco(0, 2, IDA));
        assertEquals(1, grafo.cantidadArcos());
        assertEquals(1, grafo.grado(0));
        assertEquals(1, grafo.vecino(0, IDA));
    }

    @Test
    void arcoDeUnVerticeASiMismoLanzaExcepcion() {
        Grafo grafo = new Grafo(2);
        assertThrows(IllegalArgumentException.class, () -> grafo.agregarArco(1, 1, IDA));
    }
}
