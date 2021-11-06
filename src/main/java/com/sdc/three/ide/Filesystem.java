package com.sdc.three.ide;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public interface Filesystem {
    /**
     * Loads a valid workspace directory into the Filesystem
     * @param workspace
     * @throws NullPointerException if projectFile is null
     * @throws InvalidFileException if the provided proojectFile is not a valid workspace directory
     */
    void loadWorkspace(Workspace workspace) throws IOException;

    /**
     * Creates a new workspace in the given directory
     * @param directory
     * @throws IOException if an IO error occurs
     * @throws NullPointerException if the directory or name is null
     * @returns a Workspace within the provided directory
     */
    Workspace createWorkspace(File directory, String name) throws IOException;

    /**
     * Saves a workspace directory and its files or the individual file in the directory
     * @param file
     * @throws IOException
     * @throws NullPointerException if the file is not within the Filesystem
     */
    void save(File file, String string) throws IOException;

    Set<File> getModifiedFiles();
}