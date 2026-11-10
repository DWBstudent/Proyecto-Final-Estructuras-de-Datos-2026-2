package co.edu.unbosque.modelo.juego;

/**
 * IA del rival 3: juega como {@link IARival2} (y tiene un Deck más fuerte), pero prioriza
 * el ataque directo. Un ataque que destruye el último monstruo del rival recibe un bono de
 * {@value #BONO_POR_DEJAR_SIN_MONSTRUOS} puntos, porque deja libres los ataques directos de
 * sus otros monstruos. Así, por ejemplo, acepta un empate de ATK (ambos destruidos, puntaje
 * 0 para el rival 2) si con eso el rival queda sin monstruos.
 *
 * <p>Responsable: Santiago.
 */
public class IARival3 extends IARival2 {

    static final int BONO_POR_DEJAR_SIN_MONSTRUOS = 500;

    @Override
    int puntaje(ResultadoCombate resultado, Duelista rival) {
        int puntaje = super.puntaje(resultado, rival);
        if (resultado.destruyeObjetivo() && rival.monstruosEnCampo() == 1) {
            puntaje += BONO_POR_DEJAR_SIN_MONSTRUOS;
        }
        return puntaje;
    }
}
