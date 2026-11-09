package co.edu.unbosque.modelo.juego;

/**
 * IA del rival 1 (agresiva y sin cálculo): invoca en ataque la carta de mayor ATK de su
 * mano (elegida con un {@code MonticuloMax} por ATK) y ataca con todos sus monstruos siempre
 * que pueda, aunque pierda. Si el rival no tiene monstruos, ataca directo; si tiene, ataca
 * al más débil (menor valor de combate).
 *
 * <p>Responsable: Santiago.
 */
public class IARival1 implements EstrategiaIA {

    /**
     * Complejidad: O(1): la mano y el campo tienen 5 posiciones, así que hay como máximo
     * 5 ataques y cada uno revisa 5 zonas rivales.
     */
    @Override
    public void jugarTurno(Duelo duelo) {
        if (duelo.estaTerminado()) {
            throw new IllegalStateException("El duelo ya terminó");
        }
        Duelista propio = duelo.getActual();
        Duelista rival = duelo.getRival();

        int posicionMano = SeleccionIA.posicionDeMayorAtaque(propio);
        if (posicionMano != -1 && !duelo.yaInvocoEnEsteTurno()) {
            duelo.invocar(posicionMano, SeleccionIA.zonaParaInvocar(propio), Posicion.ATAQUE);
        }
        for (int zona = 0; zona < Duelista.ZONAS_DE_MONSTRUO; zona++) {
            if (!duelo.sePuedeAtacarEnEsteTurno()) {
                return;
            }
            MonstruoEnCampo atacante = propio.getMonstruo(zona);
            if (atacante != null && atacante.puedeAtacar()) {
                if (rival.tieneMonstruos()) {
                    duelo.atacar(zona, zonaMasDebil(rival));
                } else {
                    duelo.atacarDirecto(zona);
                }
            }
        }
    }

    /** Zona del monstruo rival con menor valor de combate (ATK en ataque, DEF en defensa). */
    private static int zonaMasDebil(Duelista rival) {
        int masDebil = -1;
        for (int zona = 0; zona < Duelista.ZONAS_DE_MONSTRUO; zona++) {
            MonstruoEnCampo monstruo = rival.getMonstruo(zona);
            if (monstruo != null && (masDebil == -1
                    || monstruo.valorDeCombate() < rival.getMonstruo(masDebil).valorDeCombate())) {
                masDebil = zona;
            }
        }
        return masDebil;
    }
}
