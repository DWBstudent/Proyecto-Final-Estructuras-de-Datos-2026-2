package co.edu.unbosque.vista;

import co.edu.unbosque.controlador.ControladorMenu;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**Esta es la pantalla del menu principal que solo muestra los botones
 * va con el ControladorMenu para decirle hacia a donde se mueve
 * 
 * responsable dANIEL 
 * */

public class PantallaMenu implements Pantalla {
	
	private static final double ANCHO_BOTON = 260;

    private final VBox raiz;
    
    
    public PantallaMenu(ControladorMenu controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El menú necesita su controlador");
        }
        Label titulo = new Label("Juego de cartas");
        titulo.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
        VBox.setMargin(titulo, new Insets(0, 0, 24, 0));

        raiz = new VBox(12,
                titulo,
                boton("Modo historia", controlador::irAModoHistoria),
                boton("Modo libre", controlador::irAModoLibre),
                boton("Jugador contra jugador", controlador::irAJugadorContraJugador),
                boton("Chest y Deck", controlador::irAColeccion),
                boton("Inventario", controlador::irAInventario),
                boton("Contraseñas", controlador::irAContrasenas),
                boton("Salir", Platform::exit));
        raiz.setAlignment(Pos.CENTER);
        raiz.setPadding(new Insets(24));
    }
    
    // creacion del boton 
    
    private static Button boton(String texto, Runnable accion) {
        Button boton = new Button(texto);
        boton.setPrefWidth(ANCHO_BOTON);
        boton.setStyle("-fx-font-size: 16px;");
        boton.setOnAction(evento -> accion.run());
        return boton;
    }

	@Override
	public Parent getVista() {
        return raiz;
	}

}
