package com.natan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader load = new FXMLLoader();
        load.setResources(ResourceBundle.getBundle("messages", new Locale("pt", "BR")));
        Scene scene = new Scene(load.load(getClass().getResourceAsStream("fxml/principal.fxml")));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("SAll " + load.getResources().getString("versao"));
        primaryStage.show();
    }
}
