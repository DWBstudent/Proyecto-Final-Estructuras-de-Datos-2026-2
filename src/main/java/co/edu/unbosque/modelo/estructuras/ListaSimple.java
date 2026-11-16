package co.edu.unbosque.modelo.estructuras;

import co.edu.unbosque.modelo.ordenamiento.Comparador;

/**
 * Lista simplemente enlazada. Cada nodo guarda un elemento y un enlace al
 * siguiente. El último nodo apunta a {@code null}.
 *
 * <p>
 * Las referencias a la cabeza y la cola permiten agregar elementos en ambos
 * extremos en tiempo constante.
 *
 * <p>
 * Responsable: Brayan.
 *
 * @param <T> tipo de elemento que guarda la lista
 */
public class ListaSimple<T> implements Lista<T> {

	/** Guarda un elemento y el enlace al siguiente nodo. */
	private static final class Nodo<T> {
		private final T dato;
		private Nodo<T> siguiente;

		/** Crea un nodo con el dato recibido. */
		private Nodo(T dato) {
			this.dato = dato;
		}
	}

	/** Primer nodo de la lista. */
	private Nodo<T> cabeza;

	/** Último nodo de la lista. */
	private Nodo<T> cola;

	/** Cantidad de elementos guardados. */
	private int tamanio;

	/**
	 * Agrega un elemento antes del primero. Complejidad: O(1).
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
	 * Agrega un elemento al final de la lista. Complejidad: O(1).
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
	 * Consulta el elemento de una posición. Complejidad: O(n) en el peor caso.
	 *
	 * @param indice posición que se quiere consultar, empezando desde 0
	 * @return elemento guardado en esa posición
	 * @throws IndexOutOfBoundsException si el índice está fuera de la lista
	 */
	@Override
	public T obtener(int indice) {
		validarIndice(indice);
		return nodoEn(indice).dato;
	}

	/**
	 * Retira el elemento de una posición y actualiza los enlaces. Complejidad: O(n)
	 * en el peor caso.
	 *
	 * @param indice posición que se quiere retirar, empezando desde 0
	 * @return elemento retirado
	 * @throws IndexOutOfBoundsException si el índice está fuera de la lista
	 */
	@Override
	public T eliminar(int indice) {
		validarIndice(indice);
		Nodo<T> eliminado;

		if (indice == 0) {
			eliminado = cabeza;
			cabeza = eliminado.siguiente;
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
	 * Busca la primera coincidencia usando {@code equals}. Complejidad: O(n) en el
	 * peor caso.
	 *
	 * @param elemento dato que se quiere buscar
	 * @return índice de la primera coincidencia, o -1 si no existe
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
	 * Comprueba si un elemento está en la lista. Complejidad: O(n).
	 *
	 * @param elemento dato que se quiere buscar
	 * @return {@code true} si existe al menos una coincidencia
	 * @throws IllegalArgumentException si el elemento es {@code null}
	 */
	@Override
	public boolean contiene(T elemento) {
		return indiceDe(elemento) >= 0;
	}

	/**
	 * Consulta la cantidad de elementos. Complejidad: O(1).
	 *
	 * @return cantidad de elementos, incluidas las copias repetidas
	 */
	@Override
	public int tamanio() {
		return tamanio;
	}

	/**
	 * Comprueba si la lista está vacía. Complejidad: O(1).
	 *
	 * @return {@code true} si la lista está vacía
	 */
	@Override
	public boolean estaVacia() {
		return tamanio == 0;
	}

	/**
	 * Deja la lista vacía y lista para recibir elementos. Complejidad: O(1).
	 */
	@Override
	public void vaciar() {
		cabeza = null;
		cola = null;
		tamanio = 0;
	}

	/**
	 * Ordena los nodos con Merge Sort. Los elementos que empatan según el
	 * comparador conservan su orden original. Complejidad: O(n log n) en tiempo y
	 * O(log n) en la pila de llamadas.
	 *
	 * @param comparador criterio de ordenamiento
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

		cabeza = ordenarTramo(cabeza, comparador);
		cola = cabeza;
		while (cola.siguiente != null) {
			cola = cola.siguiente;
		}
	}

	/**
	 * Recorre la lista hasta una posición que ya fue validada.
	 *
	 * @param indice posición del nodo
	 * @return nodo ubicado en esa posición
	 */
	private Nodo<T> nodoEn(int indice) {
		Nodo<T> actual = cabeza;
		for (int posicion = 0; posicion < indice; posicion++) {
			actual = actual.siguiente;
		}
		return actual;
	}

	/**
	 * Divide un tramo en dos mitades, las ordena y vuelve a unirlas.
	 *
	 * @param primero    primer nodo del tramo
	 * @param comparador criterio de ordenamiento
	 * @return primer nodo del tramo ordenado
	 */
	private Nodo<T> ordenarTramo(Nodo<T> primero, Comparador<T> comparador) {
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

		return fusionar(ordenarTramo(primero, comparador), ordenarTramo(segundaMitad, comparador), comparador);
	}

	/**
	 * Une dos tramos ordenados. En caso de empate toma primero el nodo del tramo
	 * izquierdo para conservar el orden original.
	 *
	 * @param izquierda  primer nodo del tramo izquierdo
	 * @param derecha    primer nodo del tramo derecho
	 * @param comparador criterio de ordenamiento
	 * @return primer nodo del tramo unido
	 */
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

	/** Comprueba que el índice corresponda a una posición existente. */
	private void validarIndice(int indice) {
		if (indice < 0 || indice >= tamanio) {
			throw new IndexOutOfBoundsException("Índice " + indice + " fuera de una lista de tamaño " + tamanio);
		}
	}

	/** Comprueba que el elemento recibido pueda guardarse o buscarse. */
	private void validarElemento(T elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("El elemento no puede ser null");
		}
	}

	/**
	 * Copia los elementos al arreglo indicado, desde la cabeza hasta la cola. Las
	 * posiciones sobrantes del arreglo conservan su valor. Complejidad: O(n), donde
	 * n es la cantidad de elementos de la lista.
	 *
	 * @param destino arreglo que recibe los elementos
	 * @throws IllegalArgumentException si el arreglo es nulo o no tiene espacio
	 *                                  para todos los elementos
	 */
	public void copiarEn(T[] destino) {
		if (destino == null || destino.length < tamanio) {
			throw new IllegalArgumentException("El arreglo debe tener espacio para toda la lista");
		}

		int indice = 0;
		for (Nodo<T> actual = cabeza; actual != null; actual = actual.siguiente) {
			destino[indice] = actual.dato;
			indice++;
		}
	}
}