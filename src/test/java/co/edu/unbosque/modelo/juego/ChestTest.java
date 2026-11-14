package co.edu.unbosque.modelo.juego;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.entidades.Carta;

/** Pruebas del conteo y del límite de copias dentro del cofre. */
class ChestTest {

	// Caso normal

	@Test
	void quitarUnaCopiaConservaLasOtrasCartasDelCofre() {
		Chest chest = new Chest();
		Carta primera = CartasDePrueba.carta(1, 800, 500);
		Carta segunda = CartasDePrueba.carta(2, 900, 400);
		assertTrue(chest.agregar(primera));
		assertTrue(chest.agregar(primera));
		assertTrue(chest.agregar(segunda));
		assertEquals(2, chest.cartasDistintas());
		assertTrue(chest.quitar(primera.getId()));
		assertEquals(1, chest.copiasDe(primera.getId()));
		assertEquals(2, chest.cartasDistintas());
		assertTrue(chest.quitar(primera.getId()));
		assertEquals(0, chest.copiasDe(primera.getId()));
		assertEquals(1, chest.copiasDe(segunda.getId()));
		assertEquals(1, chest.cartasDistintas());
		assertFalse(chest.quitar(primera.getId()));
	}

	// Caso límite

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

	// Caso de error

	@Test
	void rechazaUnaCartaNula() {
		Chest chest = new Chest();
		assertThrows(IllegalArgumentException.class, () -> chest.agregar(null));
		assertEquals(0, chest.cartasDistintas());
	}
}