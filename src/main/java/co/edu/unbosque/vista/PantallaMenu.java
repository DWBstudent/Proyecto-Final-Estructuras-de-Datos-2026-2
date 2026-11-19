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
    private final ControladorMenu controlador;
    private final Label estado = new Label();
    
    
    public PantallaMenu(ControladorMenu controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El menú necesita su controlador");
        }
        this.controlador = controlador;
        Label titulo = new Label("Juego de cartas");
        titulo.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
        VBox.setMargin(titulo, new Insets(0, 0, 24, 0));
        

        Button musica = boton("Música: sí", () -> { });
        musica.setOnAction(evento -> musica.setText(controlador.alternarMusica() ? "Música: sí" : "Música: no"));

        raiz = new VBox(12,
                titulo,
                boton("Modo historia", controlador::irAModoHistoria),
                boton("Modo libre", controlador::irAModoLibre),
                boton("Jugador contra jugador", controlador::irAJugadorContraJugador),
                boton("Chest y Deck", controlador::irAColeccion),
                boton("Inventario", controlador::irAInventario),
                boton("Contraseñas", controlador::irAContrasenas),
                musica,
                boton("Salir", Platform::exit),
                estado);
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

	/** Cada vez que aparece el menú, el controlador guarda la partida. */
	@Override
	public void alEntrar() {
		controlador.alVolverAlMenu(this);
	}

	/** Muestra debajo de los botones si la partida se guardó o se cargó. */
	public void mostrarEstado(String texto) {
		estado.setText(texto);
	}

}