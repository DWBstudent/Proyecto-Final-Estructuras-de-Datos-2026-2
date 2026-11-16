package co.edu.unbosque.modelo.estructuras;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Comprueba el almacenamiento, las colisiones y la ampliación de la tabla hash.
 *
 * <p>
 * Responsable: Brayan.
 */
class TablaHashTest {

	/** Comprueba que actualizar una contraseña conserva una sola entrada. */
	@Test
	void reemplazaElValorSinDuplicarLaClave() {
		TablaHash<String, Integer> tabla = new TablaHash<>();
		tabla.poner("00001234", 25);
		tabla.poner("00001234", 42);
		assertEquals(1, tabla.tamanio());
		assertEquals(Integer.valueOf(42), tabla.obtener("00001234"));
		assertTrue(tabla.contiene("00001234"));
	}

	/** Comprueba que eliminar una clave conserva la otra entrada de su cubeta. */
	@Test
	void conservaLaOtraClaveDespuesDeUnaColision() {
		TablaHash<String, Integer> tabla = new TablaHash<>();
		tabla.poner("Aa", 1);
		tabla.poner("BB", 2);
		assertEquals(Integer.valueOf(1), tabla.obtener("Aa"));
		assertEquals(Integer.valueOf(2), tabla.obtener("BB"));
		assertEquals(Integer.valueOf(1), tabla.eliminar("Aa"));
		assertFalse(tabla.contiene("Aa"));
		assertNull(tabla.eliminar("Aa"));
		assertEquals(Integer.valueOf(2), tabla.obtener("BB"));
		assertEquals(1, tabla.tamanio());
	}

	/** Comprueba que los valores siguen accesibles después de ampliar la tabla. */
	@Test
	void conservaLosValoresDespuesDeAmpliarse() {
		TablaHash<Integer, String> tabla = new TablaHash<>();

		for (int clave = 0; clave < 100; clave++) {
			tabla.poner(clave, "Carta " + clave);
		}

		assertEquals(100, tabla.tamanio());
		for (int clave = 0; clave < 100; clave++) {
			assertEquals("Carta " + clave, tabla.obtener(clave));
		}
	}

	/** Comprueba que las inserciones con datos nulos dejan la tabla vacía. */
	@Test
	void rechazaClavesYValoresNulos() {
		TablaHash<String, Integer> tabla = new TablaHash<>();
		assertThrows(IllegalArgumentException.class, () -> tabla.poner(null, 1));
		assertThrows(IllegalArgumentException.class, () -> tabla.poner("carta", null));
		assertTrue(tabla.estaVacia());
		assertEquals(0, tabla.tamanio());
	}
}