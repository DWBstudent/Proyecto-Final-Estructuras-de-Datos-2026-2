package co.edu.unbosque.modelo.entidades;

/**
 * Lo que pasó al canjear una contraseña. El controlador lo usa para elegir el
 * mensaje que muestra la pantalla.
 *
 * <p>
 * Responsable: Daniel.
 */
public enum ResultadoCanje {

	/** La contraseña existía y la carta entró al cofre. */
	CANJEADA,

	/** Ninguna carta tiene esa contraseña. */
	NO_EXISTE,

	/** La contraseña ya se había canjeado antes. */
	YA_USADA,

	/** El jugador ya tiene 3 copias de esa carta; la contraseña no se gasta. */
	SIN_ESPACIO
}