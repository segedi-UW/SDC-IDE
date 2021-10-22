package com.sdc.three.ide;

public class InvalidFileException extends Exception {

    public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException() {
        super();
    }
}
