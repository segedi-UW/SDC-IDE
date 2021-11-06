package com.sdc.three.ide;

import javafx.scene.control.TreeView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class WorkspaceViewer extends TreeView<File> implements Filesystem {

    private final WatchService watcher = FileSystems.getDefault().newWatchService();

    private final Set<File> modifiedFiles = Collections.synchronizedSet(new LinkedHashSet<>());
    private Workspace workspace;
    private WatchKey watchKey;

    /**
     * Creates a WorkspaceViewer of an empty workspace
     */
    public WorkspaceViewer() throws IOException {
        super();
        init();
    }

    /**
     * Creates a WorkspaceViewer of a given workspace
     * @param workspace
     * @throws FileNotFoundException if the file does not exist
     * @throws InvalidFileException if the file is not a valid workspace directory
     * @throws NullPointerException if the file is null
     */
    public WorkspaceViewer(Workspace workspace) throws IOException {
        super();
        loadWorkspace(workspace);
        init();
    }

    private void init() throws IOException {
        setEditable(false);
    }

    private void setupWatch() throws IOException {
        if (watchKey != null) watchKey.cancel();
        Path path = workspace.getDirectory().toPath();
        path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    }

    @Override
    public void loadWorkspace(Workspace workspace) throws IOException {
        this.workspace = workspace;
        workspace.setExpanded(true);
        setRoot(workspace);
        File dir = workspace.getDirectory();
        // TODO implement FileVisitor
        setupWatch();
    }

    private void addFile(File file) {
        // TODO Add the file to the display
    }

    @Override
    public Workspace createWorkspace(File dir, String name) throws IOException {
        if (dir == null || name == null) {
            String error = (dir == null ? "directory":"name");
            throw new NullPointerException("The " + error + " is null");
        }
        if (!dir.isDirectory() && !dir.mkdir())
            throw new IOException(String.format("Could not make %s a directory", dir.getName()));

        try {
            File file = new File(dir, name + Workspace.EXTENSION);
            Workspace workspace = new Workspace(file);
            loadWorkspace(workspace);
            return workspace;
        } catch (InvalidFileException e) {
            throw new IllegalStateException("BUG: createWorkspace() created an invalid workspace file");
        }
    }

    @Override
    public void save(File file, String string) throws IOException {
        Files.writeString(file.toPath(), string, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    public Set<File> getModifiedFiles() {
        return new HashSet<>(modifiedFiles);
    }

    private class WatchThread implements Runnable {

        private final Thread thread;
        private volatile boolean isRunning;

        private WatchThread() {
            thread = new Thread(this);
            thread.setDaemon(true);
        }

        public void start() {
            thread.start();
        }

        @Override
        public void run() {
            try {
                while (isRunning) {
                    WatchKey key = watcher.take();
                }
            } catch (InterruptedException e) {
                System.err.println("WatchThread Interrupted: Canceling");
            }
        }
    }
}