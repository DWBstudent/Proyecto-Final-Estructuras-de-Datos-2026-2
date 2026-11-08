package co.edu.unbosque.modelo.estructuras;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.ordenamiento.Comparador;

class MonticuloMaxTest {

    private static final Comparador<Integer> MAYOR_NUMERO = (a, b) -> Integer.compare(a, b);

    // Caso normal

    @Test
    void extraeDeMayorAMenor() {
        MonticuloMax<Integer> monticulo = new MonticuloMax<>(MAYOR_NUMERO);
        monticulo.insertar(5);
        monticulo.insertar(1);
        monticulo.insertar(9);
        monticulo.insertar(3);
        monticulo.insertar(7);

        assertEquals(5, monticulo.tamanio());
        assertEquals(9, monticulo.extraerMax());
        assertEquals(7, monticulo.extraerMax());
        assertEquals(5, monticulo.extraerMax());
        assertEquals(3, monticulo.extraerMax());
        assertEquals(1, monticulo.extraerMax());
        assertTrue(monticulo.estaVacio());
    }

    @Test
    void verMaxNoSacaElElemento() {
        MonticuloMax<Integer> monticulo = new MonticuloMax<>(MAYOR_NUMERO);
        monticulo.insertar(2);
        monticulo.insertar(8);

        assertEquals(8, monticulo.verMax());
        assertEquals(2, monticulo.tamanio());
    }

    @Test
    void elComparadorDecideQueEsElMaximo() {
        // Con el comparador invertido, el "máximo" es el número más pequeño.
        MonticuloMax<Integer> monticulo = new MonticuloMax<>((a, b) -> Integer.compare(b, a));
        monticulo.insertar(5);
        monticulo.insertar(1);
        monticulo.insertar(9);

        assertEquals(1, monticulo.extraerMax());
        assertEquals(5, monticulo.extraerMax());
        assertEquals(9, monticulo.extraerMax());
    }

    @Test
    void construirDesdeArregloExtraeEnOrdenYNoModificaElArreglo() {
        Integer[] numeros = {4, 10, 3, 5, 1, 8};
        MonticuloMax<Integer> monticulo = new MonticuloMax<>(numeros, MAYOR_NUMERO);

        assertEquals(6, monticulo.tamanio());
        assertEquals(10, monticulo.extraerMax());
        assertEquals(8, monticulo.extraerMax());
        assertEquals(5, monticulo.extraerMax());
        assertEquals(4, monticulo.extraerMax());
        assertEquals(3, monticulo.extraerMax());
        assertEquals(1, monticulo.extraerMax());
        assertArrayEquals(new Integer[] {4, 10, 3, 5, 1, 8}, numeros);
    }

    @Test
    void sePuedeInsertarDespuesDeConstruirDesdeArreglo() {
        MonticuloMax<Integer> monticulo = new MonticuloMax<>(new Integer[] {2, 6}, MAYOR_NUMERO);
        monticulo.insertar(4);
        monticulo.insertar(9);

        assertEquals(9, monticulo.extraerMax());
        assertEquals(6, monticulo.extraerMax());
        assertEquals(4, monticulo.extraerMax());
        assertEquals(2, monticulo.extraerMax());
    }

    // Caso límite

    @Test
    void unSoloElemento() {
        MonticuloMax<Integer> monticulo = new MonticuloMax<>(MAYOR_NUMERO);
        monticulo.insertar(42);

        assertEquals(42, monticulo.verMax());
        assertEquals(42, monticulo.extraerMax());
        assertTrue(monticulo.estaVacio());
    }

    @Test
    void elementosRepetidos() {
        MonticuloMax<Integer> monticulo = new MonticuloMax<>(new Integer[] {4, 2, 4, 4, 1}, MAYOR_NUMERO);

        assertEquals(4, monticulo.extraerMax());
        assertEquals(4, monticulo.extraerMax());
        assertEquals(4, monticulo.extraerMax());
        assertEquals(2, monticulo.extraerMax());
        assertEquals(1, monticulo.extraerMax());
    }

    @Test
    void creceMasAllaDeLaCapacidadInicialSinPerderElOrden() {
        // 100 números en desorden (más que la capacidad inicial de 8): deben salir de 99 a 0.
        Integer[] numeros = new Integer[100];
        for (int i = 0; i < numeros.length; i++) {
            numeros[i] = i;
        }
        Random random = new Random(42);
        for (int i = numeros.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Integer temporal = numeros[i];
            numeros[i] = numeros[j];
            numeros[j] = temporal;
        }
        MonticuloMax<Integer> monticulo = new MonticuloMax<>(MAYOR_NUMERO);
        for (Integer numero : numeros) {
            monticulo.insertar(numero);
        }

        assertEquals(100, monticulo.tamanio());
        for (int esperado = 99; esperado >= 0; esperado--) {
            assertEquals(esperado, monticulo.extraerMax());
        }
    }

    @Test
    void construirDesdeArregloVacioDaMonticuloVacioQueSePuedeUsar() {
        MonticuloMax<Integer> monticulo = new MonticuloMax<>(new Integer[0], MAYOR_NUMERO);
        assertTrue(monticulo.estaVacio());

        monticulo.insertar(3);
        assertEquals(3, monticulo.extraerMax());
    }

    @Test
    void sePuedeUsarDeNuevoDespuesDeVaciar() {
        MonticuloMax<Integer> monticulo = new MonticuloMax<>(MAYOR_NUMERO);
        monticulo.insertar(1);
        monticulo.insertar(2);
        monticulo.vaciar();

        assertTrue(monticulo.estaVacio());
        assertEquals(0, monticulo.tamanio());
        monticulo.insertar(7);
        assertEquals(7, monticulo.verMax());
    }

    // Caso de error

    @Test
    void extraerOVerDeVacioLanzaExcepcion() {
        MonticuloMax<Integer> monticulo = new MonticuloMax<>(MAYOR_NUMERO);
        assertThrows(EstructuraVaciaException.class, monticulo::extraerMax);
        assertThrows(EstructuraVaciaException.class, monticulo::verMax);
    }

    @Test
    void comparadorNullLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new MonticuloMax<Integer>(null));
        assertThrows(IllegalArgumentException.class, () -> new MonticuloMax<Integer>(new Integer[] {1}, null));
    }

    @Test
    void noAceptaNull() {
        MonticuloMax<Integer> monticulo = new MonticuloMax<>(MAYOR_NUMERO);
        assertThrows(IllegalArgumentException.class, () -> monticulo.insertar(null));
        assertTrue(monticulo.estaVacio());
    }

    @Test
    void arregloNullOConElementosNullLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new MonticuloMax<Integer>(null, MAYOR_NUMERO));
        assertThrows(IllegalArgumentException.class,
                () -> new MonticuloMax<>(new Integer[] {1, null, 3}, MAYOR_NUMERO));
    }
}
