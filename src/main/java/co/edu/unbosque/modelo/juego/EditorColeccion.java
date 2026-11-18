package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Jugador;
import co.edu.unbosque.modelo.estructuras.Pila;

/**
 * Administra los traslados del editor y su historial de deshacer y rehacer.
 *
 * <p>
 * Las reglas de capacidad y copias se aplican mediante Jugador. Las pilas solo
 * cambian después de un traslado confirmado.
 *
 * <p>
 * Responsable: Brayan.
 */
public final class EditorColeccion {

	/** Jugador cuya colección se está editando. */
	private final Jugador jugador;

	/** Movimientos confirmados que todavía pueden deshacerse. */
	private final Pila<Movimiento> paraDeshacer = new Pila<>();

	/** Movimientos deshechos que todavía pueden repetirse. */
	private final Pila<Movimiento> paraRehacer = new Pila<>();

	/**
	 * Crea un editor con el historial vacío. Complejidad: O(1).
	 *
	 * @param jugador dueño de la colección
	 * @throws IllegalArgumentException si el jugador es nulo
	 */
	public EditorColeccion(Jugador jugador) {
		if (jugador == null) {
			throw new IllegalArgumentException("El editor necesita un jugador");
		}

		this.jugador = jugador;
	}

	/**
	 * Solicita pasar una copia del cofre al Deck y registra el resultado. El costo
	 * corresponde al traslado implementado en Jugador.
	 *
	 * @param idCarta ID de la carta seleccionada
	 * @return true si se trasladó una copia
	 * @throws IllegalArgumentException si el ID está fuera del catálogo
	 */
	public boolean moverAlDeck(int idCarta) {
		return moverYRegistrar(new Movimiento(idCarta, Movimiento.Destino.DECK));
	}

	/**
	 * Solicita devolver una copia del Deck al cofre y registra el resultado. El
	 * costo corresponde al traslado implementado en Jugador.
	 *
	 * @param idCarta ID de la carta seleccionada
	 * @return true si se trasladó una copia
	 * @throws IllegalArgumentException si el ID está fuera del catálogo
	 */
	public boolean moverAlChest(int idCarta) {
		return moverYRegistrar(new Movimiento(idCarta, Movimiento.Destino.CHEST));
	}

	/**
	 * Intenta revertir el último traslado confirmado. Si el modelo rechaza la
	 * operación, conserva ambas pilas.
	 *
	 * <p>
	 * El historial se actualiza en O(1), más el costo del traslado.
	 *
	 * @return true si se deshizo un movimiento
	 */
	public boolean deshacer() {
		if (!puedeDeshacer()) {
			return false;
		}

		Movimiento movimiento = paraDeshacer.cima();
		if (!aplicar(movimiento.inverso())) {
			return false;
		}

		paraDeshacer.desapilar();
		paraRehacer.apilar(movimiento);
		return true;
	}

	/**
	 * Intenta repetir el último traslado deshecho. Si el modelo rechaza la
	 * operación, conserva ambas pilas.
	 *
	 * <p>
	 * El historial se actualiza en O(1), más el costo del traslado.
	 *
	 * @return true si se rehizo un movimiento
	 */
	public boolean rehacer() {
		if (!puedeRehacer()) {
			return false;
		}

		Movimiento movimiento = paraRehacer.cima();
		if (!aplicar(movimiento)) {
			return false;
		}

		paraRehacer.desapilar();
		paraDeshacer.apilar(movimiento);
		return true;
	}

	/**
	 * Consulta si hay movimientos pendientes de deshacer. Complejidad: O(1).
	 *
	 * @return true si la pila de deshacer tiene movimientos
	 */
	public boolean puedeDeshacer() {
		return !paraDeshacer.estaVacia();
	}

	/**
	 * Consulta si hay movimientos pendientes de rehacer. Complejidad: O(1).
	 *
	 * @return true si la pila de rehacer tiene movimientos
	 */
	public boolean puedeRehacer() {
		return !paraRehacer.estaVacia();
	}

	/**
	 * Inicia un historial vacío y conserva el contenido de la colección.
	 * Complejidad: O(1).
	 */
	public void limpiarHistorial() {
		paraDeshacer.vaciar();
		paraRehacer.vaciar();
	}

	/**
	 * Aplica un traslado nuevo y lo registra únicamente si tuvo éxito.
	 *
	 * @param movimiento traslado solicitado
	 * @return true si se completó y se guardó en el historial
	 */
	private boolean moverYRegistrar(Movimiento movimiento) {
		if (!aplicar(movimiento)) {
			return false;
		}

		paraDeshacer.apilar(movimiento);
		paraRehacer.vaciar();
		return true;
	}

	/**
	 * Delega el traslado en el jugador.
	 *
	 * @param movimiento traslado que se quiere realizar
	 * @return resultado entregado por el modelo
	 */
	private boolean aplicar(Movimiento movimiento) {
		if (movimiento.destino() == Movimiento.Destino.DECK) {
			return jugador.moverCartaAlDeck(movimiento.idCarta());
		}

		return jugador.moverCartaAlChest(movimiento.idCarta());
	}
}