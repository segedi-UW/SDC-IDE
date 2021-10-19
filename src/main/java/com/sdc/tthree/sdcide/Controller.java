package com.sdc.tthree.sdcide;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Controller {
    @FXML
    private Label welcomeText;

    @FXML
    private void initialize() {
        // TODO
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to our JavaFX Application!");
    }
}