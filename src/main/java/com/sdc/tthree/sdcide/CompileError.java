package com.sdc.tthree.sdcide;

import java.util.Collection;

public interface CompileError {
    String getStackTrace();
    Collection<LineError> getLines();
}
