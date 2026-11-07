package co.edu.unbosque.modelo.entidades;

import co.edu.unbosque.modelo.juego.Chest;
import co.edu.unbosque.modelo.juego.Deck;

/**
 * El jugador: su colección (Chest y Deck), las cartas que ya descubrió y su avance
 * en el modo historia. Es lo que se guarda y se carga en la partida.
 *
 * <p>Responsable: Daniel. Contrato del hito H0: Brayan y Santiago usan
 * {@link #estaDescubierta(int)} y {@link #recibirCarta(Carta)}.
 */
public class Jugador {

    /** Cantidad de rivales del modo historia. */
    public static final int TOTAL_RIVALES = 3;

    private final String nombre;
    // Índice = id de la carta. La posición 0 no se usa, así el id se usa directo.
    private final boolean[] descubiertas = new boolean[Carta.ID_MAXIMO + 1];
    private int cantidadDescubiertas;
    private int rivalesVencidos;
    private final Chest chest = new Chest();
    private final Deck deck = new Deck();

    /**
     * @throws IllegalArgumentException si el nombre está vacío
     */
    public Jugador(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El jugador necesita un nombre");
        }
        this.nombre = nombre.trim();
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Indica si el jugador ha tenido esta carta alguna vez. El inventario la muestra
     * con imagen si es así, o en negro si no. Complejidad: O(1).
     *
     * @throws IllegalArgumentException si el id no está entre 1 y 100
     */
    public boolean estaDescubierta(int idCarta) {
        validarId(idCarta);
        return descubiertas[idCarta];
    }

    /**
     * Marca una carta como descubierta. Marcarla dos veces no cuenta doble. Complejidad: O(1).
     *
     * @throws IllegalArgumentException si el id no está entre 1 y 100
     */
    public void marcarDescubierta(int idCarta) {
        validarId(idCarta);
        if (!descubiertas[idCarta]) {
            descubiertas[idCarta] = true;
            cantidadDescubiertas++;
        }
    }

    /** Complejidad: O(1). */
    public int getCantidadDescubiertas() {
        return cantidadDescubiertas;
    }

    /**
     * Entrega una carta al jugador (recompensa de un duelo o contraseña): la marca como
     * descubierta y la agrega al Chest. Complejidad: la de {@link Chest#agregar(Carta)}.
     *
     * @return {@code false} si el Chest la rechazó, por ejemplo porque ya tiene 3 copias.
     *         Aun así queda marcada como descubierta.
     * @throws IllegalArgumentException si la carta es {@code null}
     */
    public boolean recibirCarta(Carta carta) {
        if (carta == null) {
            throw new IllegalArgumentException("La carta no puede ser null");
        }
        marcarDescubierta(carta.getId());
        return chest.agregar(carta);
    }

    /** Rivales del modo historia ya vencidos, de 0 a 3. Complejidad: O(1). */
    public int getRivalesVencidos() {
        return rivalesVencidos;
    }

    /**
     * Registra que venció al siguiente rival del modo historia. Complejidad: O(1).
     *
     * @throws IllegalStateException si ya venció a los 3
     */
    public void registrarRivalVencido() {
        if (rivalesVencidos >= TOTAL_RIVALES) {
            throw new IllegalStateException("El jugador ya venció a los " + TOTAL_RIVALES + " rivales");
        }
        rivalesVencidos++;
    }

    /** Complejidad: O(1). */
    public boolean completoModoHistoria() {
        return rivalesVencidos == TOTAL_RIVALES;
    }

    public Chest getChest() {
        return chest;
    }

    public Deck getDeck() {
        return deck;
    }

    private static void validarId(int idCarta) {
        if (idCarta < 1 || idCarta > Carta.ID_MAXIMO) {
            throw new IllegalArgumentException("El id debe estar entre 1 y " + Carta.ID_MAXIMO + ": " + idCarta);
        }
    }
}
