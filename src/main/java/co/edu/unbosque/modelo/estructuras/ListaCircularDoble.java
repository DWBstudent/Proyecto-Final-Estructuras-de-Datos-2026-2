package co.edu.unbosque.modelo.estructuras;

/**
 * Lista circular que permite recorrer los elementos en ambos sentidos. Al
 * avanzar desde el último elemento se vuelve al primero. Al retroceder desde el
 * primero se llega al último.
 *
 * <p>
 * Responsable: Brayan.
 *
 * @param <T> tipo de elemento que guarda la lista
 */
public class ListaCircularDoble<T> {

	/** Guarda un elemento y los enlaces a sus dos vecinos. */
	private static final class Nodo<T> {
		private final T dato;
		private Nodo<T> siguiente;
		private Nodo<T> anterior;

		private Nodo(T dato) {
			this.dato = dato;
		}
	}

	/** Primer nodo en el orden de la lista. */
	private Nodo<T> primero;

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
		if (primero == null) {
			nuevo.siguiente = nuevo;
			nuevo.anterior = nuevo;
			primero = nuevo;
			actual = nuevo;
		} else {
			Nodo<T> ultimo = primero.anterior;
			nuevo.anterior = ultimo;
			nuevo.siguiente = primero;
			ultimo.siguiente = nuevo;
			primero.anterior = nuevo;
		}

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
	 * Selecciona el siguiente elemento y devuelve su valor. Complejidad: O(1).
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
	 * Selecciona el elemento anterior y devuelve su valor. Complejidad: O(1).
	 *
	 * @return elemento seleccionado después de retroceder
	 * @throws EstructuraVaciaException si la lista está vacía
	 */
	public T retroceder() {
		validarQueHayElementos();
		actual = actual.anterior;
		return actual.dato;
	}

	/**
	 * Consulta un elemento por su posición en la lista, empezando desde 0.
	 * Complejidad: O(n) en el peor caso.
	 *
	 * @param indice posición que se quiere consultar
	 * @return elemento que ocupa esa posición
	 * @throws IndexOutOfBoundsException si el índice está fuera de la lista
	 */
	public T obtener(int indice) {
		return nodoEn(indice).dato;
	}

	/**
	 * Retira el elemento de la posición indicada. Si era el seleccionado, el cursor
	 * pasa al siguiente elemento disponible. Complejidad: O(n) en el peor caso.
	 *
	 * @param indice posición que se quiere retirar, empezando desde 0
	 * @return elemento retirado
	 * @throws IndexOutOfBoundsException si el índice está fuera de la lista
	 */
	public T eliminar(int indice) {
		Nodo<T> eliminado = nodoEn(indice);

		if (tamanio == 1) {
			primero = null;
			actual = null;
		} else {
			eliminado.anterior.siguiente = eliminado.siguiente;
			eliminado.siguiente.anterior = eliminado.anterior;

			if (eliminado == primero) {
				primero = eliminado.siguiente;
			}
			if (eliminado == actual) {
				actual = eliminado.siguiente;
			}
		}

		tamanio--;
		return eliminado.dato;
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

	private Nodo<T> nodoEn(int indice) {
		if (indice < 0 || indice >= tamanio) {
			throw new IndexOutOfBoundsException("Índice " + indice + " fuera de una lista de tamaño " + tamanio);
		}

		Nodo<T> nodo = primero;
		for (int posicion = 0; posicion < indice; posicion++) {
			nodo = nodo.siguiente;
		}
		return nodo;
	}

	private void validarQueHayElementos() {
		if (estaVacia()) {
			throw new EstructuraVaciaException("La lista circular doble está vacía");
		}
	}
}