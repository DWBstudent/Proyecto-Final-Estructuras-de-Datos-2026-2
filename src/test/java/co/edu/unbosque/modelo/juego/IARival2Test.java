package co.edu.unbosque.modelo.juego;

import static co.edu.unbosque.modelo.juego.CartasDePrueba.carta;
import static co.edu.unbosque.modelo.juego.CartasDePrueba.deckDe;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

/** En estas pruebas el duelista uno es una persona (se juega a mano) y el dos es la IA. */
class IARival2Test {

    private final IARival2 ia = new IARival2();

    private static Duelo duelo(int ataqueHumano, int defensaHumano, int ataqueIA) {
        return new Duelo("Humano", deckDe(carta(1, ataqueHumano, defensaHumano)), "IA",
                deckDe(carta(2, ataqueIA, 0)), new Random(42));
    }

    /** El humano pone un monstruo en defensa (zona 0) y otro en ataque (zona 1). Turno 4, de la IA. */
    private static Duelo humanoConDefensaYAtaque(int ataqueHumano, int defensaHumano, int ataqueIA) {
        Duelo duelo = duelo(ataqueHumano, defensaHumano, ataqueIA);
        duelo.invocar(0, 0, Posicion.DEFENSA);
        duelo.terminarTurno();
        duelo.terminarTurno();
        duelo.invocar(0, 1, Posicion.ATAQUE);
        duelo.terminarTurno();
        return duelo;
    }

    // Caso normal

    @Test
    void mejorJugadaEsLaDeMayorPuntaje() {
        Duelo duelo = humanoConDefensaYAtaque(1000, 2500, 2000);
        duelo.invocar(0, 0, Posicion.ATAQUE);

        Jugada mejor = ia.mejorJugada(duelo);

        // Contra la zona 1 (ATK 1000): 1000 de daño + 1000 por destruir = 2000.
        // Contra la zona 0 (DEF 2500): -500.
        assertEquals(0, mejor.getZonaAtacante());
        assertEquals(1, mejor.getZonaObjetivo());
        assertEquals(2000, mejor.getPuntaje());
    }

    @Test
    void haceLasJugadasFavorablesYNoLasQueLeQuitanPuntos() {
        Duelo duelo = humanoConDefensaYAtaque(1000, 2500, 2000);

        ia.jugarTurno(duelo);

        assertNull(duelo.getDuelistaUno().getMonstruo(1));
        assertNotNull(duelo.getDuelistaUno().getMonstruo(0));
        assertEquals(7000, duelo.getDuelistaUno().getPuntosDeVida());
        assertEquals(8000, duelo.getDuelistaDos().getPuntosDeVida(), "No debe atacar la defensa de 2500");
    }

    @Test
    void siEsMasDebilSeDefiendeYNoAtaca() {
        Duelo duelo = duelo(3000, 0, 2000);
        duelo.invocar(0, 0, Posicion.ATAQUE);
        duelo.terminarTurno();

        ia.jugarTurno(duelo);

        assertEquals(Posicion.DEFENSA, duelo.getDuelistaDos().getMonstruo(0).getPosicion());
        assertEquals(8000, duelo.getDuelistaDos().getPuntosDeVida());
        assertEquals(8000, duelo.getDuelistaUno().getPuntosDeVida());
    }

    @Test
    void atacaDirectoSiElRivalNoTieneMonstruos() {
        Duelo duelo = duelo(1000, 1000, 2000);
        duelo.terminarTurno();

        ia.jugarTurno(duelo);

        assertEquals(6000, duelo.getDuelistaUno().getPuntosDeVida());
    }

    @Test
    void puntajeSumaDanioYBonosPorDestruir() {
        Duelista rival = new Duelo("A", deckDe(carta(1, 0, 0)), "B", deckDe(carta(2, 0, 0)),
                new Random(42)).getRival();

        ResultadoCombate gana = Combate.resolver(carta(3, 2000, 0),
                new MonstruoEnCampo(carta(4, 1500, 0), Posicion.ATAQUE));
        ResultadoCombate pierde = Combate.resolver(carta(3, 1200, 0),
                new MonstruoEnCampo(carta(4, 1900, 0), Posicion.ATAQUE));

        assertEquals(500 + 1000, ia.puntaje(gana, rival));
        assertEquals(-700 - 1000, ia.puntaje(pierde, rival));
    }

    // Límite

    @Test
    void conAtaquesIgualesNoAtacaPorqueElPuntajeEs0() {
        Duelo duelo = duelo(2000, 0, 2000);
        duelo.invocar(0, 0, Posicion.ATAQUE);
        duelo.terminarTurno();

        ia.jugarTurno(duelo);

        assertEquals(Posicion.ATAQUE, duelo.getDuelistaDos().getMonstruo(0).getPosicion());
        assertNotNull(duelo.getDuelistaUno().getMonstruo(0));
        assertNotNull(duelo.getDuelistaDos().getMonstruo(0));
    }

    @Test
    void sinMonstruosQuePuedanAtacarNoHayMejorJugada() {
        Duelo duelo = duelo(1000, 1000, 2000);

        assertNull(ia.mejorJugada(duelo));
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
