package com.sdc.three.ide;

import javafx.scene.control.TreeItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class WorkspaceTest {

    private final Path path = Path.of(".", "src", "Test", "com", "sdc", "three", "ide", "workspaceTests");
    private final Path tmp = Path.of(path.toString(), "tmp");

    private Workspace wk;

    @BeforeEach
    void initWorkspace() {
        try {
            File[] files = tmp.toFile().listFiles();
            if (files != null)
                Arrays.asList(files).forEach(file -> {
                    if (file.isFile() && !file.delete())
                        fail("failed to delete tmp files");
                });
            wk = new Workspace(path.toFile());
        } catch (IOException | InvalidFileException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void build() {
        File[] files = path.toFile().listFiles();
        assert files != null;
        final int fileN = files.length;
        assertEquals(fileN, wk.getRoot().getChildren().size());
        assertTrue(containsAll(wk.getRoot(), "A", "B.java", "dir", "tmp"));
        TreeItem<Path> dirItem = getName(wk.getRoot(), "dir");
        assertTrue(containsAll(dirItem, "C.java", "D.java", "dir2"));
        TreeItem<Path> dir2Item = getName(dirItem, "dir2");
        assertTrue(containsAll(dir2Item, "E.java", "F"));
    }

    @Test
    void getClosestItem() {
        TreeItem<Path> pathItem = wk.getClosestItem(path);
        assertEquals(pathItem, wk.getRoot());
        Path dir1 = Path.of(path.toString(), "dir").normalize();
        TreeItem<Path> dir1Item = wk.getClosestItem(dir1);
        TreeItem<Path> item1 = pathItem;
        for (TreeItem<Path> item : item1.getChildren()) {
            if (item.getValue().normalize().equals(dir1))
                item1 = item;
        }
        assertEquals(item1, dir1Item);

        Path dir2 = Path.of(dir1.toString(), "dir2");
        TreeItem<Path> dir2Item = wk.getClosestItem(dir2);
        TreeItem<Path> item2 = item1;
        for (TreeItem<Path> item : item2.getChildren()) {
            if (item.getValue().normalize().equals(dir2))
                item2 = item;
        }
        assertEquals(item2, dir2Item);
    }

    @Test
    void fileAdd() {
        File g = new File(tmp.toFile(), "G");
        File h = new File(tmp.toFile(), "H");
        Waiter.waitOnRun(wk, e -> wk.getFilesToSave().size() < 2, () ->
            assertDoesNotThrow(() -> {
                assertTrue(g.createNewFile());
                assertTrue(h.createNewFile());
            })
        );
        Waiter.waitOn(wk, e-> !wk.getClosestItem(g.toPath().toAbsolutePath()).getValue().equals(g.toPath().normalize()));
        TreeItem<Path> addedG = wk.getClosestItem(g.toPath().toAbsolutePath());
        TreeItem<Path> addedH = wk.getClosestItem(h.toPath().toAbsolutePath());
        Path pg = addedG.getValue();
        Path ph = addedH.getValue();
        Path rg = path.relativize(pg);
        Path rh = path.relativize(ph);
        assertEquals(Path.of("tmp","G"), rg);
        assertEquals(Path.of("tmp","H"), rh);
    }

    @Test
    void fileRemove() {
        File g = new File(tmp.toFile(), "G");
        File h = new File(tmp.toFile(), "H");
        // wait for the closest item to be the item
        Waiter.waitOnRun(wk, e -> !wk.getClosestItem(h.toPath()).getValue().equals(h.toPath().normalize()), () -> {
            try {
                assertTrue(g.createNewFile());
                assertTrue(h.createNewFile());
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
        // wait until the closest item is no longer the item
        Waiter.waitOnRun(wk, e -> wk.getClosestItem(h.toPath()).getValue().equals(h.toPath().normalize()), () -> {
            assertTrue(g.delete());
            assertTrue(h.delete());
        });
        Waiter.waitOn(wk, e -> wk.getClosestItem(h.toPath()).getValue().equals(h.toPath().normalize()));
        // check if the tree still contains the items
        assertThrows(NoSuchElementException.class, () -> wk.getItem(g.toPath()));
        assertThrows(NoSuchElementException.class, () -> wk.getItem(h.toPath()));
    }

    @Test
    void fileModified() {
        Path file = Path.of(path.toString(), "dir", "dir2", "F");
        Waiter.waitOnRun(wk, (e) -> wk.getFilesToSave().size() < 1, () ->
            assertDoesNotThrow(() -> Files.writeString(file, "Testing Modified!", StandardOpenOption.TRUNCATE_EXISTING)));
        assertEquals(1, wk.getFilesToSave().size());
    }

    private boolean containsAll(TreeItem<Path> root, String ... names) {
        final HashMap<String, Void> toFind = new HashMap<>(names.length * 2 + 1);
        for(String name : names) {
            toFind.put(name, null);
        }
        for (TreeItem<Path> item : root.getChildren()) {
            String name = item.getValue().toFile().getName();
            toFind.remove(name);
        }
        return toFind.size() == 0;
    }

    private TreeItem<Path> getName(TreeItem<Path> item, String name) {
        for(TreeItem<Path> child : item.getChildren()) {
            if (child.getValue().toFile().getName().equals(name))
                return child;
        }
        throw new NoSuchElementException(String.format("The list %s did not contain %s", item.getChildren().toString(), name));
    }

    private static class Waiter implements FileChangeListener {

        private final int maxTimeout;
        private final Predicate<Void> isWaiting;
        private final long start = System.currentTimeMillis();
        private Runnable runnable;
        private boolean ran = false;
        private final Workspace wk;

        private Waiter(Workspace wk, Predicate<Void> isWaiting, int maxTimeout) {
            this.wk = wk;
            this.maxTimeout = maxTimeout;
            this.isWaiting = isWaiting;
        }

        private static void waitOn(Workspace wk, Predicate<Void> isWaiting) {
            Waiter waiter = new Waiter(wk, isWaiting, 1000);
            wk.getListeners().add(waiter);
            waiter.startWait(false);
        }

        private static void waitOnRun(Workspace wk, Predicate<Void> isWaiting, Runnable runnable) {
            Waiter waiter = new Waiter(wk, isWaiting, 1000);
            waiter.setRunnable(runnable);
            wk.getListeners().add(waiter);
            waiter.startWait(false);
        }

        private void setRunnable(Runnable runnable) {
            this.runnable = runnable;
        }

        private synchronized void startWait(boolean isNotify) {
            // has the objects monitor now - start the runnable if it has not been run yet
            if (!ran && runnable != null) {
                runnable.run();
                ran = true;
            }
            if (isNotify) {
                notifyAll();
            } else {
                try {
                    final long diff = System.currentTimeMillis() - start;
                    if (isWaiting.test(null)) {
                        final long waitFor = maxTimeout - diff;
                        if (diff < maxTimeout) {
                            wait(waitFor);
                        }
                        if (isWaiting.test(null) && diff >= maxTimeout) {
                            throw new IllegalStateException("No valid change found within " + maxTimeout + " milliseconds");
                        } else if (!isWaiting.test(null)) {
                            wk.getListeners().remove(this);
                        }
                    } else {
                        // no longer waiting
                        wk.getListeners().remove(this);
                    }
                } catch (InterruptedException e) {
                    System.err.println("Waiter Interrupted");
                }
            }
        }

        @Override
        public void filesystemChanged(Path path, FileEvent event) {
            startWait(true);
        }
    }
}