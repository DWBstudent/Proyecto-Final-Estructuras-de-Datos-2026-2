package co.edu.unbosque.modelo.juego;

import static co.edu.unbosque.modelo.juego.CartasDePrueba.catalogoVariado;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.entidades.Carta;

class DeckAleatorioTest {

    private static int[] copiasPorId(Carta[] deck) {
        int[] copias = new int[Carta.ID_MAXIMO + 1];
        for (Carta carta : deck) {
            copias[carta.getId()]++;
        }
        return copias;
    }

    // Caso normal

    @Test
    void tiene40CartasConMaximo3CopiasDeCadaUna() {
        for (long semilla = 1; semilla <= 50; semilla++) {
            Carta[] deck = DeckAleatorio.generar(catalogoVariado(), new Random(semilla));

            assertEquals(Deck.TAMANIO, deck.length);
            int[] copias = copiasPorId(deck);
            for (int id = 1; id <= Carta.ID_MAXIMO; id++) {
                assertTrue(copias[id] <= Chest.MAXIMO_COPIAS,
                        "Semilla " + semilla + ": " + copias[id] + " copias de la carta " + id);
            }
            for (Carta carta : deck) {
                assertNotNull(carta);
            }
        }
    }

    @Test
    void conLaMismaSemillaSaleElMismoDeck() {
        Catalogo catalogo = catalogoVariado();
        Carta[] uno = DeckAleatorio.generar(catalogo, new Random(42));
        Carta[] dos = DeckAleatorio.generar(catalogo, new Random(42));

        for (int i = 0; i < uno.length; i++) {
            assertSame(uno[i], dos[i]);
        }
    }

    @Test
    void conOtraSemillaSaleOtroDeck() {
        Catalogo catalogo = catalogoVariado();
        Carta[] uno = DeckAleatorio.generar(catalogo, new Random(1));
        Carta[] dos = DeckAleatorio.generar(catalogo, new Random(2));

        boolean distinto = false;
        for (int i = 0; i < uno.length; i++) {
            if (uno[i] != dos[i]) {
                distinto = true;
            }
        }
        assertTrue(distinto);
    }

    // Límite

    @Test
    void puedenSalirCartasDeTodoElCatalogo() {
        boolean[] salio = new boolean[Carta.ID_MAXIMO + 1];
        for (long semilla = 1; semilla <= 100; semilla++) {
            for (Carta carta : DeckAleatorio.generar(catalogoVariado(), new Random(semilla))) {
                salio[carta.getId()] = true;
            }
        }

        assertTrue(salio[1], "Debe poder salir la primera carta");
        assertTrue(salio[Carta.ID_MAXIMO], "Debe poder salir la última carta");
        int distintas = 0;
        for (int id = 1; id <= Carta.ID_MAXIMO; id++) {
            if (salio[id]) {
                distintas++;
            }
        }
        assertTrue(distintas > 90, "En 100 Decks salieron solo " + distintas + " cartas distintas");
    }

    // Error

    @Test
    void sinCatalogoOSinRandomLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> DeckAleatorio.generar(null, new Random(42)));
        assertThrows(IllegalArgumentException.class, () -> DeckAleatorio.generar(catalogoVariado(), null));
    }

    @Test
    void siAlCatalogoLeFaltaUnaCartaLanzaExcepcion() {
        Catalogo incompleto = new Catalogo() {
            @Override
            public Carta buscarPorId(int id) {
                return id == 57 ? null : CartasDePrueba.carta(id, 100, 100);
            }
        };

        assertThrows(IllegalStateException.class, () -> DeckAleatorio.generar(incompleto, new Random(42)));
    }
}
