package co.edu.unbosque.modelo.persistencia;

/**
 * Se lanza cuando el archivo de la partida guardada está dañado o fue editado a mano
 * con datos que el juego no acepta.
 *
 * <p>Responsable: Daniel.
 */
public class PartidaInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PartidaInvalidaException(String mensaje) {
        super(mensaje);
    }
}