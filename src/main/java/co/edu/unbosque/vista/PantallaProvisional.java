package co.edu.unbosque.vista;

import co.edu.unbosque.controlador.Navegador;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**Esta pantalla es provicional para las ocpiones del menu que todavia no hemos desarrollado
 * responsable : Daniel
 * */



public class PantallaProvisional implements Pantalla {
	
	private final VBox raiz;
	
	public PantallaProvisional(String nombre, Navegador navegador) {
		if (nombre== null || navegador == null ) {
			throw new IllegalArgumentException("Falta el nombre o el navegador");
		}
		Label titulo = new Label (nombre);
		titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
		Label aviso = new Label("Esta pantalla todavia esta en construccion.");
		Button volver = new Button("Volver");
		
		volver.setOnAction(evento -> navegador.volver());
		
		raiz = new VBox(16, titulo, aviso, volver);
		raiz.setAlignment(Pos.CENTER);
	}
	@Override
	public Parent getVista() {
	    return raiz;
	}

}
