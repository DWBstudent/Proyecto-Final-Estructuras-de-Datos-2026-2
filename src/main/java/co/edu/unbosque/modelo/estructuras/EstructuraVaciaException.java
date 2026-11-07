package co.edu.unbosque.modelo.estructuras;

/**
 * Se lanza cuando se pide un elemento a una estructura vacía,
 * por ejemplo al desapilar una pila sin elementos.
 */
public class EstructuraVaciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EstructuraVaciaException(String mensaje) {
        super(mensaje);
    }
}
