package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Load;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        var loadedBytes = load.load(file);

        // then
        assertEquals(content, new String(loadedBytes));
    }
}
