package co.edu.unbosque.modelo.juego;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import co.edu.unbosque.modelo.entidades.Carta;

/**
 * Comprueba las copias por ID, la capacidad y la preparación de la baraja para
 * el duelo.
 *
 * <p>
 * Responsable: Brayan.
 */
class DeckTest {

	/** Comprueba que quitar una copia deja disponible la otra. */
	@Test
	void agregaYQuitaUnaCopiaDeLaBaraja() {
		Deck deck = new Deck();
		Carta carta = CartasDePrueba.carta(1, 800, 500);
		assertTrue(deck.agregar(carta));
		assertTrue(deck.agregar(carta));
		assertTrue(deck.quitar(carta.getId()));
		assertEquals(1, deck.tamanio());
		assertEquals(1, deck.copiasDe(carta.getId()));
		assertSame(carta, deck.cartasActuales()[0]);
	}

	/**
	 * Comprueba el límite de 40 cartas y la independencia del arreglo del duelo.
	 */
	@Test
	void admiteCuarentaCartasYEntregaUnArregloIndependiente() {
		Deck deck = new Deck();

		for (int id = 1; id <= Deck.TAMANIO; id++) {
			assertTrue(deck.agregar(CartasDePrueba.carta(id, 800, 500)));
		}

		assertTrue(deck.estaCompleto());
		assertFalse(deck.agregar(CartasDePrueba.carta(Deck.TAMANIO + 1, 900, 600)));
		Carta[] copia = deck.aArreglo();
		assertEquals(Deck.TAMANIO, copia.length);
		assertEquals(1, copia[0].getId());
		assertEquals(Deck.TAMANIO, copia[copia.length - 1].getId());
		copia[0] = null;
		assertEquals(1, deck.aArreglo()[0].getId());
		assertTrue(deck.quitar(1));
		assertFalse(deck.estaCompleto());
		assertEquals(Deck.TAMANIO - 1, deck.tamanio());
	}

	/** Comprueba el máximo de copias permitido para un mismo ID. */
	@Test
	void limitaLasCopiasDeUnaMismaCarta() {
		Deck deck = new Deck();
		Carta carta = CartasDePrueba.carta(1, 800, 500);

		for (int copia = 0; copia < Chest.MAXIMO_COPIAS; copia++) {
			assertTrue(deck.agregar(carta));
		}

		assertFalse(deck.agregar(carta));
		assertEquals(Chest.MAXIMO_COPIAS, deck.copiasDe(carta.getId()));
		assertEquals(Chest.MAXIMO_COPIAS, deck.tamanio());
	}

	/**
	 * Comprueba que una baraja incompleta se puede consultar durante la edición.
	 */
	@Test
	void permiteConsultarUnaBarajaIncompletaYRechazaIniciarElDuelo() {
		Deck deck = new Deck();
		Carta carta = CartasDePrueba.carta(1, 800, 500);
		assertTrue(deck.agregar(carta));
		assertEquals(1, deck.cartasActuales().length);
		assertSame(carta, deck.cartasActuales()[0]);
		assertThrows(IllegalStateException.class, deck::aArreglo);
	}

	/** Comprueba que agregar una carta nula deja la baraja vacía. */
	@Test
	void rechazaUnaCartaNula() {
		Deck deck = new Deck();
		assertThrows(IllegalArgumentException.class, () -> deck.agregar(null));
		assertEquals(0, deck.tamanio());
	}
}