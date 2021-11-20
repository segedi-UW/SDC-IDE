package com.sdc.three.ide;

import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;

import static java.nio.file.FileVisitResult.CONTINUE;

public class Workspace extends TreeItem<File> {

    private final File dir;

    public Workspace(File dir) throws InvalidFileException, IOException {
        super();
        validateWorkspace(dir);
        this.dir = dir;
        parseWorkspace();
    }

    public File getDirectory() {
        return dir;
    }

    private void validateWorkspace(File workspace) throws InvalidFileException {
        if (workspace == null) throw new NullPointerException("Workspace cannot be null");
        else if (!workspace.isDirectory()) throw new InvalidFileException("Provided file is not a directory");
    }

    private void parseWorkspace() throws IOException {
        // want to init tree
        WorkspaceVisitor visitor = new WorkspaceVisitor(this);
        visitor.buildTree();
    }


    private class WorkspaceVisitor extends SimpleFileVisitor<Path> {

        final LinkedList<TreeItem<File>> stack;
        final TreeItem<File> root;
        TreeItem<File> current;

        public WorkspaceVisitor(TreeItem<File> root) {
            this.root = root;
            stack = new LinkedList<>();
        }

        public void buildTree() throws IOException {
            stack.clear();
            current = null;
            Files.walkFileTree(dir.toPath(), this);
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            stack.push(current);
            current = current == null ? root : new TreeItem<>(dir.toFile());
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            addToCurrent(new TreeItem<>(file.toFile()));
            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            TreeItem<File> old = current;
            current = stack.pop();
            if (current != null) addToCurrent(old);
            return CONTINUE;
        }

        private void addToCurrent(TreeItem<File> item) {
            current.getChildren().add(item);
        }
    }
}
