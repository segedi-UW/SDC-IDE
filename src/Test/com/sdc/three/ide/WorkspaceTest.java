package com.sdc.three.ide;

import javafx.scene.control.TreeItem;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class WorkspaceTest {

    @Test
    void testBuild() {
        final String path = Path.of(".", "src", "Test", "com", "sdc", "three", "ide", "workspaceTests").toString();
        File dir = new File(path);
        assert dir.exists();
        try {
            Workspace wk = new Workspace(dir);
            assertTrue(containsAll(wk, "A", "B.java", "dir"));
            TreeItem<File> dirItem = getName(wk, "dir");
            assertTrue(containsAll(dirItem, "C.java", "D.java"));
        } catch (IOException | InvalidFileException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    private boolean containsAll(TreeItem<File> root, String ... names) {
        final HashMap<String, Void> toFind = new HashMap<>(names.length * 2 + 1);
        for(String name : names) {
            toFind.put(name, null);
        }
        for (TreeItem<File> item : root.getChildren()) {
            String name = item.getValue().getName();
            toFind.remove(name);
        }
        return toFind.size() == 0;
    }

    private TreeItem<File> getName(TreeItem<File> item, String name) {
        for(TreeItem<File> child : item.getChildren()) {
            if (child.getValue().getName().equals(name))
                return child;
        }
        throw new NoSuchElementException(String.format("The list %s did not contain %s", item.getChildren().toString(), name));
    }
}