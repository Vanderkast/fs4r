package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Walk;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class JustWalkTest {
    private final Walk walk = new JustWalk();

    @Test
    void readThatJustCreated(@TempDir Path tmp) throws IOException {
        // given
        Path[] paths = new Path[]{
                tmp.resolve("first"),
                tmp.resolve("second"),
                tmp.resolve("third")
        };
        for (Path p : paths) {
            Files.createFile(p);
            assertTrue(Files.exists(p));
        }

        // when
        var actual = walk.walkDir(tmp)
                .collect(Collectors.toList());

        // then
        assertEquals(3, actual.size());
        for (Path p : paths) {
            assertTrue(actual.contains(p));
        }
    }

    @Test
    void readNonDirectory(@TempDir Path tmp) throws IOException {
        // given
        var file = Files.createFile(tmp.resolve("file"));
        assertTrue(Files.exists(file));
        boolean notDirectory;

        // when
        try {
            walk.walkDir(file);
            notDirectory = false;
        } catch (NotDirectoryException e) {
            notDirectory = true;
        }

        // then
        assertTrue(notDirectory);
    }

    @Test
    void removeFileDuringListing(@TempDir Path tmp) throws IOException {
        // given
        Path[] files = new Path[]{
                Files.createFile(tmp.resolve("first")),
                Files.createFile(tmp.resolve("second"))
        };
        for (Path p : files)
            assertTrue(Files.exists(p));

        // when
        var stream = walk.walkDir(tmp);

        // then
        AtomicInteger handledCount = new AtomicInteger(0);
        Files.delete(files[0]);
        stream.forEach(file -> {
            handledCount.incrementAndGet();
            assertTrue(file.equals(files[0]) || file.equals(files[1]));
        });

        assertEquals(1, handledCount.get());
    }

    @Test
    void pathNotExist(@TempDir Path tmp) throws IOException {
        // given
        var path = tmp.resolve("some_file_that_not_exist");
        assertTrue(Files.notExists(path));
        boolean noSuchFileCaught;

        // when
        try {
            walk.walkDir(path);
            noSuchFileCaught = false;
        } catch (NoSuchFileException e) {
            noSuchFileCaught = true;
        }

        // then
        assertTrue(noSuchFileCaught);
    }
}
