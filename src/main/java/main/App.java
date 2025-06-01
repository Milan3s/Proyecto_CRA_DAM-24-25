package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import utils.DataBaseConection;

public class App extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {    
        primaryStage = stage;
        loadView("/views/Acceso");
        stage.setTitle("Sistema CRA");
        stage.show();
        
        // shutdown hook es un hilo especial que se ejecuta cuando la aplicación se está cerrando,
        // incluso aunque sea de forma inesperada
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DataBaseConection.closeConnection();
        }));
    }

    public static void loadView(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    // Añade este método si necesitas cargar FXML directamente
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
