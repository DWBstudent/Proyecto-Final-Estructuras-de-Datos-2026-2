package co.edu.unbosque.modelo.juego;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import co.edu.unbosque.modelo.entidades.Carta;

/**
 * Comprueba el manejo de copias y la actualización del índice AVL del cofre.
 *
 * <p>
 * Responsable: Brayan.
 */
class ChestTest {

	/** Comprueba que quitar una carta conserva las demás y su orden. */
	@Test
	void quitarUnaCartaConservaElOrdenDelResto() {
		Chest chest = new Chest();
		Carta primera = CartasDePrueba.carta(4, 800, 500);
		Carta segunda = CartasDePrueba.carta(9, 900, 400);
		Carta tercera = CartasDePrueba.carta(2, 700, 600);
		assertTrue(chest.agregar(primera));
		assertTrue(chest.agregar(segunda));
		assertTrue(chest.agregar(tercera));
		assertTrue(chest.quitar(segunda.getId()));
		assertFalse(chest.quitar(segunda.getId()));
		Carta[] restantes = chest.cartasActuales();
		assertEquals(2, restantes.length);
		assertSame(primera, restantes[0]);
		assertSame(tercera, restantes[1]);
		assertEquals(2, chest.cartasDistintas());
	}

	/** Comprueba que el AVL cambia de copia y elimina el ID al quedar vacío. */
	@Test
	void indiceDevuelveLaCopiaRestanteYSeLimpiaAlQuitarLaUltima() {
		Chest chest = new Chest();
		int id = 7;
		Carta primera = CartasDePrueba.carta(id, 800, 500);
		Carta segunda = CartasDePrueba.carta(id, 800, 500);
		assertTrue(chest.agregar(primera));
		assertTrue(chest.agregar(segunda));
		assertEquals(2, chest.copiasDe(id));
		assertEquals(1, chest.cartasDistintas());
		assertSame(primera, chest.buscarPorId(id));
		assertTrue(chest.quitar(id));
		assertEquals(1, chest.copiasDe(id));
		assertEquals(1, chest.cartasDistintas());
		assertSame(segunda, chest.buscarPorId(id));
		assertTrue(chest.quitar(id));
		assertEquals(0, chest.copiasDe(id));
		assertEquals(0, chest.cartasDistintas());
		assertNull(chest.buscarPorId(id));
	}

	/** Comprueba que el cofre acepta tres copias y rechaza la cuarta. */
	@Test
	void rechazaUnaCopiaCuandoAlcanzaElLimite() {
		Chest chest = new Chest();
		Carta carta = CartasDePrueba.carta(1, 800, 500);

		for (int copia = 0; copia < Chest.MAXIMO_COPIAS; copia++) {
			assertTrue(chest.agregar(carta));
		}

		assertFalse(chest.agregar(carta));
		assertEquals(Chest.MAXIMO_COPIAS, chest.copiasDe(carta.getId()));
		assertEquals(1, chest.cartasDistintas());
	}

	/** Comprueba que una carta nula se rechaza con el cofre vacío. */
	@Test
	void rechazaUnaCartaNula() {
		Chest chest = new Chest();

		assertThrows(IllegalArgumentException.class, () -> chest.agregar(null));
		assertEquals(0, chest.cartasDistintas());
		assertEquals(0, chest.cartasActuales().length);
	}
}