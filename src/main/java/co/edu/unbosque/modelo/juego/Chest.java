package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.estructuras.Lista;
import co.edu.unbosque.modelo.estructuras.ListaSimple;

/**
 * Representa el cofre del jugador. Guarda las cartas disponibles para armar la
 * baraja y permite tener varias copias de una misma carta.
 *
 * <p>
 * Responsable: Brayan.
 */
public class Chest {

	/** Límite de copias de un mismo ID en la colección del jugador. */
	public static final int MAXIMO_COPIAS = 3;

	/**
	 * Cartas disponibles en el cofre. Cada nodo de la lista representa una copia,
	 * por lo que un mismo ID puede aparecer varias veces.
	 */
	private final Lista<Carta> cartas = new ListaSimple<>();

	/**
	 * Cantidad de copias de cada carta en el cofre. El índice corresponde al ID de
	 * la carta. La posición 0 queda libre.
	 */
	private final int[] copiasPorId = new int[Carta.ID_MAXIMO + 1];

	/**
	 * Agrega una copia al final de la lista si el cofre tiene menos de
	 * {@link #MAXIMO_COPIAS} copias de esa carta. Complejidad: O(1).
	 *
	 * @param carta carta que se quiere guardar
	 * @return {@code true} si se agregó la copia o {@code false} si el cofre ya
	 *         alcanzó el límite para ese ID
	 * @throws IllegalArgumentException si la carta es {@code null}
	 */
	public boolean agregar(Carta carta) {
		if (carta == null) {
			throw new IllegalArgumentException("La carta no puede ser null");
		}

		int idCarta = carta.getId();
		if (copiasPorId[idCarta] >= MAXIMO_COPIAS) {
			return false;
		}

		cartas.agregarAlFinal(carta);
		copiasPorId[idCarta]++;
		return true;
	}

	/**
	 * Retira la primera copia que encuentra con el ID indicado. Complejidad: O(n²)
	 * en el peor caso, porque cada consulta por índice recorre la lista desde la
	 * cabeza.
	 *
	 * @param idCarta ID de la carta que se quiere retirar
	 * @return {@code true} si se retiró una copia o {@code false} si el cofre no
	 *         contiene ese ID
	 */
	public boolean quitar(int idCarta) {
		if (copiasDe(idCarta) == 0) {
			return false;
		}

		for (int indice = 0; indice < cartas.tamanio(); indice++) {
			if (cartas.obtener(indice).getId() == idCarta) {
				cartas.eliminar(indice);
				copiasPorId[idCarta]--;
				return true;
			}
		}

		return false;
	}

	/**
	 * Consulta cuántas copias de una carta hay en el cofre. Complejidad: O(1).
	 *
	 * @param idCarta ID de la carta que se quiere consultar
	 * @return cantidad de copias, o 0 si el ID no corresponde a una carta guardada
	 *         en el cofre
	 */
	public int copiasDe(int idCarta) {
		if (idCarta < 1 || idCarta > Carta.ID_MAXIMO) {
			return 0;
		}

		return copiasPorId[idCarta];
	}

	/**
	 * Cuenta los IDs que tienen al menos una copia en el cofre. Por ejemplo, tres
	 * copias del mismo ID cuentan como una carta distinta. Complejidad: O(C), donde
	 * C es la cantidad de IDs del catálogo.
	 *
	 * @return cantidad de cartas distintas en el cofre
	 */
	public int cartasDistintas() {
		int distintas = 0;
		for (int idCarta = 1; idCarta < copiasPorId.length; idCarta++) {
			if (copiasPorId[idCarta] > 0) {
				distintas++;
			}
		}

		return distintas;
	}

	/**
	 * Devuelve las cartas del cofre en un arreglo nuevo, en el mismo orden en que
	 * aparecen en la lista. Incluye todas las copias. Complejidad: O(n²), porque
	 * cada acceso por índice recorre la lista.
	 *
	 * @return arreglo con las cartas actuales del cofre
	 */
	public Carta[] cartasActuales() {
		Carta[] copia = new Carta[cartas.tamanio()];

		for (int indice = 0; indice < copia.length; indice++) {
			copia[indice] = cartas.obtener(indice);
		}

		return copia;
	}
}