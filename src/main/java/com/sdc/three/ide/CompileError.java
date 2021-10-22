package com.sdc.three.ide;

import java.util.Collection;

public interface CompileError {
    String getStackTrace();
    Collection<LineError> getLines();
}
