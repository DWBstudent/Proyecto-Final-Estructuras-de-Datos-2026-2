package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.estructuras.Lista;
import co.edu.unbosque.modelo.estructuras.ListaSimple;

/**
 * El Deck del jugador: exactamente 40 cartas para poder duelar.
 *
 * <p>
 * Responsable: Brayan (hito H2). Contrato del hito H0: Santiago usa
 * {@link #aArreglo()} para armar el mazo del duelo.
 */
public class Deck {

	/** Cantidad de cartas que debe tener la baraja para iniciar un duelo. */
	public static final int TAMANIO = 40;

	/** Cartas elegidas para la baraja. Cada nodo representa una copia. */
	private final Lista<Carta> cartas = new ListaSimple<>();

	/**
	 * Cantidad de copias de cada carta en la baraja. El índice corresponde al ID de
	 * la carta y la posición 0 queda libre.
	 */
	private final int[] copiasPorId = new int[Carta.ID_MAXIMO + 1];

	/**
	 * Agrega una carta al final si hay cupo y si todavía se puede incluir otra
	 * copia de ese ID en la baraja. Complejidad: O(1).
	 *
	 * @param carta que se quiere agregar
	 * @return {@code true} si se agregó o {@code false} si la baraja está llena o
	 *         ya contiene el máximo de copias de esa carta
	 * @throws IllegalArgumentException si la carta es {@code null}
	 */
	public boolean agregar(Carta carta) {
		if (carta == null) {
			throw new IllegalArgumentException("La carta no puede ser null");
		}

		int idCarta = carta.getId();
		if (estaCompleto() || copiasPorId[idCarta] >= Chest.MAXIMO_COPIAS) {
			return false;
		}

		cartas.agregarAlFinal(carta);
		copiasPorId[idCarta]++;
		return true;
	}

	/**
	 * Retira la primera copia que encuentra con el ID indicado. Complejidad: O(n²)
	 * en el peor caso, porque cada consulta por índice recorre la lista desde la
	 * cabeza. La baraja admite hasta 40 cartas.
	 *
	 * @param idCarta ID de la carta que se quiere retirar
	 * @return {@code true} si se retiró una copia o {@code false} si la baraja no
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
	 * Consulta cuántas copias de una carta hay en la baraja. Complejidad: O(1).
	 *
	 * @param idCarta ID de la carta que se quiere consultar
	 * @return cantidad de copias, o 0 si el ID no está en la baraja
	 */
	public int copiasDe(int idCarta) {
		if (idCarta < 1 || idCarta > Carta.ID_MAXIMO) {
			return 0;
		}

		return copiasPorId[idCarta];
	}

	/**
	 * Devuelve la cantidad total de cartas, incluidas las copias repetidas.
	 * Complejidad: O(1).
	 *
	 * @return cantidad de cartas en la baraja
	 */
	public int tamanio() {
		return cartas.tamanio();
	}

	/**
	 * Comprueba si la baraja tiene las 40 cartas necesarias para duelar.
	 * Complejidad: O(1).
	 *
	 * @return {@code true} si la baraja está completa
	 */
	public boolean estaCompleto() {
		return tamanio() == TAMANIO;
	}

	/**
	 * Devuelve las cartas en un arreglo nuevo, en el mismo orden que tienen en la
	 * baraja. También funciona con una baraja vacía o incompleta. Complejidad:
	 * O(n²) con el acceso por índice de la lista simple.
	 *
	 * @return arreglo con las cartas actuales de la baraja
	 */
	public Carta[] cartasActuales() {
		Carta[] copia = new Carta[tamanio()];

		for (int indice = 0; indice < copia.length; indice++) {
			copia[indice] = cartas.obtener(indice);
		}

		return copia;
	}

	/**
	 * Prepara una copia de la baraja para que el duelo pueda barajarla sin cambiar
	 * el orden guardado por el jugador. Complejidad: O(n²) con el acceso por índice
	 * de la lista simple.
	 *
	 * @return arreglo nuevo con las 40 cartas de la baraja
	 * @throws IllegalStateException si la baraja todavía está incompleta
	 */
	public Carta[] aArreglo() {
		if (!estaCompleto()) {
			throw new IllegalStateException("La baraja debe tener " + TAMANIO + " cartas para iniciar un duelo");
		}

		return cartasActuales();
	}
}