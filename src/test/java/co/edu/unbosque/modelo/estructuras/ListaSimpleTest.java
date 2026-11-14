package co.edu.unbosque.modelo.estructuras;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Pruebas de los enlaces y del ordenamiento de la lista simple. */
class ListaSimpleTest {

	// Casos normales

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

	@Test
	void ordenarConservaElOrdenDeLosEmpatesYActualizaLaCola() {
		ListaSimple<String> lista = new ListaSimple<>();
		lista.agregarAlFinal("AA");
		lista.agregarAlFinal("BB");
		lista.agregarAlFinal("C");

		// Al ordenar por longitud, "AA" y "BB" quedan empatados.
		lista.ordenar((primero, segundo) -> Integer.compare(primero.length(), segundo.length()));
		lista.agregarAlFinal("DDD");

		assertEquals(4, lista.tamanio());
		assertEquals("C", lista.obtener(0));
		assertEquals("AA", lista.obtener(1));
		assertEquals("BB", lista.obtener(2));
		assertEquals("DDD", lista.obtener(3));
	}

	// Caso límite

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

	// Caso de error

	@Test
	void rechazaElementosNulosEIndicesInvalidos() {
		ListaSimple<Integer> lista = new ListaSimple<>();

		assertThrows(IllegalArgumentException.class, () -> lista.agregarAlFinal(null));
		assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(0));

		lista.agregarAlInicio(10);

		assertThrows(IndexOutOfBoundsException.class, () -> lista.eliminar(1));
		assertEquals(1, lista.tamanio());
	}
}