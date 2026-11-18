package co.edu.unbosque.controlador;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.Jugador;
import co.edu.unbosque.modelo.juego.Deck;
import co.edu.unbosque.vista.PantallaChest;
import co.edu.unbosque.vista.PantallaColeccion;
import co.edu.unbosque.vista.PantallaDeck;
import co.edu.unbosque.vista.PantallaProvisional;

/**
 * Coordina las acciones del menú y de la colección. Consulta al jugador,
 * prepara los textos para la vista y delega los traslados al modelo.
 *
 * <p>
 * Responsable del menú: Daniel. Integración de Chest y Deck: Brayan.
 */
public class ControladorMenu {

	/** Navegador compartido por las pantallas del juego. */
	private final Navegador navegador;

	/** Jugador cuya colección se muestra y modifica. */
	private final Jugador jugador;

	/** Cartas de la pantalla activa, en el mismo orden que sus filas. */
	private Carta[] cartasVisibles = new Carta[0];

	/**
	 * Abre el menú con un jugador inicial. Complejidad O(1) para el tamaño fijo del
	 * catálogo.
	 *
	 * @param navegador navegador de la aplicación
	 * @throws IllegalArgumentException si el navegador es nulo
	 */
	public ControladorMenu(Navegador navegador) {
		this(navegador, new Jugador("Jugador"));
	}

	/**
	 * Conecta el menú con el jugador de una partida. Complejidad O(1).
	 *
	 * @param navegador navegador de la aplicación
	 * @param jugador   jugador activo de la partida
	 * @throws IllegalArgumentException si algún argumento es nulo
	 */
	public ControladorMenu(Navegador navegador, Jugador jugador) {
		if (navegador == null || jugador == null) {
			throw new IllegalArgumentException("El menú necesita un navegador y un jugador");
		}

		this.navegador = navegador;
		this.jugador = jugador;
	}

	/** Abre la pantalla del modo historia. Complejidad O(1). */
	public void irAModoHistoria() {
		navegador.ir(new PantallaProvisional("Modo Historia", navegador));
	}

	/** Abre la pantalla del modo libre. Complejidad O(1). */
	public void irAModoLibre() {
		navegador.ir(new PantallaProvisional("Modo Libre", navegador));
	}

	/** Abre la pantalla de jugador contra jugador. Complejidad O(1). */
	public void irAJugadorContraJugador() {
		navegador.ir(new PantallaProvisional("JcJ", navegador));
	}

	/**
	 * Abre el cofre y deja el menú en el historial. Con la implementación actual,
	 * cargar el cofre cuesta O(n² + C), donde n es la cantidad de copias y C es la
	 * cantidad de IDs que recorre el contador.
	 */
	public void irAColeccion() {
		navegador.ir(crearPantallaChest());
	}

	/** Abre la pantalla de inventario. Complejidad O(1). */
	public void irAInventario() {
		navegador.ir(new PantallaProvisional("Inventario", navegador));
	}

	/** Abre la pantalla de contraseñas. Complejidad O(1). */
	public void irAContrasenas() {
		navegador.ir(new PantallaProvisional("Contraseñas", navegador));
	}

	/**
	 * Conecta la vista del cofre con sus consultas y acciones.
	 *
	 * @return pantalla del cofre
	 */
	private PantallaChest crearPantallaChest() {
		return new PantallaChest(vista -> cargarChest(vista, 0), this::mostrarCartaSeleccionada, this::pasarAlDeck,
				() -> navegador.reemplazar(crearPantallaDeck()), navegador::volver);
	}

	/**
	 * Conecta la vista de la baraja con sus consultas y acciones.
	 *
	 * @return pantalla de la baraja
	 */
	private PantallaDeck crearPantallaDeck() {
		return new PantallaDeck(vista -> cargarDeck(vista, 0), this::mostrarCartaSeleccionada, this::devolverAlChest,
				() -> navegador.reemplazar(crearPantallaChest()), navegador::volver);
	}

	/**
	 * Consulta el cofre y muestra sus filas y contador.
	 *
	 * @param vista         pantalla del cofre
	 * @param filaPreferida fila que se intenta conservar seleccionada
	 */
	private void cargarChest(PantallaColeccion vista, int filaPreferida) {
		Carta[] cartas = jugador.getChest().cartasActuales();
		String resumen = cartas.length + " copias · " + jugador.getChest().cartasDistintas() + " cartas distintas";
		mostrarColeccion(vista, cartas, resumen, filaPreferida);
	}

	/**
	 * Consulta la baraja y muestra su capacidad ocupada.
	 *
	 * @param vista         pantalla de la baraja
	 * @param filaPreferida fila que se intenta conservar seleccionada
	 */
	private void cargarDeck(PantallaColeccion vista, int filaPreferida) {
		Carta[] cartas = jugador.getDeck().cartasActuales();
		String resumen = cartas.length + " / " + Deck.TAMANIO + " cartas";
		mostrarColeccion(vista, cartas, resumen, filaPreferida);
	}

	/**
	 * Guarda la relación entre cartas y filas, y entrega a la vista los textos en
	 * ese mismo orden. Complejidad O(n), más la selección.
	 *
	 * @param vista         pantalla que recibe los textos
	 * @param cartas        cartas en el orden que se va a mostrar
	 * @param resumen       texto del contador
	 * @param filaPreferida índice que se quiere seleccionar
	 */
	private void mostrarColeccion(PantallaColeccion vista, Carta[] cartas, String resumen, int filaPreferida) {
		cartasVisibles = cartas;
		String[] filas = new String[cartas.length];

		for (int fila = 0; fila < cartas.length; fila++) {
			Carta carta = cartas[fila];
			filas[fila] = String.format("%03d  %s", carta.getId(), carta.getNombre());
		}

		vista.mostrarCartas(filas, resumen, filaPreferida);
	}

	/**
	 * Busca la carta de la fila y entrega sus datos visibles a la pantalla.
	 *
	 * @param vista pantalla que muestra el detalle
	 * @param fila  índice de la selección
	 */
	private void mostrarCartaSeleccionada(PantallaColeccion vista, int fila) {
		Carta carta = cartaEnFila(fila);
		if (carta == null) {
			return;
		}

		String estadisticas = carta.getTipo().getNombreVisible() + "  ·  ATK " + carta.getAtaque() + "  ·  DEF "
				+ carta.getDefensa();

		vista.mostrarDetalle(carta.getNombre(), estadisticas, carta.getDescripcion(), carta.getRutaImagen());
		vista.mostrarAcabadoEspecial(carta.getAtaque() >= 2500);
	}

	/**
	 * Solicita el traslado al Deck y actualiza el cofre si tuvo éxito.
	 *
	 * @param vista pantalla del cofre
	 * @param fila  fila seleccionada
	 */
	private void pasarAlDeck(PantallaColeccion vista, int fila) {
		Carta carta = cartaEnFila(fila);
		if (carta == null) {
			return;
		}

		if (jugador.moverCartaAlDeck(carta.getId())) {
			cargarChest(vista, fila);
			vista.mostrarMensaje("Se pasó una copia de " + carta.getNombre() + " al Deck.");
		} else if (jugador.getDeck().estaCompleto()) {
			vista.mostrarMensaje("El Deck está lleno. La carta sigue en el cofre.");
		} else {
			vista.mostrarMensaje("No se pudo pasar esa carta al Deck.");
		}
	}

	/**
	 * Solicita la devolución al Chest y actualiza la baraja si tuvo éxito.
	 *
	 * @param vista pantalla de la baraja
	 * @param fila  fila seleccionada
	 */
	private void devolverAlChest(PantallaColeccion vista, int fila) {
		Carta carta = cartaEnFila(fila);
		if (carta == null) {
			return;
		}

		if (jugador.moverCartaAlChest(carta.getId())) {
			cargarDeck(vista, fila);
			vista.mostrarMensaje("Se devolvió una copia de " + carta.getNombre() + " al cofre.");
		} else {
			vista.mostrarMensaje("No se pudo devolver esa carta al cofre.");
		}
	}

	/**
	 * Resuelve una fila de la pantalla activa. Complejidad O(1).
	 *
	 * @param fila índice que envía la vista
	 * @return carta de la fila, o {@code null} si el índice está fuera de rango
	 */
	private Carta cartaEnFila(int fila) {
		if (fila < 0 || fila >= cartasVisibles.length) {
			return null;
		}

		return cartasVisibles[fila];
	}
}