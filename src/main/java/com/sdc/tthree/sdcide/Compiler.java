package com.sdc.tthree.sdcide;

import javafx.beans.property.ReadOnlyIntegerProperty;

public interface Compiler {
    void compile();
    CompileError getError();
    ReadOnlyIntegerProperty progressProperty();
    int getProgress();
}
