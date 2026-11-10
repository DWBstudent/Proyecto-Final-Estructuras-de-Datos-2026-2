package co.edu.unbosque.modelo.juego;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.TipoCarta;

/**
 * Cada Deck de prueba tiene 40 copias de la misma carta. Así el barajado no cambia qué
 * cartas hay en la mano y cada prueba sabe el ATK y la DEF de lo que se invoca.
 */
class DueloTest {

    private static Carta carta(int id, int ataque, int defensa) {
        return new Carta(id, "Carta " + id, TipoCarta.GUERRERO, ataque, defensa, "", "",
                String.format("%08d", id));
    }

    private static Carta[] deckDe(Carta carta) {
        Carta[] deck = new Carta[Deck.TAMANIO];
        for (int i = 0; i < deck.length; i++) {
            deck[i] = carta;
        }
        return deck;
    }

    /** Ana usa cartas de ATK 2000 / DEF 1000; Beto, de ATK 1500 / DEF 1800. */
    private static Duelo dueloAnaContraBeto() {
        return new Duelo("Ana", deckDe(carta(1, 2000, 1000)), "Beto", deckDe(carta(2, 1500, 1800)),
                new Random(42));
    }

    private static Duelo dueloConAtaques(int ataqueUno, int ataqueDos) {
        return new Duelo("Ana", deckDe(carta(1, ataqueUno, 0)), "Beto", deckDe(carta(2, ataqueDos, 0)),
                new Random(42));
    }

    private static int contarEventos(Duelo duelo) {
        int eventos = 0;
        while (duelo.hayEventos()) {
            duelo.siguienteEvento();
            eventos++;
        }
        return eventos;
    }

    // Preparación y turnos

    @Test
    void alEmpezarCadaUnoTiene5EnLaMano35EnElMazoY8000Puntos() {
        Duelo duelo = dueloAnaContraBeto();

        for (Duelista d : new Duelista[] {duelo.getDuelistaUno(), duelo.getDuelistaDos()}) {
            assertEquals(5, d.cartasEnMano());
            assertEquals(35, d.cartasEnMazo());
            assertEquals(Duelista.PUNTOS_DE_VIDA_INICIALES, d.getPuntosDeVida());
            assertEquals(0, d.monstruosEnCampo());
        }
        assertSame(duelo.getDuelistaUno(), duelo.getActual());
        assertSame(duelo.getDuelistaDos(), duelo.getRival());
        assertEquals(1, duelo.getNumeroDeTurno());
        assertFalse(duelo.estaTerminado());
        assertNull(duelo.getGanador());
        assertTrue(contarEventos(duelo) > 0, "El registro debe contar el inicio del duelo");
    }

    @Test
    void terminarTurnoPasaElTurnoAlOtroYVuelve() {
        Duelo duelo = dueloAnaContraBeto();

        duelo.terminarTurno();
        assertSame(duelo.getDuelistaDos(), duelo.getActual());
        assertEquals(2, duelo.getNumeroDeTurno());

        duelo.terminarTurno();
        assertSame(duelo.getDuelistaUno(), duelo.getActual());
        assertEquals(3, duelo.getNumeroDeTurno());
    }

    @Test
    void alIniciarSuTurnoRobaHastaTener5() {
        Duelo duelo = dueloAnaContraBeto();
        Duelista ana = duelo.getActual();

        duelo.invocar(0, 0, Posicion.ATAQUE);
        assertEquals(4, ana.cartasEnMano());
        duelo.terminarTurno();
        duelo.terminarTurno();

        assertEquals(5, ana.cartasEnMano());
        assertEquals(34, ana.cartasEnMazo());
    }

    @Test
    void conLaManoLlenaNoRoba() {
        Duelo duelo = dueloAnaContraBeto();

        duelo.terminarTurno();
        duelo.terminarTurno();

        assertEquals(5, duelo.getActual().cartasEnMano());
        assertEquals(35, duelo.getActual().cartasEnMazo());
    }

    @Test
    void elDeckOriginalNoSeModifica() {
        Carta[] deckUno = deckDe(carta(1, 2000, 1000));
        Carta[] deckDos = new Carta[Deck.TAMANIO];
        for (int i = 0; i < deckDos.length; i++) {
            deckDos[i] = carta(i + 1, 1000, 1000);
        }

        new Duelo("Ana", deckUno, "Beto", deckDos, new Random(42));

        for (int i = 0; i < deckDos.length; i++) {
            assertEquals(i + 1, deckDos[i].getId());
        }
    }

    // Invocación

    @Test
    void invocarSacaLaCartaDeLaManoYLaPoneEnElCampo() {
        Duelo duelo = dueloAnaContraBeto();
        Duelista ana = duelo.getActual();
        Carta enMano = ana.getCartaEnMano(2);

        duelo.invocar(2, 3, Posicion.ATAQUE);

        assertNull(ana.getCartaEnMano(2));
        assertSame(enMano, ana.getMonstruo(3).getCarta());
        assertEquals(Posicion.ATAQUE, ana.getMonstruo(3).getPosicion());
        assertFalse(ana.getMonstruo(3).estaBocaAbajo());
        assertTrue(duelo.yaInvocoEnEsteTurno());
    }

    @Test
    void enDefensaQuedaBocaAbajo() {
        Duelo duelo = dueloAnaContraBeto();

        duelo.invocar(0, 0, Posicion.DEFENSA);

        assertTrue(duelo.getActual().getMonstruo(0).estaBocaAbajo());
    }

    @Test
    void soloSeInvocaUnMonstruoPorTurno() {
        Duelo duelo = dueloAnaContraBeto();
        duelo.invocar(0, 0, Posicion.ATAQUE);

        assertThrows(IllegalStateException.class, () -> duelo.invocar(1, 1, Posicion.ATAQUE));
        assertEquals(1, duelo.getActual().monstruosEnCampo());

        duelo.terminarTurno();
        assertFalse(duelo.yaInvocoEnEsteTurno());
        duelo.invocar(0, 0, Posicion.ATAQUE);
        assertEquals(1, duelo.getActual().monstruosEnCampo());
    }

    @Test
    void invocarEnUnaZonaOcupadaDestruyeAlMonstruoQueEstaba() {
        Duelo duelo = dueloAnaContraBeto();
        Duelista ana = duelo.getActual();
        duelo.invocar(0, 0, Posicion.DEFENSA);
        MonstruoEnCampo primero = ana.getMonstruo(0);
        duelo.terminarTurno();
        duelo.terminarTurno();

        duelo.invocar(1, 0, Posicion.ATAQUE);

        assertEquals(1, ana.monstruosEnCampo());
        assertFalse(primero == ana.getMonstruo(0), "El monstruo anterior debe salir del campo");
        assertEquals(Posicion.ATAQUE, ana.getMonstruo(0).getPosicion());
    }

    @Test
    void conLas5ZonasLlenasSeSigueInvocandoPorReemplazo() {
        Duelo duelo = dueloAnaContraBeto();
        Duelista ana = duelo.getActual();
        for (int zona = 0; zona < Duelista.ZONAS_DE_MONSTRUO; zona++) {
            duelo.invocar(0, zona, Posicion.DEFENSA);
            duelo.terminarTurno();
            duelo.terminarTurno();
        }
        assertEquals(-1, ana.primeraZonaLibre());

        duelo.invocar(0, 4, Posicion.ATAQUE);

        assertEquals(5, ana.monstruosEnCampo());
        assertEquals(Posicion.ATAQUE, ana.getMonstruo(4).getPosicion());
    }

    @Test
    void invocarConPosicionesInvalidasLanzaExcepcionSinCambiarNada() {
        Duelo duelo = dueloAnaContraBeto();
        Duelista ana = duelo.getActual();

        assertThrows(IllegalArgumentException.class, () -> duelo.invocar(5, 0, Posicion.ATAQUE));
        assertThrows(IllegalArgumentException.class, () -> duelo.invocar(-1, 0, Posicion.ATAQUE));
        assertThrows(IllegalArgumentException.class, () -> duelo.invocar(0, 5, Posicion.ATAQUE));
        assertThrows(IllegalArgumentException.class, () -> duelo.invocar(0, 0, null));
        assertEquals(5, ana.cartasEnMano());
        assertEquals(0, ana.monstruosEnCampo());
        assertFalse(duelo.yaInvocoEnEsteTurno());
    }

    // Batalla

    @Test
    void quienEmpiezaNoAtacaEnSuPrimerTurno() {
        Duelo duelo = dueloAnaContraBeto();
        duelo.invocar(0, 0, Posicion.ATAQUE);

        assertFalse(duelo.sePuedeAtacarEnEsteTurno());
        assertThrows(IllegalStateException.class, () -> duelo.atacarDirecto(0));
        assertEquals(Duelista.PUNTOS_DE_VIDA_INICIALES, duelo.getRival().getPuntosDeVida());
    }

    @Test
    void elSegundoSiAtacaEnSuPrimerTurno() {
        Duelo duelo = dueloAnaContraBeto();
        duelo.terminarTurno();
        duelo.invocar(0, 0, Posicion.ATAQUE);

        assertTrue(duelo.sePuedeAtacarEnEsteTurno());
        ResultadoCombate r = duelo.atacarDirecto(0);

        assertEquals(1500, r.getDanioAlDefensor());
        assertEquals(6500, duelo.getDuelistaUno().getPuntosDeVida());
    }

    @Test
    void atacarAUnMonstruoMasFuerteDestruyeAlAtacanteYQuitaLaDiferencia() {
        Duelo duelo = dueloAnaContraBeto();
        duelo.invocar(0, 0, Posicion.ATAQUE);       // Ana: ATK 2000
        duelo.terminarTurno();
        duelo.invocar(0, 0, Posicion.ATAQUE);       // Beto: ATK 1500

        ResultadoCombate r = duelo.atacar(0, 0);

        assertTrue(r.destruyeAtacante());
        assertNull(duelo.getDuelistaDos().getMonstruo(0));
        assertNotNull(duelo.getDuelistaUno().getMonstruo(0));
        assertEquals(7500, duelo.getDuelistaDos().getPuntosDeVida());
        assertEquals(8000, duelo.getDuelistaUno().getPuntosDeVida());
    }

    @Test
    void atacarAUnMonstruoEnDefensaLoVoltea() {
        Duelo duelo = dueloAnaContraBeto();
        duelo.invocar(0, 0, Posicion.DEFENSA);      // Ana: DEF 1000
        duelo.terminarTurno();
        duelo.invocar(0, 0, Posicion.ATAQUE);       // Beto: ATK 1500
        MonstruoEnCampo objetivo = duelo.getDuelistaUno().getMonstruo(0);

        ResultadoCombate r = duelo.atacar(0, 0);

        assertTrue(r.destruyeObjetivo());
        assertFalse(objetivo.estaBocaAbajo());
        assertNull(duelo.getDuelistaUno().getMonstruo(0));
        assertEquals(8000, duelo.getDuelistaUno().getPuntosDeVida());
    }

    @Test
    void unMonstruoEnDefensaNoPuedeAtacar() {
        Duelo duelo = dueloAnaContraBeto();
        duelo.terminarTurno();
        duelo.invocar(0, 0, Posicion.DEFENSA);

        assertThrows(IllegalStateException.class, () -> duelo.atacarDirecto(0));
    }

    @Test
    void unMonstruoNoAtacaDosVecesEnElMismoTurnoPeroSiEnElSiguiente() {
        Duelo duelo = dueloAnaContraBeto();
        duelo.terminarTurno();
        duelo.invocar(0, 0, Posicion.ATAQUE);
        duelo.atacarDirecto(0);

        assertThrows(IllegalStateException.class, () -> duelo.atacarDirecto(0));

        duelo.terminarTurno();
        duelo.terminarTurno();
        duelo.atacarDirecto(0);
        assertEquals(5000, duelo.getDuelistaUno().getPuntosDeVida());
    }

    @Test
    void noSeAtacaDirectoSiElRivalTieneMonstruos() {
        Duelo duelo = dueloAnaContraBeto();
        duelo.invocar(0, 0, Posicion.DEFENSA);
        duelo.terminarTurno();
        duelo.invocar(0, 0, Posicion.ATAQUE);

        assertThrows(IllegalStateException.class, () -> duelo.atacarDirecto(0));
    }

    @Test
    void atacarZonasVaciasLanzaExcepcion() {
        Duelo duelo = dueloAnaContraBeto();
        duelo.invocar(0, 0, Posicion.DEFENSA);
        duelo.terminarTurno();
        duelo.invocar(0, 0, Posicion.ATAQUE);

        assertThrows(IllegalArgumentException.class, () -> duelo.atacar(0, 1));
        assertThrows(IllegalArgumentException.class, () -> duelo.atacar(1, 0));
        assertThrows(IllegalArgumentException.class, () -> duelo.atacar(0, 7));
    }

    // Fin del duelo

    @Test
    void ganaQuienDejaAlRivalEnExactamente0() {
        Duelo duelo = dueloConAtaques(1000, 4000);
        duelo.terminarTurno();
        duelo.invocar(0, 0, Posicion.ATAQUE);
        duelo.atacarDirecto(0);                     // Ana queda en 4000
        duelo.terminarTurno();
        duelo.terminarTurno();
        duelo.invocar(0, 1, Posicion.ATAQUE);
        duelo.atacarDirecto(0);                     // Ana queda en 0

        assertEquals(0, duelo.getDuelistaUno().getPuntosDeVida());
        assertTrue(duelo.estaTerminado());
        assertSame(duelo.getDuelistaDos(), duelo.getGanador());
    }

    @Test
    void losPuntosDeVidaNoBajanDe0() {
        Duelo duelo = dueloConAtaques(1000, 5000);
        duelo.terminarTurno();
        duelo.invocar(0, 0, Posicion.ATAQUE);
        duelo.atacarDirecto(0);
        duelo.terminarTurno();
        duelo.terminarTurno();
        duelo.atacarDirecto(0);

        assertEquals(0, duelo.getDuelistaUno().getPuntosDeVida());
        assertSame(duelo.getDuelistaDos(), duelo.getGanador());
    }

    @Test
    void pierdeQuienSeQuedaSinPuntosAlAtacarAUnoMasFuerte() {
        Duelo duelo = dueloConAtaques(8000, 0);
        duelo.invocar(0, 0, Posicion.ATAQUE);       // Ana: ATK 8000
        duelo.terminarTurno();
        duelo.invocar(0, 0, Posicion.ATAQUE);       // Beto: ATK 0

        duelo.atacar(0, 0);

        assertEquals(0, duelo.getDuelistaDos().getPuntosDeVida());
        assertSame(duelo.getDuelistaUno(), duelo.getGanador());
    }

    @Test
    void pierdeQuienDebeRobarConElMazoVacio() {
        Duelo duelo = dueloAnaContraBeto();
        // Los dos invocan en defensa cada turno y nadie ataca: cada turno se gasta 1 carta
        // del mazo. Ana roba en los turnos 3, 5, ...; su robo número 36 cae en el turno 73.
        while (!duelo.estaTerminado()) {
            duelo.invocar(0, 0, Posicion.DEFENSA);
            duelo.terminarTurno();
        }

        assertEquals(73, duelo.getNumeroDeTurno());
        assertSame(duelo.getDuelistaDos(), duelo.getGanador());
        assertEquals(0, duelo.getDuelistaUno().cartasEnMazo());
        assertEquals(Duelista.PUNTOS_DE_VIDA_INICIALES, duelo.getDuelistaUno().getPuntosDeVida());
    }

    @Test
    void despuesDeTerminarNoSePuedeHacerNada() {
        Duelo duelo = dueloConAtaques(1000, 8000);
        duelo.terminarTurno();
        duelo.invocar(0, 0, Posicion.ATAQUE);
        duelo.atacarDirecto(0);
        assertTrue(duelo.estaTerminado());

        assertFalse(duelo.sePuedeAtacarEnEsteTurno());
        assertThrows(IllegalStateException.class, () -> duelo.terminarTurno());
        assertThrows(IllegalStateException.class, () -> duelo.invocar(1, 1, Posicion.ATAQUE));
        assertThrows(IllegalStateException.class, () -> duelo.atacarDirecto(0));
    }

    // Errores al crear

    @Test
    void unDeckQueNoTiene40CartasLanzaExcepcion() {
        Carta c = carta(1, 1000, 1000);
        Carta[] corto = new Carta[39];
        for (int i = 0; i < corto.length; i++) {
            corto[i] = c;
        }

        assertThrows(IllegalArgumentException.class,
                () -> new Duelo("Ana", corto, "Beto", deckDe(c), new Random(42)));
        assertThrows(IllegalArgumentException.class,
                () -> new Duelo("Ana", deckDe(c), "Beto", new Carta[41], new Random(42)));
    }

    @Test
    void datosFaltantesAlCrearLanzanExcepcion() {
        Carta c = carta(1, 1000, 1000);
        Carta[] conNull = deckDe(c);
        conNull[10] = null;

        assertThrows(IllegalArgumentException.class,
                () -> new Duelo("Ana", null, "Beto", deckDe(c), new Random(42)));
        assertThrows(IllegalArgumentException.class,
                () -> new Duelo("Ana", conNull, "Beto", deckDe(c), new Random(42)));
        assertThrows(IllegalArgumentException.class,
                () -> new Duelo("Ana", deckDe(c), "Beto", deckDe(c), null));
        assertThrows(IllegalArgumentException.class,
                () -> new Duelo(" ", deckDe(c), "Beto", deckDe(c), new Random(42)));
    }
}
