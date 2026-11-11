package co.edu.unbosque.modelo.estructuras;

import co.edu.unbosque.modelo.ordenamiento.Comparador;

/**
 * Lista simplemente enlazada que permite recorrer los elementos desde la cabeza
 * hasta el final. Cada nodo guarda un dato y una referencia al siguiente nodo.
 *
 * <p>
 * La lista mantiene referencias a la cabeza y a la cola para agregar elementos
 * en ambos extremos en tiempo constante.
 *
 * <p>
 * Responsable: Brayan.
 *
 * @param <T> tipo de elemento que guarda la lista
 */
public class ListaSimple<T> implements Lista<T> {

	private static final class Nodo<T> {
		private final T dato;
		private Nodo<T> siguiente;

		private Nodo(T dato) {
			this.dato = dato;
		}
	}

	private Nodo<T> cabeza;
	private Nodo<T> cola;
	private int tamanio;

	/**
	 * Agrega un elemento antes del primero. Si la lista estaba vacía, el nuevo nodo
	 * queda como cabeza y cola. Complejidad: O(1).
	 *
	 * @param elemento dato que se quiere agregar
	 * @throws IllegalArgumentException si el elemento es {@code null}
	 */
	@Override
	public void agregarAlInicio(T elemento) {
		validarElemento(elemento);
		Nodo<T> nuevo = new Nodo<>(elemento);
		nuevo.siguiente = cabeza;
		cabeza = nuevo;

		if (cola == null) {
			cola = nuevo;
		}
		tamanio++;
	}

	/**
	 * Agrega un elemento después del último nodo. Complejidad: O(1).
	 *
	 * @param elemento dato que se quiere agregar
	 * @throws IllegalArgumentException si el elemento es {@code null}
	 */
	@Override
	public void agregarAlFinal(T elemento) {
		validarElemento(elemento);
		Nodo<T> nuevo = new Nodo<>(elemento);

		if (cola == null) {
			cabeza = nuevo;
		} else {
			cola.siguiente = nuevo;
		}

		cola = nuevo;
		tamanio++;
	}

	/**
	 * Devuelve el elemento de la posición indicada. La primera posición es 0.
	 * Complejidad: O(n) en el peor caso.
	 *
	 * @param indice posición que se quiere consultar
	 * @return elemento guardado en esa posición
	 * @throws IndexOutOfBoundsException si el índice está fuera de la lista
	 */
	@Override
	public T obtener(int indice) {
		validarIndice(indice);
		return nodoEn(indice).dato;
	}

	/**
	 * Retira el nodo de la posición indicada y devuelve su elemento. Actualiza la
	 * cabeza o la cola cuando se elimina uno de los extremos. Complejidad: O(n) en
	 * el peor caso.
	 *
	 * @param indice posición del elemento que se quiere retirar
	 * @return elemento retirado
	 * @throws IndexOutOfBoundsException si el índice está fuera de la lista
	 */
	@Override
	public T eliminar(int indice) {
		validarIndice(indice);
		Nodo<T> eliminado;

		if (indice == 0) {
			eliminado = cabeza;
			cabeza = cabeza.siguiente;
			if (cabeza == null) {
				cola = null;
			}
		} else {
			Nodo<T> anterior = nodoEn(indice - 1);
			eliminado = anterior.siguiente;
			anterior.siguiente = eliminado.siguiente;
			if (eliminado == cola) {
				cola = anterior;
			}
		}

		tamanio--;
		return eliminado.dato;
	}

	/**
	 * Busca la primera posición que contiene un elemento igual al recibido. La
	 * igualdad se comprueba con {@code equals}. Complejidad: O(n) en el peor caso.
	 *
	 * @param elemento dato que se quiere buscar
	 * @return índice de la primera coincidencia, o -1 si no se encuentra
	 * @throws IllegalArgumentException si el elemento es {@code null}
	 */
	@Override
	public int indiceDe(T elemento) {
		validarElemento(elemento);
		int indice = 0;

		for (Nodo<T> actual = cabeza; actual != null; actual = actual.siguiente) {
			if (actual.dato.equals(elemento)) {
				return indice;
			}
			indice++;
		}

		return -1;
	}

	/**
	 * Comprueba si el elemento aparece en la lista. Complejidad: O(n) en el peor
	 * caso.
	 *
	 * @param elemento dato que se quiere buscar
	 * @return {@code true} si hay al menos una coincidencia
	 * @throws IllegalArgumentException si el elemento es {@code null}
	 */
	@Override
	public boolean contiene(T elemento) {
		return indiceDe(elemento) >= 0;
	}

	/**
	 * Devuelve la cantidad de elementos, incluidas las copias repetidas.
	 * Complejidad: O(1).
	 *
	 * @return cantidad de elementos
	 */
	@Override
	public int tamanio() {
		return tamanio;
	}

	/**
	 * Comprueba si la lista está vacía. Complejidad: O(1).
	 *
	 * @return {@code true} si la lista no contiene elementos
	 */
	@Override
	public boolean estaVacia() {
		return tamanio == 0;
	}

	/**
	 * Deja la lista vacía y lista para recibir elementos de nuevo. Complejidad:
	 * O(1).
	 */
	@Override
	public void vaciar() {
		cabeza = null;
		cola = null;
		tamanio = 0;
	}

	/**
	 * Ordena la lista con Merge Sort. Si dos elementos son equivalentes según el
	 * comparador, conservan el orden en que estaban. Complejidad: O(n log n) en
	 * tiempo y O(log n) en la pila de llamadas.
	 *
	 * @param comparador criterio que decide el orden de los elementos
	 * @throws IllegalArgumentException si el comparador es {@code null}
	 */
	@Override
	public void ordenar(Comparador<T> comparador) {
		if (comparador == null) {
			throw new IllegalArgumentException("El comparador no puede ser null");
		}
		if (tamanio < 2) {
			return;
		}

		cabeza = mergeSort(cabeza, comparador);
		cola = cabeza;
		while (cola.siguiente != null) {
			cola = cola.siguiente;
		}
	}

	/** Avanza desde la cabeza hasta un índice que ya fue validado. */
	private Nodo<T> nodoEn(int indice) {
		Nodo<T> actual = cabeza;
		for (int posicion = 0; posicion < indice; posicion++) {
			actual = actual.siguiente;
		}
		return actual;
	}

	/** Divide la cadena en dos mitades y ordena cada una. */
	private Nodo<T> mergeSort(Nodo<T> primero, Comparador<T> comparador) {
		if (primero == null || primero.siguiente == null) {
			return primero;
		}

		Nodo<T> mitad = primero;
		Nodo<T> rapido = primero.siguiente;
		while (rapido != null && rapido.siguiente != null) {
			mitad = mitad.siguiente;
			rapido = rapido.siguiente.siguiente;
		}

		Nodo<T> segundaMitad = mitad.siguiente;
		mitad.siguiente = null;

		Nodo<T> izquierda = mergeSort(primero, comparador);
		Nodo<T> derecha = mergeSort(segundaMitad, comparador);
		return fusionar(izquierda, derecha, comparador);
	}

	/** Enlaza dos cadenas ordenadas, tomando primero la izquierda en un empate. */
	private Nodo<T> fusionar(Nodo<T> izquierda, Nodo<T> derecha, Comparador<T> comparador) {
		Nodo<T> inicioTemporal = new Nodo<>(null);
		Nodo<T> ultimo = inicioTemporal;

		while (izquierda != null && derecha != null) {
			if (comparador.comparar(izquierda.dato, derecha.dato) <= 0) {
				ultimo.siguiente = izquierda;
				izquierda = izquierda.siguiente;
			} else {
				ultimo.siguiente = derecha;
				derecha = derecha.siguiente;
			}
			ultimo = ultimo.siguiente;
		}

		ultimo.siguiente = izquierda != null ? izquierda : derecha;
		return inicioTemporal.siguiente;
	}

	private void validarIndice(int indice) {
		if (indice < 0 || indice >= tamanio) {
			throw new IndexOutOfBoundsException("Índice " + indice + " fuera de una lista de tamaño " + tamanio);
		}
	}

	private void validarElemento(T elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("El elemento no puede ser null");
		}
	}
}
