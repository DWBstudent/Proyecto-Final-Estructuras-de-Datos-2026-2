package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.estructuras.ArbolAVL;
import co.edu.unbosque.modelo.estructuras.Lista;
import co.edu.unbosque.modelo.estructuras.ListaSimple;

/**
 * Guarda las cartas que el jugador tiene disponibles en el cofre. Cada nodo
 * representa una copia y las cartas se recorren en el orden en que aparecen en
 * la lista.
 *
 * <p>
 * El cofre limita sus propias copias por ID. El límite de la colección
 * completa, que incluye el cofre y la baraja, se valida en el jugador.
 *
 * <p>
 * Responsable: Brayan.
 */
public class Chest {

	/** Máximo de copias de un mismo ID en la colección del jugador. */
	public static final int MAXIMO_COPIAS = 3;

	/** Lista simplemente enlazada que guarda una entrada por cada copia. */
	private final Lista<Carta> cartas = new ListaSimple<>();

	/** Índice por ID que guarda una entrada por cada carta distinta del cofre. */
	private final ArbolAVL<Integer, Carta> indicePorId = new ArbolAVL<>();

	/**
	 * Cantidad de copias que hay en el cofre por cada ID. La posición 0 queda libre
	 * porque los IDs empiezan en 1.
	 */
	private final int[] copiasPorId = new int[Carta.ID_MAXIMO + 1];

	/**
	 * Agrega una copia al final del cofre. Si es la primera de su ID, también la
	 * registra en el índice. Complejidad: O(1) para una copia adicional y O(log d)
	 * para un ID nuevo, donde d es la cantidad de cartas distintas.
	 *
	 * @param carta carta que se quiere guardar
	 * @return {@code true} si se agregó o {@code false} si alcanzó el límite
	 * @throws IllegalArgumentException si la carta es nula
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

		if (copiasPorId[idCarta] == 1) {
			indicePorId.poner(idCarta, carta);
		}

		return true;
	}

	/**
	 * Retira la primera copia del ID indicado y actualiza el índice. Complejidad:
	 * O(n + log d), con n copias y d cartas distintas en el cofre.
	 *
	 * @param idCarta ID de la carta que se quiere retirar
	 * @return {@code true} si se retiró una copia o {@code false} si no había
	 *         copias
	 */
	public boolean quitar(int idCarta) {
		Carta carta = buscarPorId(idCarta);
		if (carta == null) {
			return false;
		}

		int posicion = cartas.indiceDe(carta);
		cartas.eliminar(posicion);
		copiasPorId[idCarta]--;

		if (copiasPorId[idCarta] == 0) {
			indicePorId.eliminar(idCarta);
		} else {
			int siguienteCopia = cartas.indiceDe(carta);
			indicePorId.poner(idCarta, cartas.obtener(siguienteCopia));
		}

		return true;
	}

	/**
	 * Consulta cuántas copias de un ID están guardadas en el cofre. Complejidad:
	 * O(1).
	 *
	 * @param idCarta ID de la carta que se quiere consultar
	 * @return número de copias, o 0 si el ID es inválido o no está en el cofre
	 */
	public int copiasDe(int idCarta) {
		if (idCarta < 1 || idCarta > Carta.ID_MAXIMO) {
			return 0;
		}

		return copiasPorId[idCarta];
	}

	/**
	 * Consulta cuántos IDs tienen al menos una copia en el cofre. Complejidad:
	 * O(1).
	 *
	 * @return cantidad de cartas distintas
	 */
	public int cartasDistintas() {
		return indicePorId.tamanio();
	}

	/**
	 * Devuelve un arreglo nuevo con todas las copias, en el orden de la lista.
	 * Cambiar una posición del arreglo deja intacto el contenido del cofre.
	 * Complejidad: O(n²), porque cada consulta por índice recorre la lista
	 * simplemente enlazada desde el primer nodo.
	 *
	 * @return arreglo con las cartas actuales, vacío si el cofre está vacío
	 */
	public Carta[] cartasActuales() {
		Carta[] copia = new Carta[cartas.tamanio()];

		for (int indice = 0; indice < copia.length; indice++) {
			copia[indice] = cartas.obtener(indice);
		}

		return copia;
	}

	/**
	 * Busca en el índice la primera copia disponible del ID indicado. Complejidad:
	 * O(log d), con d cartas distintas en el cofre.
	 *
	 * @param idCarta ID que se quiere buscar
	 * @return carta disponible, o {@code null} si el ID no está en el cofre
	 */
	public Carta buscarPorId(int idCarta) {
		return indicePorId.obtener(idCarta);
	}
}