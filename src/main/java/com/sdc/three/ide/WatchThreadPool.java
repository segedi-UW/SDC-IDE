package com.sdc.three.ide;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class WatchThreadPool {

    private final WatchService watcher;
    private final List<FileChangeListener> listeners = Collections.synchronizedList(new LinkedList<>());
    private final LinkedList<WatchThread> threads = new LinkedList<>();
    private final static int PROCESSORS = Runtime.getRuntime().availableProcessors();
    private final static float CREATE_CHANCE = 0.30f;
    private final static float MAX_THREADS = PROCESSORS * 2;
    private final static Map<WatchKey, Path> dirs = Collections.synchronizedMap(new HashMap<>());

    public WatchThreadPool(FileChangeListener listener) throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        listeners.add(listener);
    }

    public List<FileChangeListener> getListeners() {
        return listeners;
    }

    public boolean register(Path dir) {
        try {
            WatchKey dirKey = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            dirs.put(dirKey, dir);
            if (threads.size() < 3 || (threads.size() < MAX_THREADS && Math.random() <= CREATE_CHANCE)) {
                WatchThread watchThread = new WatchThread();
                threads.add(watchThread);
                watchThread.getThread().start();
            }
            return dirs.containsValue(dir);
        } catch (IOException e) {
            System.err.println("Failed to start watchThread " + e.getMessage());
        }
        return false;
    }

    public boolean unregister(Path dir) {
        // slow O(n) implementation as we do not expect to call often
        for (final WatchKey key : dirs.keySet()) {
            if (dirs.get(key).equals(dir)) {
                return dirs.remove(key).equals(dir);
            }
        }
        return false;
    }

    private class WatchThread implements Runnable {

        private volatile boolean isRunning;
        private final Thread thread;

        public WatchThread() {
            thread = new Thread(this);
            thread.setDaemon(true);
        }

        public Thread getThread() {
            return thread;
        }

        @Override
        public void run() {
            if (isRunning) throw new IllegalStateException("Reran while already running");
            isRunning = true;
            while(isRunning) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException e) {
                    System.err.println("WatchThread Interrupted: terminating WatchThread");
                    isRunning = false;
                    return;
                } catch (ClosedWatchServiceException e) {
                    System.err.println("Watch closed, terminating thread");
                    isRunning = false;
                    return;
                }
                List<WatchEvent<?>> events = key.pollEvents();
                for(WatchEvent<?> e : events) {
                    WatchEvent.Kind<?> type = e.kind();
                    handle(dirs.get(key), type, e);
                }
                // returns if the key is not valid
                if (!key.reset()) {
                    dirs.remove(key);
                    if (dirs.size() < 1) {
                        System.err.println("Last directory removed from watch");
                    }
                }
            }
        }

        private void handle(Path dir, WatchEvent.Kind<?> type, WatchEvent<?> e) {
            if (type == OVERFLOW) {
                System.err.println("non-fatal OVERFLOW WatchEvent occurred");
            } else {
                Object context = e.context();
                if (!(context instanceof Path)) {
                    System.err.println("Context was not a path: Not handling");
                    return;
                }
                Path path = dir.resolve((Path)context);
                if (type == ENTRY_CREATE) {
                    notifyListeners(path, FileEvent.ADDED);
                } else if (type == ENTRY_DELETE) {
                    notifyListeners(path, FileEvent.REMOVED);
                } else if (type == ENTRY_MODIFY){
                    notifyListeners(path, FileEvent.MODIFIED);
                } else {
                    System.err.println("non-fatal unknown WatchEvent type");
                }
            }
        }

        private void notifyListeners(Path path, FileEvent e) {
            for(FileChangeListener listener : listeners) {
                listener.filesystemChanged(path, e);
            }
        }
    }
}
