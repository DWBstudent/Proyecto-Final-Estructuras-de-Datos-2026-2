package co.edu.unbosque.modelo.juego;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.TipoCarta;

class CombateTest {

    private static Carta carta(int ataque, int defensa) {
        return new Carta(1, "Prueba", TipoCarta.GUERRERO, ataque, defensa, "", "", "00000001");
    }

    private static MonstruoEnCampo enAtaque(int ataque) {
        return new MonstruoEnCampo(carta(ataque, 0), Posicion.ATAQUE);
    }

    private static MonstruoEnCampo enDefensa(int defensa) {
        return new MonstruoEnCampo(carta(0, defensa), Posicion.DEFENSA);
    }

    // Objetivo en ataque

    @Test
    void ataqueMayorQueAtaqueDestruyeAlObjetivoYSuDuenioPierdeLaDiferencia() {
        ResultadoCombate r = Combate.resolver(carta(2000, 0), enAtaque(1500));

        assertTrue(r.destruyeObjetivo());
        assertFalse(r.destruyeAtacante());
        assertEquals(500, r.getDanioAlDefensor());
        assertEquals(0, r.getDanioAlAtacante());
        assertFalse(r.esAtaqueDirecto());
    }

    @Test
    void ataqueMenorQueAtaqueDestruyeAlAtacanteYEstePierdeLaDiferencia() {
        ResultadoCombate r = Combate.resolver(carta(1200, 0), enAtaque(1900));

        assertFalse(r.destruyeObjetivo());
        assertTrue(r.destruyeAtacante());
        assertEquals(700, r.getDanioAlAtacante());
        assertEquals(0, r.getDanioAlDefensor());
    }

    @Test
    void ataquesIgualesDestruyenAAmbosSinDanio() {
        ResultadoCombate r = Combate.resolver(carta(1600, 0), enAtaque(1600));

        assertTrue(r.destruyeObjetivo());
        assertTrue(r.destruyeAtacante());
        assertEquals(0, r.getDanioAlAtacante());
        assertEquals(0, r.getDanioAlDefensor());
    }

    // Objetivo en defensa

    @Test
    void ataqueMayorQueDefensaDestruyeAlObjetivoSinDanio() {
        ResultadoCombate r = Combate.resolver(carta(1800, 0), enDefensa(1000));

        assertTrue(r.destruyeObjetivo());
        assertFalse(r.destruyeAtacante());
        assertEquals(0, r.getDanioAlAtacante());
        assertEquals(0, r.getDanioAlDefensor());
    }

    @Test
    void ataqueMenorQueDefensaNoDestruyeYElAtacantePierdeLaDiferencia() {
        ResultadoCombate r = Combate.resolver(carta(1000, 0), enDefensa(2000));

        assertFalse(r.destruyeObjetivo());
        assertFalse(r.destruyeAtacante());
        assertEquals(1000, r.getDanioAlAtacante());
        assertEquals(0, r.getDanioAlDefensor());
    }

    @Test
    void ataqueIgualADefensaNoHaceNada() {
        ResultadoCombate r = Combate.resolver(carta(1500, 0), enDefensa(1500));

        assertFalse(r.destruyeObjetivo());
        assertFalse(r.destruyeAtacante());
        assertEquals(0, r.getDanioAlAtacante());
        assertEquals(0, r.getDanioAlDefensor());
    }

    @Test
    void enDefensaSeUsaLaDefensaYNoElAtaqueDelObjetivo() {
        // ATK del objetivo 3000, DEF 500: en defensa cuenta solo la DEF.
        MonstruoEnCampo objetivo = new MonstruoEnCampo(carta(3000, 500), Posicion.DEFENSA);

        ResultadoCombate r = Combate.resolver(carta(1000, 0), objetivo);

        assertTrue(r.destruyeObjetivo());
        assertEquals(0, r.getDanioAlAtacante());
    }

    // Ataque directo

    @Test
    void ataqueDirectoQuitaElAtaqueCompleto() {
        ResultadoCombate r = Combate.ataqueDirecto(carta(2300, 0));

        assertTrue(r.esAtaqueDirecto());
        assertEquals(2300, r.getDanioAlDefensor());
        assertEquals(0, r.getDanioAlAtacante());
        assertFalse(r.destruyeObjetivo());
        assertFalse(r.destruyeAtacante());
    }

    // Límite

    @Test
    void dosMonstruosDeAtaqueCeroSeDestruyenSinDanio() {
        ResultadoCombate r = Combate.resolver(carta(0, 0), enAtaque(0));

        assertTrue(r.destruyeObjetivo());
        assertTrue(r.destruyeAtacante());
        assertEquals(0, r.getDanioAlDefensor());
    }

    // Error

    @Test
    void sinAtacanteOSinObjetivoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> Combate.resolver(null, enAtaque(100)));
        assertThrows(IllegalArgumentException.class, () -> Combate.resolver(carta(100, 0), null));
        assertThrows(IllegalArgumentException.class, () -> Combate.ataqueDirecto(null));
    }
}
