package com.sdc.three.ide;

import java.nio.file.Path;

public interface FileChangeListener {
    void filesystemChanged(Path path, FileEvent event);
}
