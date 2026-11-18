package co.edu.unbosque.modelo.ordenamiento;

import co.edu.unbosque.modelo.entidades.Carta;

/**
 * Define los criterios disponibles para ordenar las cartas del cofre y de la
 * baraja.
 *
 * <p>
 * Cada criterio implementa Comparador, por lo que puede utilizarse directamente
 * con Merge Sort o Insertion Sort.
 *
 * <p>
 * Responsable: Brayan.
 */
public enum CriterioOrdenCarta implements Comparador<Carta> {

	/** Ordena por ID de menor a mayor. */
	NUMERO("Número") {
		@Override
		protected int compararCartas(Carta primera, Carta segunda) {
			return Integer.compare(primera.getId(), segunda.getId());
		}
	},

	/**
	 * Ordena por nombre, ignorando las diferencias entre mayúsculas y minúsculas.
	 */
	NOMBRE("Nombre") {
		@Override
		protected int compararCartas(Carta primera, Carta segunda) {
			return primera.getNombre().compareToIgnoreCase(segunda.getNombre());
		}
	},

	/**
	 * Ordena según la posición del tipo en TipoCarta. Las cartas del mismo tipo se
	 * ordenan por ID de menor a mayor.
	 */
	TIPO("Tipo") {
		@Override
		protected int compararCartas(Carta primera, Carta segunda) {
			int resultado = Integer.compare(primera.getTipo().ordinal(), segunda.getTipo().ordinal());

			if (resultado == 0) {
				return Integer.compare(primera.getId(), segunda.getId());
			}

			return resultado;
		}
	},

	/** Ordena por ataque de menor a mayor. */
	ATAQUE_ASCENDENTE("Ataque de menor a mayor") {
		@Override
		protected int compararCartas(Carta primera, Carta segunda) {
			return Integer.compare(primera.getAtaque(), segunda.getAtaque());
		}
	},

	/** Ordena por ataque de mayor a menor. */
	ATAQUE_DESCENDENTE("Ataque de mayor a menor") {
		@Override
		protected int compararCartas(Carta primera, Carta segunda) {
			return Integer.compare(segunda.getAtaque(), primera.getAtaque());
		}
	},

	/** Ordena por defensa de menor a mayor. */
	DEFENSA_ASCENDENTE("Defensa de menor a mayor") {
		@Override
		protected int compararCartas(Carta primera, Carta segunda) {
			return Integer.compare(primera.getDefensa(), segunda.getDefensa());
		}
	},

	/** Ordena por defensa de mayor a menor. */
	DEFENSA_DESCENDENTE("Defensa de mayor a menor") {
		@Override
		protected int compararCartas(Carta primera, Carta segunda) {
			return Integer.compare(segunda.getDefensa(), primera.getDefensa());
		}
	};

	/** Nombre legible del criterio para presentarlo al usuario. */
	private final String nombreVisible;

	/**
	 * Asigna el texto que identifica el criterio.
	 *
	 * @param nombreVisible nombre que se mostrará al usuario
	 */
	CriterioOrdenCarta(String nombreVisible) {
		this.nombreVisible = nombreVisible;
	}

	/**
	 * Consulta el nombre legible del criterio. Complejidad: O(1).
	 *
	 * @return texto que identifica el criterio
	 */
	public String getNombreVisible() {
		return nombreVisible;
	}

	/**
	 * Compara dos cartas según el criterio seleccionado.
	 *
	 * <p>
	 * Devuelve cero cuando las cartas empatan en ese criterio. Un algoritmo estable
	 * conserva el orden anterior de esos empates.
	 *
	 * <p>
	 * Complejidad: O(1) para número, tipo, ataque y defensa. Para nombre es O(L),
	 * con L caracteres comparados.
	 *
	 * @param primera primera carta de la comparación
	 * @param segunda segunda carta de la comparación
	 * @return un número negativo si la primera va antes, cero si empatan o un
	 *         número positivo si la primera va después
	 * @throws IllegalArgumentException si alguna carta es nula
	 */
	@Override
	public final int comparar(Carta primera, Carta segunda) {
		if (primera == null || segunda == null) {
			throw new IllegalArgumentException("Las cartas de la comparación no pueden ser null");
		}

		return compararCartas(primera, segunda);
	}

	/**
	 * Aplica la comparación específica de cada constante. Recibe cartas que ya
	 * fueron validadas.
	 *
	 * @param primera primera carta de la comparación
	 * @param segunda segunda carta de la comparación
	 * @return resultado de comparar las cartas
	 */
	protected abstract int compararCartas(Carta primera, Carta segunda);
}