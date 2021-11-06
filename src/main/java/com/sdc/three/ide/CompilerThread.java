package com.sdc.three.ide;

import javafx.beans.property.*;
import javafx.concurrent.Task;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CompilerThread implements Compiler {

    private CompilerTask task;
    private final DoubleProperty progressProperty = new SimpleDoubleProperty(0.0);
    private final BooleanProperty doneProperty = new SimpleBooleanProperty(false);

    @Override
    public void compile(List<File> files) {
        if (files == null) throw new NullPointerException("Files cannot be a null pointer");
        validateFiles(files);
        if (task != null && !task.isDone()) task.cancel();
        task = new CompilerTask(files);
        progressProperty.unbind();
        progressProperty.bind(task.progressProperty());
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void validateFiles(List<File> files) {
        for (File file : files) {
            if (file == null) throw new NullPointerException("Cannot compile null file");
            if (!file.exists() || !file.getName().endsWith(".java")) {
                String message = String.format("File (%s) must exist and end with .java", file.getName());
                throw new IllegalArgumentException(message);
            }
        }
    }

    @Override
    public HashMap<File, CompileError> getError() {
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Compile thread interrupted before completion. Stopped compilation to return empty HashMap");
            return new HashMap<>();
        }
    }

    @Override
    public ReadOnlyDoubleProperty progressProperty() {
        return new ReadOnlyDoubleWrapper(progressProperty, "progressProperty");
    }

    @Override
    public double getProgress() {
        return progressProperty.get();
    }

    @Override
    public ReadOnlyBooleanProperty doneProperty() {
        return new ReadOnlyBooleanWrapper(doneProperty, "doneProperty", false);
    }

    @Override
    public boolean isDone() {
        return doneProperty.get();
    }

    private static class CompilerTask extends Task<HashMap<File, CompileError>> {

        private final List<File> files;

        public CompilerTask(List<File> files) {
            this.files = files;
        }

        @Override
        protected HashMap<File, CompileError> call() throws Exception {
            final HashMap<File, CompileError> errors = new HashMap<>(files.size() * 2 + 1);
            // files already validated
            for (File file : files) {
                CompileError error = runProcess(file);
                if (error != null) {
                    errors.put(file, error);
                }
            }
            return errors;
        }

        private CompileError runProcess(File file) throws Exception {
            String[] args = {"javac", file.getAbsolutePath()};
            Process p = Runtime.getRuntime().exec(args);
            try (BufferedReader errorStream = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                final int exit = p.waitFor();
                if (exit != 0)
                    return new CompileError(errorStream.lines());
            } // don't catch error - throw it
            return null;
        }
    }
}
