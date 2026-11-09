package co.edu.unbosque.modelo.entidades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.juego.Catalogo;
import co.edu.unbosque.modelo.juego.Chest;
import co.edu.unbosque.modelo.juego.Deck;

class ContrincanteTest {

    /** Catálogo de prueba con las 100 cartas; el ATK crece con el id. */
    private static Catalogo catalogoDePrueba() {
        Carta[] cartas = new Carta[Carta.ID_MAXIMO + 1];
        for (int id = 1; id <= Carta.ID_MAXIMO; id++) {
            cartas[id] = new Carta(id, "Carta " + id, TipoCarta.GUERRERO, id * 30, id * 20, "", "",
                    String.format("%08d", id));
        }
        return new Catalogo() {
            @Override
            public Carta buscarPorId(int id) {
                return cartas[id];
            }
        };
    }

    private static int promedioDeAtaque(Carta[] cartas) {
        int suma = 0;
        for (Carta carta : cartas) {
            suma += carta.getAtaque();
        }
        return suma / cartas.length;
    }

    // Caso normal

    @Test
    void cadaRivalTieneUnDeckDe40ConMaximo3Copias() {
        Catalogo catalogo = catalogoDePrueba();
        for (int numero = 1; numero <= Jugador.TOTAL_RIVALES; numero++) {
            Contrincante rival = Contrincante.crear(numero, catalogo);
            Carta[] deck = rival.getDeck();

            assertEquals(numero, rival.getNumero());
            assertEquals(Deck.TAMANIO, deck.length);
            int[] copias = new int[Carta.ID_MAXIMO + 1];
            for (Carta carta : deck) {
                assertNotNull(carta);
                copias[carta.getId()]++;
                assertTrue(copias[carta.getId()] <= Chest.MAXIMO_COPIAS,
                        "Rival " + numero + " tiene más de 3 copias de la carta " + carta.getId());
            }
        }
    }

    @Test
    void cadaRivalEsMasFuerteQueElAnterior() {
        Catalogo catalogo = catalogoDePrueba();
        int uno = promedioDeAtaque(Contrincante.crear(1, catalogo).getDeck());
        int dos = promedioDeAtaque(Contrincante.crear(2, catalogo).getDeck());
        int tres = promedioDeAtaque(Contrincante.crear(3, catalogo).getDeck());

        assertTrue(uno < dos && dos < tres, "Promedios: " + uno + ", " + dos + ", " + tres);
    }

    @Test
    void lasRecompensasSonCartasQueNoEstanEnSuDeck() {
        Catalogo catalogo = catalogoDePrueba();
        for (int numero = 1; numero <= Jugador.TOTAL_RIVALES; numero++) {
            Contrincante rival = Contrincante.crear(numero, catalogo);
            Carta[] recompensas = rival.getRecompensas();

            assertEquals(10, recompensas.length);
            for (Carta recompensa : recompensas) {
                for (Carta delDeck : rival.getDeck()) {
                    assertTrue(recompensa.getId() != delDeck.getId());
                }
            }
        }
    }

    @Test
    void laRecompensaEsDeSuListaYSeRepiteConLaMismaSemilla() {
        Contrincante rival = Contrincante.crear(2, catalogoDePrueba());
        Random primero = new Random(42);
        Random segundo = new Random(42);

        for (int i = 0; i < 20; i++) {
            Carta recompensa = rival.elegirRecompensa(primero);
            assertSame(recompensa, rival.elegirRecompensa(segundo));
            assertTrue(recompensa.getId() >= 81 && recompensa.getId() <= 90);
        }
    }

    @Test
    void getDeckDevuelveUnaCopia() {
        Contrincante rival = Contrincante.crear(1, catalogoDePrueba());
        Carta[] deck = rival.getDeck();
        deck[0] = null;

        assertNotNull(rival.getDeck()[0]);
    }

    // Límite

    @Test
    void elRival1UsaLasPrimeras40CartasYElRival3LasUltimas() {
        Catalogo catalogo = catalogoDePrueba();
        for (Carta carta : Contrincante.crear(1, catalogo).getDeck()) {
            assertTrue(carta.getId() >= 1 && carta.getId() <= 40);
        }
        for (Carta carta : Contrincante.crear(3, catalogo).getRecompensas()) {
            assertTrue(carta.getId() >= 91 && carta.getId() <= Carta.ID_MAXIMO);
        }
    }

    // Error

    @Test
    void numeroFueraDe1A3LanzaExcepcion() {
        Catalogo catalogo = catalogoDePrueba();

        assertThrows(IllegalArgumentException.class, () -> Contrincante.crear(0, catalogo));
        assertThrows(IllegalArgumentException.class, () -> Contrincante.crear(4, catalogo));
        assertThrows(IllegalArgumentException.class, () -> Contrincante.crear(1, null));
    }

    @Test
    void siAlCatalogoLeFaltaUnaCartaLanzaExcepcion() {
        Catalogo incompleto = new Catalogo() {
            @Override
            public Carta buscarPorId(int id) {
                return null;
            }
        };

        assertThrows(IllegalStateException.class, () -> Contrincante.crear(1, incompleto));
    }

    @Test
    void elegirRecompensaSinRandomLanzaExcepcion() {
        Contrincante rival = Contrincante.crear(1, catalogoDePrueba());

        assertThrows(IllegalArgumentException.class, () -> rival.elegirRecompensa(null));
    }
}
