package co.edu.unbosque.modelo.persistencia;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.Jugador;
import co.edu.unbosque.modelo.juego.Catalogo;

/**
 * Guarda y carga la partida del jugador en un archivo de texto, una línea por dato:
 * <pre>
 * version=1
 * nombre=Jugador
 * rivalesVencidos=1
 * descubiertas=1,2,3
 * contrasenas=100
 * deck=1,2,3
 * chest=41,41
 * </pre>
 * Las cartas se guardan por id; al cargar se buscan en el catálogo.
 *
 * <p>Igual que {@link LectorCartasCsv}, separa <b>el archivo</b> ({@link #guardar}, {@link #cargar})
 * del <b>texto</b> ({@link #aTexto}, {@link #desdeTexto}) para poder probar el formato sin archivos.
 *
 * <p>Responsable: Daniel.
 */
public final class GuardadoPartida {

    /** Versión del formato. Si algún día cambia, las partidas viejas se rechazan con un mensaje claro. */
    public static final int VERSION = 1;

    private static final String[] CLAVES = {"version", "nombre", "rivalesVencidos", "descubiertas", "contrasenas", "deck", "chest"};

    private GuardadoPartida() {
        // Solo tiene métodos estáticos.
    }

    /** Archivo donde se guarda la partida: {@code <carpeta del usuario>/.juego-de-cartas/partida.txt}. */
    public static Path archivoPorDefecto() {
        return Path.of(System.getProperty("user.home"), ".juego-de-cartas", "partida.txt");
    }

    /**
     * Escribe la partida con <b>escritura atómica</b>: primero en un archivo temporal y luego
     * lo cambia por el real en un solo paso. Si el juego se cierra a mitad de la escritura,
     * la partida anterior queda intacta. Complejidad: O(n), con n cartas del jugador.
     *
     * @throws IllegalArgumentException si algún argumento es {@code null}
     * @throws UncheckedIOException si no se puede escribir
     */
    public static void guardar(Jugador jugador, Path archivo) {
        if (jugador == null || archivo == null) {
            throw new IllegalArgumentException("Se necesitan el jugador y el archivo");
        }
        Path temporal = archivo.resolveSibling(archivo.getFileName() + ".tmp");
        try {
            Path carpeta = archivo.toAbsolutePath().getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }
            Files.writeString(temporal, aTexto(jugador), StandardCharsets.UTF_8);
            try {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                // Algunos discos no permiten el cambio atómico; se reemplaza de la forma normal.
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar la partida en " + archivo, e);
        }
    }

    /**
     * Lee la partida guardada. Complejidad: O(n), con n cartas guardadas.
     *
     * @throws IllegalArgumentException si algún argumento es {@code null}
     * @throws UncheckedIOException si el archivo no existe o no se puede leer
     * @throws PartidaInvalidaException si el contenido está dañado
     */
    public static Jugador cargar(Path archivo, Catalogo catalogo) {
        if (archivo == null || catalogo == null) {
            throw new IllegalArgumentException("Se necesitan el archivo y el catálogo");
        }
        try {
            return desdeTexto(Files.readString(archivo, StandardCharsets.UTF_8), catalogo);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer la partida en " + archivo, e);
        }
    }

    /** Convierte la partida en texto. Complejidad: O(n), con n cartas del jugador. */
    public static String aTexto(Jugador jugador) {
        if (jugador == null) {
            throw new IllegalArgumentException("El jugador no puede ser null");
        }
        StringBuilder descubiertas = new StringBuilder();
        StringBuilder contrasenas = new StringBuilder();
        for (int id = 1; id <= Carta.ID_MAXIMO; id++) {
            if (jugador.estaDescubierta(id)) {
                agregar(descubiertas, id);
            }
            if (jugador.contrasenaCanjeada(id)) {
                agregar(contrasenas, id);
            }
        }
        return "version=" + VERSION + "\n"
                + "nombre=" + jugador.getNombre().replace('\n', ' ').replace('\r', ' ') + "\n"
                + "rivalesVencidos=" + jugador.getRivalesVencidos() + "\n"
                + "descubiertas=" + descubiertas + "\n"
                + "contrasenas=" + contrasenas + "\n"
                + "deck=" + ids(jugador.getDeck().cartasActuales()) + "\n"
                + "chest=" + ids(jugador.getChest().cartasActuales()) + "\n";
    }

    /**
     * Reconstruye la partida desde el texto. Usa los mismos métodos de {@link Jugador}
     * que el juego, así que también se respetan las reglas: máximo 3 copias y 40 cartas
     * en el Deck. Complejidad: O(n), con n cartas guardadas.
     *
     * @throws PartidaInvalidaException si falta un dato, sobra uno o alguno no es válido
     */
    public static Jugador desdeTexto(String texto, Catalogo catalogo) {
        if (texto == null || catalogo == null) {
            throw new IllegalArgumentException("Se necesitan el texto y el catálogo");
        }
        String[] valores = new String[CLAVES.length];
        for (String linea : texto.split("\n")) {
            linea = linea.strip();
            if (linea.isEmpty()) {
                continue;
            }
            int igual = linea.indexOf('=');
            if (igual < 0) {
                throw new PartidaInvalidaException("Línea sin '=': " + linea);
            }
            int posicion = posicionDe(linea.substring(0, igual));
            if (valores[posicion] != null) {
                throw new PartidaInvalidaException("Dato repetido: " + CLAVES[posicion]);
            }
            valores[posicion] = linea.substring(igual + 1);
        }
        for (int i = 0; i < CLAVES.length; i++) {
            if (valores[i] == null) {
                throw new PartidaInvalidaException("Falta el dato: " + CLAVES[i]);
            }
        }
        if (entero(valores[0], "version") != VERSION) {
            throw new PartidaInvalidaException("La partida es de otra versión: " + valores[0]);
        }

        try {
            Jugador jugador = new Jugador(valores[1]);
            int rivales = entero(valores[2], "rivalesVencidos");
            if (rivales < 0 || rivales > Jugador.TOTAL_RIVALES) {
                throw new PartidaInvalidaException("Rivales vencidos fuera de rango: " + rivales);
            }
            for (int i = 0; i < rivales; i++) {
                jugador.registrarRivalVencido();
            }
            for (int id : lista(valores[3], "descubiertas")) {
                jugador.marcarDescubierta(id);
            }
            for (int id : lista(valores[4], "contrasenas")) {
                jugador.marcarContrasenaCanjeada(id);
            }
            for (int id : lista(valores[5], "deck")) {
                if (!jugador.recibirCarta(catalogo.buscarPorId(id)) || !jugador.moverCartaAlDeck(id)) {
                    throw new PartidaInvalidaException("El Deck no acepta la carta " + id);
                }
            }
            for (int id : lista(valores[6], "chest")) {
                if (!jugador.recibirCarta(catalogo.buscarPorId(id))) {
                    throw new PartidaInvalidaException("Hay más de 3 copias de la carta " + id);
                }
            }
            return jugador;
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Ids fuera de rango, nombre vacío o carta que no está en el catálogo.
            throw new PartidaInvalidaException(e.getMessage());
        }
    }

    private static int posicionDe(String clave) {
        for (int i = 0; i < CLAVES.length; i++) {
            if (CLAVES[i].equals(clave)) {
                return i;
            }
        }
        throw new PartidaInvalidaException("Dato desconocido: " + clave);
    }

    private static int entero(String texto, String nombre) {
        try {
            return Integer.parseInt(texto.strip());
        } catch (NumberFormatException e) {
            throw new PartidaInvalidaException(nombre + " no es un número: \"" + texto + "\"");
        }
    }

    private static int[] lista(String texto, String nombre) {
        if (texto.isBlank()) {
            return new int[0];
        }
        String[] partes = texto.split(",");
        int[] ids = new int[partes.length];
        for (int i = 0; i < partes.length; i++) {
            ids[i] = entero(partes[i], nombre);
        }
        return ids;
    }

    private static String ids(Carta[] cartas) {
        StringBuilder texto = new StringBuilder();
        for (Carta carta : cartas) {
            agregar(texto, carta.getId());
        }
        return texto.toString();
    }

    private static void agregar(StringBuilder texto, int id) {
        if (texto.length() > 0) {
            texto.append(',');
        }
        texto.append(id);
    }
}