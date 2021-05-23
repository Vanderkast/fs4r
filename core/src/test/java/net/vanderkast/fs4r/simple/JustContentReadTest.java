package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.extention.content.ContentRead;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JustContentReadTest {
    private final ContentRead contentRead = new JustContentRead();

    @Test
    void contentRead(@TempDir Path tmp) throws IOException {
        // given
        var expected = "TEST CONTENT";
        var testFile = Files.createFile(tmp.resolve("test.txt"));
        try (var stream = new FileOutputStream(testFile.toFile())) {
            stream.write(expected.getBytes(StandardCharsets.UTF_8));
        }

        // when
        var actual = contentRead.read(testFile);

        // then
        assertEquals(expected, actual);
    }
}
