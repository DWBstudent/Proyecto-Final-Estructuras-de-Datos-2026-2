package co.edu.unbosque.modelo.juego;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.TipoCarta;

class CatalogoTest {

    private static Carta carta(int id, String contrasena) {
        return new Carta(id, "Carta " + id, TipoCarta.GUERRERO, 1000, 800, "", "", contrasena);
    }

    private static Catalogo deTres() {
        return new Catalogo(new Carta[] {carta(1, "11111111"), carta(50, "50505050"), carta(100, "99999999")});
    }

    // Caso normal

    @Test
    void buscaCadaCartaPorSuId() {
        Carta media = carta(50, "50505050");
        Catalogo catalogo = new Catalogo(new Carta[] {carta(1, "11111111"), media});

        assertSame(media, catalogo.buscarPorId(50));
        assertEquals(2, catalogo.tamanio());
    }

    @Test
    void traduceCadaContrasenaAlIdDeSuCarta() {
        Catalogo catalogo = deTres();

        assertEquals(1, catalogo.idPorContrasena("11111111"));
        assertEquals(50, catalogo.idPorContrasena("50505050"));
        assertEquals(100, catalogo.idPorContrasena("99999999"));
    }

    @Test
    void elArchivoRealTieneLas100CartasConSusContrasenas() {
        Catalogo catalogo = Catalogo.cargar();

        assertEquals(100, catalogo.tamanio());
        for (int id = 1; id <= 100; id++) {
            Carta carta = catalogo.buscarPorId(id);
            assertEquals(id, carta.getId());
            assertEquals(id, catalogo.idPorContrasena(carta.getContrasena()));
        }
    }

    // Caso límite

    @Test
    void funcionaConLosIdsDeLosExtremos() {
        Catalogo catalogo = deTres();

        assertEquals(1, catalogo.buscarPorId(1).getId());
        assertEquals(100, catalogo.buscarPorId(100).getId());
    }

    @Test
    void ignoraLosEspaciosAlrededorDeLaContrasena() {
        assertEquals(50, deTres().idPorContrasena("  50505050 "));
    }

    @Test
    void unCatalogoVacioNoTieneCartas() {
        Catalogo vacio = new Catalogo(new Carta[0]);

        assertEquals(0, vacio.tamanio());
        assertEquals(-1, vacio.idPorContrasena("11111111"));
        assertThrows(IllegalArgumentException.class, () -> vacio.buscarPorId(1));
    }

    // Caso de error

    @Test
    void unaContrasenaInvalidaDevuelveMenosUno() {
        Catalogo catalogo = deTres();

        assertEquals(-1, catalogo.idPorContrasena("12345678"));   // no existe
        assertEquals(-1, catalogo.idPorContrasena("1111111"));    // le falta un dígito
        assertEquals(-1, catalogo.idPorContrasena("abcdefgh"));
        assertEquals(-1, catalogo.idPorContrasena("   "));
        assertEquals(-1, catalogo.idPorContrasena(null));
    }

    @Test
    void rechazaIdsQueNoEstanEnElCatalogo() {
        Catalogo catalogo = deTres();

        assertThrows(IllegalArgumentException.class, () -> catalogo.buscarPorId(2));
        assertThrows(IllegalArgumentException.class, () -> catalogo.buscarPorId(0));
        assertThrows(IllegalArgumentException.class, () -> catalogo.buscarPorId(101));
    }

    @Test
    void rechazaCartasNulasYRepetidas() {
        assertThrows(IllegalArgumentException.class, () -> new Catalogo(null));
        assertThrows(IllegalArgumentException.class, () -> new Catalogo(new Carta[] {carta(1, "11111111"), null}));
        assertThrows(IllegalArgumentException.class,
                () -> new Catalogo(new Carta[] {carta(7, "11111111"), carta(7, "22222222")}));
        assertThrows(IllegalArgumentException.class,
                () -> new Catalogo(new Carta[] {carta(7, "11111111"), carta(8, "11111111")}));
    }
}