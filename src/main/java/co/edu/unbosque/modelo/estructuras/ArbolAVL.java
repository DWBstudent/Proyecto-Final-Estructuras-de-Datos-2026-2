package co.edu.unbosque.modelo.estructuras;

/**
 * Árbol binario de búsqueda que mantiene el equilibrio entre sus ramas. Al
 * insertar o eliminar una entrada, actualiza las alturas y aplica rotaciones
 * cuando la diferencia entre dos subárboles supera un nivel.
 *
 * <p>
 * Dos claves representan la misma entrada si {@code compareTo} devuelve cero.
 *
 * <p>
 * Responsable: Brayan.
 *
 * @param <K> tipo de clave, con un orden definido por {@link Comparable}
 * @param <V> tipo de valor asociado a cada clave
 */
public class ArbolAVL<K extends Comparable<? super K>, V> implements Mapa<K, V> {

	/**
	 * Entrada del árbol. Guarda la clave, el valor, los hijos y la altura del
	 * subárbol que empieza en este nodo.
	 */
	private static final class Nodo<K, V> {

		/** Clave que determina la posición del nodo en el árbol. */
		private K clave;

		/** Valor asociado a la clave. */
		private V valor;

		/** Hijo con claves menores. */
		private Nodo<K, V> izquierdo;

		/** Hijo con claves mayores. */
		private Nodo<K, V> derecho;

		/** Altura del subárbol. Una hoja tiene altura 1. */
		private int altura = 1;

		/**
		 * Crea un nodo sin hijos.
		 *
		 * @param clave clave de la entrada
		 * @param valor valor que se guarda
		 */
		private Nodo(K clave, V valor) {
			this.clave = clave;
			this.valor = valor;
		}
	}

	/** Primer nodo del árbol, o {@code null} si está vacío. */
	private Nodo<K, V> raiz;

	/** Cantidad de claves distintas guardadas. */
	private int tamanio;

	/**
	 * Guarda un valor bajo la clave indicada. Si la clave ya existe, reemplaza su
	 * valor y conserva el tamaño. Complejidad: O(log n).
	 *
	 * @param clave clave con la que se identifica el valor
	 * @param valor valor que se quiere guardar
	 * @throws IllegalArgumentException si la clave o el valor son nulos
	 */
	@Override
	public void poner(K clave, V valor) {
		validarClave(clave);
		if (valor == null) {
			throw new IllegalArgumentException("El valor no puede ser null");
		}

		raiz = insertar(raiz, clave, valor);
	}

	/**
	 * Busca el valor asociado a la clave. Complejidad: O(log n).
	 *
	 * @param clave clave que se quiere buscar
	 * @return valor asociado, o {@code null} si la clave no existe
	 * @throws IllegalArgumentException si la clave es nula
	 */
	@Override
	public V obtener(K clave) {
		validarClave(clave);

		Nodo<K, V> actual = raiz;
		while (actual != null) {
			int comparacion = clave.compareTo(actual.clave);

			if (comparacion == 0) {
				return actual.valor;
			}

			actual = comparacion < 0 ? actual.izquierdo : actual.derecho;
		}

		return null;
	}

	/**
	 * Comprueba si la clave está guardada. Complejidad: O(log n).
	 *
	 * @param clave clave que se quiere consultar
	 * @return {@code true} si existe una entrada para la clave
	 * @throws IllegalArgumentException si la clave es nula
	 */
	@Override
	public boolean contiene(K clave) {
		return obtener(clave) != null;
	}

	/**
	 * Elimina la entrada y devuelve el valor que tenía. Complejidad: O(log n),
	 * incluida la búsqueda previa.
	 *
	 * @param clave clave de la entrada que se quiere eliminar
	 * @return valor eliminado, o {@code null} si la clave no existe
	 * @throws IllegalArgumentException si la clave es nula
	 */
	@Override
	public V eliminar(K clave) {
		V valor = obtener(clave);
		if (valor == null) {
			return null;
		}

		raiz = retirar(raiz, clave);
		tamanio--;
		return valor;
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
	 * Comprueba si el árbol está vacío. Complejidad: O(1).
	 *
	 * @return {@code true} si el árbol no tiene entradas
	 */
	@Override
	public boolean estaVacia() {
		return tamanio == 0;
	}

	/**
	 * Inserta o actualiza una entrada en el subárbol indicado. Al volver por el
	 * camino de búsqueda, equilibra cada nodo visitado.
	 *
	 * @param nodo  raíz del subárbol
	 * @param clave clave de la entrada
	 * @param valor valor que se quiere guardar
	 * @return raíz del subárbol después de insertar
	 */
	private Nodo<K, V> insertar(Nodo<K, V> nodo, K clave, V valor) {
		if (nodo == null) {
			tamanio++;
			return new Nodo<>(clave, valor);
		}

		int comparacion = clave.compareTo(nodo.clave);
		if (comparacion < 0) {
			nodo.izquierdo = insertar(nodo.izquierdo, clave, valor);
		} else if (comparacion > 0) {
			nodo.derecho = insertar(nodo.derecho, clave, valor);
		} else {
			nodo.valor = valor;
			return nodo;
		}

		return equilibrar(nodo);
	}

	/**
	 * Retira una clave que existe en el subárbol. Si el nodo tiene dos hijos, lo
	 * reemplaza por la entrada menor de su rama derecha.
	 *
	 * @param nodo  raíz del subárbol
	 * @param clave clave que se quiere retirar
	 * @return raíz del subárbol después de eliminar
	 */
	private Nodo<K, V> retirar(Nodo<K, V> nodo, K clave) {
		int comparacion = clave.compareTo(nodo.clave);

		if (comparacion < 0) {
			nodo.izquierdo = retirar(nodo.izquierdo, clave);
		} else if (comparacion > 0) {
			nodo.derecho = retirar(nodo.derecho, clave);
		} else {
			if (nodo.izquierdo == null) {
				return nodo.derecho;
			}
			if (nodo.derecho == null) {
				return nodo.izquierdo;
			}

			Nodo<K, V> sucesor = buscarMenor(nodo.derecho);
			nodo.clave = sucesor.clave;
			nodo.valor = sucesor.valor;
			nodo.derecho = retirar(nodo.derecho, sucesor.clave);
		}

		return equilibrar(nodo);
	}

	/**
	 * Encuentra el nodo con la clave menor de un subárbol no vacío.
	 *
	 * @param nodo raíz del subárbol
	 * @return nodo de la última posición hacia la izquierda
	 */
	private Nodo<K, V> buscarMenor(Nodo<K, V> nodo) {
		Nodo<K, V> actual = nodo;
		while (actual.izquierdo != null) {
			actual = actual.izquierdo;
		}
		return actual;
	}

	/**
	 * Actualiza la altura y aplica una rotación simple o doble si hace falta.
	 *
	 * @param nodo raíz del subárbol que se quiere equilibrar
	 * @return raíz del subárbol equilibrado
	 */
	private Nodo<K, V> equilibrar(Nodo<K, V> nodo) {
		actualizarAltura(nodo);
		int diferencia = balance(nodo);

		if (diferencia > 1) {
			if (balance(nodo.izquierdo) < 0) {
				nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
			}
			return rotarDerecha(nodo);
		}

		if (diferencia < -1) {
			if (balance(nodo.derecho) > 0) {
				nodo.derecho = rotarDerecha(nodo.derecho);
			}
			return rotarIzquierda(nodo);
		}

		return nodo;
	}

	/**
	 * Sube al hijo izquierdo y conserva el orden de las claves.
	 *
	 * @param nodo raíz anterior del subárbol
	 * @return nueva raíz del subárbol
	 */
	private Nodo<K, V> rotarDerecha(Nodo<K, V> nodo) {
		Nodo<K, V> nuevaRaiz = nodo.izquierdo;
		nodo.izquierdo = nuevaRaiz.derecho;
		nuevaRaiz.derecho = nodo;

		actualizarAltura(nodo);
		actualizarAltura(nuevaRaiz);
		return nuevaRaiz;
	}

	/**
	 * Sube al hijo derecho y conserva el orden de las claves.
	 *
	 * @param nodo raíz anterior del subárbol
	 * @return nueva raíz del subárbol
	 */
	private Nodo<K, V> rotarIzquierda(Nodo<K, V> nodo) {
		Nodo<K, V> nuevaRaiz = nodo.derecho;
		nodo.derecho = nuevaRaiz.izquierdo;
		nuevaRaiz.izquierdo = nodo;

		actualizarAltura(nodo);
		actualizarAltura(nuevaRaiz);
		return nuevaRaiz;
	}

	/**
	 * Recalcula la altura a partir del mayor de los dos hijos.
	 *
	 * @param nodo nodo cuya altura se quiere actualizar
	 */
	private void actualizarAltura(Nodo<K, V> nodo) {
		nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));
	}

	/**
	 * Calcula la diferencia de altura entre la rama izquierda y la derecha.
	 *
	 * @param nodo raíz del subárbol
	 * @return altura izquierda menos altura derecha
	 */
	private int balance(Nodo<K, V> nodo) {
		return altura(nodo.izquierdo) - altura(nodo.derecho);
	}

	/**
	 * Consulta la altura guardada en el nodo.
	 *
	 * @param nodo raíz del subárbol, que puede ser {@code null}
	 * @return altura del subárbol, o 0 si está vacío
	 */
	private int altura(Nodo<K, V> nodo) {
		return nodo == null ? 0 : nodo.altura;
	}

	/**
	 * Comprueba que la clave pueda compararse con las demás.
	 *
	 * @param clave clave que se quiere validar
	 * @throws IllegalArgumentException si la clave es nula
	 */
	private void validarClave(K clave) {
		if (clave == null) {
			throw new IllegalArgumentException("La clave no puede ser null");
		}
	}
}