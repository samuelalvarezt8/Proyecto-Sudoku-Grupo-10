package co.edu.sudoku.model;

/**
 * Representa una celda del tablero de Sudoku.
 * <p>
 * Guarda su valor (0 significa vacía), si es una pista fija del tablero inicial
 * y si actualmente incumple alguna regla del Sudoku.
 * </p>
 */
public class Celda {

    private int valor;
    private boolean fija;
    private boolean enError;

    /**
     * Devuelve el número de la celda.
     *
     * @return un valor entre 1 y 6, o 0 si la celda está vacía
     */
    public int getValor() {
        return valor;
    }

    /**
     * Asigna un número a la celda.
     *
     * @param valor número entre 0 (vacía) y 6
     * @throws IllegalArgumentException si el valor está fuera del rango 0-6
     */
    public void setValor(int valor) {
        if (valor < 0 || valor > GeneradorSudoku.TAM) {
            throw new IllegalArgumentException("Valor fuera de rango: " + valor);
        }
        this.valor = valor;
    }

    /**
     * Indica si la celda es una pista inicial (no editable).
     *
     * @return {@code true} si es una pista fija
     */
    public boolean esFija() {
        return fija;
    }

    /**
     * Marca la celda como pista inicial con el valor dado.
     *
     * @param valor número de la pista (1-6)
     */
    public void fijar(int valor) {
        setValor(valor);
        this.fija = true;
    }

    /**
     * Indica si la celda viola alguna regla del Sudoku.
     *
     * @return {@code true} si hay un conflicto en fila, columna o bloque
     */
    public boolean estaEnError() {
        return enError;
    }

    /**
     * Establece si la celda está en conflicto.
     *
     * @param enError {@code true} si incumple las reglas
     */
    public void setEnError(boolean enError) {
        this.enError = enError;
    }

    /**
     * Indica si la celda no tiene número.
     *
     * @return {@code true} si está vacía
     */
    public boolean estaVacia() {
        return valor == 0;
    }

    /**
     * Restablece la celda a su estado inicial: vacía, no fija y sin error.
     */
    public void limpiar() {
        valor = 0;
        fija = false;
        enError = false;
    }
}
