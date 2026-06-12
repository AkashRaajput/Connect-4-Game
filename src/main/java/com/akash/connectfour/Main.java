package com.akash.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String FXML_RESOURCE = "/com/akash/connectfour/game.fxml";
    private static final String ICON_RESOURCE = "/com/akash/connectfour/icon/app-icon.png";

    public static void main(String[] args) {
        LOGGER.info("Launching Connect 4 application");
        launch(args);
    }

    @Override
    public void init() {
        LOGGER.info("Initializing application resources");
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxmlUrl = Main.class.getResource(FXML_RESOURCE);
            if (fxmlUrl == null) {
                throw new IllegalStateException("FXML resource not found: " + FXML_RESOURCE);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root, 980, 720);
            URL stylesheetUrl = Main.class.getResource("/com/akash/connectfour/styles/game.css");
            if (stylesheetUrl != null) {
                scene.getStylesheets().add(stylesheetUrl.toExternalForm());
            }
            primaryStage.setTitle("Connect 4");
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(680);
            Image applicationIcon = loadApplicationIcon();
            if (applicationIcon != null) {
                primaryStage.getIcons().add(applicationIcon);
            }
            primaryStage.setScene(scene);
            primaryStage.show();

            LOGGER.info("Application started successfully");
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Failed to load FXML layout", exception);
            showStartupError("Unable to load the game layout. Check that game.fxml is packaged correctly.");
            Platform.exit();
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Unexpected startup failure", exception);
            showStartupError(exception.getMessage());
            Platform.exit();
        }
    }

    private static Image loadApplicationIcon() {
        URL iconUrl = Main.class.getResource(ICON_RESOURCE);
        if (iconUrl == null) {
            LOGGER.warning("Application icon not found: " + ICON_RESOURCE);
            return null;
        }
        return new Image(iconUrl.toExternalForm(), true);
    }

    @Override
    public void stop() {
        LOGGER.info("Application shutdown complete");
    }

    private static void showStartupError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Startup Error");
        alert.setHeaderText("Connect 4 could not start");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
