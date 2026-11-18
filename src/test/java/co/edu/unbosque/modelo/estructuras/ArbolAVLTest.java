package co.edu.unbosque.modelo.estructuras;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de las operaciones, las rotaciones y el equilibrio de ArbolAVL.
 *
 * <p>
 * Responsable: Brayan.
 */
class ArbolAVLTest {

	// Casos normales

	@Test
	void insertaConsultaYReemplazaSinDuplicarLaClave() {
		ArbolAVL<Integer, String> arbol = new ArbolAVL<>();

		arbol.poner(20, "Carta original");
		arbol.poner(10, "Carta menor");
		arbol.poner(30, "Carta mayor");
		arbol.poner(20, "Carta actualizada");

		assertEquals(3, arbol.tamanio());
		assertEquals("Carta actualizada", arbol.obtener(20));
		assertEquals("Carta menor", arbol.obtener(10));
		assertEquals("Carta mayor", arbol.obtener(30));
		assertTrue(arbol.contiene(20));
		assertFalse(arbol.contiene(99));
	}

	@Test
	void eliminaUnNodoConDosHijosYConservaLasOtrasEntradas() throws ReflectiveOperationException {
		ArbolAVL<Integer, String> arbol = crearArbol(20, 10, 30, 25, 40);

		assertEquals("Carta 20", arbol.eliminar(20));
		assertNull(arbol.obtener(20));
		assertEquals(4, arbol.tamanio());
		assertEquals("Carta 10", arbol.obtener(10));
		assertEquals("Carta 25", arbol.obtener(25));
		assertEquals("Carta 30", arbol.obtener(30));
		assertEquals("Carta 40", arbol.obtener(40));
		verificarAlturasYBalance(leerCampo(arbol, "raiz"));
	}

	// Casos de rotación

	@Test
	void aplicaRotacionSimpleALaDerecha() throws ReflectiveOperationException {
		verificarRotacion(30, 20, 10);
	}

	@Test
	void aplicaRotacionSimpleALaIzquierda() throws ReflectiveOperationException {
		verificarRotacion(10, 20, 30);
	}

	@Test
	void aplicaRotacionDobleIzquierdaDerecha() throws ReflectiveOperationException {
		verificarRotacion(30, 10, 20);
	}

	@Test
	void aplicaRotacionDobleDerechaIzquierda() throws ReflectiveOperationException {
		verificarRotacion(10, 30, 20);
	}

	@Test
	void vuelveAEquilibrarElArbolDespuesDeEliminar() throws ReflectiveOperationException {
		ArbolAVL<Integer, String> arbol = crearArbol(30, 20, 40, 10, 25);
		assertEquals("Carta 40", arbol.eliminar(40));
		assertNull(arbol.obtener(40));
		assertEquals(4, arbol.tamanio());
		Object raiz = leerCampo(arbol, "raiz");
		assertEquals(Integer.valueOf(20), leerCampo(raiz, "clave"));
		verificarAlturasYBalance(raiz);
		assertEquals("Carta 10", arbol.obtener(10));
		assertEquals("Carta 20", arbol.obtener(20));
		assertEquals("Carta 25", arbol.obtener(25));
		assertEquals("Carta 30", arbol.obtener(30));
	}

	// Caso límite

	@Test
	void permiteEliminarElUnicoNodoYVolverAInsertar() {
		ArbolAVL<Integer, String> arbol = new ArbolAVL<>();
		assertTrue(arbol.estaVacia());
		assertNull(arbol.eliminar(99));
		arbol.poner(10, "Primera carta");
		assertEquals("Primera carta", arbol.eliminar(10));
		assertTrue(arbol.estaVacia());
		assertEquals(0, arbol.tamanio());
		assertNull(arbol.obtener(10));
		arbol.poner(20, "Segunda carta");
		assertFalse(arbol.estaVacia());
		assertEquals("Segunda carta", arbol.obtener(20));
	}

	// Caso de error

	@Test
	void rechazaClavesYValoresNulosSinCambiarElArbol() {
		ArbolAVL<Integer, String> arbol = crearArbol(10);
		assertThrows(IllegalArgumentException.class, () -> arbol.poner(null, "Carta"));
		assertThrows(IllegalArgumentException.class, () -> arbol.poner(10, null));
		assertThrows(IllegalArgumentException.class, () -> arbol.obtener(null));
		assertThrows(IllegalArgumentException.class, () -> arbol.contiene(null));
		assertThrows(IllegalArgumentException.class, () -> arbol.eliminar(null));

		assertEquals(1, arbol.tamanio());
		assertEquals("Carta 10", arbol.obtener(10));
	}

	/**
	 * Crea un árbol insertando las claves en el orden recibido.
	 */
	private static ArbolAVL<Integer, String> crearArbol(int... claves) {
		ArbolAVL<Integer, String> arbol = new ArbolAVL<>();

		for (int clave : claves) {
			arbol.poner(clave, "Carta " + clave);
		}

		return arbol;
	}

	/**
	 * Comprueba que una rotación deja 20 en la raíz y conserva las tres entradas.
	 */
	private static void verificarRotacion(int... claves) throws ReflectiveOperationException {
		ArbolAVL<Integer, String> arbol = crearArbol(claves);
		Object raiz = leerCampo(arbol, "raiz");

		assertEquals(Integer.valueOf(20), leerCampo(raiz, "clave"));
		assertEquals(2, verificarAlturasYBalance(raiz));
		assertEquals(3, arbol.tamanio());

		for (int clave : claves) {
			assertEquals("Carta " + clave, arbol.obtener(clave));
		}
	}

	/**
	 * Calcula la altura real de cada subárbol. La compara con la altura guardada y
	 * verifica que los hijos difieran como máximo en un nivel.
	 */
	private static int verificarAlturasYBalance(Object nodo) throws ReflectiveOperationException {
		if (nodo == null) {
			return 0;
		}

		int izquierda = verificarAlturasYBalance(leerCampo(nodo, "izquierdo"));
		int derecha = verificarAlturasYBalance(leerCampo(nodo, "derecho"));
		int alturaReal = 1 + Math.max(izquierda, derecha);
		Object clave = leerCampo(nodo, "clave");

		assertTrue(Math.abs(izquierda - derecha) <= 1, "El nodo " + clave + " quedó desbalanceado");
		assertEquals(Integer.valueOf(alturaReal), leerCampo(nodo, "altura"),
				"La altura guardada es incorrecta para la clave " + clave);

		return alturaReal;
	}

	/**
	 * Lee un atributo privado para comprobar la estructura interna del árbol.
	 */
	private static Object leerCampo(Object objeto, String nombre) throws ReflectiveOperationException {
		Field campo = objeto.getClass().getDeclaredField(nombre);
		campo.setAccessible(true);
		return campo.get(objeto);
	}
}