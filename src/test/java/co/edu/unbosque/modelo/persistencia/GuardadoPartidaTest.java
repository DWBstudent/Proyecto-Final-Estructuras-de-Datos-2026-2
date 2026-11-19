package co.edu.unbosque.modelo.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.entidades.Jugador;
import co.edu.unbosque.modelo.juego.Catalogo;

class GuardadoPartidaTest {

    private static final Catalogo CATALOGO = Catalogo.cargar();

    /** Jugador con Deck completo, 2 copias en el Chest, 2 rivales vencidos y una contraseña usada. */
    private static Jugador jugadorConAvance() {
        Jugador jugador = new Jugador("Daniel");
        for (int id = 1; id <= 40; id++) {
            jugador.recibirCarta(CATALOGO.buscarPorId(id));
            jugador.moverCartaAlDeck(id);
        }
        jugador.recibirCarta(CATALOGO.buscarPorId(41));
        jugador.recibirCarta(CATALOGO.buscarPorId(41));
        jugador.canjearContrasena(CATALOGO.buscarPorId(100).getContrasena(), CATALOGO);
        jugador.registrarRivalVencido();
        jugador.registrarRivalVencido();
        return jugador;
    }

    /** Partida mínima con el Deck y el Chest indicados. */
    private static String partida(String deck, String chest) {
        return "version=1\nnombre=Daniel\nrivalesVencidos=0\ndescubiertas=\ncontrasenas=\ndeck=" + deck
                + "\nchest=" + chest + "\n";
    }

    private static Path carpetaTemporal() {
        try {
            return Files.createTempDirectory("partida");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // Caso normal

    @Test
    void alCargarElTextoQuedaLaMismaPartida() {
        Jugador original = jugadorConAvance();

        Jugador cargado = GuardadoPartida.desdeTexto(GuardadoPartida.aTexto(original), CATALOGO);

        assertEquals(GuardadoPartida.aTexto(original), GuardadoPartida.aTexto(cargado));
        assertEquals("Daniel", cargado.getNombre());
        assertEquals(2, cargado.getRivalesVencidos());
        assertEquals(40, cargado.getDeck().tamanio());
        assertEquals(2, cargado.getChest().copiasDe(41));
        assertEquals(1, cargado.getChest().copiasDe(100));
        assertTrue(cargado.contrasenaCanjeada(100));
        assertEquals(original.getCantidadDescubiertas(), cargado.getCantidadDescubiertas());
    }

    @Test
    void guardaYCargaDesdeUnArchivoSinDejarElTemporal() {
        Path archivo = carpetaTemporal().resolve("partida.txt");
        Jugador original = jugadorConAvance();

        GuardadoPartida.guardar(original, archivo);
        Jugador cargado = GuardadoPartida.cargar(archivo, CATALOGO);

        assertEquals(GuardadoPartida.aTexto(original), GuardadoPartida.aTexto(cargado));
        assertFalse(Files.exists(archivo.resolveSibling("partida.txt.tmp")));
    }

    @Test
    void guardarOtraVezReemplazaLaPartidaAnterior() {
        Path archivo = carpetaTemporal().resolve("partida.txt");
        GuardadoPartida.guardar(new Jugador("Daniel"), archivo);

        GuardadoPartida.guardar(jugadorConAvance(), archivo);

        assertEquals(2, GuardadoPartida.cargar(archivo, CATALOGO).getRivalesVencidos());
    }

    // Caso límite

    @Test
    void unJugadorNuevoSinCartasTambienSeGuarda() {
        Jugador cargado = GuardadoPartida.desdeTexto(GuardadoPartida.aTexto(new Jugador("Nuevo")), CATALOGO);

        assertEquals(0, cargado.getDeck().tamanio());
        assertEquals(0, cargado.getChest().cartasDistintas());
        assertEquals(0, cargado.getCantidadDescubiertas());
    }

    @Test
    void creaLaCarpetaSiNoExiste() {
        Path archivo = carpetaTemporal().resolve("una").resolve("carpeta").resolve("partida.txt");

        GuardadoPartida.guardar(new Jugador("Daniel"), archivo);

        assertTrue(Files.exists(archivo));
    }

    @Test
    void aceptaFinesDeLineaDeWindowsYLineasEnBlanco() {
        String texto = partida("1,2", "3").replace("\n", "\r\n") + "\r\n\r\n";

        Jugador cargado = GuardadoPartida.desdeTexto(texto, CATALOGO);

        assertEquals(2, cargado.getDeck().tamanio());
        assertEquals(1, cargado.getChest().copiasDe(3));
    }

    // Caso de error

    @Test
    void rechazaDatosFaltantesRepetidosODesconocidos() {
        assertThrows(PartidaInvalidaException.class, () -> GuardadoPartida.desdeTexto("", CATALOGO));
        assertThrows(PartidaInvalidaException.class,
                () -> GuardadoPartida.desdeTexto(partida("", "").replace("chest=\n", ""), CATALOGO));
        assertThrows(PartidaInvalidaException.class,
                () -> GuardadoPartida.desdeTexto(partida("", "") + "chest=\n", CATALOGO));
        assertThrows(PartidaInvalidaException.class,
                () -> GuardadoPartida.desdeTexto(partida("", "") + "oro=999\n", CATALOGO));
        assertThrows(PartidaInvalidaException.class,
                () -> GuardadoPartida.desdeTexto(partida("", "").replace("version=1", "version=2"), CATALOGO));
    }

    @Test
    void rechazaIdsYNumerosInvalidos() {
        assertThrows(PartidaInvalidaException.class, () -> GuardadoPartida.desdeTexto(partida("0", ""), CATALOGO));
        assertThrows(PartidaInvalidaException.class, () -> GuardadoPartida.desdeTexto(partida("", "101"), CATALOGO));
        assertThrows(PartidaInvalidaException.class, () -> GuardadoPartida.desdeTexto(partida("x", ""), CATALOGO));
        assertThrows(PartidaInvalidaException.class,
                () -> GuardadoPartida.desdeTexto(partida("", "").replace("rivalesVencidos=0", "rivalesVencidos=4"), CATALOGO));
    }

    @Test
    void rechazaPartidasQueRompenLasReglas() {
        assertThrows(PartidaInvalidaException.class,
                () -> GuardadoPartida.desdeTexto(partida("7,7", "7,7"), CATALOGO));   // 4 copias
        StringBuilder deckDe41 = new StringBuilder("1");
        for (int id = 2; id <= 41; id++) {
            deckDe41.append(',').append(id);
        }
        assertThrows(PartidaInvalidaException.class,
                () -> GuardadoPartida.desdeTexto(partida(deckDe41.toString(), ""), CATALOGO));
    }

    @Test
    void rechazaArchivoInexistenteYArgumentosNulos() {
        Path noExiste = carpetaTemporal().resolve("no-existe.txt");

        assertThrows(UncheckedIOException.class, () -> GuardadoPartida.cargar(noExiste, CATALOGO));
        assertThrows(IllegalArgumentException.class, () -> GuardadoPartida.guardar(null, noExiste));
        assertThrows(IllegalArgumentException.class, () -> GuardadoPartida.desdeTexto("version=1", null));
    }
}