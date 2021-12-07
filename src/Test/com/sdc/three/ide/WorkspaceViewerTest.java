package com.sdc.three.ide;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class WorkspaceViewerTest {

    private WorkspaceViewer view;

    /**
     * Will be called with {@code @Before} semantics, i.e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) throws InvalidFileException, IOException {
        final String path = Path.of(".", "src", "Test", "com", "sdc", "three", "ide", "workspaceTests").toString();
        File dir = new File(path);
        assert dir.exists();
        Workspace wk = new Workspace(dir);
        view = new WorkspaceViewer(wk);
        stage.setScene(new Scene(new StackPane(view), 600, 600));
        stage.show();
    }

    @Test
    void selection() {
        // param FxRobot
        // TODO
        fail("Not implemented");
    }

    @Test
    void autoUpdates() {
        // TODO
        fail("Not implemented");
    }
}
