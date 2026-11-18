package co.edu.unbosque.modelo.estructuras;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Comprueba los enlaces, el ordenamiento y la copia al arreglo que utiliza la
 * baraja.
 *
 * <p>
 * Responsable: Brayan.
 */
class ListaSimpleTest {

	/** Comprueba los enlaces al quitar un nodo entre otros dos. */
	@Test
	void agregaEnAmbosExtremosYEliminaElElementoDelMedio() {
		ListaSimple<Integer> lista = new ListaSimple<>();
		lista.agregarAlFinal(20);
		lista.agregarAlInicio(10);
		lista.agregarAlFinal(30);
		assertEquals(Integer.valueOf(20), lista.eliminar(1));
		assertEquals(2, lista.tamanio());
		assertEquals(Integer.valueOf(10), lista.obtener(0));
		assertEquals(Integer.valueOf(30), lista.obtener(1));
	}

	/** Comprueba la estabilidad del ordenamiento y el enlace de la nueva cola. */
	@Test
	void ordenaSinAlterarLosEmpatesYPermiteAgregarAlFinal() {
		ListaSimple<String> lista = new ListaSimple<>();
		lista.agregarAlFinal("AA");
		lista.agregarAlFinal("BB");
		lista.agregarAlFinal("C");
		lista.ordenar((primero, segundo) -> Integer.compare(primero.length(), segundo.length()));
		lista.agregarAlFinal("DDD");
		assertEquals(4, lista.tamanio());
		assertEquals("C", lista.obtener(0));
		assertEquals("AA", lista.obtener(1));
		assertEquals("BB", lista.obtener(2));
		assertEquals("DDD", lista.obtener(3));
	}

	/** Comprueba que eliminar el único nodo permite reutilizar la lista. */
	@Test
	void permiteAgregarDespuesDeEliminarElUnicoElemento() {
		ListaSimple<Integer> lista = new ListaSimple<>();
		lista.agregarAlFinal(10);
		assertEquals(Integer.valueOf(10), lista.eliminar(0));
		assertTrue(lista.estaVacia());
		lista.agregarAlFinal(20);
		assertEquals(1, lista.tamanio());
		assertEquals(Integer.valueOf(20), lista.obtener(0));
	}

	/** Comprueba que copiar respeta el orden y las posiciones sobrantes. */
	@Test
	void copiaEnOrdenSinModificarLasPosicionesSobrantes() {
		ListaSimple<Integer> lista = new ListaSimple<>();
		lista.agregarAlFinal(10);
		lista.agregarAlFinal(20);
		Integer[] destino = { 0, 0, 99 };
		lista.copiarEn(destino);
		assertArrayEquals(new Integer[] { 10, 20, 99 }, destino);
		assertEquals(2, lista.tamanio());
	}

	/** Comprueba que los accesos y argumentos inválidos se rechazan. */
	@Test
	void rechazaElementosNulosIndicesInvalidosYArreglosPequenios() {
		ListaSimple<Integer> lista = new ListaSimple<>();
		assertThrows(IllegalArgumentException.class, () -> lista.agregarAlFinal(null));
		assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(0));
		lista.agregarAlInicio(10);
		assertThrows(IndexOutOfBoundsException.class, () -> lista.eliminar(1));
		assertThrows(IllegalArgumentException.class, () -> lista.copiarEn(new Integer[0]));
		assertEquals(1, lista.tamanio());
	}
}