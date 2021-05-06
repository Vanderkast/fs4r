package com.vanderkast.fs4r.simple;

import com.vanderkast.fs4r.domain.Delete;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JustDeleteTest {
    private final Delete delete = new JustDelete();

    @Test
    void exists(@TempDir Path tmp) throws IOException {
        // given
        Path path = Files.createTempFile(tmp, JustDeleteTest.class.getName(), "1");
        assertTrue(Files.exists(path));

        // when
        delete.delete(path);

        // then
        assertFalse(Files.exists(path));
    }

    @Test
    void notExists() {
        // given
        Path path = Path.of(JustDelete.class.getSimpleName() + 2);
        assertFalse(Files.exists(path));

        // when
        try {
            delete.delete(path);
            fail();
        } catch (IOException e) {
            // then
            // okay
        }
    }
}
