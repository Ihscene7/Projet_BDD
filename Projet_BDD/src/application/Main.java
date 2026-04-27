package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        // Test connexion
        if (DatabaseConnection.getConnection() != null) {
            System.out.println("✓ Connexion Oracle réussie !");
        } else {
            System.out.println("✗ Connexion échouée !");
        }

        primaryStage = stage;
        loadScene("menu.fxml", "Menu Principal");
    }
    // Static helper — call this from any controller to switch scenes
    public static void loadScene(String fxmlFile, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            Main.class.getResource(fxmlFile)  // ← use the parameter
        );

        Parent root = loader.load();
        primaryStage.setTitle(title);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
}