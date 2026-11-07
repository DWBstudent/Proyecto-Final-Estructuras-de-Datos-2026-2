package co.edu.unbosque.modelo.entidades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JugadorTest {

    // Caso normal

    @Test
    void empiezaSinCartasDescubiertasNiRivalesVencidos() {
        Jugador jugador = new Jugador("Daniel");

        assertEquals(0, jugador.getCantidadDescubiertas());
        assertFalse(jugador.estaDescubierta(1));
        assertEquals(0, jugador.getRivalesVencidos());
        assertFalse(jugador.completoModoHistoria());
    }

    @Test
    void marcaCartasComoDescubiertas() {
        Jugador jugador = new Jugador("Daniel");
        jugador.marcarDescubierta(7);

        assertTrue(jugador.estaDescubierta(7));
        assertFalse(jugador.estaDescubierta(8));
        assertEquals(1, jugador.getCantidadDescubiertas());
    }

    // Caso límite

    @Test
    void funcionaConLosIdsDeLosExtremos() {
        Jugador jugador = new Jugador("Daniel");
        jugador.marcarDescubierta(1);
        jugador.marcarDescubierta(100);

        assertTrue(jugador.estaDescubierta(1));
        assertTrue(jugador.estaDescubierta(100));
        assertEquals(2, jugador.getCantidadDescubiertas());
    }

    @Test
    void marcarDosVecesNoCuentaDoble() {
        Jugador jugador = new Jugador("Daniel");
        jugador.marcarDescubierta(3);
        jugador.marcarDescubierta(3);

        assertEquals(1, jugador.getCantidadDescubiertas());
    }

    @Test
    void completaElModoHistoriaAlVencerTresRivales() {
        Jugador jugador = new Jugador("Daniel");
        jugador.registrarRivalVencido();
        jugador.registrarRivalVencido();
        jugador.registrarRivalVencido();

        assertTrue(jugador.completoModoHistoria());
    }

    // Caso de error

    @Test
    void rechazaIdsFueraDeRango() {
        Jugador jugador = new Jugador("Daniel");
        assertThrows(IllegalArgumentException.class, () -> jugador.estaDescubierta(0));
        assertThrows(IllegalArgumentException.class, () -> jugador.marcarDescubierta(101));
    }

    @Test
    void noPuedeVencerUnCuartoRival() {
        Jugador jugador = new Jugador("Daniel");
        jugador.registrarRivalVencido();
        jugador.registrarRivalVencido();
        jugador.registrarRivalVencido();

        assertThrows(IllegalStateException.class, jugador::registrarRivalVencido);
    }

    @Test
    void rechazaNombreVacioYCartaNula() {
        assertThrows(IllegalArgumentException.class, () -> new Jugador(" "));
        Jugador jugador = new Jugador("Daniel");
        assertThrows(IllegalArgumentException.class, () -> jugador.recibirCarta(null));
    }
}
