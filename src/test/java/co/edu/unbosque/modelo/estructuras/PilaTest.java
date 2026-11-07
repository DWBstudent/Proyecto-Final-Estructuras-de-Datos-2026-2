package co.edu.unbosque.modelo.estructuras;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PilaTest {

    // Caso normal

    @Test
    void desapilaEnOrdenInversoAlQueSeApilo() {
        Pila<String> pila = new Pila<>();
        pila.apilar("menu");
        pila.apilar("chest");
        pila.apilar("detalle");

        assertEquals(3, pila.tamanio());
        assertEquals("detalle", pila.desapilar());
        assertEquals("chest", pila.desapilar());
        assertEquals("menu", pila.desapilar());
    }

    @Test
    void cimaNoSacaElElemento() {
        Pila<Integer> pila = new Pila<>();
        pila.apilar(7);

        assertEquals(7, pila.cima());
        assertEquals(1, pila.tamanio());
    }

    // Caso límite

    @Test
    void quedaVaciaDespuesDeSacarElUnicoElemento() {
        Pila<Integer> pila = new Pila<>();
        pila.apilar(1);
        pila.desapilar();

        assertTrue(pila.estaVacia());
        assertEquals(0, pila.tamanio());
    }

    @Test
    void sePuedeUsarDeNuevoDespuesDeVaciar() {
        Pila<Integer> pila = new Pila<>();
        pila.apilar(1);
        pila.apilar(2);
        pila.vaciar();
        pila.apilar(3);

        assertEquals(1, pila.tamanio());
        assertEquals(3, pila.cima());
    }

    @Test
    void soportaMilElementos() {
        Pila<Integer> pila = new Pila<>();
        for (int i = 0; i < 1000; i++) {
            pila.apilar(i);
        }
        assertEquals(1000, pila.tamanio());
        assertEquals(999, pila.desapilar());
        assertFalse(pila.estaVacia());
    }

    // Caso de error

    @Test
    void desapilarVaciaLanzaExcepcion() {
        Pila<Integer> pila = new Pila<>();
        assertThrows(EstructuraVaciaException.class, pila::desapilar);
    }

    @Test
    void cimaDeVaciaLanzaExcepcion() {
        Pila<Integer> pila = new Pila<>();
        assertThrows(EstructuraVaciaException.class, pila::cima);
    }

    @Test
    void noAceptaNull() {
        Pila<String> pila = new Pila<>();
        assertThrows(IllegalArgumentException.class, () -> pila.apilar(null));
        assertTrue(pila.estaVacia());
    }
}
