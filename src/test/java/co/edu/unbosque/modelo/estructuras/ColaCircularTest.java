package co.edu.unbosque.modelo.estructuras;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ColaCircularTest {

    // Caso normal

    @Test
    void desencolaEnElMismoOrdenEnQueSeEncolo() {
        ColaCircular<String> cola = new ColaCircular<>(3);
        cola.encolar("a");
        cola.encolar("b");
        cola.encolar("c");

        assertEquals(3, cola.tamanio());
        assertEquals("a", cola.desencolar());
        assertEquals("b", cola.desencolar());
        assertEquals("c", cola.desencolar());
    }

    @Test
    void rotarAlternaLosTurnosEntreDosJugadores() {
        ColaCircular<String> turnos = new ColaCircular<>(2);
        turnos.encolar("jugador");
        turnos.encolar("rival");

        assertEquals("jugador", turnos.frente());
        assertEquals("rival", turnos.rotar());
        assertEquals("jugador", turnos.rotar());
        assertEquals("rival", turnos.rotar());
        assertEquals(2, turnos.tamanio());
    }

    @Test
    void frenteNoSacaElElemento() {
        ColaCircular<Integer> cola = new ColaCircular<>(2);
        cola.encolar(5);
        cola.encolar(6);

        assertEquals(5, cola.frente());
        assertEquals(2, cola.tamanio());
    }

    // Caso límite

    @Test
    void daLaVueltaAlArregloSinPerderElOrden() {
        // Con capacidad 3 y 10 vueltas, el frente y el fin pasan varias veces por la posición 0.
        ColaCircular<Integer> cola = new ColaCircular<>(3);
        cola.encolar(0);
        cola.encolar(1);
        for (int i = 2; i < 12; i++) {
            cola.encolar(i);
            assertEquals(i - 2, cola.desencolar());
        }
        assertEquals(10, cola.desencolar());
        assertEquals(11, cola.desencolar());
        assertTrue(cola.estaVacia());
    }

    @Test
    void rotarConLaColaLlenaConservaElOrden() {
        ColaCircular<Integer> cola = new ColaCircular<>(3);
        cola.encolar(1);
        cola.encolar(2);
        cola.encolar(3);

        assertEquals(2, cola.rotar());
        assertEquals(2, cola.desencolar());
        assertEquals(3, cola.desencolar());
        assertEquals(1, cola.desencolar());
    }

    @Test
    void rotarConUnSoloElementoDevuelveElMismo() {
        ColaCircular<String> cola = new ColaCircular<>(1);
        cola.encolar("solo");

        assertEquals("solo", cola.rotar());
        assertEquals(1, cola.tamanio());
    }

    @Test
    void estaLlenaAlLlegarALaCapacidad() {
        ColaCircular<Integer> cola = new ColaCircular<>(2);
        assertFalse(cola.estaLlena());
        cola.encolar(1);
        cola.encolar(2);

        assertTrue(cola.estaLlena());
        assertEquals(2, cola.capacidad());
    }

    @Test
    void sePuedeUsarDeNuevoDespuesDeVaciar() {
        ColaCircular<Integer> cola = new ColaCircular<>(2);
        cola.encolar(1);
        cola.desencolar();
        cola.encolar(2);
        cola.encolar(3);
        cola.vaciar();

        assertTrue(cola.estaVacia());
        cola.encolar(4);
        cola.encolar(5);
        assertEquals(4, cola.desencolar());
        assertEquals(5, cola.desencolar());
    }

    // Caso de error

    @Test
    void desencolarVaciaLanzaExcepcion() {
        ColaCircular<Integer> cola = new ColaCircular<>(2);
        assertThrows(EstructuraVaciaException.class, cola::desencolar);
    }

    @Test
    void frenteYRotarDeVaciaLanzanExcepcion() {
        ColaCircular<Integer> cola = new ColaCircular<>(2);
        assertThrows(EstructuraVaciaException.class, cola::frente);
        assertThrows(EstructuraVaciaException.class, cola::rotar);
    }

    @Test
    void encolarEnColaLlenaLanzaExcepcionYNoCambiaNada() {
        ColaCircular<Integer> cola = new ColaCircular<>(2);
        cola.encolar(1);
        cola.encolar(2);

        assertThrows(IllegalStateException.class, () -> cola.encolar(3));
        assertEquals(2, cola.tamanio());
        assertEquals(1, cola.frente());
    }

    @Test
    void noAceptaNull() {
        ColaCircular<String> cola = new ColaCircular<>(2);
        assertThrows(IllegalArgumentException.class, () -> cola.encolar(null));
        assertTrue(cola.estaVacia());
    }

    @Test
    void capacidadMenorQueUnoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new ColaCircular<Integer>(0));
        assertThrows(IllegalArgumentException.class, () -> new ColaCircular<Integer>(-3));
    }
}
