package co.edu.unbosque.modelo.juego;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import co.edu.unbosque.modelo.entidades.Carta;

/** Pruebas de las copias, la capacidad y la preparación de la baraja. */
class DeckTest {

	// Caso normal

	@Test
	void agregaYQuitaUnaCopiaDeLaBaraja() {
		Deck deck = new Deck();
		Carta carta = CartasDePrueba.carta(1, 800, 500);

		assertTrue(deck.agregar(carta));
		assertTrue(deck.agregar(carta));
		assertTrue(deck.quitar(carta.getId()));
		assertEquals(1, deck.tamanio());
		assertEquals(1, deck.copiasDe(carta.getId()));
		assertEquals(carta, deck.cartasActuales()[0]);
	}

	// Casos límite

	@Test
	void admiteCuarentaCartasYEntregaUnArregloIndependiente() {
		Deck deck = new Deck();

		for (int id = 1; id <= Deck.TAMANIO; id++) {
			assertTrue(deck.agregar(CartasDePrueba.carta(id, 800, 500)));
		}

		assertTrue(deck.estaCompleto());
		assertFalse(deck.agregar(CartasDePrueba.carta(41, 900, 600)));

		Carta[] copia = deck.aArreglo();
		assertEquals(Deck.TAMANIO, copia.length);

		// Cambiar el arreglo que recibe el duelo debe dejar intacta la baraja.
		copia[0] = null;
		assertEquals(1, deck.aArreglo()[0].getId());
		assertTrue(deck.quitar(1));
		assertFalse(deck.estaCompleto());
		assertEquals(39, deck.tamanio());
	}

	@Test
	void limitaLasCopiasDeUnaMismaCarta() {
		Deck deck = new Deck();
		Carta carta = CartasDePrueba.carta(1, 800, 500);

		for (int copia = 0; copia < Chest.MAXIMO_COPIAS; copia++) {
			assertTrue(deck.agregar(carta));
		}

		assertFalse(deck.agregar(carta));
		assertEquals(Chest.MAXIMO_COPIAS, deck.copiasDe(carta.getId()));
	}

	// Casos de error

	@Test
	void rechazaPrepararUnaBarajaIncompletaParaElDuelo() {
		Deck deck = new Deck();
		assertTrue(deck.agregar(CartasDePrueba.carta(1, 800, 500)));
		assertEquals(1, deck.cartasActuales().length);
		assertThrows(IllegalStateException.class, deck::aArreglo);
	}

	@Test
	void rechazaUnaCartaNula() {
		Deck deck = new Deck();
		assertThrows(IllegalArgumentException.class, () -> deck.agregar(null));
		assertEquals(0, deck.tamanio());
	}
}