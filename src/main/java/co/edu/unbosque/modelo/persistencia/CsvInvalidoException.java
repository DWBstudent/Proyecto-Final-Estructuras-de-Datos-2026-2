package co.edu.unbosque.modelo.persistencia;
/**
 * Esto se activa cuando cartas.csv tiene algun error
 * vamos a guardar el numero de la linea para saber exactamente donde esta
 * el problema
 * */

public class CsvInvalidoException extends RuntimeException {
	private static final long serialVersionUID =1L;
	private final int numeroLinea;
	
	public CsvInvalidoException (int numeroLinea,String mensaje) {
		super ("Linea "+ numeroLinea +": "+mensaje);
		this.numeroLinea = numeroLinea;
	}
	
	/** linea del archivo deonde va estar el error */
	public int getNumeroLinea() {
		return numeroLinea;
	}

}
