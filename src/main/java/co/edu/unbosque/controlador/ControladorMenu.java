package co.edu.unbosque.controlador;

import co.edu.unbosque.vista.PantallaProvisional;

/**Esta es la pantalla que lleva daa opcion del menu principal
 * la tapnalla del menu solo llama los metodos
 * 
 * mientras que una pantalla no este construida muestra la [PantallaProvisional]
 * 
 * Responsable Daniel 
 * */

public class ControladorMenu {
	private final Navegador navegador;
	
	// constructor
	public ControladorMenu(Navegador navegador) {
		if (navegador == null) {
			throw new IllegalArgumentException("El controlador del menu necesita el navegador");
		}
		this.navegador=navegador;
	}
	
	// para el modo con tres rivales seguidos
	public void irAModoHistoria() {
		navegador.ir(new PantallaProvisional("modo Historio", navegador));
	}
	
	// para el modo libre 
	public void irAModoLibre() {
		navegador.ir(new PantallaProvisional("Modo Libre", navegador));
	}
	
	// jugador contra jugador con cartas aleatorias
	public void irAJugadorContraJugador() {
		navegador.ir(new PantallaProvisional("JcJ", navegador)); 
	}
	
	// acceso al chest y al deck 
	public void irAColeccion() {
		navegador.ir(new PantallaProvisional("Chest y Deck", navegador));
	}
	
	// inventario con las 100 cartas
	public void irAInventario() {	
		navegador.ir(new PantallaProvisional("Inventario", navegador));
	}
	
	// pantalla pra rediminir las contraseñas
	public void irAContrasenas() {
		navegador.ir(new PantallaProvisional("Contraseñas", navegador));
	}
}
