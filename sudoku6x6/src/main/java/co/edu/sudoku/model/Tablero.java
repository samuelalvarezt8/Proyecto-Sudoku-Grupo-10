package co.edu.sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static co.edu.sudoku.model.GeneradorSudoku.ALTO_BLOQUE;
import static co.edu.sudoku.model.GeneradorSudoku.ANCHO_BLOQUE;
import static co.edu.sudoku.model.GeneradorSudoku.TAM;

/**
 * Modelo del juego: contiene la <b>estructura de datos</b> principal, una matriz
 * {@code Celda[6][6]}, y toda la lógica de reglas (validación, ayuda y estado).
 * No depende de JavaFX.
 */
public class Tablero {

    /** Cantidad máxima de ayudas por partida. */
    public static final int MAX_AYUDAS = 3;
    /** Pistas iniciales por cada bloque de 2x3. */
    public static final int PISTAS_POR_BLOQUE = 2;

    /**
     * Sugerencia de ayuda: qué número probar en qué celda vacía.
     *
     * @param fila fila de la celda (0-5)
     * @param columna columna de la celda (0-5)
     * @param valor número sugerido (1-6)
     */
    public record Sugerencia(int fila, int columna, int valor) { }

    private final Celda[][] celdas = new Celda[TAM][TAM];
    private final GeneradorSudoku generador = new GeneradorSudoku();
    private final Random random = new Random();
    private int ayudasUsadas;

    /** Crea un tablero vacío. */
    public Tablero() {
        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                celdas[f][c] = new Celda();
            }
        }
    }

    /**
     * Inicia una partida nueva: genera una solución válida y deja visibles
     * {@value #PISTAS_POR_BLOQUE} números en cada bloque de 2x3 como pistas fijas.
     * El tablero inicial es siempre válido porque sale de una solución completa.
     */
    public void nuevoJuego() {
        int[][] solucion = generador.generarSolucion();
        for (Celda[] fila : celdas) {
            for (Celda celda : fila) {
                celda.limpiar();
            }
        }
        for (int bf = 0; bf < TAM; bf += ALTO_BLOQUE) {
            for (int bc = 0; bc < TAM; bc += ANCHO_BLOQUE) {
                List<int[]> posiciones = new ArrayList<>();
                for (int i = 0; i < ALTO_BLOQUE; i++) {
                    for (int j = 0; j < ANCHO_BLOQUE; j++) {
                        posiciones.add(new int[]{bf + i, bc + j});
                    }
                }
                Collections.shuffle(posiciones, random);
                for (int k = 0; k < PISTAS_POR_BLOQUE; k++) {
                    int[] p = posiciones.get(k);
                    celdas[p[0]][p[1]].fijar(solucion[p[0]][p[1]]);
                }
            }
        }
        ayudasUsadas = 0;
        recalcularErrores();
    }

    /**
     * Obtiene una celda.
     *
     * @param fila fila (0-5)
     * @param col columna (0-5)
     * @return la celda correspondiente
     */
    public Celda getCelda(int fila, int col) {
        return celdas[fila][col];
    }

    /**
     * Copia los valores actuales del tablero a una matriz de enteros.
     *
     * @return matriz 6x6 (0 = vacía)
     */
    public int[][] getValores() {
        int[][] m = new int[TAM][TAM];
        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                m[f][c] = celdas[f][c].getValor();
            }
        }
        return m;
    }

    /**
     * Revisa todo el tablero y marca como erróneas las celdas cuyo número se
     * repite en su fila, columna o bloque de 2x3 (validación en tiempo real).
     */
    public void recalcularErrores() {
        int[][] m = getValores();
        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                int v = m[f][c];
                celdas[f][c].setEnError(v != 0 && !GeneradorSudoku.puedeColocar(m, f, c, v));
            }
        }
    }

    /**
     * Describe en qué zonas se repite el número de una celda.
     *
     * @param fila fila (0-5)
     * @param col columna (0-5)
     * @return mensaje legible, o {@code null} si no hay conflicto
     */
    public String describirConflicto(int fila, int col) {
        int v = celdas[fila][col].getValor();
        if (v == 0) {
            return null;
        }
        List<String> zonas = new ArrayList<>();
        for (int i = 0; i < TAM; i++) {
            if (i != col && celdas[fila][i].getValor() == v) {
                zonas.add("la fila " + (fila + 1));
                break;
            }
        }
        for (int i = 0; i < TAM; i++) {
            if (i != fila && celdas[i][col].getValor() == v) {
                zonas.add("la columna " + (col + 1));
                break;
            }
        }
        int bf = (fila / ALTO_BLOQUE) * ALTO_BLOQUE;
        int bc = (col / ANCHO_BLOQUE) * ANCHO_BLOQUE;
        boolean repetidoEnBloque = false;
        for (int i = 0; i < ALTO_BLOQUE && !repetidoEnBloque; i++) {
            for (int j = 0; j < ANCHO_BLOQUE; j++) {
                int f = bf + i;
                int c = bc + j;
                if ((f != fila || c != col) && celdas[f][c].getValor() == v) {
                    repetidoEnBloque = true;
                    break;
                }
            }
        }
        if (repetidoEnBloque) {
            zonas.add("el bloque " + ((fila / ALTO_BLOQUE) * 2 + col / ANCHO_BLOQUE + 1));
        }
        if (zonas.isEmpty()) {
            return null;
        }
        return "El número " + v + " ya está en " + String.join(", ", zonas) + ".";
    }

    /**
     * Indica si existe alguna celda en conflicto.
     *
     * @return {@code true} si hay al menos un error
     */
    public boolean tieneErrores() {
        for (Celda[] fila : celdas) {
            for (Celda celda : fila) {
                if (celda.estaEnError()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Cuenta las celdas vacías.
     *
     * @return número de celdas sin valor
     */
    public int contarVacias() {
        int n = 0;
        for (Celda[] fila : celdas) {
            for (Celda celda : fila) {
                if (celda.estaVacia()) {
                    n++;
                }
            }
        }
        return n;
    }

    /**
     * Indica si el tablero está completo y sin errores.
     *
     * @return {@code true} si el jugador resolvió el Sudoku
     */
    public boolean estaResuelto() {
        return contarVacias() == 0 && !tieneErrores();
    }

    /**
     * Ayudas que aún puede usar el jugador.
     *
     * @return ayudas restantes (0 a {@value #MAX_AYUDAS})
     */
    public int ayudasRestantes() {
        return MAX_AYUDAS - ayudasUsadas;
    }

    /**
     * Calcula una sugerencia para una celda vacía <b>sin escribirla</b>.
     * Se elige la celda vacía con menos candidatos posibles y el número se toma de una
     * solución del tablero actual, por lo que siempre es coherente con lo que el
     * jugador ya escribió. Para que la ayuda nunca pueda terminar el tablero, se
     * limita a {@value #MAX_AYUDAS} usos y exige al menos 2 celdas vacías.
     *
     * @return la sugerencia, o vacío si no se cumplen las condiciones o no hay solución
     */
    public Optional<Sugerencia> calcularSugerencia() {
        if (ayudasRestantes() <= 0 || contarVacias() < 2 || tieneErrores()) {
            return Optional.empty();
        }
        int[][] valores = getValores();
        int[][] solucion = GeneradorSudoku.resolver(valores);
        if (solucion == null) {
            return Optional.empty();
        }
        int minimo = Integer.MAX_VALUE;
        List<int[]> candidatas = new ArrayList<>();
        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                if (valores[f][c] != 0) {
                    continue;
                }
                int opciones = 0;
                for (int v = 1; v <= TAM; v++) {
                    if (GeneradorSudoku.puedeColocar(valores, f, c, v)) {
                        opciones++;
                    }
                }
                if (opciones < minimo) {
                    minimo = opciones;
                    candidatas.clear();
                }
                if (opciones == minimo) {
                    candidatas.add(new int[]{f, c});
                }
            }
        }
        int[] elegida = candidatas.get(random.nextInt(candidatas.size()));
        ayudasUsadas++;
        return Optional.of(new Sugerencia(elegida[0], elegida[1], solucion[elegida[0]][elegida[1]]));
    }
}
