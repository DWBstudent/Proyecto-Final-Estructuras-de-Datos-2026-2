package co.edu.unbosque.modelo.ordenamiento;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * Comprueba el ordenamiento por inserción y el tratamiento de sus argumentos.
 *
 * <p>
 * Responsable: Brayan.
 */
class InsertionSortTest {

	/** Comprueba que ordena un arreglo desordenado de menor a mayor. */
	@Test
	void ordenaLosElementosEnSentidoAscendente() {
		Integer[] elementos = { 30, 10, 40, 20, 10 };
		InsertionSort.ordenar(elementos, Integer::compare);
		assertArrayEquals(new Integer[] { 10, 10, 20, 30, 40 }, elementos);
	}

	/** Comprueba que utiliza el criterio recibido para ordenar de mayor a menor. */
	@Test
	void permiteOrdenarEnSentidoDescendente() {
		Integer[] elementos = { 10, 40, 20, 30 };
		InsertionSort.ordenar(elementos, (primero, segundo) -> Integer.compare(segundo, primero));
		assertArrayEquals(new Integer[] { 40, 30, 20, 10 }, elementos);
	}

	/** Comprueba que un arreglo vacío se procesa correctamente. */
	@Test
	void aceptaUnArregloVacio() {
		Integer[] elementos = {};
		InsertionSort.ordenar(elementos, Integer::compare);
		assertEquals(0, elementos.length);
	}

	/** Comprueba que el único elemento conserva su posición. */
	@Test
	void aceptaUnSoloElemento() {
		Integer[] elementos = { 25 };
		InsertionSort.ordenar(elementos, Integer::compare);
		assertArrayEquals(new Integer[] { 25 }, elementos);
	}

	/** Comprueba que los elementos empatados conservan su orden original. */
	@Test
	void mantieneElOrdenOriginalDeLosEmpates() {
		String[] elementos = { "BBB", "AA", "CC", "D" };
		InsertionSort.ordenar(elementos, (primero, segundo) -> Integer.compare(primero.length(), segundo.length()));
		assertArrayEquals(new String[] { "D", "AA", "CC", "BBB" }, elementos);
	}

	/** Comprueba que un arreglo nulo produce una excepción controlada. */
	@Test
	void rechazaUnArregloNulo() {
		assertThrows(IllegalArgumentException.class, () -> InsertionSort.<Integer>ordenar(null, Integer::compare));
	}

	/**
	 * Comprueba que un comparador nulo se rechaza antes de modificar el arreglo.
	 */
	@Test
	void rechazaUnComparadorNulo() {
		Integer[] elementos = { 30, 10, 20 };
		assertThrows(IllegalArgumentException.class, () -> InsertionSort.ordenar(elementos, null));
		assertArrayEquals(new Integer[] { 30, 10, 20 }, elementos);
	}

	/** Comprueba que se validan todos los elementos antes de empezar a ordenar. */
	@Test
	void rechazaUnElementoNuloSinModificarElArreglo() {
		Integer[] elementos = { 30, 10, null };
		assertThrows(IllegalArgumentException.class, () -> InsertionSort.ordenar(elementos, Integer::compare));
		assertArrayEquals(new Integer[] { 30, 10, null }, elementos);
	}
}