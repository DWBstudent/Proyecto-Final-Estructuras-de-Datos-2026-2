package co.edu.unbosque.modelo.entidades;

/**
 * Una carta del catálogo. Es inmutable: una vez creada no cambia, así que se puede
 * compartir sin copiar entre el Chest, el Deck, el duelo y el inventario.
 * Dos cartas son iguales si tienen el mismo id.
 *
 * <p>Contrato del hito H0. No se cambia sin acuerdo de los tres.
 */
public final class Carta {

    /** Id más alto del catálogo. Los ids van de 1 a 100. */
    public static final int ID_MAXIMO = 100;

    /** Cantidad de dígitos de una contraseña. */
    public static final int DIGITOS_CONTRASENA = 8;

    private final int id;
    private final String nombre;
    private final TipoCarta tipo;
    private final int ataque;
    private final int defensa;
    private final String descripcion;
    private final String rutaImagen;
    private final String contrasena;

    /**
     * Complejidad: O(1).
     *
     * @throws IllegalArgumentException si el id no está entre 1 y 100, el nombre está vacío,
     *         el tipo es {@code null}, el ataque o la defensa son negativos, o la contraseña
     *         no tiene exactamente 8 dígitos
     */
    public Carta(int id, String nombre, TipoCarta tipo, int ataque, int defensa,
                 String descripcion, String rutaImagen, String contrasena) {
        if (id < 1 || id > ID_MAXIMO) {
            throw new IllegalArgumentException("El id debe estar entre 1 y " + ID_MAXIMO + ": " + id);
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("La carta " + id + " no tiene nombre");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("La carta " + id + " no tiene tipo");
        }
        if (ataque < 0 || defensa < 0) {
            throw new IllegalArgumentException("Ataque y defensa no pueden ser negativos en la carta " + id);
        }
        if (!esContrasenaValida(contrasena)) {
            throw new IllegalArgumentException("La contraseña de la carta " + id + " debe tener "
                    + DIGITOS_CONTRASENA + " dígitos");
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.tipo = tipo;
        this.ataque = ataque;
        this.defensa = defensa;
        this.descripcion = descripcion == null ? "" : descripcion.trim();
        this.rutaImagen = rutaImagen == null ? "" : rutaImagen.trim();
        this.contrasena = contrasena;
    }

    /**
     * Indica si un texto tiene la forma de una contraseña: exactamente 8 dígitos.
     * No dice si la contraseña existe en el catálogo; eso lo resuelve {@code Catalogo}.
     * Complejidad: O(1), porque siempre revisa 8 caracteres.
     */
    public static boolean esContrasenaValida(String texto) {
        if (texto == null || texto.length() != DIGITOS_CONTRASENA) {
            return false;
        }
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoCarta getTipo() {
        return tipo;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefensa() {
        return defensa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** Ruta dentro de {@code src/main/resources}. Puede estar vacía: la vista dibuja un marco por tipo. */
    public String getRutaImagen() {
        return rutaImagen;
    }

    public String getContrasena() {
        return contrasena;
    }

    /** Complejidad: O(1). Dos cartas son iguales si tienen el mismo id. */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Carta)) {
            return false;
        }
        return id == ((Carta) otro).id;
    }

    /** Complejidad: O(1). Coherente con {@link #equals(Object)}. */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("#%03d %s (%s) ATK %d / DEF %d", id, nombre, tipo.getNombreVisible(), ataque, defensa);
    }
}
