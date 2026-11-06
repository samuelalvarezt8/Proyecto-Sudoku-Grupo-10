package co.edu.sudoku;

import javafx.application.Application;

/**
 * Punto de entrada de la aplicación.
 * <p>
 * Esta clase no extiende {@link Application} a propósito: así el proyecto se puede
 * ejecutar directamente desde IntelliJ IDEA (botón "Run") sin configurar el
 * <i>module path</i> de JavaFX.
 * </p>
 */
public class Launcher {

    /**
     * Lanza la aplicación JavaFX.
     *
     * @param args argumentos de línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
