package com.sdc.tthree.sdcide;

public class InvalidFileException extends Exception {

    public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException() {
        super();
    }
}
