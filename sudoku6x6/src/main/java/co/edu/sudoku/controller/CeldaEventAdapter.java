package co.edu.sudoku.controller;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * <b>Clase adaptadora</b> de eventos para una celda del tablero.
 * <p>
 * Traduce los eventos crudos de mouse y teclado de JavaFX a acciones del juego
 * ({@link #onClick}, {@link #onNumero}, {@link #onBorrar}, {@link #onMover},
 * {@link #onNumeroInvalido}). Todos los métodos están vacíos, de modo que el
 * controlador solo sobrescribe los que necesita (mismo patrón que
 * {@code MouseAdapter}/{@code KeyAdapter} de Swing).
 * </p>
 */
public abstract class CeldaEventAdapter {

    private final int fila;
    private final int columna;

    /**
     * Crea un adaptador asociado a una celda.
     *
     * @param fila fila de la celda (0-5)
     * @param columna columna de la celda (0-5)
     */
    protected CeldaEventAdapter(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    /**
     * Se invoca al hacer clic izquierdo sobre la celda.
     *
     * @param fila fila de la celda
     * @param col columna de la celda
     */
    public void onClick(int fila, int col) { }

    /**
     * Se invoca al pulsar una tecla del 1 al 6.
     *
     * @param fila fila de la celda
     * @param col columna de la celda
     * @param numero número digitado
     */
    public void onNumero(int fila, int col, int numero) { }

    /**
     * Se invoca al pulsar Suprimir, Retroceso o 0.
     *
     * @param fila fila de la celda
     * @param col columna de la celda
     */
    public void onBorrar(int fila, int col) { }

    /**
     * Se invoca al pulsar una flecha de dirección.
     *
     * @param fila fila de la celda
     * @param col columna de la celda
     * @param deltaFila desplazamiento vertical (-1, 0, 1)
     * @param deltaCol desplazamiento horizontal (-1, 0, 1)
     */
    public void onMover(int fila, int col, int deltaFila, int deltaCol) { }

    /**
     * Se invoca al pulsar un dígito fuera del rango permitido (7, 8 o 9).
     *
     * @param fila fila de la celda
     * @param col columna de la celda
     */
    public void onNumeroInvalido(int fila, int col) { }

    /**
     * Manejador de eventos de mouse listo para asignar a la celda.
     *
     * @return manejador que reenvía el clic izquierdo a {@link #onClick}
     */
    public final EventHandler<MouseEvent> getManejadorMouse() {
        return evento -> {
            if (evento.getButton() == MouseButton.PRIMARY) {
                onClick(fila, columna);
            }
        };
    }

    /**
     * Manejador de eventos de teclado listo para asignar a la celda.
     *
     * @return manejador que clasifica la tecla y llama al método correspondiente
     */
    public final EventHandler<KeyEvent> getManejadorTeclado() {
        return evento -> {
            KeyCode tecla = evento.getCode();
            int numero = numeroDe(tecla);
            if (numero >= 1) {
                onNumero(fila, columna, numero);
            } else if (tecla == KeyCode.DIGIT0 || tecla == KeyCode.NUMPAD0
                    || tecla == KeyCode.BACK_SPACE || tecla == KeyCode.DELETE) {
                onBorrar(fila, columna);
            } else if (tecla == KeyCode.DIGIT7 || tecla == KeyCode.DIGIT8 || tecla == KeyCode.DIGIT9
                    || tecla == KeyCode.NUMPAD7 || tecla == KeyCode.NUMPAD8 || tecla == KeyCode.NUMPAD9) {
                onNumeroInvalido(fila, columna);
            } else if (tecla == KeyCode.UP) {
                onMover(fila, columna, -1, 0);
            } else if (tecla == KeyCode.DOWN) {
                onMover(fila, columna, 1, 0);
            } else if (tecla == KeyCode.LEFT) {
                onMover(fila, columna, 0, -1);
            } else if (tecla == KeyCode.RIGHT) {
                onMover(fila, columna, 0, 1);
            } else {
                return;
            }
            evento.consume();
        };
    }

    private static int numeroDe(KeyCode tecla) {
        switch (tecla) {
            case DIGIT1: case NUMPAD1: return 1;
            case DIGIT2: case NUMPAD2: return 2;
            case DIGIT3: case NUMPAD3: return 3;
            case DIGIT4: case NUMPAD4: return 4;
            case DIGIT5: case NUMPAD5: return 5;
            case DIGIT6: case NUMPAD6: return 6;
            default: return -1;
        }
    }
}
