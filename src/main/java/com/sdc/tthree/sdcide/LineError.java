package com.sdc.tthree.sdcide;

public class LineError {

    private final int line;
    private final String message;

    public LineError(int line, String message) {
        this.line = line;
        this.message = message;
    }

    int getLine() {
        return line;
    }

    String getMessage() {
        return message;
    }

    String getClassname() {
        return "Not Implemented";
    }

}
