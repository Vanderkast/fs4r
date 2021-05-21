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

class SingleRootVirtualFsTest {
    private static Path publish;
    private static Path ghostInRoot;
    private static SingleRootVirtualFs fs;

    @BeforeAll
    static void beforeAll(@TempDir Path tmp) throws IOException {
        publish = tmp;
        ghostInRoot = Files.createFile(publish.resolve("ghost"));
        fs = new SingleRootVirtualFs(publish, Files::list);
    }

    @Test
    void protect() {
        assertTrue(fs.isProtected(Path.of("/")));
        assertFalse(fs.isProtected(Path.of("/ghost")));
    }

    @Test
    void mapRoot() {
        // given
        var virtualRoot = Path.of("/");

        // when
        var real = fs.map(virtualRoot);

        // then
        assertTrue(real.isPresent());
        assertEquals(publish, real.get());
    }

    @Test
    void mapGhost() {
        // given
        var ghost = Path.of("/ghost");

        // when
        var real = fs.map(ghost);

        // then
        assertTrue(real.isPresent());
        assertEquals(ghostInRoot, real.get());
    }

    @Test
    void walkRoot() throws IOException {
        // when
        List<Path> paths = fs.walkVirtualRoot().collect(Collectors.toList());

        // then
        assertEquals(1, paths.size());
        assertEquals(ghostInRoot, paths.get(0));
    }
}
