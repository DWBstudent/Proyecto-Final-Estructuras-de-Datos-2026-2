package co.edu.unbosque.modelo.entidades;

import co.edu.unbosque.modelo.juego.Chest;
import co.edu.unbosque.modelo.juego.Deck;

/**
 * El jugador: su colección (Chest y Deck), las cartas que ya descubrió y su
 * avance en el modo historia. Es lo que se guarda y se carga en la partida.
 *
 * <p>
 * Responsable: Daniel. Contrato del hito H0: Brayan y Santiago usan
 * {@link #estaDescubierta(int)} y {@link #recibirCarta(Carta)}.
 */
public class Jugador {

	/** Cantidad de rivales del modo historia. */
	public static final int TOTAL_RIVALES = 3;

	private final String nombre;
	// Índice = id de la carta. La posición 0 no se usa, así el id se usa directo.
	private final boolean[] descubiertas = new boolean[Carta.ID_MAXIMO + 1];
	private int cantidadDescubiertas;
	private int rivalesVencidos;
	private final Chest chest = new Chest();
	private final Deck deck = new Deck();

	/**
	 * @throws IllegalArgumentException si el nombre está vacío
	 */
	public Jugador(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El jugador necesita un nombre");
		}
		this.nombre = nombre.trim();
	}

	public String getNombre() {
		return nombre;
	}

	/**
	 * Indica si el jugador ha tenido esta carta alguna vez. El inventario la
	 * muestra con imagen si es así, o en negro si no. Complejidad: O(1).
	 *
	 * @throws IllegalArgumentException si el id no está entre 1 y 100
	 */
	public boolean estaDescubierta(int idCarta) {
		validarId(idCarta);
		return descubiertas[idCarta];
	}

	/**
	 * Marca una carta como descubierta. Marcarla dos veces no cuenta doble.
	 * Complejidad: O(1).
	 *
	 * @throws IllegalArgumentException si el id no está entre 1 y 100
	 */
	public void marcarDescubierta(int idCarta) {
		validarId(idCarta);
		if (!descubiertas[idCarta]) {
			descubiertas[idCarta] = true;
			cantidadDescubiertas++;
		}
	}

	/** Complejidad: O(1). */
	public int getCantidadDescubiertas() {
		return cantidadDescubiertas;
	}

	/**
	 * Marca la carta como descubierta y agrega una copia al cofre si el jugador aún
	 * tiene menos de tres copias entre el cofre y la baraja. Complejidad: O(1).
	 *
	 * @param carta carta que recibe el jugador
	 * @return {@code true} si se agregó una copia o {@code false} si el jugador ya
	 *         alcanzó el límite de ese ID
	 * @throws IllegalArgumentException si la carta es {@code null}
	 */
	public boolean recibirCarta(Carta carta) {
		if (carta == null) {
			throw new IllegalArgumentException("La carta no puede ser null");
		}

		int idCarta = carta.getId();
		marcarDescubierta(idCarta);

		int totalCopias = chest.copiasDe(idCarta) + deck.copiasDe(idCarta);
		if (totalCopias >= Chest.MAXIMO_COPIAS) {
			return false;
		}

		return chest.agregar(carta);
	}

	/**
	 * Pasa una copia del cofre a la baraja. Si la carta no está en el cofre o la
	 * baraja rechaza la copia, el movimiento se cancela. Complejidad: O(n²), donde
	 * n es la cantidad de cartas del cofre.
	 *
	 * @param idCarta ID de la carta que se quiere llevar a la baraja
	 * @return {@code true} si se movió una copia
	 */
	public boolean moverCartaAlDeck(int idCarta) {
		if (deck.estaCompleto() || chest.copiasDe(idCarta) == 0) {
			return false;
		}

		Carta carta = buscarPorId(chest.cartasActuales(), idCarta);
		if (carta == null || !deck.agregar(carta)) {
			return false;
		}

		chest.quitar(idCarta);
		return true;
	}

	/**
	 * Devuelve al cofre una copia que está en la baraja. Complejidad: O(n²), donde
	 * n es la cantidad de cartas de la baraja.
	 *
	 * @param idCarta ID de la carta que se quiere devolver al cofre
	 * @return {@code true} si se movió una copia
	 */
	public boolean moverCartaAlChest(int idCarta) {
		if (deck.copiasDe(idCarta) == 0) {
			return false;
		}

		Carta carta = buscarPorId(deck.cartasActuales(), idCarta);
		if (carta == null || !chest.agregar(carta)) {
			return false;
		}

		deck.quitar(idCarta);
		return true;
	}

	/**
	 * Busca la primera copia con el ID indicado en el arreglo recibido.
	 */
	private Carta buscarPorId(Carta[] cartas, int idCarta) {
		for (Carta carta : cartas) {
			if (carta.getId() == idCarta) {
				return carta;
			}
		}

		return null;
	}

	/** Rivales del modo historia ya vencidos, de 0 a 3. Complejidad: O(1). */
	public int getRivalesVencidos() {
		return rivalesVencidos;
	}

	/**
	 * Registra que venció al siguiente rival del modo historia. Complejidad: O(1).
	 *
	 * @throws IllegalStateException si ya venció a los 3
	 */
	public void registrarRivalVencido() {
		if (rivalesVencidos >= TOTAL_RIVALES) {
			throw new IllegalStateException("El jugador ya venció a los " + TOTAL_RIVALES + " rivales");
		}
		rivalesVencidos++;
	}

	/** Complejidad: O(1). */
	public boolean completoModoHistoria() {
		return rivalesVencidos == TOTAL_RIVALES;
	}

	public Chest getChest() {
		return chest;
	}

	public Deck getDeck() {
		return deck;
	}

	private static void validarId(int idCarta) {
		if (idCarta < 1 || idCarta > Carta.ID_MAXIMO) {
			throw new IllegalArgumentException("El id debe estar entre 1 y " + Carta.ID_MAXIMO + ": " + idCarta);
		}
	}
}
