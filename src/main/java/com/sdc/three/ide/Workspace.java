package com.sdc.three.ide;

import javafx.scene.control.TreeItem;

import java.io.File;

public class Workspace extends TreeItem<File> {

    public static final String EXTENSION = ".wks";
    private final File file;
    public Workspace(File file) throws InvalidFileException {
        super();
        validateWorkspace(file);
        this.file = file;
    }

    private void validateWorkspace(File workspace) throws InvalidFileException {
        if (workspace == null) throw new NullPointerException("Workspace cannot be null");
        final String name = workspace.getName();
        if (!workspace.isDirectory())
            throw new InvalidFileException(String.format("%s is not a directory", name));
        if (!name.endsWith(EXTENSION))
            throw new InvalidFileException(String.format("%s is not a workspace file", name));
    }

    public File getDirectory() {
        return file;
    }
}
