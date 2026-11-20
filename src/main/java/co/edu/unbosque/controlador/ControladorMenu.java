package co.edu.unbosque.controlador;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.Direccion;
import co.edu.unbosque.modelo.entidades.Jugador;
import co.edu.unbosque.modelo.entidades.ResultadoCanje;
import co.edu.unbosque.modelo.juego.Catalogo;
import co.edu.unbosque.modelo.juego.Deck;
import co.edu.unbosque.modelo.juego.EditorColeccion;
import co.edu.unbosque.modelo.juego.Inventario;
import co.edu.unbosque.modelo.ordenamiento.CriterioOrdenCarta;
import co.edu.unbosque.modelo.persistencia.GuardadoPartida;
import co.edu.unbosque.modelo.persistencia.PartidaInvalidaException;
import co.edu.unbosque.vista.Pantalla;
import co.edu.unbosque.vista.PantallaCambioTurno;
import co.edu.unbosque.vista.PantallaChest;
import co.edu.unbosque.vista.PantallaColeccion;
import co.edu.unbosque.vista.PantallaContrasenas;
import co.edu.unbosque.vista.PantallaDeck;
import co.edu.unbosque.vista.PantallaDuelo;
import co.edu.unbosque.vista.PantallaInventario;
import co.edu.unbosque.vista.PantallaMenu;
import javafx.application.Platform;

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

	/** Las 100 cartas del juego. Se carga una sola vez y se comparte. */
	private final Catalogo catalogo;

	/** Controlador de los modos historia, libre y jugador contra jugador. */
	private final ControladorPartida partida;

	/** Editor que comparte el historial entre las pantallas Chest y Deck. */
	private final EditorColeccion editorColeccion;

	/** Cartas de la pantalla activa, en el mismo orden que sus filas. */
	private Carta[] cartasVisibles = new Carta[0];

	/** Modelo que conserva el cursor y la navegación por el grafo. */
	private Inventario inventario;

	/** Vista del inventario, creada al abrirla por primera vez. */
	private PantallaInventario pantallaInventario;

	/** Criterios disponibles, en el mismo orden que las opciones del selector. */
	private final CriterioOrdenCarta[] criteriosOrden = CriterioOrdenCarta.values();

	/** Archivo donde se guarda la partida. */
	private final Path archivoPartida;

	/**
	 * Mensaje para mostrar la primera vez que aparece el menú (partida nueva,
	 * cargada o dañada).
	 */
	private String estadoPartida;

	/** Música de fondo: una pista para el menú y otra para el duelo. */
	private final ReproductorMusica musica = new ReproductorMusica();

	/**
	 * Abre el juego con la partida guardada en la carpeta del usuario, o con una
	 * nueva si no hay. Complejidad: O(n), con n = 100 cartas del catálogo.
	 *
	 * @param navegador navegador de la aplicación
	 * @throws IllegalArgumentException si el navegador es nulo
	 */
	public ControladorMenu(Navegador navegador) {
		this(navegador, Catalogo.cargar(), GuardadoPartida.archivoPorDefecto(), new Random());
	}

	/**
	 * Conecta el menú con el catálogo, el archivo de la partida y la partida. Si el
	 * jugador no tiene cartas, recibe el Deck inicial. Complejidad: O(n), con n
	 * cartas de la partida guardada.
	 *
	 * @param navegador      navegador de la aplicación
	 * @param catalogo       las 100 cartas del juego
	 * @param archivoPartida archivo donde se guarda y se carga la partida
	 * @param random         azar para barajar y elegir recompensas
	 * @throws IllegalArgumentException si algún argumento es nulo
	 */
	public ControladorMenu(Navegador navegador, Catalogo catalogo, Path archivoPartida, Random random) {
		if (navegador == null || catalogo == null || archivoPartida == null || random == null) {
			throw new IllegalArgumentException("El menú necesita navegador, catálogo, archivo y Random");
		}

		this.navegador = navegador;
		this.catalogo = catalogo;
		this.archivoPartida = archivoPartida;
		this.jugador = cargarOCrearJugador();
		this.editorColeccion = new EditorColeccion(jugador);
		this.partida = new ControladorPartida(navegador, jugador, catalogo, random);
		partida.entregarDeckInicialSiHaceFalta();
		navegador.alCambiarDePantalla(this::elegirMusica);
	}

	/**
	 * Elige la música según la pantalla: la del duelo durante el duelo y el cambio
	 * de turno; la del menú en todas las demás. Complejidad: O(1).
	 *
	 * @param pantalla pantalla que se acaba de mostrar
	 */
	private void elegirMusica(Pantalla pantalla) {
		boolean enDuelo = pantalla instanceof PantallaDuelo || pantalla instanceof PantallaCambioTurno;
		musica.tocar(enDuelo ? ReproductorMusica.Pista.DUELO : ReproductorMusica.Pista.MENU);
	}

	/**
	 * Prende o apaga la música. Complejidad: O(1).
	 *
	 * @return {@code true} si la música quedó encendida
	 */
	public boolean alternarMusica() {
		return !musica.alternarSilencio();
	}

	/**
	 * Guarda la partida cada vez que el menú aparece: al abrir el juego y al volver
	 * de un duelo, de un canje o de la colección. Complejidad: O(n), con n cartas.
	 *
	 * @param vista menú donde se muestra el resultado
	 */
	public void alVolverAlMenu(PantallaMenu vista) {
		if (!guardarPartida()) {
			vista.mostrarEstado("No se pudo guardar la partida.");
			return;
		}
		vista.mostrarEstado(estadoPartida != null ? estadoPartida : "Partida guardada.");
		estadoPartida = null;
	}

	/**
	 * Guarda la partida en su archivo. También se llama al cerrar el juego.
	 * Complejidad: O(n), con n cartas del jugador.
	 *
	 * @return {@code true} si se guardó
	 */
	public boolean guardarPartida() {
		try {
			GuardadoPartida.guardar(jugador, archivoPartida);
			return true;
		} catch (UncheckedIOException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}

	/**
	 * Lee la partida guardada. Si no existe, o está dañada, empieza una nueva.
	 *
	 * @return jugador de la partida
	 */
	private Jugador cargarOCrearJugador() {
		if (!Files.exists(archivoPartida)) {
			estadoPartida = "Partida nueva.";
			return new Jugador("Jugador");
		}
		try {
			Jugador cargado = GuardadoPartida.cargar(archivoPartida, catalogo);
			estadoPartida = "Partida cargada: " + cargado.getRivalesVencidos() + " de " + Jugador.TOTAL_RIVALES
					+ " rivales vencidos.";
			return cargado;
		} catch (PartidaInvalidaException | UncheckedIOException e) {
			estadoPartida = "La partida guardada estaba dañada (" + e.getMessage() + "). Empezaste una nueva.";
			return new Jugador("Jugador");
		}
	}

	/** Abre la selección de rival del modo historia. */
	public void irAModoHistoria() {
		partida.abrirModoHistoria();
	}

	/** Abre la selección de rival del modo libre. */
	public void irAModoLibre() {
		partida.abrirModoLibre();
	}

	/** Empieza un duelo de jugador contra jugador con Decks aleatorios. */
	public void irAJugadorContraJugador() {
		partida.abrirJugadorContraJugador();
	}

	/**
	 * Abre el cofre con un historial nuevo para esta sesión de edición. La carga
	 * tiene el costo de consultar y presentar las cartas del cofre.
	 */
	public void irAColeccion() {
		editorColeccion.limpiarHistorial();
		navegador.ir(crearPantallaChest());
	}

	/**
	 * Abre el inventario y conserva su cursor entre visitas. La primera apertura
	 * construye el grafo y las 100 casillas.
	 */
	public void irAInventario() {
		if (pantallaInventario == null) {
			inventario = new Inventario(catalogo);
			pantallaInventario = new PantallaInventario(Inventario.FILAS, Inventario.COLUMNAS, this::cargarInventario,
					this::seleccionarCasillaInventario, this::moverCursorInventario, this::buscarCartaSinDescubrir,
					navegador::volver);
		}

		navegador.ir(pantallaInventario);
	}

	/** Abre la pantalla de contraseñas. Complejidad O(1). */
	public void irAContrasenas() {
		navegador.ir(new PantallaContrasenas(this::canjearContrasena, navegador::volver));
	}

	/**
	 * Intenta canjear lo que escribió la persona y elige el mensaje según el
	 * resultado. Complejidad: O(1) promedio.
	 *
	 * @param vista pantalla de contraseñas
	 * @param texto contraseña escrita
	 */
	private void canjearContrasena(PantallaContrasenas vista, String texto) {
		ResultadoCanje resultado = jugador.canjearContrasena(texto, catalogo);
		vista.mostrarCarta("", "", null);

		switch (resultado) {
		case CANJEADA -> {
			Carta carta = catalogo.buscarPorId(catalogo.idPorContrasena(texto));
			vista.mostrarCarta(carta.getNombre(),
					carta.getTipo().getNombreVisible() + " · ATK " + carta.getAtaque() + " · DEF " + carta.getDefensa(),
					carta.getRutaImagen());
			vista.mostrarMensaje("¡Obtuviste " + carta.getNombre() + "! Quedó en tu cofre.");
			vista.limpiarCampo();
		}
		case YA_USADA -> vista.mostrarMensaje("Esa contraseña ya la usaste.");
		case SIN_ESPACIO -> vista.mostrarMensaje("Ya tienes 3 copias de esa carta. La contraseña no se gastó.");
		case NO_EXISTE -> vista.mostrarMensaje("Esa contraseña no es de ninguna carta. Revisa los 8 dígitos.");
		}
	}

	/**
	 * Construye el cofre y conecta sus acciones de selección, traslado y
	 * ordenamiento.
	 *
	 * @return pantalla del cofre
	 */
	private PantallaChest crearPantallaChest() {
		PantallaChest pantalla = new PantallaChest(vista -> cargarChest(vista, 0), this::mostrarCartaSeleccionada,
				this::pasarAlDeck, () -> navegador.reemplazar(crearPantallaDeck()), navegador::volver);
		pantalla.configurarOrden(nombresCriterios(), this::ordenarChest);
		pantalla.configurarBusqueda(this::buscarCartaEnChest);
		pantalla.configurarHistorial(this::deshacerMovimiento, this::rehacerMovimiento);
		return pantalla;
	}

	/**
	 * Construye la baraja y conecta sus acciones de selección, traslado y
	 * ordenamiento.
	 *
	 * @return pantalla de la baraja
	 */
	private PantallaDeck crearPantallaDeck() {
		PantallaDeck pantalla = new PantallaDeck(vista -> cargarDeck(vista, 0), this::mostrarCartaSeleccionada,
				this::devolverAlChest, () -> navegador.reemplazar(crearPantallaChest()), navegador::volver);
		pantalla.configurarOrden(nombresCriterios(), this::ordenarDeck);
		pantalla.configurarHistorial(this::deshacerMovimiento, this::rehacerMovimiento);
		return pantalla;
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
		vista.mostrarEstadoHistorial(editorColeccion.puedeDeshacer(), editorColeccion.puedeRehacer());
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

		if (editorColeccion.moverAlDeck(carta.getId())) {
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

		if (editorColeccion.moverAlChest(carta.getId())) {
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

	/**
	 * Prepara los textos del selector respetando el orden de los criterios.
	 * Complejidad: O(k), con k criterios disponibles.
	 *
	 * @return nombres que recibirá la vista
	 */
	private String[] nombresCriterios() {
		String[] nombres = new String[criteriosOrden.length];

		for (int indice = 0; indice < criteriosOrden.length; indice++) {
			nombres[indice] = criteriosOrden[indice].getNombreVisible();
		}

		return nombres;
	}

	/**
	 * Solicita ordenar el cofre con Merge Sort y actualiza la pantalla. Con la
	 * copia actual de Chest, el flujo completo cuesta O(n²) para comparaciones de
	 * costo constante.
	 *
	 * @param vista          pantalla del cofre
	 * @param indiceCriterio índice elegido en el selector
	 */
	private void ordenarChest(PantallaColeccion vista, int indiceCriterio) {
		if (indiceCriterio < 0 || indiceCriterio >= criteriosOrden.length) {
			return;
		}

		Carta seleccionada = cartaEnFila(vista.getFilaSeleccionada());
		CriterioOrdenCarta criterio = criteriosOrden[indiceCriterio];

		jugador.getChest().ordenar(criterio);
		cargarChest(vista, 0);
		restaurarSeleccion(vista, seleccionada);
		vista.mostrarMensaje("Cofre ordenado por " + criterio.getNombreVisible() + ".");
	}

	/**
	 * Solicita ordenar la baraja con Insertion Sort y actualiza la pantalla.
	 * Complejidad: O(n²) en el peor caso para comparaciones de costo constante.
	 *
	 * @param vista          pantalla de la baraja
	 * @param indiceCriterio índice elegido en el selector
	 */
	private void ordenarDeck(PantallaColeccion vista, int indiceCriterio) {
		if (indiceCriterio < 0 || indiceCriterio >= criteriosOrden.length) {
			return;
		}

		Carta seleccionada = cartaEnFila(vista.getFilaSeleccionada());
		CriterioOrdenCarta criterio = criteriosOrden[indiceCriterio];
		jugador.getDeck().ordenar(criterio);
		cargarDeck(vista, 0);
		restaurarSeleccion(vista, seleccionada);
		vista.mostrarMensaje("Baraja ordenada por " + criterio.getNombreVisible() + ".");
	}

	/**
	 * Selecciona la primera copia del ID que estaba seleccionado antes de ordenar.
	 * Utiliza las cartas que se cargaron en la pantalla actualizada. Complejidad:
	 * O(n).
	 *
	 * @param vista    pantalla que se acaba de actualizar
	 * @param anterior carta seleccionada antes del ordenamiento, o null
	 */
	private void restaurarSeleccion(PantallaColeccion vista, Carta anterior) {
		if (anterior == null) {
			return;
		}

		for (int fila = 0; fila < cartasVisibles.length; fila++) {
			if (cartasVisibles[fila].getId() == anterior.getId()) {
				vista.seleccionarFila(fila);
				return;
			}
		}
	}

	/**
	 * Solicita deshacer un traslado y actualiza la pantalla activa. El costo
	 * incluye el traslado y la recarga de la colección.
	 *
	 * @param vista pantalla desde la que se solicitó la acción
	 */
	private void deshacerMovimiento(PantallaColeccion vista) {
		if (!editorColeccion.deshacer()) {
			vista.mostrarMensaje("No se pudo deshacer el movimiento.");
			return;
		}

		recargarColeccion(vista);
		vista.mostrarMensaje("Se deshizo el último traslado.");
	}

	/**
	 * Solicita rehacer un traslado y actualiza la pantalla activa. El costo incluye
	 * el traslado y la recarga de la colección.
	 *
	 * @param vista pantalla desde la que se solicitó la acción
	 */
	private void rehacerMovimiento(PantallaColeccion vista) {
		if (!editorColeccion.rehacer()) {
			vista.mostrarMensaje("No se pudo rehacer el movimiento.");
			return;
		}

		recargarColeccion(vista);
		vista.mostrarMensaje("Se rehizo el último traslado.");
	}

	/**
	 * Actualiza la colección visible después de cambiar su contenido. Intenta
	 * conservar seleccionada la misma carta si sigue presente.
	 *
	 * @param vista pantalla que debe actualizarse
	 */
	private void recargarColeccion(PantallaColeccion vista) {
		Carta seleccionada = cartaEnFila(vista.getFilaSeleccionada());
		int fila = Math.max(0, vista.getFilaSeleccionada());

		if (vista instanceof PantallaChest) {
			cargarChest(vista, fila);
		} else {
			cargarDeck(vista, fila);
		}

		restaurarSeleccion(vista, seleccionada);
	}

	/**
	 * Prepara las 100 posiciones y el estado de descubrimiento del jugador.
	 * Complejidad: O(n), con n posiciones, suponiendo consultas de catálogo O(1).
	 */
	private void cargarInventario() {
		int cantidad = Inventario.FILAS * Inventario.COLUMNAS;
		String[] textos = new String[cantidad];
		boolean[] descubiertas = new boolean[cantidad];

		for (int indice = 0; indice < cantidad; indice++) {
			int idCarta = indice + 1;
			descubiertas[indice] = jugador.estaDescubierta(idCarta);
			textos[indice] = String.format("%03d", idCarta);
		}

		String resumen = jugador.getCantidadDescubiertas() + " / " + cantidad
				+ " descubiertas · Dorado: descubierta · Oscuro: pendiente";

		pantallaInventario.mostrarCuadricula(textos, descubiertas, resumen);
		actualizarDetalleInventario();
	}

	/**
	 * Solicita un paso al grafo y actualiza el detalle si el cursor se movió.
	 * Complejidad: O(1), porque cada vértice tiene como máximo ocho vecinos.
	 *
	 * @param cambioFila    desplazamiento solicitado en filas
	 * @param cambioColumna desplazamiento solicitado en columnas
	 */
	private void moverCursorInventario(int cambioFila, int cambioColumna) {
		Direccion direccion = direccionPara(cambioFila, cambioColumna);

		if (direccion == null) {
			return;
		}

		if (inventario.mover(direccion)) {
			actualizarDetalleInventario();
			pantallaInventario.mostrarMensaje("");
		} else {
			pantallaInventario.mostrarMensaje("Llegaste al borde del inventario.");
		}
	}

	/**
	 * Lleva el cursor a la casilla pulsada avanzando por los enlaces del grafo.
	 * Complejidad: O(max(f, c)), con f y c pasos de diferencia hasta el destino.
	 *
	 * @param indice posición pulsada, empezando desde 0
	 */
	private void seleccionarCasillaInventario(int indice) {
		int cantidad = Inventario.FILAS * Inventario.COLUMNAS;
		if (indice < 0 || indice >= cantidad) {
			return;
		}

		int filaDestino = indice / Inventario.COLUMNAS;
		int columnaDestino = indice % Inventario.COLUMNAS;
		while (inventario.getFila() != filaDestino || inventario.getColumna() != columnaDestino) {
			int cambioFila = Integer.compare(filaDestino, inventario.getFila());
			int cambioColumna = Integer.compare(columnaDestino, inventario.getColumna());
			Direccion direccion = direccionPara(cambioFila, cambioColumna);

			if (direccion == null || !inventario.mover(direccion)) {
				pantallaInventario.mostrarMensaje("No se pudo alcanzar esa posición.");
				actualizarDetalleInventario();
				return;
			}
		}

		actualizarDetalleInventario();
		pantallaInventario.mostrarMensaje("");
	}

	/**
	 * Usa BFS para seleccionar la carta sin descubrir más cercana. Complejidad: O(V
	 * + E), con V vértices y E arcos del grafo.
	 */
	private void buscarCartaSinDescubrir() {
		if (inventario.irALaMasCercanaSinDescubrir(jugador)) {
			actualizarDetalleInventario();
			pantallaInventario.mostrarMensaje("Se seleccionó la carta sin descubrir más cercana.");
		} else {
			pantallaInventario.mostrarMensaje("Ya descubriste todas las cartas.");
		}
	}

	/**
	 * Entrega a la vista el detalle permitido para la selección actual.
	 * Complejidad: O(1) con el catálogo indexado por ID.
	 */
	private void actualizarDetalleInventario() {
		Carta carta = inventario.getCartaEnCursor();
		int fila = inventario.getFila();
		int columna = inventario.getColumna();

		if (!jugador.estaDescubierta(carta.getId())) {
			pantallaInventario.mostrarSeleccion(fila, columna, String.format("Carta #%03d", carta.getId()),
					"Sin descubrir", "Consigue esta carta para consultar su imagen y sus datos.", null, false);
			return;
		}

		String estadisticas = carta.getTipo().getNombreVisible() + " · ATK " + carta.getAtaque() + " · DEF "
				+ carta.getDefensa();

		pantallaInventario.mostrarSeleccion(fila, columna, carta.getNombre(), estadisticas, carta.getDescripcion(),
				carta.getRutaImagen(), carta.getAtaque() >= 2500);
	}

	/**
	 * Traduce un desplazamiento de la interfaz a una dirección del modelo.
	 * Complejidad: O(1), porque existen ocho direcciones fijas.
	 *
	 * @param cambioFila    desplazamiento en filas
	 * @param cambioColumna desplazamiento en columnas
	 * @return dirección correspondiente, o null si el desplazamiento es inválido
	 */
	private Direccion direccionPara(int cambioFila, int cambioColumna) {
		for (Direccion direccion : Direccion.values()) {
			if (direccion.getDeltaFila() == cambioFila && direccion.getDeltaColumna() == cambioColumna) {
				return direccion;
			}
		}

		return null;
	}

	/**
	 * Busca un nombre mediante el AVL del cofre y selecciona su primera
	 * coincidencia en la lista visible. Conserva el orden actual de las filas.
	 *
	 * @param vista  pantalla del cofre
	 * @param nombre nombre completo escrito por el usuario
	 */
	private void buscarCartaEnChest(PantallaColeccion vista, String nombre) {
		if (nombre == null || nombre.isBlank()) {
			vista.mostrarMensaje("Escribe el nombre completo de una carta.");
			return;
		}

		Carta[] coincidencias = jugador.getChest().buscarPorNombre(nombre);
		if (coincidencias.length == 0) {
			vista.mostrarMensaje("No tienes una carta con ese nombre en el cofre.");
			return;
		}

		int idEncontrado = coincidencias[0].getId();
		for (int fila = 0; fila < cartasVisibles.length; fila++) {
			if (cartasVisibles[fila].getId() == idEncontrado) {
				vista.seleccionarFila(fila);
				vista.mostrarMensaje("Se encontraron " + coincidencias.length
						+ " cartas distintas con ese nombre. Primera coincidencia seleccionada.");
				return;
			}
		}

		vista.mostrarMensaje("Actualiza el cofre para mostrar la carta encontrada.");
	}

	/**
	 * Construye el menú principal y conecta sus eventos.
	 *
	 * <p>
	 * La vista recibe funciones para comunicar las acciones del usuario, sin
	 * depender directamente de esta clase.
	 *
	 * @return menú principal con sus acciones configuradas
	 */
	public PantallaMenu crearPantallaMenu() {
		PantallaMenu.Acciones acciones = new PantallaMenu.Acciones(this::alVolverAlMenu, this::irAModoHistoria,
				this::irAModoLibre, this::irAJugadorContraJugador, this::irAColeccion, this::irAInventario,
				this::irAContrasenas, this::alternarMusica, Platform::exit);
		return new PantallaMenu(acciones);
	}
}