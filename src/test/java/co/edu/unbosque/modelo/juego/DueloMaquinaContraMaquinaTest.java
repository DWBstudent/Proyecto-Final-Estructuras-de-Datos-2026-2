package co.edu.unbosque.modelo.juego;

import static co.edu.unbosque.modelo.juego.CartasDePrueba.catalogoVariado;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.entidades.Contrincante;
import co.edu.unbosque.modelo.entidades.Jugador;

/**
 * Prueba del hito H2: dos IA juegan un duelo completo, sin nadie que intervenga, hasta que
 * hay un ganador. Los Decks son los de los 3 rivales del modo historia.
 */
class DueloMaquinaContraMaquinaTest {

    /** Tope de seguridad: un duelo con Decks de 40 cartas termina mucho antes. */
    private static final int TOPE_DE_TURNOS = 1000;

    private static final Catalogo CATALOGO = catalogoVariado();

    /** Juega el duelo completo entre los rivales {@code uno} y {@code dos} y lo devuelve terminado. */
    private static Duelo jugar(int uno, int dos, long semilla) {
        Contrincante rivalUno = Contrincante.crear(uno, CATALOGO);
        Contrincante rivalDos = Contrincante.crear(dos, CATALOGO);
        Duelo duelo = new Duelo(rivalUno.getNombre() + " (1)", rivalUno.getDeck(),
                rivalDos.getNombre() + " (2)", rivalDos.getDeck(), new Random(semilla));
        EstrategiaIA iaUno = Campania.estrategiaPara(uno);
        EstrategiaIA iaDos = Campania.estrategiaPara(dos);

        while (!duelo.estaTerminado() && duelo.getNumeroDeTurno() <= TOPE_DE_TURNOS) {
            EstrategiaIA ia = duelo.getActual() == duelo.getDuelistaUno() ? iaUno : iaDos;
            ia.jugarTurno(duelo);
            if (!duelo.estaTerminado()) {
                duelo.terminarTurno();
            }
        }
        return duelo;
    }

    @Test
    void todasLasCombinacionesDeRivalesTerminanConUnGanador() {
        for (int uno = 1; uno <= Jugador.TOTAL_RIVALES; uno++) {
            for (int dos = 1; dos <= Jugador.TOTAL_RIVALES; dos++) {
                for (long semilla = 1; semilla <= 5; semilla++) {
                    Duelo duelo = jugar(uno, dos, semilla);
                    String caso = "Rival " + uno + " contra rival " + dos + ", semilla " + semilla;

                    assertTrue(duelo.estaTerminado(), caso + ": no terminó en " + TOPE_DE_TURNOS + " turnos");
                    assertNotNull(duelo.getGanador(), caso);
                    Duelista perdedor = duelo.getGanador() == duelo.getDuelistaUno()
                            ? duelo.getDuelistaDos() : duelo.getDuelistaUno();
                    assertTrue(perdedor.getPuntosDeVida() == 0 || perdedor.cartasEnMazo() == 0,
                            caso + ": el perdedor debe quedar sin puntos o sin mazo");
                    assertTrue(duelo.getGanador().getPuntosDeVida() > 0, caso);
                }
            }
        }
    }

    @Test
    void elRival3LeGanaAlRival1LaMayoriaDeLasVeces() {
        int victoriasDelRival3 = 0;
        int duelos = 0;
        for (long semilla = 1; semilla <= 10; semilla++) {
            Duelo empiezaEl3 = jugar(3, 1, semilla);
            Duelo empiezaEl1 = jugar(1, 3, semilla);
            if (empiezaEl3.getGanador() == empiezaEl3.getDuelistaUno()) {
                victoriasDelRival3++;
            }
            if (empiezaEl1.getGanador() == empiezaEl1.getDuelistaDos()) {
                victoriasDelRival3++;
            }
            duelos += 2;
        }

        assertTrue(victoriasDelRival3 > duelos / 2,
                "El rival 3 ganó " + victoriasDelRival3 + " de " + duelos);
    }
}
