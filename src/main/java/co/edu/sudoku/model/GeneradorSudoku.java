package co.edu.sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Genera y resuelve tableros de Sudoku 6x6 mediante <b>backtracking</b>
 * (búsqueda con retroceso). Los bloques son de 2 filas por 3 columnas.
 */
public final class GeneradorSudoku {

    /** Tamaño del tablero (6x6). */
    public static final int TAM = 6;
    /** Alto de cada bloque. */
    public static final int ALTO_BLOQUE = 2;
    /** Ancho de cada bloque. */
    public static final int ANCHO_BLOQUE = 3;

    private final Random random;

    /** Crea un generador con semilla aleatoria. */
    public GeneradorSudoku() {
        this(new Random());
    }

    /**
     * Crea un generador con una fuente de aleatoriedad dada (útil para pruebas).
     *
     * @param random fuente de números aleatorios
     */
    public GeneradorSudoku(Random random) {
        this.random = random;
    }

    /**
     * Genera una solución completa y válida de Sudoku 6x6.
     *
     * @return matriz 6x6 con todos los números del 1 al 6 bien ubicados
     */
    public int[][] generarSolucion() {
        int[][] matriz = new int[TAM][TAM];
        completar(matriz, 0, random);
        return matriz;
    }

    /**
     * Intenta resolver un tablero parcial sin modificar el original.
     *
     * @param base matriz 6x6 con 0 en las celdas vacías
     * @return una solución completa, o {@code null} si el tablero no tiene solución
     */
    public static int[][] resolver(int[][] base) {
        int[][] copia = copiar(base);
        return completar(copia, 0, null) ? copia : null;
    }

    /**
     * Verifica si un número puede ubicarse en una celda sin repetirse en su
     * fila, columna o bloque de 2x3 (ignorando la propia celda).
     *
     * @param m matriz de valores
     * @param fila fila de la celda (0-5)
     * @param col columna de la celda (0-5)
     * @param valor número a probar (1-6)
     * @return {@code true} si no hay repetidos
     */
    public static boolean puedeColocar(int[][] m, int fila, int col, int valor) {
        for (int i = 0; i < TAM; i++) {
            if (i != col && m[fila][i] == valor) {
                return false;
            }
            if (i != fila && m[i][col] == valor) {
                return false;
            }
        }
        int bf = (fila / ALTO_BLOQUE) * ALTO_BLOQUE;
        int bc = (col / ANCHO_BLOQUE) * ANCHO_BLOQUE;
        for (int i = 0; i < ALTO_BLOQUE; i++) {
            for (int j = 0; j < ANCHO_BLOQUE; j++) {
                int f = bf + i;
                int c = bc + j;
                if ((f != fila || c != col) && m[f][c] == valor) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Copia profunda de una matriz 6x6.
     *
     * @param origen matriz a copiar
     * @return nueva matriz con los mismos valores
     */
    public static int[][] copiar(int[][] origen) {
        int[][] copia = new int[TAM][];
        for (int i = 0; i < TAM; i++) {
            copia[i] = origen[i].clone();
        }
        return copia;
    }

    private static boolean completar(int[][] m, int pos, Random rnd) {
        if (pos == TAM * TAM) {
            return true;
        }
        int fila = pos / TAM;
        int col = pos % TAM;
        if (m[fila][col] != 0) {
            return completar(m, pos + 1, rnd);
        }
        List<Integer> candidatos = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        if (rnd != null) {
            Collections.shuffle(candidatos, rnd);
        }
        for (int v : candidatos) {
            if (puedeColocar(m, fila, col, v)) {
                m[fila][col] = v;
                if (completar(m, pos + 1, rnd)) {
                    return true;
                }
                m[fila][col] = 0;
            }
        }
        return false;
    }
}
