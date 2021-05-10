package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Load;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JustLoadTest {
    private final Load load = new JustLoad();

    @Test
    void loadDirectory(@TempDir Path tmp) {
        assertThrows(IOException.class, () -> load.load(tmp));
    }

    @Test
    void load(@TempDir Path tmp) throws IOException {
        // given
        var content = "Expected content";
        var file = tmp.resolve("test");
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));

        // when
        var inputStream = load.load(file);

        // then
        assertEquals(content, new String(inputStream.readAllBytes()));
    }

    @Test
    void readEmpty(@TempDir Path tmp) throws IOException {
        // given
        var file = Files.createFile(tmp.resolve("empty"));
        InputStream content;

        // when
        content = load.load(file);

        // then
        assertEquals(0, content.available());
    }

    @Test
    void loadNotExists(@TempDir Path tmp) {
        // given
        var file = tmp.resolve("some");
        assertTrue(Files.notExists(file));
        boolean caughtNoSuchFile;

        // when
        try {
            load.load(file);
            caughtNoSuchFile = false;
        } catch (NoSuchFileException | FileNotFoundException e) {
            caughtNoSuchFile = true;
        } catch (IOException e) {
            caughtNoSuchFile = false;
        }

        // then
        assertTrue(caughtNoSuchFile);
    }
}
