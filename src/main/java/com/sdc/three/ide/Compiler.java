package com.sdc.three.ide;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public interface Compiler {
    /**
     * Starts the compiling process. If compiling had not finished, it is canceled and restarted.
     * Files must be valid compilable java files.
     * @throws IllegalArgumentException if the file does not exist, or it is not a .java file.
     * @throws NullPointerException if the file is null
     * @param files
     */
    void compile(List<File> files);

    /**
     * Returns a map of CompileError associated with the previous compile. Note that
     * this method should be called when the compilation is done! Early access will
     * result in the compilation being canceled, and an empty map to be returned.
     *
     * To properly utilize this method, callers should create a listener for the doneProperty,
     * which will notify them when there is a change in the property (then the caller should
     * check if the change was from false to true etc.). The immediate check is using
     * isDone().
     *
     * @return a map of File to CompileError. It may be empty if there are no errors or the compilation
     * thread was stopped before completion, but will never be null
     */
    HashMap<File, CompileError> getError();

    /**
     * Returns the compiler's progress of the latest compilation
     * @return the progress property of this Compiler
     */
    ReadOnlyDoubleProperty progressProperty();

    /**
     * Returns the progress.
     * @return the progress from 0.0 to 1.0
     */
    double getProgress();

    /**
     * Returns the compiler's progress as a boolean property.
     * @return the done property of the compiler.
     */
    ReadOnlyBooleanProperty doneProperty();

    /**
     * Returns the compiler's progress as a boolean value.
     * @return the done state of the compiler. True if done, False if working still.
     */
    boolean isDone();
}
