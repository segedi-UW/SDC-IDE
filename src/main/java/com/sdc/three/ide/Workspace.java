package com.sdc.three.ide;

import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.sdc.three.ide.FileEvent.MODIFIED;
import static java.nio.file.FileVisitResult.CONTINUE;

public class Workspace implements Filesystem, FileChangeListener {

    private final Path dir;
    private final WatchThreadPool watchPool;
    private final ObservableMap<Path, FileEvent> modifiedFiles = FXCollections.synchronizedObservableMap(FXCollections.observableHashMap());
    private final TreeItem<Path> root;
    private final LinkedList<FileChangeListener> listeners = new LinkedList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    public Workspace(File dir) throws InvalidFileException, IOException {
        super();
        validateWorkspace(dir);
        this.dir = dir.toPath().normalize();
        watchPool = new WatchThreadPool(this);
        root = new TreeItem<>(this.dir);
        parseWorkspace();
    }

    @Override
    public void save(Path path, String string) throws IOException {
        Files.writeString(path, string, StandardOpenOption.TRUNCATE_EXISTING);
        modifiedFiles.remove(path);
    }

    @Override
    public ReadOnlyMapWrapper<Path, FileEvent> getModifiedFiles() {
        return new ReadOnlyMapWrapper<>(modifiedFiles);
    }

    @Override
    public void filesystemChanged(Path path, FileEvent event) {
        if (event == MODIFIED) {
            modifiedFiles.put(path, event);
        } else {
            TreeItem<Path> item = getClosestItem(path);
            switch(event) {
                case ADDED:
                    lock.writeLock().lock();
                    Path added = addPathTo(item, path);
                    modifiedFiles.put(added, event);
                    lock.writeLock().unlock();
                    break;
                case REMOVED:
                    modifiedFiles.put(path, event);
                    try {
                        lock.writeLock().lock();
                        TreeItem<Path> toRemove = getItem(path);
                        TreeItem<Path> parent = toRemove.getParent();
                        if (!parent.getChildren().remove(toRemove)) {
                            System.err.println("Failed to remove" + item.getValue());
                        }
                    } catch (NoSuchElementException e) {
                        System.err.println("Failed to remove" + item.getValue());
                    }
                    lock.writeLock().unlock();
                    break;
            }
        }
        if (!listeners.isEmpty()) {
            for (FileChangeListener listener : listeners) {
                listener.filesystemChanged(path, event);
            }
        }
    }

    public List<FileChangeListener> getListeners() {
        return listeners;
    }

    public File getDirectory() {
        return dir.toFile();
    }

    public TreeItem<Path> getRoot() {
        return root;
    }

    /**
     * Returns the TreeItem of a given Path, or if it is not
     * contained in the Tree, then it returns the closest item
     * it can. This is synchronized with the ReadWriteLock for
     * thread safe use
     * @param path The path to get the item of
     * @return The closest item, at the minimum the root of the tree
     */
    public TreeItem<Path> getClosestItem(Path path) {
        Path search = dir.toAbsolutePath().relativize(path.toAbsolutePath());
        TreeItem<Path> item = root;
        for (int i = 0; i < search.getNameCount(); i++) {
            Path name = search.getName(i);
            if (name.toString().isEmpty())
                return item;
            lock.readLock().lock();
            for (TreeItem<Path> child : item.getChildren()) {
                Path childName = child.getValue().getFileName();
                if (childName.equals(name)) {
                    item = child;
                    break;
                }
            }
            lock.readLock().unlock();
        }
        return item;
    }

    /**
     * Similar to getClosestItem but throws an exception on not finding an item
     * @param path the path to retrieve the item of
     * @return the TreeItem of the given path
     * @throws NoSuchElementException when the path is not contained in the tree
     */
    public TreeItem<Path> getItem(Path path) {
        TreeItem<Path> item = getClosestItem(path);
        if (!item.getValue().equals(path)) throw new NoSuchElementException();
        return item;
    }

    private Path addPathTo(TreeItem<Path> item, Path path) {
        if (item.getValue().equals(path)) {
            System.err.println("Path already exists in tree: " + path);
            return path;
        }
        Path toAdd = item.getValue().relativize(path);
        // lock should be held before entering here!
        if (!lock.writeLock().isHeldByCurrentThread()) throw new IllegalStateException("Current thread does not hold the writeLock!");
        for(Path p : toAdd) {
            TreeItem<Path> next = new TreeItem<>(item.getValue().resolve(p));
            item.getChildren().add(next);
            item = next;
        }
        return item.getValue();
    }

    private void validateWorkspace(File workspace) throws InvalidFileException {
        if (workspace == null) throw new NullPointerException("Workspace cannot be null");
        else if (!workspace.isDirectory()) throw new InvalidFileException("Provided file is not a directory");
    }

    private void parseWorkspace() throws IOException {
        WorkspaceVisitor visitor = new WorkspaceVisitor(root);
        visitor.buildTree();
    }

    private class WorkspaceVisitor extends SimpleFileVisitor<Path> {

        final LinkedList<TreeItem<Path>> stack;
        final TreeItem<Path> root;
        TreeItem<Path> current;

        public WorkspaceVisitor(TreeItem<Path> root) {
            this.root = root;
            stack = new LinkedList<>();
        }

        public void buildTree() throws IOException {
            stack.clear();
            current = null;
            // hold lock while building the tree so that no changes are interfered with.
            lock.writeLock().lock();
            root.getChildren().clear(); // reset root if it has changes
            Files.walkFileTree(dir, this);
            lock.writeLock().unlock();
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            stack.push(current);
            current = current == null ? root : new TreeItem<>(dir);
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
            addToCurrent(new TreeItem<>(path));
            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            TreeItem<Path> old = current;
            current = stack.pop();
            if (current != null) {
                addToCurrent(old);
                // register the directory with the watch
                watchPool.register(dir);
                // qualified this statement!
            }
            return CONTINUE;
        }

        private void addToCurrent(TreeItem<Path> item) {
            current.getChildren().add(item);
        }
    }
}
