package com.sdc.three.ide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface Filesystem {
    void load(File projectFile) throws FileNotFoundException, InvalidFileException;
    void save(File file) throws IOException;
    void saveAs(File file, File newFile) throws IOException;
    // TODO
}
