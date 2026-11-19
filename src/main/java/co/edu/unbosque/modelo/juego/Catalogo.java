package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.estructuras.TablaHash;
import co.edu.unbosque.modelo.persistencia.LectorCartasCsv;

/**
 * Las 100 cartas del juego. Usa dos tablas hash para responder en O(1) promedio:
 * <ul>
 *   <li>{@code porId}: id de la carta → la carta.</li>
 *   <li>{@code idPorContrasena}: contraseña de 8 dígitos → id de su carta.</li>
 * </ul>
 *
 * <p>Se crea una sola vez al iniciar el juego con {@link #cargar()} y se comparte con
 * todos los controladores.
 *
 * <p>Responsable: Daniel (hito H2). Contrato del hito H0.
 */
public class Catalogo {

    /** Archivo de {@code src/main/resources} con las cartas. */
    public static final String RUTA_CSV = "/cartas.csv";

    private final TablaHash<Integer, Carta> porId = new TablaHash<>();
    private final TablaHash<String, Integer> idPorContrasena = new TablaHash<>();

    /**
     * Catálogo vacío. Solo existe para que las pruebas puedan crear catálogos falsos
     * redefiniendo los métodos ({@code new Catalogo() { ... }}).
     */
    protected Catalogo() {
        // Las tablas quedan vacías.
    }

    /**
     * Crea el catálogo con las cartas recibidas. Complejidad: O(n) promedio, con n cartas,
     * porque cada carta se guarda en las dos tablas en O(1) promedio.
     *
     * @throws IllegalArgumentException si el arreglo o alguna carta es {@code null},
     *         o si se repite un id o una contraseña
     */
    public Catalogo(Carta[] cartas) {
        if (cartas == null) {
            throw new IllegalArgumentException("Las cartas no pueden ser null");
        }
        for (Carta carta : cartas) {
            if (carta == null) {
                throw new IllegalArgumentException("El catálogo no acepta cartas null");
            }
            if (porId.contiene(carta.getId())) {
                throw new IllegalArgumentException("El id " + carta.getId() + " está repetido");
            }
            if (idPorContrasena.contiene(carta.getContrasena())) {
                throw new IllegalArgumentException("La contraseña " + carta.getContrasena() + " está repetida");
            }
            porId.poner(carta.getId(), carta);
            idPorContrasena.poner(carta.getContrasena(), carta.getId());
        }
    }

    /**
     * Lee {@code cartas.csv} y arma el catálogo. Complejidad: O(n), con n líneas del archivo.
     *
     * @throws co.edu.unbosque.modelo.persistencia.CsvInvalidoException si el archivo tiene errores
     */
    public static Catalogo cargar() {
        return new Catalogo(LectorCartasCsv.leerRecurso(RUTA_CSV));
    }

    /**
     * Carta con ese id. Complejidad: O(1) promedio.
     *
     * @throws IllegalArgumentException si el id no existe en el catálogo
     */
    public Carta buscarPorId(int id) {
        Carta carta = porId.obtener(id);
        if (carta == null) {
            throw new IllegalArgumentException("No existe la carta con id " + id);
        }
        return carta;
    }

    /**
     * Id de la carta que corresponde a la contraseña, o -1 si no corresponde a ninguna.
     * Ignora los espacios que la persona escriba antes o después. Complejidad: O(1) promedio.
     */
    public int idPorContrasena(String contrasena) {
        if (contrasena == null || contrasena.isBlank()) {
            return -1;
        }
        Integer id = idPorContrasena.obtener(contrasena.strip());
        return id == null ? -1 : id;
    }

    /** Cantidad de cartas cargadas. Complejidad: O(1). */
    public int tamanio() {
        return porId.tamanio();
    }
}