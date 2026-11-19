package co.edu.unbosque.modelo.ordenamiento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.TipoCarta;

/**
 * Comprueba los siete criterios disponibles para ordenar cartas.
 *
 * <p>
 * Responsable: Brayan.
 */
class CriterioOrdenCartaTest {

	/** Comprueba que el número se ordena de menor a mayor. */
	@Test
	void ordenaPorNumeroAscendente() {
		Carta primera = carta(1, "Carta B", TipoCarta.GUERRERO, 2000, 1000);
		Carta segunda = carta(2, "Carta A", TipoCarta.GUERRERO, 500, 3000);

		verificarOrden(CriterioOrdenCarta.NUMERO, primera, segunda);
	}

	/**
	 * Comprueba el orden por nombre y la equivalencia entre mayúsculas y
	 * minúsculas.
	 */
	@Test
	void ordenaPorNombreIgnorandoMayusculas() {
		Carta primera = carta(2, "Aprendiz", TipoCarta.GUERRERO, 500, 500);
		Carta segunda = carta(1, "Dragon", TipoCarta.DRAGON, 2000, 1000);
		Carta mismoNombre = carta(3, "APRENDIZ", TipoCarta.BESTIA, 800, 900);

		verificarOrden(CriterioOrdenCarta.NOMBRE, primera, segunda);
		assertEquals(0, CriterioOrdenCarta.NOMBRE.comparar(primera, mismoNombre));
	}

	/** Comprueba el orden del enum y el desempate por ID dentro de un tipo. */
	@Test
	void ordenaPorTipoYDesempataPorNumero() {
		Carta dragon = carta(20, "Dragon", TipoCarta.DRAGON, 1000, 1000);
		Carta guerrero = carta(1, "Guerrero", TipoCarta.GUERRERO, 1000, 1000);
		Carta otroGuerrero = carta(2, "Otro guerrero", TipoCarta.GUERRERO, 500, 500);

		verificarOrden(CriterioOrdenCarta.TIPO, dragon, guerrero);
		verificarOrden(CriterioOrdenCarta.TIPO, guerrero, otroGuerrero);
	}

	/** Comprueba que el ataque ascendente coloca primero al de menor ataque. */
	@Test
	void ordenaPorAtaqueAscendente() {
		Carta menor = carta(1, "Menor", TipoCarta.GUERRERO, 500, 3000);
		Carta mayor = carta(2, "Mayor", TipoCarta.GUERRERO, 2500, 100);

		verificarOrden(CriterioOrdenCarta.ATAQUE_ASCENDENTE, menor, mayor);
	}

	/** Comprueba que el ataque descendente coloca primero al de mayor ataque. */
	@Test
	void ordenaPorAtaqueDescendente() {
		Carta mayor = carta(1, "Mayor", TipoCarta.GUERRERO, 2500, 100);
		Carta menor = carta(2, "Menor", TipoCarta.GUERRERO, 500, 3000);

		verificarOrden(CriterioOrdenCarta.ATAQUE_DESCENDENTE, mayor, menor);
	}

	/** Comprueba que la defensa ascendente utiliza la defensa y no el ataque. */
	@Test
	void ordenaPorDefensaAscendente() {
		Carta menor = carta(1, "Menor defensa", TipoCarta.GUERRERO, 3000, 500);
		Carta mayor = carta(2, "Mayor defensa", TipoCarta.GUERRERO, 100, 2500);

		verificarOrden(CriterioOrdenCarta.DEFENSA_ASCENDENTE, menor, mayor);
	}

	/** Comprueba que la defensa descendente coloca primero al de mayor defensa. */
	@Test
	void ordenaPorDefensaDescendente() {
		Carta mayor = carta(1, "Mayor defensa", TipoCarta.GUERRERO, 100, 2500);
		Carta menor = carta(2, "Menor defensa", TipoCarta.GUERRERO, 3000, 500);

		verificarOrden(CriterioOrdenCarta.DEFENSA_DESCENDENTE, mayor, menor);
	}

	/**
	 * Comprueba que los criterios de estadísticas devuelven cero cuando hay empate.
	 */
	@Test
	void reconoceEmpatesEnAtaqueYDefensa() {
		Carta primera = carta(1, "Primera", TipoCarta.DRAGON, 1000, 1500);
		Carta segunda = carta(2, "Segunda", TipoCarta.GUERRERO, 1000, 1500);

		assertEquals(0, CriterioOrdenCarta.ATAQUE_ASCENDENTE.comparar(primera, segunda));
		assertEquals(0, CriterioOrdenCarta.ATAQUE_DESCENDENTE.comparar(primera, segunda));
		assertEquals(0, CriterioOrdenCarta.DEFENSA_ASCENDENTE.comparar(primera, segunda));
		assertEquals(0, CriterioOrdenCarta.DEFENSA_DESCENDENTE.comparar(primera, segunda));
	}

	/**
	 * Comprueba que todos los criterios rechazan una carta nula en cualquier
	 * posición.
	 */
	@Test
	void rechazaCartasNulasEnTodosLosCriterios() {
		Carta carta = carta(1, "Carta", TipoCarta.GUERRERO, 800, 500);

		for (CriterioOrdenCarta criterio : CriterioOrdenCarta.values()) {
			assertThrows(IllegalArgumentException.class, () -> criterio.comparar(null, carta));
			assertThrows(IllegalArgumentException.class, () -> criterio.comparar(carta, null));
		}
	}

	/**
	 * Comprueba el resultado en ambos sentidos y la comparación consigo misma.
	 *
	 * @param criterio criterio que se está probando
	 * @param primera  carta que debe aparecer antes
	 * @param segunda  carta que debe aparecer después
	 */
	private static void verificarOrden(CriterioOrdenCarta criterio, Carta primera, Carta segunda) {
		assertTrue(criterio.comparar(primera, segunda) < 0);
		assertTrue(criterio.comparar(segunda, primera) > 0);
		assertEquals(0, criterio.comparar(primera, primera));
	}

	/**
	 * Crea una carta con datos conocidos y una contraseña válida.
	 *
	 * @param id      identificador dentro del catálogo
	 * @param nombre  nombre de la carta
	 * @param tipo    tipo de monstruo
	 * @param ataque  puntos de ataque
	 * @param defensa puntos de defensa
	 * @return carta para la prueba
	 */
	private static Carta carta(int id, String nombre, TipoCarta tipo, int ataque, int defensa) {
		return new Carta(id, nombre, tipo, ataque, defensa, "", "", String.format("%08d", id));
	}
}