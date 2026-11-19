package co.edu.unbosque.modelo.entidades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.juego.Catalogo;

class CanjeContrasenaTest {

	private static Carta carta(int id, String contrasena) {
		return new Carta(id, "Carta " + id, TipoCarta.DRAGON, 1500, 1200, "", "", contrasena);
	}

	private static final Catalogo CATALOGO = new Catalogo(
			new Carta[] { carta(1, "11111111"), carta(2, "22222222"), carta(100, "99999999") });

	// Caso normal

	@Test
	void unaContrasenaValidaEntregaSuCartaAlCofre() {
		Jugador jugador = new Jugador("Daniel");

		assertEquals(ResultadoCanje.CANJEADA, jugador.canjearContrasena("22222222", CATALOGO));
		assertEquals(1, jugador.getChest().copiasDe(2));
		assertTrue(jugador.estaDescubierta(2));
		assertTrue(jugador.contrasenaCanjeada(2));
		assertFalse(jugador.contrasenaCanjeada(1));
	}

	@Test
	void cadaContrasenaSirveUnaSolaVez() {
		Jugador jugador = new Jugador("Daniel");
		jugador.canjearContrasena("11111111", CATALOGO);

		assertEquals(ResultadoCanje.YA_USADA, jugador.canjearContrasena("11111111", CATALOGO));
		assertEquals(1, jugador.getChest().copiasDe(1));
	}

	@Test
	void canjeaUnaContrasenaDelArchivoReal() {
		Catalogo real = Catalogo.cargar();
		Carta ultima = real.buscarPorId(100);
		Jugador jugador = new Jugador("Daniel");

		assertEquals(ResultadoCanje.CANJEADA, jugador.canjearContrasena(ultima.getContrasena(), real));
		assertEquals(1, jugador.getChest().copiasDe(100));
	}

	// Caso límite

	@Test
	void conTresCopiasNoSeGastaLaContrasena() {
		Jugador jugador = new Jugador("Daniel");
		for (int i = 0; i < 3; i++) {
			jugador.recibirCarta(CATALOGO.buscarPorId(100));
		}

		assertEquals(ResultadoCanje.SIN_ESPACIO, jugador.canjearContrasena("99999999", CATALOGO));
		assertEquals(3, jugador.getChest().copiasDe(100));
		assertFalse(jugador.contrasenaCanjeada(100));
	}

	@Test
	void ignoraLosEspaciosAlrededor() {
		Jugador jugador = new Jugador("Daniel");

		assertEquals(ResultadoCanje.CANJEADA, jugador.canjearContrasena("  11111111 ", CATALOGO));
	}

	// Caso de error

	@Test
	void unaContrasenaQueNoExisteNoEntregaNada() {
		Jugador jugador = new Jugador("Daniel");

		assertEquals(ResultadoCanje.NO_EXISTE, jugador.canjearContrasena("12345678", CATALOGO));
		assertEquals(ResultadoCanje.NO_EXISTE, jugador.canjearContrasena("abc", CATALOGO));
		assertEquals(ResultadoCanje.NO_EXISTE, jugador.canjearContrasena("", CATALOGO));
		assertEquals(ResultadoCanje.NO_EXISTE, jugador.canjearContrasena(null, CATALOGO));
		assertEquals(0, jugador.getCantidadDescubiertas());
	}

	@Test
	void rechazaCatalogoNuloEIdsFueraDeRango() {
		Jugador jugador = new Jugador("Daniel");

		assertThrows(IllegalArgumentException.class, () -> jugador.canjearContrasena("11111111", null));
		assertThrows(IllegalArgumentException.class, () -> jugador.contrasenaCanjeada(0));
		assertThrows(IllegalArgumentException.class, () -> jugador.contrasenaCanjeada(101));
	}
}