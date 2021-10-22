package com.sdc.three.ide;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * An example class used to illustrate how to use FXML with a custom
 * subclass with a non-trivial content or for the general case of
 * cleaner code
 *
 * A Group is simply a container that we use in this case. We could
 * use one of many concrete Pane classes to achieve the same effect
 * with different layouts, including an HBox or VBox (although if only
 * one child is added most Panes will display the child in the same way).
 */
public class Chat extends StackPane {

    @FXML private TextArea chat;
    @FXML private TextField input;

    public Chat() {
        super();
        try {
            FXMLLoader loader = App.fxmlLoader("chat.fxml");
            loader.setControllerFactory(callback -> this);
            getChildren().add(loader.load());
        } catch (IOException e) {
            throw new IllegalStateException("Could not load chat.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void sendChat() {
        /* send the message */
        chat.setText(String.format("%s\n> %s", chat.getText(), input.getText()));
        input.setText("");
    }

    @FXML
    public void switchToHome() throws IOException {
        FXMLLoader loader = App.fxmlLoader("controller.fxml");
        Scene home = new Scene(loader.load(), 320, 240);
        App.addDefaultStylesheets(home);
        App.setScene(home);
    }

    public TextArea getChat() {
        return chat;
    }
    /* other methods that you want */
}
