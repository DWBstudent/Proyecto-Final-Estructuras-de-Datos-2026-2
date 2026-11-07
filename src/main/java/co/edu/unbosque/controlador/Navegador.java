package co.edu.unbosque.controlador;

import co.edu.unbosque.modelo.estructuras.Pila;
import co.edu.unbosque.vista.Pantalla;
import javafx.scene.Scene;

/**
 * Cambia la pantalla que se ve. Guarda las pantallas anteriores en una {@link Pila},
 * así "volver" siempre lleva a la última pantalla visitada.
 *
 * <p>Responsable: Daniel. Contrato del hito H0: todas las pantallas navegan con esta clase.
 */
public class Navegador {

    private final Scene escena;
    private final Pila<Pantalla> historial = new Pila<>();
    private Pantalla actual;

    /**
     * @throws IllegalArgumentException si la escena es {@code null}
     */
    public Navegador(Scene escena) {
        if (escena == null) {
            throw new IllegalArgumentException("El navegador necesita una escena");
        }
        this.escena = escena;
    }

    /**
     * Muestra otra pantalla y guarda la actual para poder volver. Complejidad: O(1).
     *
     * @throws IllegalArgumentException si la pantalla es {@code null}
     */
    public void ir(Pantalla destino) {
        validar(destino);
        if (actual != null) {
            historial.apilar(actual);
        }
        mostrar(destino);
    }

    /**
     * Muestra otra pantalla sin guardar la actual, por ejemplo al pasar del duelo
     * al resultado (no se debe poder volver al duelo terminado). Complejidad: O(1).
     *
     * @throws IllegalArgumentException si la pantalla es {@code null}
     */
    public void reemplazar(Pantalla destino) {
        validar(destino);
        mostrar(destino);
    }

    /**
     * Vuelve a la pantalla anterior. Complejidad: O(1).
     *
     * @return {@code false} si no hay pantalla anterior
     */
    public boolean volver() {
        if (historial.estaVacia()) {
            return false;
        }
        mostrar(historial.desapilar());
        return true;
    }

    public Pantalla getActual() {
        return actual;
    }

    private void mostrar(Pantalla pantalla) {
        actual = pantalla;
        pantalla.alEntrar();
        escena.setRoot(pantalla.getVista());
    }

    private static void validar(Pantalla pantalla) {
        if (pantalla == null) {
            throw new IllegalArgumentException("La pantalla no puede ser null");
        }
    }
}
