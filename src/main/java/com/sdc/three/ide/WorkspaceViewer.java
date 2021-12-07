package com.sdc.three.ide;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.nio.file.*;

public class WorkspaceViewer extends TreeView<Path> {

    private Workspace workspace;

    /**
     * Creates a WorkspaceViewer of a given workspace
     * @param workspace the workspace to view or null for an empty workspace
     */
    public WorkspaceViewer(Workspace workspace) {
        super();
        setEditable(false);
        setWorkspace(workspace);
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
        if (workspace != null) {
            TreeItem<Path> root = workspace.getRoot();
            root.setExpanded(true);
            setRoot(root);
        }
    }
}