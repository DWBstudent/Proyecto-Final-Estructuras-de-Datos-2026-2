package co.edu.unbosque.controlador;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.juego.Catalogo;
import co.edu.unbosque.modelo.persistencia.LectorCartasCsv;

/**
 * Puente temporal mientras {@link Catalogo} no está implementado: lee {@code cartas.csv} con
 * {@link LectorCartasCsv} y guarda las cartas en un arreglo indexado por id. Solo responde
 * {@link #buscarPorId(int)} y {@link #tamanio()}, que es lo que usan el duelo, los rivales y
 * la campaña.
 *
 * <p>Cuando Daniel implemente {@code Catalogo}, se cambia {@code new CatalogoCsv()} por el
 * catálogo real y se borra esta clase.
 *
 * <p>Responsable: Santiago.
 */
public class CatalogoCsv extends Catalogo {

    /** Archivo de src/main/resources con las 100 cartas. */
    public static final String RUTA = "cartas.csv";

    // Índice = id de la carta; la posición 0 no se usa.
    private final Carta[] porId = new Carta[Carta.ID_MAXIMO + 1];
    private final int cantidad;

    /**
     * Lee {@value #RUTA}. Complejidad: O(n), con n = cartas del archivo.
     *
     * @throws IllegalArgumentException si no existe el archivo
     * @throws co.edu.unbosque.modelo.persistencia.CsvInvalidoException si el archivo tiene errores
     */
    public CatalogoCsv() {
        Carta[] cartas = LectorCartasCsv.leerRecurso(RUTA);
        for (Carta carta : cartas) {
            porId[carta.getId()] = carta;
        }
        cantidad = cartas.length;
    }

    /**
     * La carta con ese id, o {@code null} si el archivo no la trae. Complejidad: O(1).
     *
     * @throws IllegalArgumentException si el id no está entre 1 y 100
     */
    @Override
    public Carta buscarPorId(int id) {
        if (id < 1 || id > Carta.ID_MAXIMO) {
            throw new IllegalArgumentException("El id debe estar entre 1 y " + Carta.ID_MAXIMO + ": " + id);
        }
        return porId[id];
    }

    /** Cantidad de cartas leídas. Complejidad: O(1). */
    @Override
    public int tamanio() {
        return cantidad;
    }
}
