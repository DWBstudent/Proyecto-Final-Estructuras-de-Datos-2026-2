package co.edu.unbosque.modelo.entidades;

import co.edu.unbosque.modelo.juego.Chest;
import co.edu.unbosque.modelo.juego.Deck;
import co.edu.unbosque.modelo.juego.Catalogo;

/**
 * Representa al jugador de una partida. Administra su colección, registra las
 * cartas descubiertas y conserva el avance de la campaña.
 *
 * <p>
 * Responsable: Daniel.
 */
public class Jugador {

	/** Cantidad de rivales del modo historia. */
	public static final int TOTAL_RIVALES = 3;

	/** Nombre con el que se identifica al jugador. */
	private final String nombre;

	/** Cofre con las cartas disponibles para armar la baraja. */
	private final Chest chest = new Chest();

	/** Baraja personal que el jugador prepara para los duelos. */
	private final Deck deck = new Deck();

	/** Registro de cartas descubiertas por ID. La posición 0 queda libre. */
	private final boolean[] descubiertas = new boolean[Carta.ID_MAXIMO + 1];

	/** Cantidad de IDs distintos que el jugador ha descubierto. */
	private int cantidadDescubiertas;

	/** Cantidad de rivales vencidos en el modo historia. */
	private int rivalesVencidos;
	/**
	 * Contraseñas ya canjeadas, por ID de su carta. Cada carta tiene una sola
	 * contraseña, así que basta un arreglo de 101 casillas. La posición 0 queda libre.
	 */
	private final boolean[] contrasenasCanjeadas = new boolean[Carta.ID_MAXIMO + 1];
	/**
	 * Crea un jugador con la colección vacía y sin avance en la campaña.
	 * Complejidad: O(n) respecto a la longitud del nombre.
	 *
	 * @param nombre nombre del jugador
	 * @throws IllegalArgumentException si el nombre es nulo, está vacío o solo
	 *                                  contiene espacios
	 */
	public Jugador(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El jugador necesita un nombre");
		}

		this.nombre = nombre.trim();
	}

	/**
	 * Consulta el nombre del jugador. Complejidad: O(1).
	 *
	 * @return nombre del jugador
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Devuelve el cofre del jugador. Complejidad: O(1).
	 *
	 * @return cofre de la partida
	 */
	public Chest getChest() {
		return chest;
	}

	/**
	 * Devuelve la baraja personal del jugador. Complejidad: O(1).
	 *
	 * @return baraja de la partida
	 */
	public Deck getDeck() {
		return deck;
	}

	/**
	 * Marca la carta como descubierta y agrega una copia al cofre si el jugador aún
	 * tiene menos de tres copias entre cofre y baraja. Complejidad: O(1).
	 *
	 * @param carta carta recibida por recompensa, contraseña u otra entrega
	 * @return {@code true} si se agregó la copia o {@code false} si se alcanzó el
	 *         límite. La carta queda descubierta en ambos casos.
	 * @throws IllegalArgumentException si la carta es nula
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
	 * Traslada una copia del cofre a la baraja. Si la baraja está llena o rechaza
	 * la copia, la carta permanece en el cofre. Complejidad: O(n²) en el peor caso,
	 * con n copias en el cofre.
	 *
	 * @param idCarta ID de la carta que se quiere pasar a la baraja
	 * @return {@code true} si se trasladó una copia
	 */
	public boolean moverCartaAlDeck(int idCarta) {
		if (deck.estaCompleto() || chest.copiasDe(idCarta) == 0) {
			return false;
		}

		Carta carta = chest.buscarPorId(idCarta);
		if (carta == null || !deck.agregar(carta)) {
			return false;
		}

		if (!chest.quitar(idCarta)) {
			deck.quitar(idCarta);
			return false;
		}

		return true;
	}

	/**
	 * Devuelve al cofre una copia de la baraja. Si el cofre rechaza la copia, la
	 * carta permanece en la baraja. Complejidad: O(n²) en el peor caso, con n
	 * copias en la baraja.
	 *
	 * @param idCarta ID de la carta que se quiere devolver al cofre
	 * @return {@code true} si se trasladó una copia
	 */
	public boolean moverCartaAlChest(int idCarta) {
		if (deck.copiasDe(idCarta) == 0) {
			return false;
		}

		Carta carta = buscarPorId(deck.cartasActuales(), idCarta);
		if (carta == null || !chest.agregar(carta)) {
			return false;
		}

		if (!deck.quitar(idCarta)) {
			chest.quitar(idCarta);
			return false;
		}

		return true;
	}

	/**
	 * Consulta si el jugador ya descubrió una carta. Complejidad: O(1).
	 *
	 * @param idCarta ID de la carta que se quiere consultar
	 * @return {@code true} si la carta está descubierta
	 * @throws IllegalArgumentException si el ID está fuera del catálogo
	 */
	public boolean estaDescubierta(int idCarta) {
		validarId(idCarta);
		return descubiertas[idCarta];
	}

	/**
	 * Registra una carta como descubierta. El contador aumenta solo la primera vez
	 * que se registra ese ID. Complejidad: O(1).
	 *
	 * @param idCarta ID de la carta que se quiere registrar
	 * @throws IllegalArgumentException si el ID está fuera del catálogo
	 */
	public void marcarDescubierta(int idCarta) {
		validarId(idCarta);

		if (!descubiertas[idCarta]) {
			descubiertas[idCarta] = true;
			cantidadDescubiertas++;
		}
	}

	/**
	 * Consulta cuántas cartas distintas se han descubierto. Complejidad: O(1).
	 *
	 * @return cantidad de IDs descubiertos
	 */
	public int getCantidadDescubiertas() {
		return cantidadDescubiertas;
	}

	/**
	 * Consulta el progreso del jugador en el modo historia. Complejidad: O(1).
	 *
	 * @return cantidad de rivales vencidos
	 */
	public int getRivalesVencidos() {
		return rivalesVencidos;
	}

	/**
	 * Registra la victoria contra el siguiente rival. Complejidad: O(1).
	 *
	 * @throws IllegalStateException si el jugador ya venció a todos los rivales
	 */
	public void registrarRivalVencido() {
		if (rivalesVencidos >= TOTAL_RIVALES) {
			throw new IllegalStateException("El jugador ya venció a los " + TOTAL_RIVALES + " rivales");
		}

		rivalesVencidos++;
	}

	/**
	 * Comprueba si el jugador terminó la campaña. Complejidad: O(1).
	 *
	 * @return {@code true} si ya venció a todos los rivales
	 */
	public boolean completoModoHistoria() {
		return rivalesVencidos == TOTAL_RIVALES;
	}
	
	
	/**
	 * Canjea una contraseña. Si existe y no se ha usado, entrega una copia de su
	 * carta al cofre. Cada contraseña sirve una sola vez. Si el jugador ya tiene 3
	 * copias de la carta, la contraseña no se gasta y puede usarla después.
	 * Complejidad: O(1) promedio, por las tablas hash del catálogo.
	 *
	 * @param contrasena texto escrito por la persona (se ignoran los espacios de los extremos)
	 * @param catalogo   catálogo que traduce la contraseña al id de su carta
	 * @return qué pasó con el canje
	 * @throws IllegalArgumentException si el catálogo es nulo
	 */
	public ResultadoCanje canjearContrasena(String contrasena, Catalogo catalogo) {
		if (catalogo == null) {
			throw new IllegalArgumentException("Se necesita el catálogo para canjear contraseñas");
		}

		int idCarta = catalogo.idPorContrasena(contrasena);
		if (idCarta == -1) {
			return ResultadoCanje.NO_EXISTE;
		}
		if (contrasenasCanjeadas[idCarta]) {
			return ResultadoCanje.YA_USADA;
		}
		if (!recibirCarta(catalogo.buscarPorId(idCarta))) {
			return ResultadoCanje.SIN_ESPACIO;
		}

		contrasenasCanjeadas[idCarta] = true;
		return ResultadoCanje.CANJEADA;
	}

	/**
	 * Consulta si ya se canjeó la contraseña de una carta. Complejidad: O(1).
	 *
	 * @param idCarta ID de la carta
	 * @return {@code true} si su contraseña ya se usó
	 * @throws IllegalArgumentException si el ID está fuera del catálogo
	 */
	public boolean contrasenaCanjeada(int idCarta) {
		validarId(idCarta);
		return contrasenasCanjeadas[idCarta];
	}
	
	/**
	 * Marca como usada la contraseña de una carta sin entregar la carta. Solo se usa
	 * al cargar una partida guardada. Complejidad: O(1).
	 *
	 * @param idCarta ID de la carta
	 * @throws IllegalArgumentException si el ID está fuera del catálogo
	 */
	public void marcarContrasenaCanjeada(int idCarta) {
		validarId(idCarta);
		contrasenasCanjeadas[idCarta] = true;
	}
	/**
	 * Busca la primera carta con el ID indicado. Complejidad: O(n), con n elementos
	 * en el arreglo.
	 *
	 * @param cartas  arreglo que se va a recorrer
	 * @param idCarta ID que se quiere encontrar
	 * @return primera carta con ese ID, o {@code null} si está ausente
	 */
	private static Carta buscarPorId(Carta[] cartas, int idCarta) {
		for (Carta carta : cartas) {
			if (carta.getId() == idCarta) {
				return carta;
			}
		}

		return null;
	}

	/**
	 * Comprueba que el ID esté dentro del rango admitido por el catálogo.
	 * Complejidad: O(1).
	 *
	 * @param idCarta ID que se quiere validar
	 * @throws IllegalArgumentException si el ID es menor que 1 o mayor que
	 *                                  {@link Carta#ID_MAXIMO}
	 */
	private static void validarId(int idCarta) {
		if (idCarta < 1 || idCarta > Carta.ID_MAXIMO) {
			throw new IllegalArgumentException("El id debe estar entre 1 y " + Carta.ID_MAXIMO + ": " + idCarta);
		}
	}
}