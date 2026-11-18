package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;

/**
 * Describe el traslado de una copia entre el cofre y la baraja.
 *
 * <p>
 * Responsable: Brayan.
 *
 * @param idCarta ID de la carta trasladada
 * @param destino lugar al que se envió la copia
 */
public record Movimiento(int idCarta, Destino destino) {

	/** Lugares entre los que puede moverse una copia. */
	public enum Destino {
		CHEST, DECK
	}

	/**
	 * Valida los datos del movimiento. Complejidad: O(1).
	 *
	 * @throws IllegalArgumentException si el ID está fuera del catálogo o el
	 *                                  destino es nulo
	 */
	public Movimiento {
		if (idCarta < 1 || idCarta > Carta.ID_MAXIMO) {
			throw new IllegalArgumentException("El ID debe estar entre 1 y " + Carta.ID_MAXIMO);
		}

		if (destino == null) {
			throw new IllegalArgumentException("El destino no puede ser null");
		}
	}

	/**
	 * Prepara el traslado que devuelve la copia a su contenedor anterior.
	 * Complejidad: O(1).
	 *
	 * @return movimiento con el destino contrario
	 */
	public Movimiento inverso() {
		Destino anterior = destino == Destino.DECK ? Destino.CHEST : Destino.DECK;

		return new Movimiento(idCarta, anterior);
	}
}