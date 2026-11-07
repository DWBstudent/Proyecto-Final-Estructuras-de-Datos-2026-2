package co.edu.unbosque.modelo.estructuras;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ColaTest {

    // Caso normal

    @Test
    void desencolaEnElMismoOrdenEnQueSeEncolo() {
        Cola<String> cola = new Cola<>();
        cola.encolar("rival 1");
        cola.encolar("rival 2");
        cola.encolar("rival 3");

        assertEquals(3, cola.tamanio());
        assertEquals("rival 1", cola.desencolar());
        assertEquals("rival 2", cola.desencolar());
        assertEquals("rival 3", cola.desencolar());
    }

    @Test
    void frenteNoSacaElElemento() {
        Cola<Integer> cola = new Cola<>();
        cola.encolar(5);
        cola.encolar(6);

        assertEquals(5, cola.frente());
        assertEquals(2, cola.tamanio());
    }

    // Caso límite

    @Test
    void quedaVaciaDespuesDeSacarElUnicoElemento() {
        Cola<Integer> cola = new Cola<>();
        cola.encolar(1);
        cola.desencolar();

        assertTrue(cola.estaVacia());
        assertEquals(0, cola.tamanio());
    }

    @Test
    void funcionaAlEncolarDespuesDeQuedarVacia() {
        // Si el fin no se reinicia al vaciar, este caso falla.
        Cola<Integer> cola = new Cola<>();
        cola.encolar(1);
        cola.desencolar();
        cola.encolar(2);
        cola.encolar(3);

        assertEquals(2, cola.desencolar());
        assertEquals(3, cola.desencolar());
        assertTrue(cola.estaVacia());
    }

    @Test
    void sePuedeUsarDeNuevoDespuesDeVaciar() {
        Cola<Integer> cola = new Cola<>();
        cola.encolar(1);
        cola.encolar(2);
        cola.vaciar();
        cola.encolar(3);

        assertEquals(1, cola.tamanio());
        assertEquals(3, cola.frente());
    }

    // Caso de error

    @Test
    void desencolarVaciaLanzaExcepcion() {
        Cola<Integer> cola = new Cola<>();
        assertThrows(EstructuraVaciaException.class, cola::desencolar);
    }

    @Test
    void frenteDeVaciaLanzaExcepcion() {
        Cola<Integer> cola = new Cola<>();
        assertThrows(EstructuraVaciaException.class, cola::frente);
    }

    @Test
    void noAceptaNull() {
        Cola<String> cola = new Cola<>();
        assertThrows(IllegalArgumentException.class, () -> cola.encolar(null));
        assertTrue(cola.estaVacia());
    }
}
