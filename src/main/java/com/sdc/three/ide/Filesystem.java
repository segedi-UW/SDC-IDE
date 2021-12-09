package com.sdc.three.ide;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface Filesystem {

    /**
     * Saves a workspace directory and its files or the individual file in the directory
     * @param path The path to the file save
     * @throws IOException for failed writes
     * @throws NullPointerException if the file is not within the Filesystem
     */
    void save(Path path, String string) throws IOException;

    Map<Path, FileEvent> getFilesToSave();
}