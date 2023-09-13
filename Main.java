package com.akash.javafxapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.util.Optional;

public class Main extends Application{

    public static void main (String[] args){
        launch(args);

    }

    @Override
    public void init() throws Exception {
        System.out.println("init");
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("start");

        FXMLLoader loader = new
                FXMLLoader(getClass().getResource("app_layout.fxml"));
        VBox rootNode = loader.load();

        MenuBar menuBar = createMenu();
        rootNode.getChildren().add(0,menuBar);

        Scene scene = new Scene(rootNode);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Temperature Converter Tool");
        primaryStage.show();

    }

    private MenuBar createMenu(){
        //File Menu
        Menu fileMenu = new Menu("File");
        MenuItem newMenuItem = new Menu("New");

        newMenuItem.setOnAction(event -> {
                    System.out.println("New Menu Item Clicked");
        });

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem quitMenuItem = new Menu("Quit");

        quitMenuItem.setOnAction(event -> {
                    Platform.exit();
                    System.exit(0);
        });

        fileMenu.getItems().addAll(newMenuItem,separatorMenuItem,quitMenuItem);


        //Help menu
        Menu helpMenu = new Menu("Help");
        final MenuItem aboutApp = new MenuItem("About");

        helpMenu.setOnAction(event -> {
            aboutApp();
        });

        helpMenu.getItems().add(aboutApp);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;


    }

    private void aboutApp() {
        Alert alertDialog = new Alert(Alert.AlertType.INFORMATION);
        alertDialog.setTitle("My First Desktop App");
        alertDialog.setHeaderText("Learning Javafx");
        alertDialog.setContentText("I am just a beginner but soon I will be pro ans start developing awesome java apps");

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("NO");
        alertDialog.getButtonTypes().setAll(yesBtn,noBtn);

        Optional<ButtonType> clickedBtn = alertDialog.showAndWait();

        if (clickedBtn.isPresent() && clickedBtn.get() == yesBtn){
            System.out.println("Yes Button Clicked");
        }else{
            System.out.println("No Button Clicked");
        }

        alertDialog.show();


    }

    @Override
    public void stop() throws Exception {
        System.out.println("stop");
        super.stop();
    }
}
