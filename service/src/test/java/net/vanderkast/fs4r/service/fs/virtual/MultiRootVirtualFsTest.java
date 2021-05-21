package net.vanderkast.fs4r.service.fs.virtual;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MultiRootVirtualFsTest {
    private static Path[] publish;
    private static Path ghostInDir;

    private static MultiRootVirtualFs fs;

    @BeforeAll
    static void beforeAll(@TempDir Path tmp) throws IOException {
        publish = new Path[]{
                Files.createDirectory(tmp.resolve("dir")),
                Files.createFile(tmp.resolve("file"))
        };
        ghostInDir = Files.createFile(publish[0].resolve("ghost"));
        fs = new MultiRootVirtualFs(List.of(publish));
    }

    @Test
    void protect() {
        assertTrue(fs.isProtected(Path.of("/")));
        assertTrue(fs.isProtected(Path.of("/dir")));
        assertTrue(fs.isProtected(Path.of("/file")));
        assertFalse(fs.isProtected(Path.of("/dir/ghost")));
    }

    @Test
    void rootNotMapped() {
        var root = fs.map(Path.of("/"));
        assertTrue(root.isEmpty());
    }

    @Test
    void filePublished() {
        var tmpFile = fs.map(Path.of("/file"));

        assertTrue(tmpFile.isPresent());
        assertEquals(publish[1], tmpFile.get());
    }

    @Test
    void dirGhostPublished() {
        var ghost = fs.map(Path.of("/dir/ghost"));

        assertTrue(ghost.isPresent());
        assertEquals(ghostInDir, ghost.get());
    }

    @Test
    void walkRoot() {
        var list = fs.walkVirtualRoot().collect(Collectors.toList());
        assertEquals(2, list.size());
        for (Path path : publish)
            assertTrue(list.contains(path));
    }

    @Test
    void unmappedFile(@TempDir Path tmp) {
        // when
        var result = fs.map(tmp);

        // then
        assertTrue(result.isEmpty());
    }
}
