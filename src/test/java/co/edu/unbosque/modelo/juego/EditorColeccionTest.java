package co.edu.unbosque.modelo.juego;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.Jugador;

/**
 * Comprueba el historial de traslados entre el cofre y la baraja.
 *
 * <p>
 * Responsable: Brayan.
 */
class EditorColeccionTest {

	/** Comprueba el ciclo completo de trasladar, deshacer y rehacer. */
	@Test
	void deshaceYRehaceUnTrasladoAlDeck() {
		Jugador jugador = new Jugador("Brayan");
		Carta carta = CartasDePrueba.carta(1, 800, 500);
		assertTrue(jugador.recibirCarta(carta));
		EditorColeccion editor = new EditorColeccion(jugador);
		assertTrue(editor.moverAlDeck(1));
		assertEquals(0, jugador.getChest().copiasDe(1));
		assertEquals(1, jugador.getDeck().copiasDe(1));
		assertTrue(editor.puedeDeshacer());
		assertTrue(editor.deshacer());
		assertSame(carta, jugador.getChest().buscarPorId(1));
		assertEquals(0, jugador.getDeck().copiasDe(1));
		assertFalse(editor.puedeDeshacer());
		assertTrue(editor.puedeRehacer());
		assertTrue(editor.rehacer());
		assertEquals(0, jugador.getChest().copiasDe(1));
		assertEquals(1, jugador.getDeck().copiasDe(1));
		assertTrue(editor.puedeDeshacer());
		assertFalse(editor.puedeRehacer());
	}

	/** Comprueba que varios movimientos se deshacen desde el último al primero. */
	@Test
	void deshaceLosTrasladosEnOrdenInverso() {
		Jugador jugador = new Jugador("Brayan");
		assertTrue(jugador.recibirCarta(CartasDePrueba.carta(1, 800, 500)));
		assertTrue(jugador.recibirCarta(CartasDePrueba.carta(2, 900, 600)));
		EditorColeccion editor = new EditorColeccion(jugador);
		assertTrue(editor.moverAlDeck(1));
		assertTrue(editor.moverAlDeck(2));
		assertTrue(editor.deshacer());
		assertEquals(1, jugador.getDeck().copiasDe(1));
		assertEquals(0, jugador.getDeck().copiasDe(2));
		assertTrue(editor.deshacer());
		assertEquals(0, jugador.getDeck().tamanio());
		assertEquals(2, jugador.getChest().cartasDistintas());
		assertFalse(editor.deshacer());
	}

	/** Comprueba que un traslado fallido conserva la opción de rehacer. */
	@Test
	void unTrasladoRechazadoConservaElHistorial() {
		Jugador jugador = new Jugador("Brayan");
		assertTrue(jugador.recibirCarta(CartasDePrueba.carta(1, 800, 500)));
		EditorColeccion editor = new EditorColeccion(jugador);
		assertTrue(editor.moverAlDeck(1));
		assertTrue(editor.deshacer());
		assertFalse(editor.moverAlDeck(99));
		assertTrue(editor.puedeRehacer());
		assertTrue(editor.rehacer());
	}

	/** Comprueba que un movimiento nuevo confirmado limpia la pila de rehacer. */
	@Test
	void unTrasladoNuevoLimpiaRehacer() {
		Jugador jugador = new Jugador("Brayan");
		assertTrue(jugador.recibirCarta(CartasDePrueba.carta(1, 800, 500)));
		assertTrue(jugador.recibirCarta(CartasDePrueba.carta(2, 900, 600)));
		EditorColeccion editor = new EditorColeccion(jugador);
		assertTrue(editor.moverAlDeck(1));
		assertTrue(editor.deshacer());
		assertTrue(editor.moverAlDeck(2));
		assertFalse(editor.puedeRehacer());
		assertFalse(editor.rehacer());
		assertEquals(1, jugador.getChest().copiasDe(1));
		assertEquals(1, jugador.getDeck().copiasDe(2));
	}

	/**
	 * Comprueba que limpiar el historial conserva las cartas en sus contenedores.
	 */
	@Test
	void limpiarHistorialConservaLaColeccion() {
		Jugador jugador = new Jugador("Brayan");
		assertTrue(jugador.recibirCarta(CartasDePrueba.carta(1, 800, 500)));
		EditorColeccion editor = new EditorColeccion(jugador);
		assertTrue(editor.moverAlDeck(1));
		editor.limpiarHistorial();
		assertFalse(editor.puedeDeshacer());
		assertFalse(editor.puedeRehacer());
		assertEquals(1, jugador.getDeck().copiasDe(1));
	}

	/** Comprueba los argumentos inválidos y las operaciones con historial vacío. */
	@Test
	void rechazaArgumentosInvalidosYControlaElHistorialVacio() {
		assertThrows(IllegalArgumentException.class, () -> new EditorColeccion(null));
		EditorColeccion editor = new EditorColeccion(new Jugador("Brayan"));
		assertFalse(editor.deshacer());
		assertFalse(editor.rehacer());
		assertThrows(IllegalArgumentException.class, () -> editor.moverAlDeck(0));
		assertThrows(IllegalArgumentException.class, () -> editor.moverAlChest(101));
		assertFalse(editor.puedeDeshacer());
	}
}