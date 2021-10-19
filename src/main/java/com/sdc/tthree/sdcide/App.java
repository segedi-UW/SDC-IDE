package com.sdc.tthree.sdcide;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = fxmlLoader("Controller.fxml");
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        addDefaultStylesheets(scene);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static FXMLLoader fxmlLoader(String resource) {
        return new FXMLLoader(resourceURL(resource));
    }

    public static void addDefaultStylesheets(Scene scene) {
        String custom = resourceURL("stylesheet.css").toExternalForm();
        scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet(), custom);
        // our custom sheet has the highest precedence when added last
    }

    public static URL resourceURL(String resource) {
        URL url = App.class.getResource(resource);
        if (url == null) throw new NullPointerException(String.format("Resource \"%s\" was not found", resource));
        return url;
    }

    public static void main(String[] args) {
        launch();
    }
}