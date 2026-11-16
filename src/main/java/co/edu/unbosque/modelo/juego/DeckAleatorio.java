package co.edu.unbosque.modelo.juego;

import java.util.Random;

import co.edu.unbosque.modelo.entidades.Carta;

/**
 * Arma el Deck de un jugador del modo jugador contra jugador: 40 cartas al azar del catálogo
 * completo, con máximo {@value Chest#MAXIMO_COPIAS} copias de cada carta.
 *
 * <p>Pone las 3 copias de las 100 cartas en un arreglo de 300 posiciones y aplica
 * Fisher-Yates parcial: solo baraja las primeras 40 posiciones, que son el Deck. Como cada
 * carta aparece 3 veces en el arreglo, nunca salen más de 3 copias.
 *
 * <p>Responsable: Santiago.
 */
public final class DeckAleatorio {

    private DeckAleatorio() {
        // Solo tiene métodos estáticos.
    }

    /**
     * Genera un Deck aleatorio de {@value Deck#TAMANIO} cartas. Con el mismo {@code Random}
     * (misma semilla) sale el mismo Deck. Complejidad: O(n · c), con n = 100 cartas del
     * catálogo y c = 3 copias, para llenar el arreglo; el barajado parcial es O(40).
     *
     * @throws IllegalArgumentException si el catálogo o el {@code Random} son {@code null}
     * @throws IllegalStateException si al catálogo le falta alguna carta
     */
    public static Carta[] generar(Catalogo catalogo, Random random) {
        if (catalogo == null) {
            throw new IllegalArgumentException("Se necesita el catálogo para armar el Deck");
        }
        if (random == null) {
            throw new IllegalArgumentException("Se necesita un Random para armar el Deck");
        }
        Carta[] copias = new Carta[Carta.ID_MAXIMO * Chest.MAXIMO_COPIAS];
        int posicion = 0;
        for (int id = 1; id <= Carta.ID_MAXIMO; id++) {
            Carta carta = catalogo.buscarPorId(id);
            if (carta == null) {
                throw new IllegalStateException("El catálogo no tiene la carta " + id);
            }
            for (int copia = 0; copia < Chest.MAXIMO_COPIAS; copia++) {
                copias[posicion++] = carta;
            }
        }
        // Fisher-Yates parcial: la posición i se cambia con una al azar entre i y el final.
        for (int i = 0; i < Deck.TAMANIO; i++) {
            int j = i + random.nextInt(copias.length - i);
            Carta temporal = copias[i];
            copias[i] = copias[j];
            copias[j] = temporal;
        }
        Carta[] deck = new Carta[Deck.TAMANIO];
        System.arraycopy(copias, 0, deck, 0, Deck.TAMANIO);
        return deck;
    }
}
