package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.estructuras.ListaSimple;

/**
 * Guarda las cartas elegidas por el jugador para su baraja personal. La baraja
 * puede editarse mientras está incompleta y requiere 40 cartas para preparar un
 * duelo.
 *
 * <p>
 * Responsable: Brayan.
 */
public class Deck {

	/** Capacidad de la baraja y cantidad de cartas requerida para duelar. */
	public static final int TAMANIO = 40;

	/** Cartas de la baraja en orden. Cada nodo representa una copia. */
	private final ListaSimple<Carta> cartas = new ListaSimple<>();

	/**
	 * Cantidad de copias en la baraja por ID de carta. La posición 0 queda libre
	 * porque los IDs empiezan en 1.
	 */
	private final int[] copiasPorId = new int[Carta.ID_MAXIMO + 1];

	/**
	 * Agrega una copia al final de la baraja si hay espacio y el ID todavía admite
	 * otra copia. Complejidad: O(1).
	 *
	 * @param carta carta que se quiere agregar
	 * @return {@code true} si se agregó, o {@code false} si la baraja está llena o
	 *         ya tiene el máximo de copias de ese ID
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
	 * Retira la primera copia del ID indicado y actualiza su contador. Complejidad:
	 * O(n²) en el peor caso, porque cada consulta por índice recorre la lista desde
	 * la cabeza.
	 *
	 * @param idCarta ID de la carta que se quiere retirar
	 * @return {@code true} si se retiró una copia, o {@code false} si el ID no
	 *         tiene copias en la baraja
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
	 * Consulta cuántas copias de un ID hay en la baraja. Complejidad: O(1).
	 *
	 * @param idCarta ID de la carta que se quiere consultar
	 * @return número de copias, o 0 si el ID es inválido o está ausente
	 */
	public int copiasDe(int idCarta) {
		if (idCarta < 1 || idCarta > Carta.ID_MAXIMO) {
			return 0;
		}

		return copiasPorId[idCarta];
	}

	/**
	 * Consulta el tamaño de la baraja. Complejidad: O(1).
	 *
	 * @return cantidad total de cartas, incluidas las copias repetidas
	 */
	public int tamanio() {
		return cartas.tamanio();
	}

	/**
	 * Comprueba si la baraja tiene las cartas necesarias para iniciar un duelo.
	 * Complejidad: O(1).
	 *
	 * @return {@code true} si la baraja tiene 40 cartas
	 */
	public boolean estaCompleto() {
		return tamanio() == TAMANIO;
	}

	/**
	 * Copia el contenido actual en un arreglo nuevo. Sirve para consultar o guardar
	 * la baraja aunque esté vacía o incompleta. Complejidad: O(n).
	 *
	 * @return arreglo con las cartas en el orden de la baraja
	 */
	public Carta[] cartasActuales() {
		Carta[] copia = new Carta[tamanio()];
		cartas.copiarEn(copia);
		return copia;
	}

	/**
	 * Entrega al duelo una copia que puede barajarse sin cambiar el orden de la
	 * baraja del jugador. Complejidad: O(n).
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