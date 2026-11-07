package co.edu.unbosque.modelo.entidades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CartaTest {

    private static Carta carta(int id, int ataque, int defensa, String contrasena) {
        return new Carta(id, "Prueba", TipoCarta.DRAGON, ataque, defensa, "Descripción", "", contrasena);
    }

    // Caso normal

    @Test
    void guardaTodosSusDatos() {
        Carta c = new Carta(1, "  Dragón de Ceniza ", TipoCarta.DRAGON, 2100, 1500,
                "Un dragón.", "img/001.png", "48201937");

        assertEquals(1, c.getId());
        assertEquals("Dragón de Ceniza", c.getNombre());
        assertEquals(TipoCarta.DRAGON, c.getTipo());
        assertEquals(2100, c.getAtaque());
        assertEquals(1500, c.getDefensa());
        assertEquals("48201937", c.getContrasena());
    }

    @Test
    void dosCartasConElMismoIdSonIguales() {
        Carta a = carta(5, 1000, 1000, "11111111");
        Carta b = carta(5, 2000, 0, "22222222");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, carta(6, 1000, 1000, "11111111"));
    }

    // Caso límite

    @Test
    void aceptaLosIdsDeLosExtremos() {
        assertEquals(1, carta(1, 0, 0, "00000000").getId());
        assertEquals(100, carta(100, 0, 0, "99999999").getId());
    }

    @Test
    void aceptaAtaqueYDefensaEnCero() {
        Carta c = carta(1, 0, 0, "12345678");
        assertEquals(0, c.getAtaque());
        assertEquals(0, c.getDefensa());
    }

    @Test
    void descripcionEImagenNulasQuedanVacias() {
        Carta c = new Carta(1, "Prueba", TipoCarta.AGUA, 100, 100, null, null, "12345678");
        assertEquals("", c.getDescripcion());
        assertEquals("", c.getRutaImagen());
    }

    // Caso de error

    @Test
    void rechazaIdsFueraDeRango() {
        assertThrows(IllegalArgumentException.class, () -> carta(0, 100, 100, "12345678"));
        assertThrows(IllegalArgumentException.class, () -> carta(101, 100, 100, "12345678"));
    }

    @Test
    void rechazaAtaqueODefensaNegativos() {
        assertThrows(IllegalArgumentException.class, () -> carta(1, -1, 100, "12345678"));
        assertThrows(IllegalArgumentException.class, () -> carta(1, 100, -1, "12345678"));
    }

    @Test
    void rechazaNombreVacioYTipoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Carta(1, "   ", TipoCarta.AGUA, 1, 1, "", "", "12345678"));
        assertThrows(IllegalArgumentException.class,
                () -> new Carta(1, "Prueba", null, 1, 1, "", "", "12345678"));
    }

    @Test
    void validaElFormatoDeLaContrasena() {
        assertTrue(Carta.esContrasenaValida("00000000"));
        assertFalse(Carta.esContrasenaValida("1234567"));
        assertFalse(Carta.esContrasenaValida("123456789"));
        assertFalse(Carta.esContrasenaValida("1234abcd"));
        assertFalse(Carta.esContrasenaValida(null));
        assertThrows(IllegalArgumentException.class, () -> carta(1, 100, 100, "1234abcd"));
    }
}
