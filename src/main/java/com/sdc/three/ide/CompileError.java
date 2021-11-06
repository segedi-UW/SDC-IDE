package com.sdc.three.ide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

public class CompileError {

    private final String stackTrace;
    private final ArrayList<LineError> lines = new ArrayList<>();

    public CompileError(String stackTrace) {
        this.stackTrace = stackTrace;
        parseStackTrace();
    }

    public CompileError(Stream<String> stream) {
        StringBuilder builder = new StringBuilder();
        stream.forEach(line -> builder.append(line).append("\n"));
        stackTrace = builder.toString();
        parseStackTrace();
    }

    private void parseStackTrace() {
        // TODO
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public Collection<LineError> getLines() {
        return lines;
    }
}
