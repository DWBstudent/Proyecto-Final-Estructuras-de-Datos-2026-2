package co.edu.unbosque.modelo.juego;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.estructuras.Pila;

class BarajadorTest {

    /** Arreglo con los números 0, 1, ..., n - 1 en orden. */
    private static Integer[] numerosEnOrden(int n) {
        Integer[] numeros = new Integer[n];
        for (int i = 0; i < n; i++) {
            numeros[i] = i;
        }
        return numeros;
    }

    // Caso normal

    @Test
    void conLaMismaSemillaSiempreDaElMismoOrden() {
        Integer[] primero = numerosEnOrden(40);
        Integer[] segundo = numerosEnOrden(40);

        Barajador.barajar(primero, new Random(42));
        Barajador.barajar(segundo, new Random(42));

        assertArrayEquals(primero, segundo);
    }

    @Test
    void cambiaElOrdenPeroConservaLasMismasCartas() {
        Integer[] numeros = numerosEnOrden(40);
        Barajador.barajar(numeros, new Random(42));

        int fueraDeSuLugar = 0;
        for (int i = 0; i < numeros.length; i++) {
            if (numeros[i] != i) {
                fueraDeSuLugar++;
            }
        }
        assertTrue(fueraDeSuLugar > 0, "El orden debería cambiar");
        boolean[] visto = new boolean[40];
        for (Integer numero : numeros) {
            assertFalse(visto[numero], "El " + numero + " aparece dos veces");
            visto[numero] = true;
        }
        for (int i = 0; i < 40; i++) {
            assertTrue(visto[i], "Falta el " + i);
        }
    }

    @Test
    void todasLasPermutacionesSalenConLaMismaFrecuencia() {
        // Con 3 elementos hay 6 permutaciones; en 60 000 barajados cada una debe salir
        // cerca de 10 000 veces. La versión con error (nextInt(n) en cada paso) da
        // unas 8 900 y 11 100, y esta prueba la detecta.
        Random random = new Random(42);
        int[] veces = new int[27];
        for (int k = 0; k < 60_000; k++) {
            Integer[] numeros = numerosEnOrden(3);
            Barajador.barajar(numeros, random);
            veces[numeros[0] * 9 + numeros[1] * 3 + numeros[2]]++;
        }
        int permutacionesVistas = 0;
        for (int cantidad : veces) {
            if (cantidad > 0) {
                permutacionesVistas++;
                assertTrue(cantidad > 9_500 && cantidad < 10_500, "Frecuencia desbalanceada: " + cantidad);
            }
        }
        assertEquals(6, permutacionesVistas);
    }

    @Test
    void aPilaDejaLaPrimeraPosicionEnLaCima() {
        Pila<String> pila = Barajador.aPila(new String[] {"a", "b", "c"});

        assertEquals(3, pila.tamanio());
        assertEquals("a", pila.desapilar());
        assertEquals("b", pila.desapilar());
        assertEquals("c", pila.desapilar());
    }

    @Test
    void aPilaNoModificaElArreglo() {
        String[] cartas = {"a", "b", "c"};
        Barajador.aPila(cartas);

        assertArrayEquals(new String[] {"a", "b", "c"}, cartas);
    }

    // Caso límite

    @Test
    void barajarArregloVacioOConUnElementoNoFalla() {
        Integer[] vacio = new Integer[0];
        Integer[] uno = {7};

        Barajador.barajar(vacio, new Random(42));
        Barajador.barajar(uno, new Random(42));

        assertEquals(0, vacio.length);
        assertArrayEquals(new Integer[] {7}, uno);
    }

    @Test
    void aPilaDeArregloVacioDaPilaVacia() {
        assertTrue(Barajador.aPila(new Integer[0]).estaVacia());
    }

    // Caso de error

    @Test
    void barajarConArregloORandomNullLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> Barajador.barajar(null, new Random(42)));
        assertThrows(IllegalArgumentException.class, () -> Barajador.barajar(numerosEnOrden(3), null));
    }

    @Test
    void aPilaConArregloNullOConNullDentroLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> Barajador.aPila(null));
        assertThrows(IllegalArgumentException.class, () -> Barajador.aPila(new String[] {"a", null, "c"}));
    }
}
