package co.edu.unbosque.controlador;

import java.net.URL;

import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

/**
 * Toca la música de fondo en bucle: una pista para el menú y otra para el duelo.
 * Si se pide la pista que ya suena, no la reinicia. Si el equipo no tiene audio o
 * falta el archivo, el juego sigue sin música.
 *
 * <p>Las dos pistas son originales, compuestas para este juego en estilo 8 bits:
 * el menú con aire egipcio (escala Hijaz en Re) y el duelo con estilo de
 * electrónica de los 2000 a 140 pulsos por minuto.
 *
 * <p>Responsable: Daniel.
 */
public class ReproductorMusica {

    /** Pistas disponibles y su archivo en {@code src/main/resources}. */
    public enum Pista {
        MENU("/musica/menu.wav"),
        DUELO("/musica/duelo.wav");

        private final String ruta;

        Pista(String ruta) {
            this.ruta = ruta;
        }
    }

    private static final double VOLUMEN = 0.45;

    /** Un reproductor por pista; se crean la primera vez que se usan. */
    private final MediaPlayer[] reproductores = new MediaPlayer[Pista.values().length];

    /** Pistas que no se pudieron cargar, para no intentarlo (ni avisar) otra vez. */
    private final boolean[] noDisponible = new boolean[Pista.values().length];

    private Pista sonando;
    private boolean silenciada;

    /**
     * Cambia a la pista indicada. Si ya está sonando, la deja seguir. Complejidad: O(1).
     *
     * @throws IllegalArgumentException si la pista es {@code null}
     */
    public void tocar(Pista pista) {
        if (pista == null) {
            throw new IllegalArgumentException("La pista no puede ser null");
        }
        if (pista == sonando) {
            return;
        }
        detenerActual();
        sonando = pista;
        if (!silenciada) {
            iniciar(pista);
        }
    }

    /**
     * Apaga o prende la música. Complejidad: O(1).
     *
     * @return {@code true} si quedó silenciada
     */
    public boolean alternarSilencio() {
        silenciada = !silenciada;
        if (silenciada) {
            detenerActual();
        } else if (sonando != null) {
            iniciar(sonando);
        }
        return silenciada;
    }

    private void iniciar(Pista pista) {
        MediaPlayer reproductor = reproductor(pista);
        if (reproductor != null) {
            reproductor.play();
        }
    }

    private void detenerActual() {
        if (sonando != null && reproductores[sonando.ordinal()] != null) {
            reproductores[sonando.ordinal()].stop();
        }
    }

    private MediaPlayer reproductor(Pista pista) {
        int i = pista.ordinal();
        if (reproductores[i] == null && !noDisponible[i]) {
            URL archivo = ReproductorMusica.class.getResource(pista.ruta);
            try {
                if (archivo == null) {
                    throw new MediaException("no existe " + pista.ruta);
                }
                MediaPlayer nuevo = new MediaPlayer(new Media(archivo.toExternalForm()));
                nuevo.setCycleCount(MediaPlayer.INDEFINITE);
                nuevo.setVolume(VOLUMEN);
                reproductores[i] = nuevo;
            } catch (MediaException | UnsupportedOperationException e) {
                noDisponible[i] = true;
                System.err.println("El juego sigue sin música: " + e.getMessage());
            }
        }
        return reproductores[i];
    }
}