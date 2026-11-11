package co.edu.unbosque.modelo.persistencia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.TipoCarta;

/**
 * Convertimos el arhivo csv en un arreglo de Carta, asi verificamos la carta
 * por linea y se separa en 8 campos la primera es el encabezado Responsable:
 * Daniel.
 */
public final class LectorCartasCsv {

	public static final String ENCABEZADO = "id;nombre;tipo;ataque;defensa;contrasena;imagen;descripcion";
	private static final int CAMPOS = 8;
	private static final char MARCA_UTF8 = '\uFEFF';

	private LectorCartasCsv() {
	}

	/**
	 * Abre un archivo de src/main/resources y lo lee.
	 */
	public static Carta[] leerRecurso(String ruta) {
		if (ruta == null) {
			throw new IllegalArgumentException("La ruta no puede ser null");
		}
		String rutaAbsoluta = ruta.startsWith("/") ? ruta : "/" + ruta;
		InputStream flujo = LectorCartasCsv.class.getResourceAsStream(rutaAbsoluta);
		if (flujo == null) {
			throw new IllegalArgumentException("No existe el archivo " + rutaAbsoluta + " en resources");
		}
		// try-with-resources: cierra el archivo al terminar, aunque haya un error.
		try (BufferedReader entrada = new BufferedReader(new InputStreamReader(flujo, StandardCharsets.UTF_8))) {
			return leer(entrada);
		} catch (IOException e) {
			throw new UncheckedIOException("No se pudo cerrar " + rutaAbsoluta, e);
		}
	}

	/**
	 * Lee el encabezado y luego una carta por línea. Las líneas en blanco se
	 * ignoran.
	 */
	public static Carta[] leer(BufferedReader entrada) {
		if (entrada == null) {
			throw new IllegalArgumentException("La entrada no puede ser null");
		}
		Carta[] leidas = new Carta[Carta.ID_MAXIMO]; // nunca hay más de 100
		boolean[] idVisto = new boolean[Carta.ID_MAXIMO + 1];
		int cantidad = 0;

		int numeroLinea = 1;
		String encabezado = leerLinea(entrada);
		if (encabezado == null) {
			throw new CsvInvalidoException(numeroLinea, "el archivo está vacío");
		}
		if (!encabezado.isEmpty() && encabezado.charAt(0) == MARCA_UTF8) {
			encabezado = encabezado.substring(1);
		}
		if (!encabezado.trim().equals(ENCABEZADO)) {
			throw new CsvInvalidoException(numeroLinea, "encabezado inválido; debe ser: " + ENCABEZADO);
		}

		String linea;
		while ((linea = leerLinea(entrada)) != null) {
			numeroLinea++;
			if (linea.isBlank()) {
				continue;
			}
			if (cantidad == Carta.ID_MAXIMO) {
				throw new CsvInvalidoException(numeroLinea, "hay más de " + Carta.ID_MAXIMO + " cartas");
			}
			Carta carta = convertir(linea, numeroLinea);
			if (idVisto[carta.getId()]) { // O(1): una sola casilla
				throw new CsvInvalidoException(numeroLinea, "el id " + carta.getId() + " está repetido");
			}
			idVisto[carta.getId()] = true;
			leidas[cantidad] = carta;
			cantidad++;
		}

		Carta[] resultado = new Carta[cantidad];
		System.arraycopy(leidas, 0, resultado, 0, cantidad);
		return resultado;
	}

	private static Carta convertir(String linea, int numeroLinea) {
		// El -1 conserva los campos vacíos del final: sin él, "...;;" perdería imagen y
		// descripción.
		String[] campos = linea.split(";", -1);
		if (campos.length != CAMPOS) {
			throw new CsvInvalidoException(numeroLinea, "se esperaban " + CAMPOS + " campos y hay " + campos.length);
		}
		int id = entero(campos[0], "el id", numeroLinea);
		TipoCarta tipo = tipo(campos[2], numeroLinea);
		int ataque = entero(campos[3], "el ataque", numeroLinea);
		int defensa = entero(campos[4], "la defensa", numeroLinea);
		try {
			// Carta valida el resto: id entre 1 y 100, nombre no vacío, contraseña de 8
			// dígitos...
			return new Carta(id, campos[1], tipo, ataque, defensa, campos[7], campos[6], campos[5].trim());
		} catch (IllegalArgumentException e) {
			throw new CsvInvalidoException(numeroLinea, e.getMessage());
		}
	}

	private static int entero(String texto, String nombreCampo, int numeroLinea) {
		try {
			return Integer.parseInt(texto.trim());
		} catch (NumberFormatException e) {
			throw new CsvInvalidoException(numeroLinea, nombreCampo + " no es un número: \"" + texto + "\"");
		}
	}

	private static TipoCarta tipo(String texto, int numeroLinea) {
		try {
			return TipoCarta.valueOf(texto.trim());
		} catch (IllegalArgumentException e) {
			throw new CsvInvalidoException(numeroLinea, "tipo desconocido: \"" + texto + "\"");
		}
	}

	/**
	 * readLine lanza IOException (excepción verificada); aquí se envuelve para no
	 * obligar a capturarla.
	 */
	private static String leerLinea(BufferedReader entrada) {
		try {
			return entrada.readLine();
		} catch (IOException e) {
			throw new UncheckedIOException("Error leyendo el archivo de cartas", e);
		}
	}
}