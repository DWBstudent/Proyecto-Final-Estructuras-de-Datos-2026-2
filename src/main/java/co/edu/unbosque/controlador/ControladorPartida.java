package co.edu.unbosque.controlador;

import java.util.Random;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.Contrincante;
import co.edu.unbosque.modelo.entidades.Jugador;
import co.edu.unbosque.modelo.juego.Campania;
import co.edu.unbosque.modelo.juego.Catalogo;
import co.edu.unbosque.modelo.juego.Deck;
import co.edu.unbosque.modelo.juego.DeckAleatorio;
import co.edu.unbosque.modelo.juego.Duelista;
import co.edu.unbosque.modelo.juego.Duelo;
import co.edu.unbosque.modelo.juego.Recompensa;
import co.edu.unbosque.vista.PantallaResultado;
import co.edu.unbosque.vista.PantallaSeleccionRival;

/**
 * Los tres modos de duelo del menú:
 * <ul>
 * <li><b>Modo historia:</b> los 3 rivales en orden con la {@link Campania}; al ganar se
 *     recibe 1 recompensa y se pasa al siguiente; al perder se repite el mismo.</li>
 * <li><b>Modo libre:</b> cualquiera de los 3 rivales, para practicar; no da recompensa ni
 *     avanza la campaña.</li>
 * <li><b>Jugador contra jugador:</b> dos personas en el mismo computador, cada una con 40
 *     cartas al azar del catálogo ({@link DeckAleatorio}).</li>
 * </ul>
 * Abre la selección de rival, arma el duelo, se lo pasa a un {@link ControladorDuelo} y al
 * final muestra la {@link PantallaResultado}.
 *
 * <p>Si el jugador todavía no tiene cartas, al entrar al modo historia o al modo libre recibe
 * el Deck inicial de 40 cartas ({@link Campania#deckInicial(Catalogo)}).
 *
 * <p>Responsable: Santiago.
 */
public class ControladorPartida {

    private static final String[] NOMBRES_JCJ = {"Jugador 1", "Jugador 2"};

    private final Navegador navegador;
    private final Jugador jugador;
    private final Catalogo catalogo;
    private final Random random;

    private Contrincante[] rivales;
    private Campania campania;
    private boolean deckInicialRecienEntregado;

    /**
     * Complejidad: O(1). Los rivales y la campaña se crean la primera vez que se usan.
     *
     * @param random para barajar, elegir recompensas y armar los Decks de jugador contra jugador
     * @throws IllegalArgumentException si falta algún argumento
     */
    public ControladorPartida(Navegador navegador, Jugador jugador, Catalogo catalogo, Random random) {
        if (navegador == null || jugador == null || catalogo == null || random == null) {
            throw new IllegalArgumentException("La partida necesita navegador, jugador, catálogo y Random");
        }
        this.navegador = navegador;
        this.jugador = jugador;
        this.catalogo = catalogo;
        this.random = random;
    }

    // Entradas desde el menú

    /** Abre la selección de rival del modo historia. Complejidad: O(n), con n = 120 cartas de los rivales. */
    public void abrirModoHistoria() {
        entregarDeckInicialSiHaceFalta();
        if (campania == null) {
            campania = new Campania(jugador, catalogo);
        }
        navegador.ir(new PantallaSeleccionRival("Modo historia",
                "Vence a los 3 rivales en orden. Cada victoria te da 1 carta de la lista del rival.",
                this::mostrarRivalesHistoria, this::elegirRivalHistoria, navegador::volver));
    }

    /** Abre la selección de rival del modo libre. Complejidad: O(n), con n = 120 cartas de los rivales. */
    public void abrirModoLibre() {
        entregarDeckInicialSiHaceFalta();
        navegador.ir(new PantallaSeleccionRival("Modo libre",
                "Duelo de práctica contra cualquier rival. No da recompensas ni avanza el modo historia.",
                this::mostrarRivalesLibre, this::elegirRivalLibre, navegador::volver));
    }

    /** Empieza un duelo entre dos personas con Decks aleatorios. Complejidad: O(n), con n = 300 copias. */
    public void abrirJugadorContraJugador() {
        Duelo duelo = new Duelo(NOMBRES_JCJ[0], DeckAleatorio.generar(catalogo, random),
                NOMBRES_JCJ[1], DeckAleatorio.generar(catalogo, random), random);
        new ControladorDuelo(navegador, duelo, null, null, this::terminarJugadorContraJugador).iniciar();
    }

    // Modo historia

    private void mostrarRivalesHistoria(PantallaSeleccionRival vista) {
        Contrincante[] todos = rivales();
        String[] nombres = new String[todos.length];
        String[] estados = new String[todos.length];
        boolean[] habilitados = new boolean[todos.length];
        int vencidos = jugador.getRivalesVencidos();
        for (int i = 0; i < todos.length; i++) {
            nombres[i] = todos[i].toString();
            if (todos[i].getNumero() <= vencidos) {
                estados[i] = "Vencido";
            } else if (todos[i].getNumero() == vencidos + 1) {
                estados[i] = "Siguiente rival";
                habilitados[i] = deckListo();
            } else {
                estados[i] = "Bloqueado";
            }
        }
        String mensaje;
        if (campania.estaCompleta()) {
            mensaje = "¡Completaste el modo historia! Puedes seguir practicando en el modo libre.";
        } else {
            mensaje = mensajeDelDeck("Elige al siguiente rival para empezar el duelo.");
        }
        vista.mostrarRivales(nombres, estados, habilitados, mensaje);
    }

    private void elegirRivalHistoria(int indice) {
        if (campania.estaCompleta() || !deckListo()
                || campania.getRivalActual().getNumero() != rivales()[indice].getNumero()) {
            return;
        }
        Duelo duelo = campania.crearDuelo(jugador.getDeck().aArreglo(), random);
        new ControladorDuelo(navegador, duelo, null, campania.estrategiaDelRivalActual(),
                this::terminarHistoria).iniciar();
    }

    private void terminarHistoria(Duelo duelo) {
        Contrincante rival = campania.getRivalActual();
        Recompensa recompensa = campania.registrarResultado(duelo, random);
        String[] lineas;
        if (recompensa == null) {
            lineas = new String[] {rival.getNombre() + " te venció. Puedes intentarlo otra vez.",
                    resumenDelDuelo(duelo)};
            mostrarResultado("Perdiste", lineas);
            return;
        }
        String cierre = campania.estaCompleta()
                ? "¡Completaste el modo historia!"
                : "Siguiente rival: " + campania.getRivalActual().getNombre();
        lineas = new String[] {"Venciste a " + rival.getNombre() + ".", recompensa.mensaje() + ".",
                resumenDelDuelo(duelo), cierre};
        mostrarResultado("¡Ganaste!", lineas);
    }

    // Modo libre

    private void mostrarRivalesLibre(PantallaSeleccionRival vista) {
        Contrincante[] todos = rivales();
        String[] nombres = new String[todos.length];
        String[] estados = new String[todos.length];
        boolean[] habilitados = new boolean[todos.length];
        for (int i = 0; i < todos.length; i++) {
            nombres[i] = todos[i].toString();
            estados[i] = "Disponible";
            habilitados[i] = deckListo();
        }
        vista.mostrarRivales(nombres, estados, habilitados, mensajeDelDeck("Elige un rival para practicar."));
    }

    private void elegirRivalLibre(int indice) {
        if (!deckListo()) {
            return;
        }
        Contrincante rival = rivales()[indice];
        Duelo duelo = new Duelo(jugador.getNombre(), jugador.getDeck().aArreglo(), rival.getNombre(),
                rival.getDeck(), random);
        new ControladorDuelo(navegador, duelo, null, Campania.estrategiaPara(rival.getNumero()),
                this::terminarLibre).iniciar();
    }

    private void terminarLibre(Duelo duelo) {
        boolean gano = duelo.getGanador() == duelo.getDuelistaUno();
        mostrarResultado(gano ? "¡Ganaste!" : "Perdiste", new String[] {
            "Ganó " + duelo.getGanador().getNombre() + ".",
            resumenDelDuelo(duelo),
            "Duelo de práctica: no hay recompensa."});
    }

    // Jugador contra jugador

    private void terminarJugadorContraJugador(Duelo duelo) {
        mostrarResultado("¡Ganó " + duelo.getGanador().getNombre() + "!", new String[] {resumenDelDuelo(duelo)});
    }

    // Apoyo

    /** El resultado reemplaza al duelo: "Continuar" vuelve a la pantalla de antes del duelo. */
    private void mostrarResultado(String titulo, String[] lineas) {
        navegador.reemplazar(new PantallaResultado(titulo, lineas, navegador::volver));
    }

    private String resumenDelDuelo(Duelo duelo) {
        Duelista uno = duelo.getDuelistaUno();
        Duelista dos = duelo.getDuelistaDos();
        Duelista perdedor = duelo.getGanador() == uno ? dos : uno;
        String como = perdedor.estaDerrotado() ? "sin puntos de vida" : "sin cartas para robar";
        return "Puntos de vida: " + uno.getNombre() + " " + uno.getPuntosDeVida() + " · " + dos.getNombre() + " "
                + dos.getPuntosDeVida() + ". " + perdedor.getNombre() + " quedó " + como + " en el turno "
                + duelo.getNumeroDeTurno() + ".";
    }

    private boolean deckListo() {
        return jugador.getDeck().estaCompleto();
    }

    private String mensajeDelDeck(String siEstaListo) {
        if (!deckListo()) {
            return "Tu Deck tiene " + jugador.getDeck().tamanio() + " de " + Deck.TAMANIO
                    + " cartas. Complétalo en Chest y Deck para poder duelar.";
        }
        if (deckInicialRecienEntregado) {
            deckInicialRecienEntregado = false;
            return "Recibiste tu Deck inicial de " + Deck.TAMANIO + " cartas. " + siEstaListo;
        }
        return siEstaListo;
    }

    private Contrincante[] rivales() {
        if (rivales == null) {
            rivales = new Contrincante[Jugador.TOTAL_RIVALES];
            for (int i = 0; i < rivales.length; i++) {
                rivales[i] = Contrincante.crear(i + 1, catalogo);
            }
        }
        return rivales;
    }

    /**
     * Si el jugador no tiene ninguna carta, le da el Deck inicial: cada carta entra al Chest
     * (así queda descubierta) y pasa al Deck.
     */
    private void entregarDeckInicialSiHaceFalta() {
        if (jugador.getChest().cartasDistintas() > 0 || jugador.getDeck().tamanio() > 0) {
            return;
        }
        for (Carta carta : Campania.deckInicial(catalogo)) {
            if (jugador.recibirCarta(carta) && jugador.getDeck().agregar(carta)) {
                jugador.getChest().quitar(carta.getId());
            }
        }
        deckInicialRecienEntregado = true;
    }
}
