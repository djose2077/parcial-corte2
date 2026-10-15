package co.edu.poli.parcial2_jose.vista;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Punto de entrada y ventana principal de la aplicación JavaFX.
 */
public class App extends Application {

	private static Scene scene;

	/**
	 * Carga el formulario principal y muestra la ventana de tarjetas.
	 *
	 * @param stage ventana principal proporcionada por JavaFX
	 * @throws IOException si no se encuentra o no se puede cargar el FXML
	 */
	@Override
	public void start(Stage stage) throws IOException {
		URL fxml = App.class.getResource("/co/edu/poli/parcial2_jose/formCard.fxml");
		if (fxml == null) {
			throw new IOException("No se encontró el archivo formCard.fxml");
		}
		Parent root = FXMLLoader.load(fxml);
		scene = new Scene(root);
		stage.setTitle("Tarjetas");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Cambia el contenido de la escena al formulario indicado.
	 *
	 * @param fxml nombre del recurso FXML sin la extensión
	 * @throws IOException si el recurso no se puede cargar
	 */
	static void setRoot(String fxml) throws IOException {
		scene.setRoot(loadFXML(fxml));
	}

	/**
	 * Carga un archivo FXML ubicado junto a los recursos de la vista.
	 *
	 * @param fxml nombre del recurso sin la extensión
	 * @return nodo raíz creado desde el archivo FXML
	 * @throws IOException si el recurso no se puede cargar
	 */
	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	/**
	 * Inicia el ciclo de vida de JavaFX.
	 *
	 * @param args argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		launch();
	}
}
