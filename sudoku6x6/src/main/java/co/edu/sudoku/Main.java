package co.edu.sudoku;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de JavaFX. Carga la vista FXML (diseñada con Scene Builder)
 * y muestra la ventana del juego.
 */
public class Main extends Application {

    /**
     * Construye la escena a partir del archivo FXML y muestra la ventana.
     *
     * @param stage ventana principal proporcionada por JavaFX
     * @throws IOException si no se puede cargar el archivo FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("/co/edu/sudoku/view/sudoku-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Sudoku 6x6");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
