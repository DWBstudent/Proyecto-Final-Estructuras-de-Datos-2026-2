package co.edu.unbosque.modelo.ordenamiento;

/**
 * Ordena arreglos mediante inserción. En cada paso toma un elemento y lo ubica
 * dentro del tramo que ya está ordenado.
 *
 * <p>
 * Responsable: Brayan.
 */
public final class InsertionSort {

	/** Evita crear instancias de esta clase de algoritmos. */
	private InsertionSort() {
	}

	/**
	 * Ordena el arreglo recibido usando el comparador indicado. Un arreglo vacío o
	 * con un solo elemento conserva su contenido. Los elementos que empatan
	 * mantienen su orden anterior.
	 *
	 * <p>
	 * Complejidad: O(n) en el mejor caso y O(n²) en el peor caso, suponiendo
	 * comparaciones de costo constante. Usa O(1) de espacio adicional.
	 *
	 * @param <T>        tipo de los elementos
	 * @param elementos  arreglo que se quiere ordenar
	 * @param comparador criterio que decide el orden
	 * @throws IllegalArgumentException si el arreglo, el comparador o alguno de los
	 *                                  elementos es nulo
	 */
	public static <T> void ordenar(T[] elementos, Comparador<T> comparador) {
		validarArgumentos(elementos, comparador);

		for (int posicion = 1; posicion < elementos.length; posicion++) {
			T elemento = elementos[posicion];
			int anterior = posicion - 1;

			while (anterior >= 0 && comparador.comparar(elementos[anterior], elemento) > 0) {
				elementos[anterior + 1] = elementos[anterior];
				anterior--;
			}

			elementos[anterior + 1] = elemento;
		}
	}

	/**
	 * Revisa los argumentos antes de modificar el arreglo.
	 *
	 * @param <T>        tipo de los elementos
	 * @param elementos  arreglo que se quiere validar
	 * @param comparador criterio que se quiere utilizar
	 * @throws IllegalArgumentException si algún argumento o elemento es nulo
	 */
	private static <T> void validarArgumentos(T[] elementos, Comparador<T> comparador) {
		if (elementos == null) {
			throw new IllegalArgumentException("El arreglo no puede ser null");
		}

		if (comparador == null) {
			throw new IllegalArgumentException("El comparador no puede ser null");
		}

		for (T elemento : elementos) {
			if (elemento == null) {
				throw new IllegalArgumentException("El arreglo no puede contener elementos null");
			}
		}
	}
}