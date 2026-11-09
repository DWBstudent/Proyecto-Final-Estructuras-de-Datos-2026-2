package co.edu.unbosque.modelo.juego;

import static co.edu.unbosque.modelo.juego.CartasDePrueba.carta;
import static co.edu.unbosque.modelo.juego.CartasDePrueba.catalogoUniforme;
import static co.edu.unbosque.modelo.juego.CartasDePrueba.deckDe;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.Jugador;

class CampaniaTest {

    /**
     * Jugador de prueba: el Chest de Brayan todavía no está, así que {@code recibirCarta}
     * guarda la última carta y responde lo que se le diga.
     */
    private static class JugadorDePrueba extends Jugador {
        Carta ultimaRecibida;
        int recibidas;
        boolean responder = true;

        JugadorDePrueba() {
            super("Santi");
        }

        @Override
        public boolean recibirCarta(Carta carta) {
            ultimaRecibida = carta;
            recibidas++;
            marcarDescubierta(carta.getId());
            return responder;
        }
    }

    /** Todas las cartas de los rivales tienen ATK 4000 y DEF 0. */
    private static Campania campania(Jugador jugador) {
        return new Campania(jugador, catalogoUniforme(4000, 0));
    }

    /** El jugador (ATK 8000) invoca en el turno 1 y ataca directo en el 3; el rival no hace nada. */
    private static Duelo dueloGanado(Campania campania) {
        Duelo duelo = campania.crearDuelo(deckDe(carta(1, 8000, 0)), new Random(42));
        duelo.invocar(0, 0, Posicion.ATAQUE);
        duelo.terminarTurno();
        duelo.terminarTurno();
        duelo.atacarDirecto(0);
        return duelo;
    }

    /** El rival (ATK 4000) ataca directo en los turnos 2 y 4; el jugador no hace nada. */
    private static Duelo dueloPerdido(Campania campania) {
        Duelo duelo = campania.crearDuelo(deckDe(carta(1, 0, 0)), new Random(42));
        duelo.terminarTurno();
        duelo.invocar(0, 0, Posicion.ATAQUE);
        duelo.atacarDirecto(0);
        duelo.terminarTurno();
        duelo.terminarTurno();
        duelo.atacarDirecto(0);
        return duelo;
    }

    // Caso normal

    @Test
    void empiezaContraElRival1ConLos3Pendientes() {
        Campania campania = campania(new JugadorDePrueba());

        assertEquals(1, campania.getRivalActual().getNumero());
        assertEquals(3, campania.rivalesRestantes());
        assertFalse(campania.estaCompleta());
        assertInstanceOf(IARival1.class, campania.estrategiaDelRivalActual());
    }

    @Test
    void elDueloEsDelJugadorContraElRivalActual() {
        JugadorDePrueba jugador = new JugadorDePrueba();
        Campania campania = campania(jugador);

        Duelo duelo = campania.crearDuelo(deckDe(carta(1, 1000, 0)), new Random(42));

        assertEquals("Santi", duelo.getDuelistaUno().getNombre());
        assertEquals(campania.getRivalActual().getNombre(), duelo.getDuelistaDos().getNombre());
        assertSame(duelo.getDuelistaUno(), duelo.getActual());
    }

    @Test
    void ganarPasaAlSiguienteRivalYDaUnaRecompensa() {
        JugadorDePrueba jugador = new JugadorDePrueba();
        Campania campania = campania(jugador);

        Recompensa recompensa = campania.registrarResultado(dueloGanado(campania), new Random(42));

        int id = recompensa.getCarta().getId();
        assertTrue(id >= 41 && id <= 50, "La recompensa del rival 1 está entre 41 y 50: " + id);
        assertTrue(recompensa.fueAgregada());
        assertSame(recompensa.getCarta(), jugador.ultimaRecibida);
        assertEquals(1, jugador.getRivalesVencidos());
        assertEquals(2, campania.getRivalActual().getNumero());
        assertInstanceOf(IARival2.class, campania.estrategiaDelRivalActual());
    }

    @Test
    void perderRepiteElMismoRivalSinRecompensa() {
        JugadorDePrueba jugador = new JugadorDePrueba();
        Campania campania = campania(jugador);

        Recompensa recompensa = campania.registrarResultado(dueloPerdido(campania), new Random(42));

        assertNull(recompensa);
        assertEquals(0, jugador.recibidas);
        assertEquals(0, jugador.getRivalesVencidos());
        assertEquals(1, campania.getRivalActual().getNumero());
        assertEquals(3, campania.rivalesRestantes());
    }

    @Test
    void ganarLos3DuelosTerminaLaCampania() {
        JugadorDePrueba jugador = new JugadorDePrueba();
        Campania campania = campania(jugador);

        for (int i = 0; i < Jugador.TOTAL_RIVALES; i++) {
            campania.registrarResultado(dueloGanado(campania), new Random(i));
        }

        assertTrue(campania.estaCompleta());
        assertTrue(jugador.completoModoHistoria());
        assertEquals(3, jugador.recibidas);
        assertThrows(IllegalStateException.class, campania::getRivalActual);
    }

    @Test
    void conRecompensaRepetidaSeInformaQueNoSeAgrego() {
        JugadorDePrueba jugador = new JugadorDePrueba();
        jugador.responder = false;
        Campania campania = campania(jugador);

        Recompensa recompensa = campania.registrarResultado(dueloGanado(campania), new Random(42));

        assertFalse(recompensa.fueAgregada());
        assertTrue(recompensa.mensaje().contains("ya tienes 3 copias"), recompensa.mensaje());
        assertEquals(1, jugador.getRivalesVencidos(), "El rival cuenta como vencido igual");
    }

    // Límite

    @Test
    void conRivalesYaVencidosEmpiezaEnElSiguiente() {
        JugadorDePrueba jugador = new JugadorDePrueba();
        jugador.registrarRivalVencido();
        jugador.registrarRivalVencido();

        Campania campania = campania(jugador);

        assertEquals(3, campania.getRivalActual().getNumero());
        assertEquals(1, campania.rivalesRestantes());
        assertInstanceOf(IARival3.class, campania.estrategiaDelRivalActual());
    }

    @Test
    void conLos3VencidosLaCampaniaYaEstaCompleta() {
        JugadorDePrueba jugador = new JugadorDePrueba();
        for (int i = 0; i < Jugador.TOTAL_RIVALES; i++) {
            jugador.registrarRivalVencido();
        }

        Campania campania = campania(jugador);

        assertTrue(campania.estaCompleta());
        assertThrows(IllegalStateException.class,
                () -> campania.crearDuelo(deckDe(carta(1, 1000, 0)), new Random(42)));
    }

    // Error

    @Test
    void registrarUnDueloSinTerminarLanzaExcepcion() {
        Campania campania = campania(new JugadorDePrueba());
        Duelo enCurso = campania.crearDuelo(deckDe(carta(1, 1000, 0)), new Random(42));

        assertThrows(IllegalStateException.class, () -> campania.registrarResultado(enCurso, new Random(42)));
        assertThrows(IllegalArgumentException.class, () -> campania.registrarResultado(null, new Random(42)));
        assertThrows(IllegalArgumentException.class, () -> campania.registrarResultado(enCurso, null));
    }

    @Test
    void datosInvalidosLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Campania(null, catalogoUniforme(1, 1)));
        assertThrows(IllegalArgumentException.class, () -> new Campania(new JugadorDePrueba(), null));
        assertThrows(IllegalArgumentException.class, () -> Campania.estrategiaPara(0));
        assertThrows(IllegalArgumentException.class, () -> Campania.estrategiaPara(4));
    }
}
