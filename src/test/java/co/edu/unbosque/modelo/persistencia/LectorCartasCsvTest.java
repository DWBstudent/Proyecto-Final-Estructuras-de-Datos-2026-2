package co.edu.unbosque.modelo.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import co.edu.unbosque.modelo.entidades.Carta;
import co.edu.unbosque.modelo.entidades.TipoCarta;

class LectorCartasCsvTest {

    private static final String ENCABEZADO = LectorCartasCsv.ENCABEZADO + "\n";

    /** Lee un CSV escrito directamente en la prueba, sin archivos. */
    private static Carta[] leerTexto(String texto) {
        return LectorCartasCsv.leer(new BufferedReader(new StringReader(texto)));
    }

    /** Línea válida con el id y la contraseña indicados. */
    private static String linea(int id, String contrasena) {
        return id + ";Carta " + id + ";GUERRERO;1000;900;" + contrasena + ";;Descripción, con coma\n";
    }

    /** Comprueba que el texto lanza CsvInvalidoException y devuelve la línea del error. */
    private static int lineaDelError(String texto) {
        return assertThrows(CsvInvalidoException.class, () -> leerTexto(texto)).getNumeroLinea();
    }

    // Caso normal

    @Test
    void leeTodosLosCamposDeUnaCarta() {
        Carta[] cartas = leerTexto(ENCABEZADO
                + "1;Dragón de Ceniza;DRAGON;2100;1500;48201937;img/001.png;Nace del humo, de los volcanes\n");

        assertEquals(1, cartas.length);
        Carta c = cartas[0];
        assertEquals(1, c.getId());
        assertEquals("Dragón de Ceniza", c.getNombre());
        assertEquals(TipoCarta.DRAGON, c.getTipo());
        assertEquals(2100, c.getAtaque());
        assertEquals(1500, c.getDefensa());
        assertEquals("48201937", c.getContrasena());
        assertEquals("img/001.png", c.getRutaImagen());
        assertEquals("Nace del humo, de los volcanes", c.getDescripcion());
    }

    @Test
    void respetaElOrdenDelArchivo() {
        Carta[] cartas = leerTexto(ENCABEZADO + linea(3, "33333333") + linea(1, "11111111") + linea(2, "22222222"));

        assertEquals(3, cartas[0].getId());
        assertEquals(1, cartas[1].getId());
        assertEquals(2, cartas[2].getId());
    }

    @Test
    void leeElArchivoRealDeResources() {
        Carta[] cartas = LectorCartasCsv.leerRecurso("/cartas.csv");
        assertTrue(cartas.length > 0, "cartas.csv debería tener cartas");
    }

    // Caso límite

    @Test
    void soloEncabezadoDevuelveArregloVacio() {
        assertEquals(0, leerTexto(ENCABEZADO).length);
    }

    @Test
    void ignoraLineasEnBlancoYLaMarcaDeWindows() {
        Carta[] cartas = leerTexto("\uFEFF" + ENCABEZADO + "\n" + linea(1, "11111111") + "   \n" + linea(2, "22222222") + "\n");
        assertEquals(2, cartas.length);
    }

    @Test
    void aceptaImagenYDescripcionVacias() {
        Carta[] cartas = leerTexto(ENCABEZADO + "1;Sin texto;AGUA;500;500;12345678;;\n");
        assertEquals("", cartas[0].getRutaImagen());
        assertEquals("", cartas[0].getDescripcion());
    }

    @Test
    void aceptaExactamenteCienCartas() {
        StringBuilder csv = new StringBuilder(ENCABEZADO);
        for (int id = 1; id <= 100; id++) {
            csv.append(linea(id, String.format("%08d", id)));
        }
        assertEquals(100, leerTexto(csv.toString()).length);
    }

    // Caso de error

    @Test
    void rechazaLaCartaNumeroCientoUno() {
        StringBuilder csv = new StringBuilder(ENCABEZADO);
        for (int id = 1; id <= 100; id++) {
            csv.append(linea(id, String.format("%08d", id)));
        }
        csv.append(linea(50, "99999999"));
        assertEquals(102, lineaDelError(csv.toString()));
    }

    @Test
    void rechazaArchivoVacioYEncabezadoInvalido() {
        assertEquals(1, lineaDelError(""));
        assertEquals(1, lineaDelError("codigo;titulo\n" + linea(1, "11111111")));
    }

    @Test
    void rechazaLineaConCamposDeMasODeMenos() {
        assertEquals(2, lineaDelError(ENCABEZADO + "1;Dragón;DRAGON\n"));
        assertEquals(3, lineaDelError(ENCABEZADO + linea(1, "11111111") + "2;A;AGUA;1;1;22222222;;texto;sobra\n"));
    }

    @Test
    void rechazaNumerosYTiposInvalidos() {
        assertEquals(2, lineaDelError(ENCABEZADO + "x;Dragón;DRAGON;100;100;11111111;;\n"));
        assertEquals(2, lineaDelError(ENCABEZADO + "1;Dragón;DRAGON;abc;100;11111111;;\n"));
        assertEquals(2, lineaDelError(ENCABEZADO + "1;Dragón;DRAGON;100;;11111111;;\n"));
        assertEquals(2, lineaDelError(ENCABEZADO + "1;Dragón;PEZ;100;100;11111111;;\n"));
    }

    @Test
    void rechazaIdRepetido() {
        assertEquals(3, lineaDelError(ENCABEZADO + linea(5, "11111111") + linea(5, "22222222")));
    }

    @Test
    void rechazaLoQueLaCartaNoAcepta() {
        assertEquals(2, lineaDelError(ENCABEZADO + linea(0, "11111111")));       // id fuera de rango
        assertEquals(2, lineaDelError(ENCABEZADO + linea(1, "1234")));           // contraseña corta
        assertEquals(2, lineaDelError(ENCABEZADO + "1; ;AGUA;1;1;11111111;;\n")); // nombre vacío
    }

    @Test
    void rechazaRecursoInexistenteYEntradaNula() {
        assertThrows(IllegalArgumentException.class, () -> LectorCartasCsv.leerRecurso("/no-existe.csv"));
        assertThrows(IllegalArgumentException.class, () -> LectorCartasCsv.leer(null));
    }
}