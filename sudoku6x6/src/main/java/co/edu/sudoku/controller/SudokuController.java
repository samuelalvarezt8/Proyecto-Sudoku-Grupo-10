package co.edu.sudoku.controller;

import co.edu.sudoku.model.Celda;
import co.edu.sudoku.model.GeneradorSudoku;
import co.edu.sudoku.model.Tablero;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Optional;

/**
 * Controlador de la vista principal (patrón MVC).
 * <p>
 * Conecta la vista FXML con el modelo {@link Tablero}: construye la cuadrícula de
 * 6x6, atiende los eventos de mouse y teclado a través de {@link CeldaEventAdapter},
 * valida en tiempo real, gestiona la ayuda limitada y actualiza los estilos CSS de
 * cada celda.
 * </p>
 */
public class SudokuController {

    private static final int TAM = GeneradorSudoku.TAM;
    private static final int LADO_CELDA = 70;

    @FXML private GridPane gridTablero;
    @FXML private Label lblMensaje;
    @FXML private Label lblAyudas;
    @FXML private Button btnAyuda;

    private final Tablero tablero = new Tablero();
    private final Label[][] etiquetas = new Label[TAM][TAM];

    private int filaSeleccionada = -1;
    private int colSeleccionada = -1;
    private boolean juegoActivo = false;
    private Tablero.Sugerencia sugerencia;

    /**
     * Método invocado automáticamente por JavaFX tras cargar el FXML.
     * Construye la cuadrícula y muestra el estado inicial.
     */
    @FXML
    public void initialize() {
        construirCuadricula();
        actualizarVista();
        actualizarAyudas();
        mostrarMensaje("Pulsa «Nuevo juego» para comenzar.", "info");
    }

    // ------------------------------------------------------------------
    // Construcción de la cuadrícula
    // ------------------------------------------------------------------

    private void construirCuadricula() {
        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                Label etiqueta = new Label();
                etiqueta.setPrefSize(LADO_CELDA, LADO_CELDA);
                etiqueta.setMinSize(LADO_CELDA, LADO_CELDA);
                etiqueta.setAlignment(Pos.CENTER);
                etiqueta.setFocusTraversable(true);
                etiqueta.getStyleClass().add("celda");
                // Líneas gruesas entre bloques de 2x3 (el borde exterior lo dibuja el GridPane)
                int derecha = (c == TAM - 1) ? 0 : (c == 2 ? 3 : 1);
                int abajo = (f == TAM - 1) ? 0 : (f % 2 == 1 ? 3 : 1);
                etiqueta.setStyle("-fx-border-width: 0 " + derecha + " " + abajo + " 0;");

                CeldaEventAdapter adaptador = new CeldaEventAdapter(f, c) {
                    @Override
                    public void onClick(int fila, int col) {
                        seleccionar(fila, col);
                    }

                    @Override
                    public void onNumero(int fila, int col, int numero) {
                        colocarNumero(fila, col, numero);
                    }

                    @Override
                    public void onBorrar(int fila, int col) {
                        borrarNumero(fila, col);
                    }

                    @Override
                    public void onMover(int fila, int col, int df, int dc) {
                        seleccionar(Math.max(0, Math.min(TAM - 1, fila + df)),
                                Math.max(0, Math.min(TAM - 1, col + dc)));
                    }

                    @Override
                    public void onNumeroInvalido(int fila, int col) {
                        mostrarMensaje("Solo se permiten números del 1 al 6.", "error");
                    }
                };
                etiqueta.setOnMouseClicked(adaptador.getManejadorMouse());
                etiqueta.setOnKeyPressed(adaptador.getManejadorTeclado());

                etiquetas[f][c] = etiqueta;
                gridTablero.add(etiqueta, c, f);
            }
        }
    }

    // ------------------------------------------------------------------
    // Acciones de la interfaz (botones)
    // ------------------------------------------------------------------

    /** Maneja el botón «Nuevo juego»: pide confirmación y genera un tablero válido. */
    @FXML
    private void onNuevoJuego() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Nuevo juego");
        alerta.setHeaderText("¿Deseas iniciar un nuevo juego?");
        alerta.setContentText(juegoActivo
                ? "Se perderá el progreso de la partida actual."
                : "Se generará un tablero de Sudoku 6x6.");
        Optional<ButtonType> respuesta = alerta.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            tablero.nuevoJuego();
            juegoActivo = true;
            sugerencia = null;
            filaSeleccionada = -1;
            colSeleccionada = -1;
            actualizarVista();
            actualizarAyudas();
            mostrarMensaje("¡Nuevo juego! Selecciona una celda y escribe un número del 1 al 6.", "ok");
        }
    }

    /** Maneja el botón «Ayuda»: resalta un número sugerido sin escribirlo. */
    @FXML
    private void onAyuda() {
        if (!juegoActivo) {
            mostrarMensaje("Primero inicia un juego.", "error");
            return;
        }
        if (tablero.ayudasRestantes() <= 0) {
            mostrarMensaje("Ya usaste todas las ayudas disponibles.", "error");
            return;
        }
        if (tablero.tieneErrores()) {
            mostrarMensaje("Corrige las celdas en rojo antes de pedir ayuda.", "error");
            return;
        }
        if (tablero.contarVacias() < 2) {
            mostrarMensaje("Ya casi terminas: la ayuda no puede completar el tablero.", "error");
            return;
        }
        Optional<Tablero.Sugerencia> resultado = tablero.calcularSugerencia();
        if (resultado.isEmpty()) {
            mostrarMensaje("Con los números actuales el tablero no tiene solución. Revisa tus jugadas.", "error");
            return;
        }
        sugerencia = resultado.get();
        actualizarVista();
        actualizarAyudas();
        mostrarMensaje("Pista: prueba el " + sugerencia.valor() + " en la fila "
                + (sugerencia.fila() + 1) + ", columna " + (sugerencia.columna() + 1)
                + " (celda amarilla).", "ayuda");
    }

    /** Maneja el botón «Salir» con confirmación. */
    @FXML
    private void onSalir() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Salir");
        alerta.setHeaderText("¿Seguro que quieres salir?");
        Optional<ButtonType> respuesta = alerta.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    // ------------------------------------------------------------------
    // Lógica de juego disparada por eventos de celda
    // ------------------------------------------------------------------

    private void seleccionar(int fila, int col) {
        filaSeleccionada = fila;
        colSeleccionada = col;
        etiquetas[fila][col].requestFocus();
        actualizarVista();
    }

    private void colocarNumero(int fila, int col, int numero) {
        if (!comprobarEditable(fila, col)) {
            return;
        }
        sugerencia = null;
        tablero.getCelda(fila, col).setValor(numero);
        tablero.recalcularErrores();
        actualizarVista();

        Celda celda = tablero.getCelda(fila, col);
        if (celda.estaEnError()) {
            mostrarMensaje(tablero.describirConflicto(fila, col), "error");
        } else if (tablero.estaResuelto()) {
            terminarJuego();
        } else {
            mostrarMensaje("Número " + numero + " colocado correctamente.", "ok");
        }
    }

    private void borrarNumero(int fila, int col) {
        if (!comprobarEditable(fila, col)) {
            return;
        }
        sugerencia = null;
        tablero.getCelda(fila, col).setValor(0);
        tablero.recalcularErrores();
        actualizarVista();
        mostrarMensaje("Celda borrada.", "info");
    }

    private boolean comprobarEditable(int fila, int col) {
        if (!juegoActivo) {
            mostrarMensaje("Primero pulsa «Nuevo juego».", "error");
            return false;
        }
        if (tablero.getCelda(fila, col).esFija()) {
            mostrarMensaje("Esa celda es una pista y no se puede modificar.", "error");
            return false;
        }
        return true;
    }

    private void terminarJuego() {
        juegoActivo = false;
        mostrarMensaje("¡Felicidades! Resolviste el Sudoku.", "ok");
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("¡Ganaste!");
        alerta.setHeaderText("¡Sudoku completado!");
        alerta.setContentText("Todas las filas, columnas y bloques están correctos. "
                + "Pulsa «Nuevo juego» para jugar otra vez.");
        alerta.showAndWait();
    }

    // ------------------------------------------------------------------
    // Actualización de la vista
    // ------------------------------------------------------------------

    private void actualizarVista() {
        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                Celda celda = tablero.getCelda(f, c);
                Label etiqueta = etiquetas[f][c];
                boolean esSugerida = sugerencia != null && sugerencia.fila() == f && sugerencia.columna() == c;

                if (!celda.estaVacia()) {
                    etiqueta.setText(String.valueOf(celda.getValor()));
                } else if (esSugerida) {
                    etiqueta.setText(String.valueOf(sugerencia.valor()));
                } else {
                    etiqueta.setText("");
                }

                etiqueta.getStyleClass().setAll("celda");
                if (filaSeleccionada >= 0 && (f == filaSeleccionada || c == colSeleccionada)) {
                    etiqueta.getStyleClass().add("relacionada");
                }
                if (celda.esFija()) {
                    etiqueta.getStyleClass().add("fija");
                } else if (!celda.estaVacia()) {
                    etiqueta.getStyleClass().add("usuario");
                }
                if (f == filaSeleccionada && c == colSeleccionada) {
                    etiqueta.getStyleClass().add("seleccionada");
                }
                if (esSugerida) {
                    etiqueta.getStyleClass().add("sugerida");
                }
                if (celda.estaEnError()) {
                    etiqueta.getStyleClass().add("error");
                }
            }
        }
    }

    private void actualizarAyudas() {
        lblAyudas.setText("Ayudas restantes: " + tablero.ayudasRestantes() + " / " + Tablero.MAX_AYUDAS);
    }

    private void mostrarMensaje(String texto, String tipo) {
        lblMensaje.setText(texto);
        lblMensaje.getStyleClass().setAll("mensaje", tipo);
    }
}
