package co.edu.unbosque.modelo.entidades;

/**
 * Las 8 direcciones del cursor del inventario. Cada una dice cuánto cambian
 * la fila y la columna (la fila 0 es la de arriba).
 */
public enum Direccion {
    ARRIBA(-1, 0),
    ABAJO(1, 0),
    IZQUIERDA(0, -1),
    DERECHA(0, 1),
    ARRIBA_IZQUIERDA(-1, -1),
    ARRIBA_DERECHA(-1, 1),
    ABAJO_IZQUIERDA(1, -1),
    ABAJO_DERECHA(1, 1);

    private final int deltaFila;
    private final int deltaColumna;

    Direccion(int deltaFila, int deltaColumna) {
        this.deltaFila = deltaFila;
        this.deltaColumna = deltaColumna;
    }

    public int getDeltaFila() {
        return deltaFila;
    }

    public int getDeltaColumna() {
        return deltaColumna;
    }
}
