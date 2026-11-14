package co.edu.unbosque.modelo.estructuras;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Pruebas de inserción, búsqueda y eliminación en la tabla hash. */
class TablaHashTest {

	// Caso normal

	@Test
	void reemplazaElValorSinDuplicarLaClave() {
		TablaHash<String, Integer> tabla = new TablaHash<>();
		tabla.poner("00001234", 25);
		tabla.poner("00001234", 42);

		assertEquals(1, tabla.tamanio());
		assertEquals(Integer.valueOf(42), tabla.obtener("00001234"));
	}

	// Casos límite

	@Test
	void conservaLaOtraClaveAlEliminarUnaColision() {
		TablaHash<String, Integer> tabla = new TablaHash<>();

		// En Java, "Aa" y "BB" tienen el mismo hashCode.
		tabla.poner("Aa", 1);
		tabla.poner("BB", 2);

		assertEquals(Integer.valueOf(1), tabla.obtener("Aa"));
		assertEquals(Integer.valueOf(2), tabla.obtener("BB"));
		assertEquals(Integer.valueOf(1), tabla.eliminar("Aa"));
		assertFalse(tabla.contiene("Aa"));
		assertEquals(Integer.valueOf(2), tabla.obtener("BB"));
		assertEquals(1, tabla.tamanio());
	}

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

	// Caso de error

	@Test
	void rechazaUnaClaveOUnValorNulo() {
		TablaHash<String, Integer> tabla = new TablaHash<>();

		assertThrows(IllegalArgumentException.class, () -> tabla.poner(null, 1));
		assertThrows(IllegalArgumentException.class, () -> tabla.poner("carta", null));
		assertTrue(tabla.estaVacia());
	}
}