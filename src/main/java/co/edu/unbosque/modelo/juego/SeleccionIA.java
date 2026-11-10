package co.edu.unbosque.modelo.juego;

import co.edu.unbosque.modelo.estructuras.MonticuloMax;

/**
 * Elecciones que comparten las 3 IA: qué carta de la mano invocar y en qué zona.
 * Solo la usan las clases de IA de este paquete.
 */
final class SeleccionIA {

    private SeleccionIA() {
        // Solo tiene métodos estáticos.
    }

    /**
     * Posición de la mano con la carta de mayor ATK, o -1 si la mano está vacía. Arma un
     * {@link MonticuloMax} con las posiciones ocupadas (heapify) y ve el máximo.
     * Complejidad: O(1), porque la mano tiene 5 posiciones.
     */
    static int posicionDeMayorAtaque(Duelista duelista) {
        int ocupadas = duelista.cartasEnMano();
        if (ocupadas == 0) {
            return -1;
        }
        Integer[] posiciones = new Integer[ocupadas];
        int i = 0;
        for (int posicion = 0; posicion < Duelista.TAMANIO_MANO; posicion++) {
            if (duelista.getCartaEnMano(posicion) != null) {
                posiciones[i++] = posicion;
            }
        }
        MonticuloMax<Integer> porAtaque = new MonticuloMax<>(posiciones,
                (a, b) -> Integer.compare(duelista.getCartaEnMano(a).getAtaque(),
                        duelista.getCartaEnMano(b).getAtaque()));
        return porAtaque.verMax();
    }

    /**
     * Zona donde invocar: la primera libre o, si el campo está lleno, la del monstruo propio
     * de menor ATK (ese se reemplaza). Complejidad: O(1) (5 zonas).
     */
    static int zonaParaInvocar(Duelista duelista) {
        int libre = duelista.primeraZonaLibre();
        if (libre != -1) {
            return libre;
        }
        int masDebil = 0;
        for (int zona = 1; zona < Duelista.ZONAS_DE_MONSTRUO; zona++) {
            if (duelista.getMonstruo(zona).getCarta().getAtaque()
                    < duelista.getMonstruo(masDebil).getCarta().getAtaque()) {
                masDebil = zona;
            }
        }
        return masDebil;
    }

    /** Mayor ATK entre los monstruos en ataque del duelista; 0 si no tiene. Complejidad: O(1). */
    static int mayorAtaqueEnCampo(Duelista duelista) {
        int mayor = 0;
        for (int zona = 0; zona < Duelista.ZONAS_DE_MONSTRUO; zona++) {
            MonstruoEnCampo monstruo = duelista.getMonstruo(zona);
            if (monstruo != null && monstruo.getPosicion() == Posicion.ATAQUE) {
                mayor = Math.max(mayor, monstruo.getCarta().getAtaque());
            }
        }
        return mayor;
    }
}
