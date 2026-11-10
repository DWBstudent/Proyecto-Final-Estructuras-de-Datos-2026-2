package co.edu.unbosque.modelo.juego;

import static co.edu.unbosque.modelo.juego.CartasDePrueba.carta;
import static co.edu.unbosque.modelo.juego.CartasDePrueba.deckDe;
import static co.edu.unbosque.modelo.juego.CartasDePrueba.deckVariado;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

/** En estas pruebas el duelista uno es una persona (se juega a mano) y el dos es la IA. */
class IARival1Test {

    private final EstrategiaIA ia = new IARival1();

    private static Duelo duelo(int ataqueHumano, int defensaHumano, int ataqueIA) {
        return new Duelo("Humano", deckDe(carta(1, ataqueHumano, defensaHumano)), "IA",
                deckDe(carta(2, ataqueIA, 0)), new Random(42));
    }

    // Caso normal

    @Test
    void invocaEnAtaqueLaCartaDeMayorAtaqueDeSuMano() {
        Duelo duelo = new Duelo("IA", deckVariado(), "Humano", deckVariado(), new Random(42));
        Duelista propio = duelo.getActual();
        int mayor = 0;
        for (int i = 0; i < Duelista.TAMANIO_MANO; i++) {
            mayor = Math.max(mayor, propio.getCartaEnMano(i).getAtaque());
        }

        ia.jugarTurno(duelo);

        assertEquals(mayor, propio.getMonstruo(0).getCarta().getAtaque());
        assertEquals(Posicion.ATAQUE, propio.getMonstruo(0).getPosicion());
        assertEquals(Duelista.PUNTOS_DE_VIDA_INICIALES, duelo.getRival().getPuntosDeVida(),
                "En el turno 1 no se ataca");
    }

    @Test
    void atacaDirectoSiElRivalNoTieneMonstruos() {
        Duelo duelo = duelo(1000, 1000, 1500);
        duelo.terminarTurno();

        ia.jugarTurno(duelo);

        assertEquals(6500, duelo.getDuelistaUno().getPuntosDeVida());
    }

    @Test
    void atacaAunqueVayaAPerder() {
        Duelo duelo = duelo(3000, 0, 1000);
        duelo.invocar(0, 0, Posicion.ATAQUE);
        duelo.terminarTurno();

        ia.jugarTurno(duelo);

        assertNull(duelo.getDuelistaDos().getMonstruo(0), "Su monstruo debe destruirse");
        assertEquals(6000, duelo.getDuelistaDos().getPuntosDeVida());
        assertNotNull(duelo.getDuelistaUno().getMonstruo(0));
    }

    @Test
    void atacaAlMonstruoMasDebilDelRival() {
        Duelo duelo = duelo(1000, 3000, 2000);
        duelo.invocar(0, 0, Posicion.DEFENSA);      // vale 3000 (DEF)
        duelo.terminarTurno();
        duelo.terminarTurno();
        duelo.invocar(0, 1, Posicion.ATAQUE);       // vale 1000 (ATK)
        duelo.terminarTurno();

        ia.jugarTurno(duelo);

        assertNull(duelo.getDuelistaUno().getMonstruo(1));
        assertNotNull(duelo.getDuelistaUno().getMonstruo(0));
        assertEquals(7000, duelo.getDuelistaUno().getPuntosDeVida());
    }

    // Límite

    @Test
    void dejaDeJugarCuandoGanaEnMedioDelTurno() {
        Duelo duelo = duelo(0, 0, 8000);
        duelo.terminarTurno();

        ia.jugarTurno(duelo);

        assertEquals(0, duelo.getDuelistaUno().getPuntosDeVida());
        assertEquals(duelo.getDuelistaDos(), duelo.getGanador());
    }

    // Error

    @Test
    void conElDueloTerminadoLanzaExcepcion() {
        Duelo duelo = duelo(0, 0, 8000);
        duelo.terminarTurno();
        ia.jugarTurno(duelo);

        assertThrows(IllegalStateException.class, () -> ia.jugarTurno(duelo));
    }
}
