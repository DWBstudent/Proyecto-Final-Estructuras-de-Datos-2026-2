package co.edu.unbosque.modelo.estructuras;

/**
 * Guarda pares de clave y valor. Si dos claves caen en la misma cubeta, sus
 * nodos se enlazan y cada clave conserva su propio valor.
 *
 * <p>
 * Responsable: Brayan.
 *
 * @param <K> tipo de clave
 * @param <V> tipo de valor
 */
public class TablaHash<K, V> implements Mapa<K, V> {

	/** Cantidad de cubetas al crear la tabla. */
	private static final int CAPACIDAD_INICIAL = 16;

	/** Ocupación a partir de la cual se amplía el arreglo de cubetas. */
	private static final double FACTOR_DE_CARGA = 0.75;

	/** Primer nodo de cada cubeta. Las cubetas vacías contienen {@code null}. */
	private Nodo<K, V>[] cubetas = crearCubetas(CAPACIDAD_INICIAL);

	/** Cantidad de claves distintas almacenadas. */
	private int tamanio;

	/** Guarda una entrada y el enlace a la siguiente de su cubeta. */
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
	 * Guarda un valor. Si la clave ya existe, reemplaza su valor y conserva el
	 * tamaño. Complejidad: O(1) en promedio amortizado.
	 *
	 * @param clave clave con la que se identifica el valor
	 * @param valor dato que se quiere guardar
	 * @throws IllegalArgumentException si la clave o el valor son nulos
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
	 * Busca una clave en su cubeta. Complejidad: O(1) en promedio.
	 *
	 * @param clave clave que se quiere buscar
	 * @return valor asociado, o {@code null} si la clave está ausente
	 * @throws IllegalArgumentException si la clave es nula
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
	 * Comprueba si la clave está guardada. Complejidad: O(1) en promedio.
	 *
	 * @param clave clave que se quiere consultar
	 * @return {@code true} si existe una entrada con esa clave
	 * @throws IllegalArgumentException si la clave es nula
	 */
	@Override
	public boolean contiene(K clave) {
		return obtener(clave) != null;
	}

	/**
	 * Retira la entrada de la clave indicada y conserva las demás entradas de la
	 * cubeta. Complejidad: O(1) en promedio.
	 *
	 * @param clave clave que se quiere retirar
	 * @return valor retirado, o {@code null} si la clave estaba ausente
	 * @throws IllegalArgumentException si la clave es nula
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
	 * Consulta la cantidad de entradas. Complejidad: O(1).
	 *
	 * @return cantidad de claves distintas
	 */
	@Override
	public int tamanio() {
		return tamanio;
	}

	/**
	 * Comprueba si la tabla está vacía. Complejidad: O(1).
	 *
	 * @return {@code true} si la tabla no contiene entradas
	 */
	@Override
	public boolean estaVacia() {
		return tamanio == 0;
	}

	/**
	 * Duplica la capacidad y vuelve a calcular la cubeta de cada entrada. La
	 * cantidad de claves almacenadas se conserva.
	 */
	private void ampliar() {
		Nodo<K, V>[] nuevasCubetas = crearCubetas(cubetas.length * 2);

		for (Nodo<K, V> primero : cubetas) {
			Nodo<K, V> actual = primero;
			while (actual != null) {
				Nodo<K, V> siguiente = actual.siguiente;
				int indice = indiceDe(actual.clave, nuevasCubetas.length);

				actual.siguiente = nuevasCubetas[indice];
				nuevasCubetas[indice] = actual;
				actual = siguiente;
			}
		}

		cubetas = nuevasCubetas;
	}

	/** Calcula un índice válido aunque el hash de la clave sea negativo. */
	private static int indiceDe(Object clave, int capacidad) {
		return Math.floorMod(clave.hashCode(), capacidad);
	}

	/** Rechaza las claves nulas antes de calcular su hash. */
	private static void validarClave(Object clave) {
		if (clave == null) {
			throw new IllegalArgumentException("La clave no puede ser null");
		}
	}

	/** Crea un arreglo de cubetas vacías para los tipos de esta tabla. */
	@SuppressWarnings("unchecked")
	private static <K, V> Nodo<K, V>[] crearCubetas(int capacidad) {
		return (Nodo<K, V>[]) new Nodo<?, ?>[capacidad];
	}
}
