package com.sdc.tthree.sdcide;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = fxmlLoader("Controller.fxml");
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        addStylesheet(scene);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static FXMLLoader fxmlLoader(String resource) {
        URL url = App.class.getResource(resource);
        if (url == null) throw new NullPointerException("FXML resource \"" + resource + "\" was not found");
        return new FXMLLoader(url);
    }

    public static void addStylesheet(Scene scene) {
        URL url = App.class.getResource("stylesheet.css");
        if (url == null) throw new NullPointerException("Default stylesheet was not found");
        scene.getStylesheets().add(url.toExternalForm());
    }

    public static void main(String[] args) {
        launch();
    }
}