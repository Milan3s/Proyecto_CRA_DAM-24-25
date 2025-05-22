package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
//import javafx.geometry.Rectangle2D;
//import javafx.stage.Screen;
import utils.DataBaseConection;

public class App extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {    
        primaryStage = stage;
        //loadView("/views/Acceso");
        //loadView("/views/Dashboard");
        loadView("/views/Espacio");
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
        
        /*
        // Se obtiene la resolución de pantalla
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        
        // Se pone a pantalla completa
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        
        // No permitir cambiar tamaño
        primaryStage.setResizable(false);
        */
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
