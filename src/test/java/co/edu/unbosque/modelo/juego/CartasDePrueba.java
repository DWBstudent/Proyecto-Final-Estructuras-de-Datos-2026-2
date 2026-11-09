package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.TipoCarta;

/** Cartas, Decks y catálogos para las pruebas de la IA y la campaña. */
final class CartasDePrueba {

    private CartasDePrueba() {
    }

    static Carta carta(int id, int ataque, int defensa) {
        return new Carta(id, "Carta " + id, TipoCarta.GUERRERO, ataque, defensa, "", "",
                String.format("%08d", id));
    }

    /** 40 copias de la misma carta: el barajado no cambia qué hay en la mano. */
    static Carta[] deckDe(Carta carta) {
        Carta[] deck = new Carta[Deck.TAMANIO];
        for (int i = 0; i < deck.length; i++) {
            deck[i] = carta;
        }
        return deck;
    }

    /** 40 cartas distintas (ids 1 a 40) con ATK = id × 50. */
    static Carta[] deckVariado() {
        Carta[] deck = new Carta[Deck.TAMANIO];
        for (int i = 0; i < deck.length; i++) {
            deck[i] = carta(i + 1, (i + 1) * 50, 500);
        }
        return deck;
    }

    /** Catálogo de 100 cartas en el que todas tienen el mismo ATK y la misma DEF. */
    static Catalogo catalogoUniforme(int ataque, int defensa) {
        Carta[] cartas = new Carta[Carta.ID_MAXIMO + 1];
        for (int id = 1; id <= Carta.ID_MAXIMO; id++) {
            cartas[id] = carta(id, ataque, defensa);
        }
        return catalogoCon(cartas);
    }

    /**
     * Catálogo de 100 cartas parecido al real: el ATK y la DEF crecen con el id y tienen
     * algo de variación, así los rivales 1, 2 y 3 son cada vez más fuertes.
     */
    static Catalogo catalogoVariado() {
        Carta[] cartas = new Carta[Carta.ID_MAXIMO + 1];
        for (int id = 1; id <= Carta.ID_MAXIMO; id++) {
            int ataque = 300 + id * 25 + (id * 7 % 10) * 60;
            int defensa = 200 + id * 22 + (id * 3 % 10) * 70;
            cartas[id] = carta(id, ataque, defensa);
        }
        return catalogoCon(cartas);
    }

    private static Catalogo catalogoCon(Carta[] cartasPorId) {
        return new Catalogo() {
            @Override
            public Carta buscarPorId(int id) {
                return cartasPorId[id];
            }
        };
    }
}
