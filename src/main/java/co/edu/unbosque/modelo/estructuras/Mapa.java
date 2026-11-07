package co.edu.unbosque.modelo.estructuras;

/**
 * Contrato de un mapa clave → valor. Lo implementa {@code TablaHash} (Brayan) con
 * encadenamiento y rehash. Usos: catálogo por id, contraseñas, conteo de copias, música.
 *
 * <p>Las claves usan {@code hashCode()} y {@code equals()}. Ni claves ni valores pueden ser {@code null}.
 *
 * @param <K> tipo de las claves
 * @param <V> tipo de los valores
 */
public interface Mapa<K, V> {

    /**
     * Asocia el valor a la clave; si la clave ya existía, reemplaza el valor anterior.
     * Complejidad esperada: O(1) promedio.
     *
     * @throws IllegalArgumentException si la clave o el valor son {@code null}
     */
    void poner(K clave, V valor);

    /**
     * Valor asociado a la clave, o {@code null} si la clave no está.
     * Complejidad esperada: O(1) promedio.
     */
    V obtener(K clave);

    /** Complejidad esperada: O(1) promedio. */
    boolean contiene(K clave);

    /**
     * Elimina la clave y devuelve su valor, o {@code null} si no estaba.
     * Complejidad esperada: O(1) promedio.
     */
    V eliminar(K clave);

    /** Complejidad esperada: O(1). */
    int tamanio();

    /** Complejidad esperada: O(1). */
    boolean estaVacia();
}
