package co.edu.unbosque.modelo.juego;

import static co.edu.unbosque.modelo.juego.CartasDePrueba.carta;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.estructuras.Pila;

class SeleccionIATest {

    /** Duelista con las cartas dadas en la mano (en ese orden) y el mazo vacío. */
    private static Duelista duelistaConMano(Carta... cartas) {
        Pila<Carta> mazo = new Pila<>();
        for (int i = cartas.length - 1; i >= 0; i--) {
            mazo.apilar(cartas[i]);
        }
        Duelista duelista = new Duelista("Prueba", mazo);
        duelista.robarHastaCompletar();
        return duelista;
    }

    // Caso normal

    @Test
    void eligeLaPosicionDeLaCartaDeMayorAtaque() {
        Duelista d = duelistaConMano(carta(1, 800, 0), carta(2, 2500, 0), carta(3, 1200, 0),
                carta(4, 300, 0), carta(5, 1900, 0));

        assertEquals(1, SeleccionIA.posicionDeMayorAtaque(d));
    }

    @Test
    void invocaEnLaPrimeraZonaLibre() {
        Duelista d = duelistaConMano(carta(1, 100, 0));
        d.colocar(0, new MonstruoEnCampo(carta(2, 100, 0), Posicion.ATAQUE));
        d.colocar(2, new MonstruoEnCampo(carta(3, 100, 0), Posicion.ATAQUE));

        assertEquals(1, SeleccionIA.zonaParaInvocar(d));
    }

    @Test
    void mayorAtaqueEnCampoSoloCuentaMonstruosEnAtaque() {
        Duelista d = duelistaConMano(carta(1, 100, 0));
        d.colocar(0, new MonstruoEnCampo(carta(2, 1500, 0), Posicion.ATAQUE));
        d.colocar(1, new MonstruoEnCampo(carta(3, 3000, 0), Posicion.DEFENSA));

        assertEquals(1500, SeleccionIA.mayorAtaqueEnCampo(d));
    }

    // Límite

    @Test
    void conElCampoLlenoEligeLaZonaDelMonstruoMasDebil() {
        Duelista d = duelistaConMano(carta(1, 100, 0));
        int[] ataques = {1200, 900, 1500, 400, 2000};
        for (int zona = 0; zona < ataques.length; zona++) {
            d.colocar(zona, new MonstruoEnCampo(carta(zona + 2, ataques[zona], 0), Posicion.ATAQUE));
        }

        assertEquals(3, SeleccionIA.zonaParaInvocar(d));
    }

    @Test
    void conLaManoVaciaDevuelveMenosUno() {
        Duelista d = new Duelista("Prueba", new Pila<>());

        assertEquals(-1, SeleccionIA.posicionDeMayorAtaque(d));
        assertEquals(0, SeleccionIA.mayorAtaqueEnCampo(d));
    }
}
