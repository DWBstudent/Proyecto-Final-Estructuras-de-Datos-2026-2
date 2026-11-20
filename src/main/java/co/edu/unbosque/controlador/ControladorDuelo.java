package co.edu.unbosque.controlador;

import java.util.function.Consumer;
import java.util.function.Supplier;
import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.juego.Duelista;
import co.edu.unbosque.modelo.juego.Duelo;
import co.edu.unbosque.modelo.juego.EstrategiaIA;
import co.edu.unbosque.modelo.juego.MonstruoEnCampo;
import co.edu.unbosque.modelo.juego.Posicion;
import co.edu.unbosque.modelo.juego.ResultadoCombate;
import co.edu.unbosque.vista.PantallaCambioTurno;
import co.edu.unbosque.vista.PantallaDuelo;
import co.edu.unbosque.vista.VistaCarta;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * Conecta la {@link PantallaDuelo} con un {@link Duelo}. Recibe los clics de la
 * pantalla, llama al modelo y le dice a la pantalla qué mostrar y qué botones
 * activar. Las reglas están en el modelo: aquí solo se decide el orden de los
 * clics (elegir carta, luego posición; elegir atacante, luego objetivo).
 *
 * <p>
 * Cada lado del duelo lo juega una persona ({@code null}) o una
 * {@link EstrategiaIA}. El turno de la máquina se juega con pausas
 * ({@link PauseTransition}, nunca {@code Thread.sleep}) para que la ventana no
 * se congele y se alcance a ver qué pasó. Si los dos lados son personas
 * (jugador contra jugador), entre turnos se muestra la
 * {@link PantallaCambioTurno}, que oculta la mano del jugador anterior.
 *
 * <p>
 * Responsable: Santiago.
 */
public final class ControladorDuelo {

	private static final Duration PAUSA_ANTES_DE_LA_IA = Duration.millis(700);
	private static final Duration PAUSA_DESPUES_DE_LA_IA = Duration.millis(1400);
	private static final Duration PAUSA_AL_TERMINAR = Duration.millis(1600);
	private static final int EVENTOS_VISIBLES = 9;
	private final Navegador navegador;
	private final Duelo duelo;
	private final EstrategiaIA iaUno;
	private final EstrategiaIA iaDos;
	private final Consumer<Duelo> alTerminar;
	private final PantallaDuelo pantalla;
	private final String[] eventos = new String[EVENTOS_VISIBLES];
	private int cantidadEventos;
	private int manoSeleccionada = -1;
	private int atacanteSeleccionado = -1;
	/**
	 * Posición elegida cuando el campo está lleno y falta elegir el monstruo a
	 * reemplazar.
	 */
	private Posicion invocacionPendiente;
	private String mensaje = "";
	/** La máquina está jugando o el duelo terminó: se ignoran los clics. */
	private boolean ocupado;
	private boolean abandonado;

	/**
	 * Prepara el duelo y su pantalla; se muestra con {@link #iniciar()}.
	 * Complejidad: O(1).
	 *
	 * @param iaUno      estrategia del duelista uno, o {@code null} si lo juega una
	 *                   persona
	 * @param iaDos      estrategia del duelista dos, o {@code null} si lo juega una
	 *                   persona
	 * @param alTerminar qué hacer cuando hay ganador (por ejemplo, mostrar el
	 *                   resultado)
	 * @throws IllegalArgumentException si falta el navegador, el duelo o
	 *                                  {@code alTerminar}, o si los dos lados son
	 *                                  máquinas
	 */
	public ControladorDuelo(Navegador navegador, Duelo duelo, EstrategiaIA iaUno, EstrategiaIA iaDos,
			Consumer<Duelo> alTerminar) {
		if (navegador == null || duelo == null || alTerminar == null) {
			throw new IllegalArgumentException("El duelo necesita navegador, duelo y qué hacer al terminar");
		}
		if (iaUno != null && iaDos != null) {
			throw new IllegalArgumentException("Al menos un lado del duelo debe ser una persona");
		}
		this.navegador = navegador;
		this.duelo = duelo;
		this.iaUno = iaUno;
		this.iaDos = iaDos;
		this.alTerminar = alTerminar;
		this.pantalla = new PantallaDuelo(new PantallaDuelo.Acciones(this::actualizarVista, this::clicMano,
				this::clicZonaPropia, this::clicZonaRival, () -> invocar(Posicion.ATAQUE),
				() -> invocar(Posicion.DEFENSA), this::atacarDirecto, this::terminarTurno, this::abandonar));
		leerEventos();
	}

	/**
	 * Muestra el duelo. En jugador contra jugador empieza con el aviso de turno.
	 * Complejidad: O(1).
	 */
	public void iniciar() {
		navegador.ir(pantalla);
		if (esJugadorContraJugador()) {
			mostrarCambioDeTurno();
		} else {
			mensaje = "Elige una carta de tu mano para invocarla";
			siguienteTurno();
		}
	}

	/**
	 * Traduce el estado del modelo a datos exclusivamente de presentación. Las
	 * cartas rivales boca abajo se convierten en reversos sin nombre, estadísticas
	 * ni ruta de imagen.
	 *
	 * @param vista tablero que recibe la actualización
	 */
	private void actualizarVista(PantallaDuelo vista) {
		Duelista arriba = getArriba();
		Duelista abajo = getAbajo();
		boolean eligiendoObjetivo = atacanteSeleccionado != -1;
		VistaCarta.Datos[] campoRival = new VistaCarta.Datos[Duelista.ZONAS_DE_MONSTRUO];
		VistaCarta.Datos[] campoPropio = new VistaCarta.Datos[Duelista.ZONAS_DE_MONSTRUO];
		VistaCarta.Datos[] mano = new VistaCarta.Datos[Duelista.TAMANIO_MANO];

		for (int zona = 0; zona < campoRival.length; zona++) {
			MonstruoEnCampo rival = arriba.getMonstruo(zona);
			if (rival == null) {
				campoRival[zona] = VistaCarta.Datos.vacia();
			} else if (rival.estaBocaAbajo()) {
				campoRival[zona] = VistaCarta.Datos.reverso(eligiendoObjetivo);
			} else {
				campoRival[zona] = datosCarta(rival.getCarta(), rival.getPosicion() == Posicion.DEFENSA, false, false,
						eligiendoObjetivo, false);
			}

			MonstruoEnCampo propio = abajo.getMonstruo(zona);
			campoPropio[zona] = propio == null ? VistaCarta.Datos.vacia()
					: datosCarta(propio.getCarta(), propio.getPosicion() == Posicion.DEFENSA, propio.estaBocaAbajo(),
							zona == atacanteSeleccionado, esperaZonaParaReemplazar(), propio.yaAtaco());
		}
		for (int posicion = 0; posicion < mano.length; posicion++) {
			Carta carta = abajo.getCartaEnMano(posicion);
			mano[posicion] = carta == null ? VistaCarta.Datos.vacia()
					: datosCarta(carta, false, false, posicion == manoSeleccionada, false, false);
		}
		vista.mostrarResumen(resumen(arriba) + "   ·   Mano " + arriba.cartasEnMano(), resumen(abajo), getEstado(),
				getMensaje());
		vista.mostrarCartas(campoRival, campoPropio, mano);
		vista.mostrarAcciones(puedeInvocar(), puedeAtacarDirecto(), puedeTerminarTurno());
		vista.mostrarRegistro(getEventos());
	}

	/**
	 * Prepara una carta cuyos datos se pueden mostrar. El acabado platinado utiliza
	 * el mismo criterio visual de la colección: ATK mayor o igual que 2500. No
	 * modifica las reglas del combate.
	 */
	private static VistaCarta.Datos datosCarta(Carta carta, boolean enDefensa, boolean oculta, boolean seleccionada,
			boolean objetivo, boolean gastada) {
		return new VistaCarta.Datos(VistaCarta.Contenido.CARTA, carta.getNombre(), carta.getTipo().name(),
				carta.getRutaImagen(), carta.getAtaque(), carta.getDefensa(), enDefensa, oculta, seleccionada, objetivo,
				gastada, carta.getAtaque() >= 2500);
	}

	/** Construye el resumen textual que necesita el tablero. */
	private static String resumen(Duelista duelista) {
		return duelista.getNombre() + "   ·   PV " + duelista.getPuntosDeVida() + "   ·   Mazo "
				+ duelista.cartasEnMazo();
	}

	// Consultas utilizadas para preparar los datos de presentación

	/**
	 * El lado que se dibuja abajo: la persona o, en jugador contra jugador, quien
	 * tiene el turno. Complejidad: O(1).
	 */
	public Duelista getAbajo() {
		if (esJugadorContraJugador()) {
			return duelo.getActual();
		}
		return iaUno == null ? duelo.getDuelistaUno() : duelo.getDuelistaDos();
	}

	/** El lado que se dibuja arriba (su mano no se muestra). Complejidad: O(1). */
	public Duelista getArriba() {
		return getAbajo() == duelo.getDuelistaUno() ? duelo.getDuelistaDos() : duelo.getDuelistaUno();
	}

	/** Indica si la persona de abajo puede hacer algo ahora. Complejidad: O(1). */
	public boolean puedeActuar() {
		return !ocupado && !abandonado && !duelo.estaTerminado() && duelo.getActual() == getAbajo()
				&& estrategiaDe(duelo.getActual()) == null;
	}

	/** Complejidad: O(1). */
	public boolean puedeInvocar() {
		return puedeActuar() && manoSeleccionada != -1 && !duelo.yaInvocoEnEsteTurno();
	}

	/** Complejidad: O(1). */
	public boolean puedeAtacarDirecto() {
		return puedeActuar() && atacanteSeleccionado != -1 && !getArriba().tieneMonstruos();
	}

	/** Complejidad: O(1). */
	public boolean puedeTerminarTurno() {
		return puedeActuar();
	}

	/** Complejidad: O(1). */
	public int getManoSeleccionada() {
		return manoSeleccionada;
	}

	/** Complejidad: O(1). */
	public int getAtacanteSeleccionado() {
		return atacanteSeleccionado;
	}

	/**
	 * Indica si se espera que la persona elija qué monstruo propio reemplazar.
	 * Complejidad: O(1).
	 */
	public boolean esperaZonaParaReemplazar() {
		return invocacionPendiente != null;
	}

	/** Texto con el turno o el ganador. Complejidad: O(1). */
	public String getEstado() {
		if (duelo.estaTerminado()) {
			return "Ganó " + duelo.getGanador().getNombre();
		}
		return "Turno " + duelo.getNumeroDeTurno() + " · juega " + duelo.getActual().getNombre();
	}

	/**
	 * Indicación para la persona: qué hacer ahora o qué pasó con su última acción.
	 * Complejidad: O(1).
	 */
	public String getMensaje() {
		return mensaje;
	}

	/**
	 * Los últimos eventos del duelo, del más antiguo al más reciente. Complejidad:
	 * O(1).
	 */
	public String[] getEventos() {
		String[] copia = new String[cantidadEventos];
		System.arraycopy(eventos, 0, copia, 0, cantidadEventos);
		return copia;
	}

	// Lo que hace la persona

	/** Elige (o suelta) una carta de la mano para invocarla. Complejidad: O(1). */
	public void clicMano(int posicion) {
		if (!puedeActuar() || getAbajo().getCartaEnMano(posicion) == null) {
			return;
		}
		limpiarSeleccion();
		if (duelo.yaInvocoEnEsteTurno()) {
			mensaje = "Ya invocaste un monstruo en este turno";
		} else {
			manoSeleccionada = posicion;
			mensaje = "Invoca " + getAbajo().getCartaEnMano(posicion).getNombre() + " en ataque o en defensa";
		}
		pantalla.refrescar();
	}

	/**
	 * Invoca la carta elegida en la primera zona libre; si el campo está lleno,
	 * pide la zona. Complejidad: O(1).
	 */
	public void invocar(Posicion posicion) {
		if (!puedeInvocar()) {
			return;
		}
		int zona = getAbajo().primeraZonaLibre();
		if (zona == -1) {
			invocacionPendiente = posicion;
			mensaje = "Tu campo está lleno: haz clic en el monstruo que quieres reemplazar";
			pantalla.refrescar();
			return;
		}
		hacerInvocacion(zona, posicion);
	}

	/**
	 * Clic en una zona propia: reemplazar (si se estaba invocando) o elegir el
	 * atacante. Complejidad: O(1).
	 */
	public void clicZonaPropia(int zona) {
		if (!puedeActuar()) {
			return;
		}
		if (invocacionPendiente != null) {
			hacerInvocacion(zona, invocacionPendiente);
			return;
		}
		MonstruoEnCampo monstruo = getAbajo().getMonstruo(zona);
		if (monstruo == null) {
			return;
		}
		int anterior = atacanteSeleccionado;
		limpiarSeleccion();
		if (!duelo.sePuedeAtacarEnEsteTurno()) {
			mensaje = "Quien empieza el duelo no ataca en su primer turno";
		} else if (monstruo.getPosicion() == Posicion.DEFENSA) {
			mensaje = "Un monstruo en defensa no puede atacar";
		} else if (monstruo.yaAtaco()) {
			mensaje = monstruo.getCarta().getNombre() + " ya atacó en este turno";
		} else if (anterior != zona) {
			atacanteSeleccionado = zona;
			mensaje = getArriba().tieneMonstruos()
					? "Elige el monstruo rival que va a atacar " + monstruo.getCarta().getNombre()
					: "El rival no tiene monstruos: usa Ataque directo";
		} else {
			mensaje = "";
		}
		pantalla.refrescar();
	}

	/**
	 * Clic en una zona rival: si hay un atacante elegido, lo ataca. Complejidad:
	 * O(1).
	 */
	public void clicZonaRival(int zona) {
		if (!puedeActuar() || atacanteSeleccionado == -1 || getArriba().getMonstruo(zona) == null) {
			return;
		}
		int atacante = atacanteSeleccionado;
		hacerAtaque(() -> duelo.atacar(atacante, zona));
	}

	/** Ataca directo con el atacante elegido. Complejidad: O(1). */
	public void atacarDirecto() {
		if (!puedeAtacarDirecto()) {
			return;
		}
		int atacante = atacanteSeleccionado;
		hacerAtaque(() -> duelo.atacarDirecto(atacante));
	}

	/** Termina el turno de la persona. Complejidad: O(1). */
	public void terminarTurno() {
		if (!puedeTerminarTurno()) {
			return;
		}
		limpiarSeleccion();
		duelo.terminarTurno();
		leerEventos();
		if (duelo.estaTerminado()) {
			terminar();
		} else if (esJugadorContraJugador()) {
			mostrarCambioDeTurno();
		} else {
			siguienteTurno();
		}
	}

	/**
	 * Sale del duelo sin resultado y vuelve a la pantalla anterior. Complejidad:
	 * O(1).
	 */
	public void abandonar() {
		abandonado = true;
		navegador.volver();
	}

	// Pasos internos

	private void hacerInvocacion(int zona, Posicion posicion) {
		int posicionMano = manoSeleccionada;
		limpiarSeleccion();
		try {
			duelo.invocar(posicionMano, zona, posicion);
			mensaje = duelo.sePuedeAtacarEnEsteTurno()
					? "Elige un monstruo tuyo en ataque para atacar, o termina el turno"
					: "En el turno 1 no se ataca: termina el turno";
		} catch (IllegalStateException | IllegalArgumentException e) {
			mensaje = e.getMessage();
		}
		leerEventos();
		pantalla.refrescar();
	}

	private void hacerAtaque(Supplier<ResultadoCombate> ataque) {
		limpiarSeleccion();
		try {
			mensaje = describir(ataque.get());
		} catch (IllegalStateException | IllegalArgumentException e) {
			mensaje = e.getMessage();
		}
		leerEventos();
		if (duelo.estaTerminado()) {
			terminar();
		} else {
			pantalla.refrescar();
		}
	}

	/**
	 * Si el turno es de la máquina, lo juega con pausas; si es de la persona, la
	 * espera.
	 */
	private void siguienteTurno() {
		EstrategiaIA ia = estrategiaDe(duelo.getActual());
		if (ia == null) {
			pantalla.refrescar();
			return;
		}
		ocupado = true;
		mensaje = duelo.getActual().getNombre() + " está jugando…";
		pantalla.refrescar();
		pausa(PAUSA_ANTES_DE_LA_IA, () -> {
			ia.jugarTurno(duelo);
			leerEventos();
			if (duelo.estaTerminado()) {
				terminar();
				return;
			}
			pantalla.refrescar();
			pausa(PAUSA_DESPUES_DE_LA_IA, () -> {
				duelo.terminarTurno();
				leerEventos();
				ocupado = false;
				if (duelo.estaTerminado()) {
					terminar();
				} else {
					mensaje = "Tu turno: elige una carta de tu mano";
					siguienteTurno();
				}
			});
		});
	}

	private void mostrarCambioDeTurno() {
		String nombre = duelo.getActual().getNombre();
		mensaje = "Tu turno, " + nombre + ": elige una carta de tu mano";
		navegador.reemplazar(new PantallaCambioTurno(nombre, () -> navegador.reemplazar(pantalla)));
	}

	private void terminar() {
		ocupado = true;
		mensaje = "¡" + duelo.getGanador().getNombre() + " gana el duelo!";
		pantalla.refrescar();
		pausa(PAUSA_AL_TERMINAR, () -> alTerminar.accept(duelo));
	}

	/**
	 * Ejecuta la acción después de la pausa, salvo que la persona haya abandonado
	 * el duelo.
	 */
	private void pausa(Duration duracion, Runnable accion) {
		PauseTransition pausa = new PauseTransition(duracion);
		pausa.setOnFinished(evento -> {
			if (!abandonado) {
				accion.run();
			}
		});
		pausa.play();
	}

	private String describir(ResultadoCombate resultado) {
		if (resultado.esAtaqueDirecto()) {
			return "Ataque directo: " + getArriba().getNombre() + " pierde " + resultado.getDanioAlDefensor()
					+ " puntos de vida";
		}
		StringBuilder texto = new StringBuilder();
		if (resultado.destruyeObjetivo()) {
			texto.append("Destruiste al monstruo rival. ");
		}
		if (resultado.destruyeAtacante()) {
			texto.append("Tu monstruo fue destruido. ");
		}
		if (resultado.getDanioAlDefensor() > 0) {
			texto.append("El rival pierde ").append(resultado.getDanioAlDefensor()).append(". ");
		}
		if (resultado.getDanioAlAtacante() > 0) {
			texto.append("Pierdes ").append(resultado.getDanioAlAtacante()).append(". ");
		}
		return texto.length() == 0 ? "El ataque no tuvo efecto" : texto.toString().trim();
	}

	private void leerEventos() {
		while (duelo.hayEventos()) {
			String evento = duelo.siguienteEvento();
			if (cantidadEventos == eventos.length) {
				System.arraycopy(eventos, 1, eventos, 0, eventos.length - 1);
				cantidadEventos--;
			}
			eventos[cantidadEventos++] = evento;
		}
	}

	private void limpiarSeleccion() {
		manoSeleccionada = -1;
		atacanteSeleccionado = -1;
		invocacionPendiente = null;
	}

	private EstrategiaIA estrategiaDe(Duelista duelista) {
		return duelista == duelo.getDuelistaUno() ? iaUno : iaDos;
	}

	private boolean esJugadorContraJugador() {
		return iaUno == null && iaDos == null;
	}
}
