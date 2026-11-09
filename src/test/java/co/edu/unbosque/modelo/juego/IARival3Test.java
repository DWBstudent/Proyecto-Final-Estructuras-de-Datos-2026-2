package co.edu.unbosque.modelo.juego;

import static co.edu.unbosque.modelo.juego.CartasDePrueba.carta;
import static co.edu.unbosque.modelo.juego.CartasDePrueba.deckDe;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

/** En estas pruebas el duelista uno es una persona (se juega a mano) y el dos es la IA. */
class IARival3Test {

    /**
     * Humano e IA con cartas de ATK 2000. La IA tiene 1 monstruo en ataque y el humano 1.
     * Queda en el turno 4, el de la IA.
     */
    private static Duelo empateDeAtaques() {
        Duelo duelo = new Duelo("Humano", deckDe(carta(1, 2000, 0)), "IA", deckDe(carta(2, 2000, 0)),
                new Random(42));
        duelo.terminarTurno();
        duelo.invocar(0, 0, Posicion.ATAQUE);       // IA, a mano, sin atacar
        duelo.terminarTurno();
        duelo.invocar(0, 0, Posicion.ATAQUE);       // humano, sin atacar
        duelo.terminarTurno();
        return duelo;
    }

    // Caso normal

    @Test
    void cambiaUnMonstruoPorElUltimoDelRivalParaAtacarDirecto() {
        Duelo duelo = empateDeAtaques();

        new IARival3().jugarTurno(duelo);

        // Invoca un 2.º monstruo; uno empata con el del humano (ambos destruidos) y el otro
        // ataca directo.
        assertFalse(duelo.getDuelistaUno().tieneMonstruos());
        assertEquals(1, duelo.getDuelistaDos().monstruosEnCampo());
        assertEquals(6000, duelo.getDuelistaUno().getPuntosDeVida());
    }

    @Test
    void elRival2EnLaMismaSituacionNoAtaca() {
        Duelo duelo = empateDeAtaques();

        new IARival2().jugarTurno(duelo);

        assertTrue(duelo.getDuelistaUno().tieneMonstruos());
        assertEquals(2, duelo.getDuelistaDos().monstruosEnCampo());
        assertEquals(8000, duelo.getDuelistaUno().getPuntosDeVida());
    }

    // Límite

    @Test
    void elBonoSoloSeDaSiDestruyeAlUltimoMonstruo() {
        Duelo duelo = new Duelo("Humano", deckDe(carta(1, 1000, 0)), "IA", deckDe(carta(2, 2000, 0)),
                new Random(42));
        Duelista humano = duelo.getActual();
        duelo.invocar(0, 0, Posicion.ATAQUE);
        IARival3 ia = new IARival3();
        ResultadoCombate destruye = Combate.resolver(carta(2, 2000, 0), humano.getMonstruo(0));

        assertEquals(1000 + 1000 + IARival3.BONO_POR_DEJAR_SIN_MONSTRUOS, ia.puntaje(destruye, humano));

        duelo.terminarTurno();
        duelo.terminarTurno();
        duelo.invocar(0, 1, Posicion.ATAQUE);

        assertEquals(1000 + 1000, ia.puntaje(destruye, humano), "Con 2 monstruos no hay bono");
    }
}
