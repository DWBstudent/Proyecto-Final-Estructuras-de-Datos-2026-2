package co.edu.unbosque.modelo.juego;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.Direccion;
import co.edu.unbosque.modelo.entidades.Jugador;
import co.edu.unbosque.modelo.entidades.TipoCarta;

class InventarioTest {

    /**
     * Catálogo de prueba: el de Daniel todavía no está implementado (H2), así que este
     * devuelve una carta inventada con el id pedido.
     */
    private static Catalogo catalogoDePrueba() {
        return new Catalogo() {
            @Override
            public Carta buscarPorId(int id) {
                return new Carta(id, "Carta " + id, TipoCarta.GUERRERO, 1000, 1000, "", "",
                        String.format("%08d", id));
            }
        };
    }

    private static Inventario nuevoInventario() {
        return new Inventario(catalogoDePrueba());
    }

    /** Lleva el cursor desde (0, 0) hasta (fila, columna) moviéndose solo abajo y a la derecha. */
    private static void irA(Inventario inventario, int fila, int columna) {
        for (int i = 0; i < fila; i++) {
            assertTrue(inventario.mover(Direccion.ABAJO));
        }
        for (int i = 0; i < columna; i++) {
            assertTrue(inventario.mover(Direccion.DERECHA));
        }
        assertEquals(fila, inventario.getFila());
        assertEquals(columna, inventario.getColumna());
    }

    private static Jugador jugadorConTodasDescubiertasMenos(int... idsSinDescubrir) {
        Jugador jugador = new Jugador("Prueba");
        for (int id = 1; id <= Carta.ID_MAXIMO; id++) {
            boolean dejarSinDescubrir = false;
            for (int sinDescubrir : idsSinDescubrir) {
                if (id == sinDescubrir) {
                    dejarSinDescubrir = true;
                }
            }
            if (!dejarSinDescubrir) {
                jugador.marcarDescubierta(id);
            }
        }
        return jugador;
    }

    // Caso normal

    @Test
    void elCursorEmpiezaEnLaEsquinaSuperiorIzquierda() {
        Inventario inventario = nuevoInventario();

        assertEquals(0, inventario.getFila());
        assertEquals(0, inventario.getColumna());
        assertEquals(1, inventario.getCartaEnCursor().getId());
    }

    @Test
    void desdeElCentroSePuedeMoverEnLas8Direcciones() {
        for (Direccion direccion : Direccion.values()) {
            Inventario inventario = nuevoInventario();
            irA(inventario, 4, 4);

            assertTrue(inventario.mover(direccion), "No se movió hacia " + direccion);
            assertEquals(4 + direccion.getDeltaFila(), inventario.getFila(), "Fila mal hacia " + direccion);
            assertEquals(4 + direccion.getDeltaColumna(), inventario.getColumna(), "Columna mal hacia " + direccion);
        }
    }

    @Test
    void laCartaEnElCursorEsLaDeIdFilaPorDiezMasColumnaMasUno() {
        Inventario inventario = nuevoInventario();
        irA(inventario, 3, 7);
        assertEquals(38, inventario.getCartaEnCursor().getId());

        inventario.mover(Direccion.ABAJO_IZQUIERDA);
        assertEquals(47, inventario.getCartaEnCursor().getId());
    }

    @Test
    void vaALaCartaSinDescubrirMasCercana() {
        // Desde (0, 0): la 23, en (2, 2), está a 2 pasos en diagonal; la 4, en (0, 3), a 3.
        Inventario inventario = nuevoInventario();
        Jugador jugador = jugadorConTodasDescubiertasMenos(4, 23);

        assertTrue(inventario.irALaMasCercanaSinDescubrir(jugador));
        assertEquals(2, inventario.getFila());
        assertEquals(2, inventario.getColumna());
        assertEquals(23, inventario.getCartaEnCursor().getId());
    }

    @Test
    void llegaHastaLaEsquinaOpuesta() {
        Inventario inventario = nuevoInventario();
        Jugador jugador = jugadorConTodasDescubiertasMenos(100);

        assertTrue(inventario.irALaMasCercanaSinDescubrir(jugador));
        assertEquals(9, inventario.getFila());
        assertEquals(9, inventario.getColumna());
    }

    // Caso límite

    @Test
    void lasEsquinasTienen3VecinasLosBordes5YElInterior8() {
        Inventario inventario = nuevoInventario();

        assertEquals(3, inventario.cantidadVecinas(0, 0));
        assertEquals(3, inventario.cantidadVecinas(0, 9));
        assertEquals(3, inventario.cantidadVecinas(9, 0));
        assertEquals(3, inventario.cantidadVecinas(9, 9));
        assertEquals(5, inventario.cantidadVecinas(0, 5));
        assertEquals(5, inventario.cantidadVecinas(9, 5));
        assertEquals(5, inventario.cantidadVecinas(5, 0));
        assertEquals(5, inventario.cantidadVecinas(5, 9));
        assertEquals(8, inventario.cantidadVecinas(4, 4));
        assertEquals(8, inventario.cantidadVecinas(1, 1));
    }

    @Test
    void laCuadriculaTiene342Aristas() {
        assertEquals(342, nuevoInventario().cantidadAristas());
    }

    @Test
    void recorreUnaFilaCompletaYSeDetieneEnElBorde() {
        Inventario inventario = nuevoInventario();
        for (int i = 0; i < 9; i++) {
            assertTrue(inventario.mover(Direccion.DERECHA));
        }

        assertFalse(inventario.mover(Direccion.DERECHA));
        assertEquals(0, inventario.getFila());
        assertEquals(9, inventario.getColumna());
        assertEquals(10, inventario.getCartaEnCursor().getId());
    }

    @Test
    void siLaCartaDelCursorNoEstaDescubiertaSeQuedaAhi() {
        Inventario inventario = nuevoInventario();
        irA(inventario, 5, 5);
        Jugador jugador = jugadorConTodasDescubiertasMenos(56, 1);

        assertTrue(inventario.irALaMasCercanaSinDescubrir(jugador));
        assertEquals(5, inventario.getFila());
        assertEquals(5, inventario.getColumna());
    }

    @Test
    void conTodasDescubiertasNoMueveElCursor() {
        Inventario inventario = nuevoInventario();
        irA(inventario, 2, 3);

        assertFalse(inventario.irALaMasCercanaSinDescubrir(jugadorConTodasDescubiertasMenos()));
        assertEquals(2, inventario.getFila());
        assertEquals(3, inventario.getColumna());
    }

    // Caso de error

    @Test
    void moverseFueraDeLaCuadriculaNoCambiaElCursor() {
        Inventario inventario = nuevoInventario();

        assertFalse(inventario.mover(Direccion.ARRIBA));
        assertFalse(inventario.mover(Direccion.IZQUIERDA));
        assertFalse(inventario.mover(Direccion.ARRIBA_IZQUIERDA));
        assertFalse(inventario.mover(Direccion.ARRIBA_DERECHA));
        assertFalse(inventario.mover(Direccion.ABAJO_IZQUIERDA));
        assertEquals(0, inventario.getFila());
        assertEquals(0, inventario.getColumna());
    }

    @Test
    void noSaltaALaFilaSiguienteNiALaAnterior() {
        // Si el movimiento se calculara como "vértice + 1", desde (0, 9) iría a (1, 0).
        Inventario inventario = nuevoInventario();
        irA(inventario, 0, 9);
        assertFalse(inventario.mover(Direccion.DERECHA));
        assertFalse(inventario.mover(Direccion.ABAJO_DERECHA));
        assertEquals(9, inventario.getColumna());

        Inventario otro = nuevoInventario();
        irA(otro, 9, 0);
        assertFalse(otro.mover(Direccion.IZQUIERDA));
        assertFalse(otro.mover(Direccion.ARRIBA_IZQUIERDA));
        assertFalse(otro.mover(Direccion.ABAJO));
        assertEquals(9, otro.getFila());
        assertEquals(0, otro.getColumna());
    }

    @Test
    void parametrosNullLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Inventario(null));
        Inventario inventario = nuevoInventario();
        assertThrows(IllegalArgumentException.class, () -> inventario.mover(null));
        assertThrows(IllegalArgumentException.class, () -> inventario.irALaMasCercanaSinDescubrir(null));
    }

    @Test
    void casillaFueraDeLaCuadriculaLanzaExcepcion() {
        Inventario inventario = nuevoInventario();
        assertThrows(IllegalArgumentException.class, () -> inventario.cantidadVecinas(10, 0));
        assertThrows(IllegalArgumentException.class, () -> inventario.cantidadVecinas(0, -1));
    }
}
