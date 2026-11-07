package co.edu.unbosque.modelo.entidades;

/**
 * Los 8 tipos de monstruo, tomados de Forbidden Memories.
 * En {@code cartas.csv} se escribe el nombre de la constante (por ejemplo {@code LANZADOR_DE_CONJUROS}).
 * El orden de declaración es el que usa el criterio "ordenar por tipo".
 */
public enum TipoCarta {
    DRAGON("Dragón"),
    GUERRERO("Guerrero"),
    LANZADOR_DE_CONJUROS("Lanzador de Conjuros"),
    BESTIA("Bestia"),
    DEMONIO("Demonio"),
    MAQUINA("Máquina"),
    AGUA("Agua"),
    PLANTA("Planta");

    private final String nombreVisible;

    TipoCarta(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    /** Nombre con tildes para mostrar en pantalla. */
    public String getNombreVisible() {
        return nombreVisible;
    }
}
