package com.sdc.three.ide;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.nio.file.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The WorkspaceViewer class acts as the graphical UI wrapper for a Workspace.
 *
 * Workspace specific modification can be done by calling {@link #getWorkspace()}, which returns a {@link Workspace} object
 *
 * @see #getWorkspace()
 * @see Workspace
 *
 * @author Anthony Segedi
 */
public class WorkspaceViewer extends TreeView<Path> {

    private Workspace workspace;
    private static final HashMap<String, Image> fileGraphics = initFileGraphics();
    private static final String generalFileExtension = ".*";

    /**
     * Creates a WorkspaceViewer of a given workspace
     * @param workspace the workspace to view or null for an empty workspace
     */
    public WorkspaceViewer(Workspace workspace) {
        super();
        setCellFactory(view -> new TreeCellPathSkin());
        setEditable(false); // can change in the future to rename file
        setShowRoot(true);
        setWorkspace(workspace);
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    /**
     * Initialize the mappings for file type to image for display
     *
     * @return initialized fileGraphics HashMap
     */
    private static HashMap<String, Image> initFileGraphics() {
        HashMap<String, Image> map = new HashMap<>();
        // Add custom images to the resources/.../images directory, then put the file into the map as seen below
        final String resourceDirectory = "images/";
        map.put(generalFileExtension, resourceToIcon(resourceDirectory + "file-gen.png"));
        map.put(File.separator, resourceToIcon(resourceDirectory + "directory.png"));
        // custom mappings
        map.put(".java", resourceToIcon(resourceDirectory + "file-java.png"));
        map.put(".jpeg", resourceToIcon(resourceDirectory + "file-jpeg.png"));
        return map;
    }

    private static Image resourceToIcon(String resource) {
        final double length = 20.0;
        // square icon
        return new Image(App.toResourceURL(resource).toExternalForm(), length, length, true, true);
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
        if (workspace != null) {
            TreeItem<Path> root = workspace.getRoot();
            root.setExpanded(true);
            setRoot(root);
        }
    }

    /**
     * Attributions:
     * directory image: https://www.freeiconspng.com/img/12404
     * file image (I modified all files off this one): https://pixabay.com/illustrations/file-icon-vector-file-jpeg-icon-3671169/
     */
    private static class TreeCellPathSkin extends TreeCell<Path> {

        @Override
        protected void updateItem(Path path, boolean isEmpty) {
            super.updateItem(path, isEmpty);
            // sets text as only the trailing name
            if (isEmpty || path == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            setText(path.getFileName().toString());
            setGraphic(getFileGraphic(path));
        }

        private ImageView getFileGraphic(Path path) {
            final String extension = getExtension(path);
            final Image image = fileGraphics.get(extension);
            return image != null ? new ImageView(image) : new ImageView(fileGraphics.get(generalFileExtension));
        }

        private String getExtension(Path path) {
            if (path.toFile().isDirectory())
                return File.separator;

            final Pattern extension = Pattern.compile("\\.+.+$");
            final Matcher matcher = extension.matcher(path.getFileName().toString());
            if (matcher.find())
                return matcher.group();

            return generalFileExtension;
        }

    }
}