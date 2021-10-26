package com.sdc.three.ide;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = toLoader("controller.fxml");
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        addDefaultStylesheets(scene);
        stage.setTitle("This is the title!");
        stage.setScene(scene);
        stage.show();
    }

    public static void setScene(Scene scene) {
        if (mainStage == null) throw new NullPointerException("Attempted to set scene of mainStage before initialization");
        mainStage.setScene(scene);
    }

    /**
     * Returns a FXMLLoader of a given system resource
     * @param resource the resource name
     * @return a FXMLLoader with a non-null FXML sheet
     * @throws NullPointerException if resource does not exist
     */
    public static FXMLLoader toLoader(String resource) {
        return new FXMLLoader(toResourceURL(resource));
    }

    /**
     * Adds the stylesheet.css file and the BootstrapFX stylesheet to the Scene. This
     * should be called on all new Stages / Scenes in order to maintain a consistent look
     * for the Application.
     * @param scene the scene to add the stylesheets to.
     * @throws NullPointerException if stylesheet.css is not on the resource path
     */
    public static void addDefaultStylesheets(Scene scene) {
        String custom = toResourceURL("stylesheet.css").toExternalForm();
        scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet(), custom);
        // our custom sheet has the highest precedence when added last
    }

    /**
     * Returns a non-null URL of an Application resource.
     * @param resource the resource name
     * @return non-null URL
     * @throws NullPointerException if resource does not exist
     */
    public static URL toResourceURL(String resource) {
        URL url = App.class.getResource(resource);
        if (url == null) throw new NullPointerException(String.format("Resource \"%s\" was not found", resource));
        return url;
    }

    public static void main(String[] args) {
        launch();
    }
}