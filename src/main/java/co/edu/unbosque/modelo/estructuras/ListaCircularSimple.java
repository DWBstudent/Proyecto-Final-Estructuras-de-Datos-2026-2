package co.edu.unbosque.modelo.estructuras;

/**
 * Lista circular en la que cada nodo apunta al siguiente. El último nodo apunta
 * al primero, lo que permite dar la vuelta completa.
 *
 * <p>
 * Responsable: Brayan.
 *
 * @param <T> tipo de elemento que guarda la lista
 */
public class ListaCircularSimple<T> {

	/** Guarda un elemento y el enlace al nodo siguiente. */
	private static final class Nodo<T> {
		private final T dato;
		private Nodo<T> siguiente;

		private Nodo(T dato) {
			this.dato = dato;
		}
	}

	/** Último nodo de la lista. Su enlace lleva al primero. */
	private Nodo<T> ultimo;

	/** Nodo seleccionado durante el recorrido. */
	private Nodo<T> actual;

	/** Cantidad de elementos guardados. */
	private int tamanio;

	/**
	 * Agrega un elemento al final. Si es el primero, queda seleccionado.
	 * Complejidad: O(1).
	 *
	 * @param elemento dato que se quiere agregar
	 * @throws IllegalArgumentException si el elemento es {@code null}
	 */
	public void agregarAlFinal(T elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("El elemento no puede ser null");
		}

		Nodo<T> nuevo = new Nodo<>(elemento);
		if (ultimo == null) {
			nuevo.siguiente = nuevo;
			actual = nuevo;
		} else {
			nuevo.siguiente = ultimo.siguiente;
			ultimo.siguiente = nuevo;
		}

		ultimo = nuevo;
		tamanio++;
	}

	/**
	 * Consulta el elemento seleccionado. Complejidad: O(1).
	 *
	 * @return elemento seleccionado
	 * @throws EstructuraVaciaException si la lista está vacía
	 */
	public T actual() {
		validarQueHayElementos();
		return actual.dato;
	}

	/**
	 * Selecciona el siguiente elemento. Desde el último vuelve al primero.
	 * Complejidad: O(1).
	 *
	 * @return elemento seleccionado después de avanzar
	 * @throws EstructuraVaciaException si la lista está vacía
	 */
	public T avanzar() {
		validarQueHayElementos();
		actual = actual.siguiente;
		return actual.dato;
	}

	/**
	 * Busca y selecciona el nodo anterior al actual. Desde el primero pasa al
	 * último. Complejidad: O(n) en el peor caso.
	 *
	 * @return elemento seleccionado después de retroceder
	 * @throws EstructuraVaciaException si la lista está vacía
	 */
	public T retroceder() {
		validarQueHayElementos();

		Nodo<T> anterior = ultimo;
		while (anterior.siguiente != actual) {
			anterior = anterior.siguiente;
		}

		actual = anterior;
		return actual.dato;
	}

	/**
	 * Comprueba si la selección está en el primer elemento. Complejidad: O(1).
	 *
	 * @return {@code true} si está seleccionado el primero
	 */
	public boolean esPrimero() {
		return ultimo != null && actual == ultimo.siguiente;
	}

	/**
	 * Comprueba si la selección está en el último elemento. Complejidad: O(1).
	 *
	 * @return {@code true} si está seleccionado el último
	 */
	public boolean esUltimo() {
		return ultimo != null && actual == ultimo;
	}

	/**
	 * Devuelve la cantidad de elementos guardados. Complejidad: O(1).
	 *
	 * @return cantidad de elementos
	 */
	public int tamanio() {
		return tamanio;
	}

	/**
	 * Comprueba si la lista está vacía. Complejidad: O(1).
	 *
	 * @return {@code true} si la lista está vacía
	 */
	public boolean estaVacia() {
		return tamanio == 0;
	}

	private void validarQueHayElementos() {
		if (estaVacia()) {
			throw new EstructuraVaciaException("La lista circular simple está vacía");
		}
	}
}