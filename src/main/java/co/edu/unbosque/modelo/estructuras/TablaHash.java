package co.edu.unbosque.modelo.estructuras;

/**
 * Guarda pares de clave y valor en un arreglo de cubetas. Si varias claves caen
 * en la misma cubeta, sus nodos se enlazan para que cada clave conserve su
 * propio valor.
 *
 * @param <K> tipo de la clave
 * @param <V> tipo del valor
 */
public class TablaHash<K, V> implements Mapa<K, V> {
	private static final int CAPACIDAD_INICIAL = 16;
	private static final double FACTOR_DE_CARGA = 0.75;
	private Nodo<K, V>[] cubetas = crearCubetas(CAPACIDAD_INICIAL);
	private int tamanio;

	/** Una entrada de la tabla y su enlace a la siguiente de la misma cubeta. */
	private static final class Nodo<K, V> {
		private final K clave;
		private V valor;
		private Nodo<K, V> siguiente;

		private Nodo(K clave, V valor, Nodo<K, V> siguiente) {
			this.clave = clave;
			this.valor = valor;
			this.siguiente = siguiente;
		}
	}

	/**
	 * Guarda el valor asociado a la clave. Si la clave ya existe, actualiza su
	 * valor y el tamaño se mantiene. Complejidad: O(1) promedio amortizado.
	 *
	 * @param clave clave con la que se identifica el valor
	 * @param valor dato que se quiere guardar
	 * @throws IllegalArgumentException si la clave o el valor son null
	 */
	@Override
	public void poner(K clave, V valor) {
		validarClave(clave);
		if (valor == null) {
			throw new IllegalArgumentException("El valor no puede ser null");
		}

		int indice = indiceDe(clave, cubetas.length);
		for (Nodo<K, V> actual = cubetas[indice]; actual != null; actual = actual.siguiente) {
			if (actual.clave.equals(clave)) {
				actual.valor = valor;
				return;
			}
		}

		if (tamanio + 1 > cubetas.length * FACTOR_DE_CARGA) {
			ampliar();
			indice = indiceDe(clave, cubetas.length);
		}

		cubetas[indice] = new Nodo<>(clave, valor, cubetas[indice]);
		tamanio++;
	}

	/**
	 * Busca el valor asociado a una clave. Complejidad: O(1) promedio.
	 *
	 * @param clave clave que se quiere buscar
	 * @return el valor asociado, o null si la clave está ausente
	 * @throws IllegalArgumentException si la clave es null
	 */
	@Override
	public V obtener(K clave) {
		validarClave(clave);
		int indice = indiceDe(clave, cubetas.length);

		for (Nodo<K, V> actual = cubetas[indice]; actual != null; actual = actual.siguiente) {
			if (actual.clave.equals(clave)) {
				return actual.valor;
			}
		}
		return null;
	}

	/**
	 * Comprueba si la clave está guardada en la tabla. Complejidad: O(1) promedio.
	 *
	 * @param clave clave que se quiere comprobar
	 * @return true si la clave está guardada
	 * @throws IllegalArgumentException si la clave es null
	 */
	@Override
	public boolean contiene(K clave) {
		return obtener(clave) != null;
	}

	/**
	 * Retira la entrada asociada a la clave y devuelve el valor que tenía.
	 * Complejidad: O(1) promedio.
	 *
	 * @param clave clave de la entrada que se quiere retirar
	 * @return el valor retirado, o null si la clave estaba ausente
	 * @throws IllegalArgumentException si la clave es null
	 */
	@Override
	public V eliminar(K clave) {
		validarClave(clave);
		int indice = indiceDe(clave, cubetas.length);
		Nodo<K, V> anterior = null;
		Nodo<K, V> actual = cubetas[indice];

		while (actual != null) {
			if (actual.clave.equals(clave)) {
				if (anterior == null) {
					cubetas[indice] = actual.siguiente;
				} else {
					anterior.siguiente = actual.siguiente;
				}
				tamanio--;
				return actual.valor;
			}

			anterior = actual;
			actual = actual.siguiente;
		}
		return null;
	}

	/**
	 * Devuelve la cantidad de claves distintas guardadas. Complejidad: O(1).
	 *
	 * @return cantidad de claves guardadas
	 */
	@Override
	public int tamanio() {
		return tamanio;
	}

	/**
	 * Indica si la tabla está vacía. Complejidad: O(1).
	 *
	 * @return true si la tabla tiene cero entradas
	 */
	@Override
	public boolean estaVacia() {
		return tamanio == 0;
	}

	private void ampliar() {
		Nodo<K, V>[] nuevasCubetas = crearCubetas(cubetas.length * 2);

		for (Nodo<K, V> primero : cubetas) {
			Nodo<K, V> actual = primero;
			while (actual != null) {
				Nodo<K, V> siguiente = actual.siguiente;
				int nuevoIndice = indiceDe(actual.clave, nuevasCubetas.length);
				actual.siguiente = nuevasCubetas[nuevoIndice];
				nuevasCubetas[nuevoIndice] = actual;
				actual = siguiente;
			}
		}
		cubetas = nuevasCubetas;
	}

	private static int indiceDe(Object clave, int capacidad) {
		return Math.floorMod(clave.hashCode(), capacidad);
	}

	private static void validarClave(Object clave) {
		if (clave == null) {
			throw new IllegalArgumentException("La clave no puede ser null");
		}
	}

	@SuppressWarnings("unchecked")
	private static <K, V> Nodo<K, V>[] crearCubetas(int capacidad) {
		return (Nodo<K, V>[]) new Nodo<?, ?>[capacidad];
	}
}