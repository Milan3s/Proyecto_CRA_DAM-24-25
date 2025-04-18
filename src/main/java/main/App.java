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
        //loadView("/views/Acceso");
        loadView("/views/Alumnos");
        stage.setTitle("Sistema CRA");
        stage.show();   
     }

    // Cambia este método a public static
    public static void loadView(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        
        boolean wasMaximized = primaryStage.isMaximized();
        double width = primaryStage.getWidth();
        double height = primaryStage.getHeight();
        
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        
        if (wasMaximized) {
            primaryStage.setMaximized(true);
        } else {
            primaryStage.setWidth(Math.max(width, 1980));
            primaryStage.setHeight(Math.max(height, 1080));
        }
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