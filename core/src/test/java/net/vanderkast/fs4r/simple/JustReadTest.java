package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Read;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JustReadTest {
    private final Read read = new JustRead();

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
        var actual = read.readContains(tmp)
                .orElseThrow()
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

        // when
        var data = read.readContains(file);

        // then
        assertTrue(data.isEmpty());
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
        var stream = read.readContains(tmp).orElseThrow();

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

        // when
        var result = read.readContains(path);

        // then
        assertTrue(result.isEmpty());
    }
}
