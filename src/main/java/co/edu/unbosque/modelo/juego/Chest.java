package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.estructuras.ArbolAVL;
import co.edu.unbosque.modelo.estructuras.Lista;
import co.edu.unbosque.modelo.estructuras.ListaSimple;
import co.edu.unbosque.modelo.ordenamiento.Comparador;

/**
 * Guarda las cartas que el jugador tiene disponibles en el cofre. Cada nodo
 * representa una copia y las cartas se recorren en el orden en que aparecen en
 * la lista.
 *
 * <p>
 * El cofre limita sus propias copias por ID. El límite de la colección
 * completa, que incluye el cofre y la baraja, se valida en el jugador.
 *
 * <p>
 * Responsable: Brayan.
 */
public class Chest {

	/** Máximo de copias de un mismo ID en la colección del jugador. */
	public static final int MAXIMO_COPIAS = 3;

	/** Lista simplemente enlazada que guarda una entrada por cada copia. */
	private final Lista<Carta> cartas = new ListaSimple<>();

	/** Índice por ID que guarda una entrada por cada carta distinta del cofre. */
	private final ArbolAVL<Integer, Carta> indicePorId = new ArbolAVL<>();

	/**
	 * Cantidad de copias que hay en el cofre por cada ID. La posición 0 queda libre
	 * porque los IDs empiezan en 1.
	 */
	private final int[] copiasPorId = new int[Carta.ID_MAXIMO + 1];

	/**
	 * Índice por nombre. Cada nombre agrupa los IDs distintos que le corresponden.
	 */
	private final ArbolAVL<ClaveNombre, ListaSimple<Integer>> indicePorNombre = new ArbolAVL<>();

	/**
	 * Clave que compara nombres sin distinguir mayúsculas y minúsculas. El AVL
	 * considera equivalentes las claves cuya comparación devuelve cero.
	 *
	 * @param texto nombre utilizado en la búsqueda
	 */
	private record ClaveNombre(String texto) implements Comparable<ClaveNombre> {

		/**
		 * Compara dos nombres ignorando mayúsculas y minúsculas. Complejidad: O(L), con
		 * L caracteres comparados.
		 *
		 * @param otra clave con la que se compara
		 * @return resultado del orden alfabético
		 */
		@Override
		public int compareTo(ClaveNombre otra) {
			return texto.compareToIgnoreCase(otra.texto);
		}
	}

	/**
	 * Agrega una copia al final del cofre. Si es la primera de su ID, también la
	 * registra en el índice. Complejidad: O(1) para una copia adicional y O(log d)
	 * para un ID nuevo, donde d es la cantidad de cartas distintas.
	 *
	 * @param carta carta que se quiere guardar
	 * @return {@code true} si se agregó o {@code false} si alcanzó el límite
	 * @throws IllegalArgumentException si la carta es nula
	 */
	public boolean agregar(Carta carta) {
		if (carta == null) {
			throw new IllegalArgumentException("La carta no puede ser null");
		}

		int idCarta = carta.getId();
		if (copiasPorId[idCarta] >= MAXIMO_COPIAS) {
			return false;
		}

		cartas.agregarAlFinal(carta);
		copiasPorId[idCarta]++;

		if (copiasPorId[idCarta] == 1) {
			indicePorId.poner(idCarta, carta);
			registrarNombre(carta);
		}

		return true;
	}

	/**
	 * Retira la primera copia del ID indicado y actualiza el índice. Complejidad:
	 * O(n + log d), con n copias y d cartas distintas en el cofre.
	 *
	 * @param idCarta ID de la carta que se quiere retirar
	 * @return {@code true} si se retiró una copia o {@code false} si no había
	 *         copias
	 */
	public boolean quitar(int idCarta) {
		Carta carta = buscarPorId(idCarta);
		if (carta == null) {
			return false;
		}

		int posicion = cartas.indiceDe(carta);
		cartas.eliminar(posicion);
		copiasPorId[idCarta]--;

		if (copiasPorId[idCarta] == 0) {
			retirarNombre(carta);
			indicePorId.eliminar(idCarta);
		} else {
			int siguienteCopia = cartas.indiceDe(carta);
			indicePorId.poner(idCarta, cartas.obtener(siguienteCopia));
		}
		return true;
	}

	/**
	 * Consulta cuántas copias de un ID están guardadas en el cofre. Complejidad:
	 * O(1).
	 *
	 * @param idCarta ID de la carta que se quiere consultar
	 * @return número de copias, o 0 si el ID es inválido o no está en el cofre
	 */
	public int copiasDe(int idCarta) {
		if (idCarta < 1 || idCarta > Carta.ID_MAXIMO) {
			return 0;
		}

		return copiasPorId[idCarta];
	}

	/**
	 * Consulta cuántos IDs tienen al menos una copia en el cofre. Complejidad:
	 * O(1).
	 *
	 * @return cantidad de cartas distintas
	 */
	public int cartasDistintas() {
		return indicePorId.tamanio();
	}

	/**
	 * Devuelve un arreglo nuevo con todas las copias, en el orden de la lista.
	 * Cambiar una posición del arreglo deja intacto el contenido del cofre.
	 * Complejidad: O(n²), porque cada consulta por índice recorre la lista
	 * simplemente enlazada desde el primer nodo.
	 *
	 * @return arreglo con las cartas actuales, vacío si el cofre está vacío
	 */
	public Carta[] cartasActuales() {
		Carta[] copia = new Carta[cartas.tamanio()];

		for (int indice = 0; indice < copia.length; indice++) {
			copia[indice] = cartas.obtener(indice);
		}

		return copia;
	}

	/**
	 * Busca en el índice la primera copia disponible del ID indicado. Complejidad:
	 * O(log d), con d cartas distintas en el cofre.
	 *
	 * @param idCarta ID que se quiere buscar
	 * @return carta disponible, o {@code null} si el ID no está en el cofre
	 */
	public Carta buscarPorId(int idCarta) {
		return indicePorId.obtener(idCarta);
	}

	/**
	 * Ordena las copias del cofre usando el Merge Sort de la lista simple. Los
	 * elementos que empatan conservan su orden anterior.
	 *
	 * <p>
	 * Complejidad: O(n log n), suponiendo comparaciones de costo constante. Al
	 * comparar nombres, el costo también depende de su longitud.
	 *
	 * @param comparador criterio que se quiere aplicar
	 * @throws IllegalArgumentException si el comparador es nulo
	 */
	public void ordenar(Comparador<Carta> comparador) {
		cartas.ordenar(comparador);
	}

	/**
	 * Busca las cartas distintas cuyo nombre completo coincide con el recibido.
	 * Ignora mayúsculas, minúsculas y espacios en los extremos.
	 *
	 * <p>
	 * Devuelve una carta por ID, aunque el cofre tenga varias copias de ella.
	 *
	 * <p>
	 * Complejidad: O(L log g + k log d), con L caracteres comparados, g nombres
	 * distintos, d IDs distintos y k IDs encontrados.
	 *
	 * @param nombre nombre completo que se quiere buscar
	 * @return arreglo con las coincidencias, vacío si el nombre está ausente
	 * @throws IllegalArgumentException si el nombre es nulo o está en blanco
	 */
	public Carta[] buscarPorNombre(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("Escribe el nombre de una carta");
		}

		ClaveNombre clave = new ClaveNombre(nombre.strip());
		ListaSimple<Integer> ids = indicePorNombre.obtener(clave);

		if (ids == null) {
			return new Carta[0];
		}

		Integer[] identificadores = new Integer[ids.tamanio()];
		ids.copiarEn(identificadores);
		Carta[] coincidencias = new Carta[identificadores.length];
		for (int indice = 0; indice < identificadores.length; indice++) {
			coincidencias[indice] = indicePorId.obtener(identificadores[indice]);
		}

		return coincidencias;
	}

	/**
	 * Registra el ID de una carta cuando entra su primera copia al cofre. Los IDs
	 * que comparten nombre se agrupan bajo una misma clave.
	 *
	 * @param carta carta que acaba de incorporarse al índice por ID
	 */
	private void registrarNombre(Carta carta) {
		ClaveNombre clave = new ClaveNombre(carta.getNombre().strip());
		ListaSimple<Integer> ids = indicePorNombre.obtener(clave);
		if (ids == null) {
			ids = new ListaSimple<>();
			indicePorNombre.poner(clave, ids);
		}

		ids.agregarAlFinal(carta.getId());
	}

	/**
	 * Retira el ID cuando sale su última copia del cofre. Si el nombre queda sin
	 * IDs asociados, también elimina su entrada del AVL.
	 *
	 * @param carta carta que acaba de quedarse sin copias
	 */
	private void retirarNombre(Carta carta) {
		ClaveNombre clave = new ClaveNombre(carta.getNombre().strip());
		ListaSimple<Integer> ids = indicePorNombre.obtener(clave);
		int posicion = ids.indiceDe(carta.getId());
		ids.eliminar(posicion);
		if (ids.estaVacia()) {
			indicePorNombre.eliminar(clave);
		}
	}
}