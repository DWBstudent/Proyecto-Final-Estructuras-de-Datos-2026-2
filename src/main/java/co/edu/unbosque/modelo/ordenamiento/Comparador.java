package co.edu.unbosque.modelo.ordenamiento;

/**
 * Compara dos elementos para ordenarlos. Reemplaza a {@code java.util.Comparator},
 * que no se permite en el modelo.
 *
 * <p>Los 7 criterios del Chest y el Deck (código, nombre, tipo, ataque y defensa
 * ascendente y descendente) se escriben como comparadores. Ejemplo:
 * <pre>{@code Comparador<Carta> porAtaque = (a, b) -> Integer.compare(a.getAtaque(), b.getAtaque());}</pre>
 *
 * @param <T> tipo de los elementos
 */
@FunctionalInterface
public interface Comparador<T> {

    /**
     * @return un número negativo si {@code a} va antes que {@code b},
     *         0 si son equivalentes y un número positivo si {@code a} va después
     */
    int comparar(T a, T b);
}
