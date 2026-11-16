package co.edu.unbosque.modelo.entidades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import co.edu.unbosque.modelo.juego.Deck;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JugadorTest {

	// Caso normal

	@Test
	void empiezaSinCartasDescubiertasNiRivalesVencidos() {
		Jugador jugador = new Jugador("Daniel");

		assertEquals(0, jugador.getCantidadDescubiertas());
		assertFalse(jugador.estaDescubierta(1));
		assertEquals(0, jugador.getRivalesVencidos());
		assertFalse(jugador.completoModoHistoria());
	}

	@Test
	void marcaCartasComoDescubiertas() {
		Jugador jugador = new Jugador("Daniel");
		jugador.marcarDescubierta(7);

		assertTrue(jugador.estaDescubierta(7));
		assertFalse(jugador.estaDescubierta(8));
		assertEquals(1, jugador.getCantidadDescubiertas());
	}

	// Caso límite

	@Test
	void funcionaConLosIdsDeLosExtremos() {
		Jugador jugador = new Jugador("Daniel");
		jugador.marcarDescubierta(1);
		jugador.marcarDescubierta(100);

		assertTrue(jugador.estaDescubierta(1));
		assertTrue(jugador.estaDescubierta(100));
		assertEquals(2, jugador.getCantidadDescubiertas());
	}

	@Test
	void marcarDosVecesNoCuentaDoble() {
		Jugador jugador = new Jugador("Daniel");
		jugador.marcarDescubierta(3);
		jugador.marcarDescubierta(3);

		assertEquals(1, jugador.getCantidadDescubiertas());
	}

	@Test
	void completaElModoHistoriaAlVencerTresRivales() {
		Jugador jugador = new Jugador("Daniel");
		jugador.registrarRivalVencido();
		jugador.registrarRivalVencido();
		jugador.registrarRivalVencido();

		assertTrue(jugador.completoModoHistoria());
	}

	// Caso de error

	@Test
	void rechazaIdsFueraDeRango() {
		Jugador jugador = new Jugador("Daniel");
		assertThrows(IllegalArgumentException.class, () -> jugador.estaDescubierta(0));
		assertThrows(IllegalArgumentException.class, () -> jugador.marcarDescubierta(101));
	}

	@Test
	void noPuedeVencerUnCuartoRival() {
		Jugador jugador = new Jugador("Daniel");
		jugador.registrarRivalVencido();
		jugador.registrarRivalVencido();
		jugador.registrarRivalVencido();
		assertThrows(IllegalStateException.class, jugador::registrarRivalVencido);
	}

	@Test
	void rechazaNombreVacioYCartaNula() {
		assertThrows(IllegalArgumentException.class, () -> new Jugador(" "));
		Jugador jugador = new Jugador("Daniel");
		assertThrows(IllegalArgumentException.class, () -> jugador.recibirCarta(null));
	}

	@Test
	void moverLaUltimaCopiaAlDeckYDevolverlaActualizaElIndiceDelChest() {
		Jugador jugador = new Jugador("Brayan");
		Carta carta = cartaParaTraslado(1);
		assertTrue(jugador.recibirCarta(carta));
		assertSame(carta, jugador.getChest().buscarPorId(1));
		assertTrue(jugador.moverCartaAlDeck(1));
		assertEquals(0, jugador.getChest().copiasDe(1));
		assertEquals(0, jugador.getChest().cartasDistintas());
		assertNull(jugador.getChest().buscarPorId(1));
		assertEquals(1, jugador.getDeck().copiasDe(1));
		assertTrue(jugador.moverCartaAlChest(1));
		assertEquals(1, jugador.getChest().copiasDe(1));
		assertEquals(1, jugador.getChest().cartasDistintas());
		assertSame(carta, jugador.getChest().buscarPorId(1));
		assertEquals(0, jugador.getDeck().copiasDe(1));
	}

	@Test
	void deckLlenoDejaLaCartaEnElCofre() {
		Jugador jugador = new Jugador("Brayan");
		for (int id = 1; id <= Deck.TAMANIO; id++) {
			assertTrue(jugador.recibirCarta(cartaParaTraslado(id)));
			assertTrue(jugador.moverCartaAlDeck(id));
		}

		Carta cartaAdicional = cartaParaTraslado(Deck.TAMANIO + 1);
		int idAdicional = cartaAdicional.getId();
		assertTrue(jugador.recibirCarta(cartaAdicional));
		assertFalse(jugador.moverCartaAlDeck(idAdicional));
		assertTrue(jugador.getDeck().estaCompleto());
		assertEquals(Deck.TAMANIO, jugador.getDeck().tamanio());
		assertEquals(0, jugador.getDeck().copiasDe(idAdicional));
		assertEquals(1, jugador.getChest().copiasDe(idAdicional));
		assertSame(cartaAdicional, jugador.getChest().buscarPorId(idAdicional));
	}

	@Test
	void rechazaLaCuartaCopiaAlSumarCofreYBaraja() {
		Jugador jugador = new Jugador("Brayan");
		Carta carta = cartaParaTraslado(1);
		assertTrue(jugador.recibirCarta(carta));
		assertTrue(jugador.recibirCarta(carta));
		assertTrue(jugador.recibirCarta(carta));
		assertTrue(jugador.moverCartaAlDeck(1));
		assertTrue(jugador.moverCartaAlDeck(1));
		assertFalse(jugador.recibirCarta(carta));
		assertEquals(1, jugador.getChest().copiasDe(1));
		assertEquals(2, jugador.getDeck().copiasDe(1));
		assertEquals(1, jugador.getCantidadDescubiertas());
	}

	/**
	 * Crea una carta con un ID y una contraseña válidos para probar los traslados.
	 *
	 * @param id identificador de la carta dentro del catálogo
	 * @return carta de prueba
	 */
	private static Carta cartaParaTraslado(int id) {
		return new Carta(id, "Carta " + id, TipoCarta.GUERRERO, 800, 500, "", "", String.format("%08d", id));
	}
}
