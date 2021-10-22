package com.sdc.three.ide;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.IOException;

public class Controller {

    @FXML private Label welcomeText;

    @FXML
    private void initialize() {
        // TODO
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to our JavaFX Application!");
    }

    @FXML
    public void switchToChat(ActionEvent actionEvent) throws IOException {
        // ActionEvent is not used, simply included as an example
        actionEvent.consume(); // not necessary and sometimes not wanted - only for example
        Scene chat = new Scene(new Chat(), 320, 240);
        App.addDefaultStylesheets(chat);
        App.setScene(chat);
    }
}