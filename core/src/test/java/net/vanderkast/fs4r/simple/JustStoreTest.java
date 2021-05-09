package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Store;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JustStoreTest {
    private final Store store = new JustStore();

    @Test
    void write(@TempDir Path tmp) throws IOException {
        // given
        var file = tmp.resolve("write_test");
        assertTrue(Files.notExists(file));
        var content = "write content";

        // when
        store.write(file, content.getBytes(StandardCharsets.UTF_8));

        // then
        var actual = Files.readString(file);
        assertEquals(content, actual);
    }

    @Test
    void append(@TempDir Path tmp) throws IOException {
        // given
        var contentSplit = new String[]{"con", "tent"};
        var file = Files.write(tmp.resolve("append_test"), contentSplit[0].getBytes(StandardCharsets.UTF_8));

        // when
        store.append(file, contentSplit[1].getBytes(StandardCharsets.UTF_8));

        // then
        var actual = Files.readString(file);
        assertEquals(contentSplit[0] + contentSplit[1], actual);
    }
}
