package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Write;
import net.vanderkast.fs4r.dto.impl.WriteDtoImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class JustWriteTest {
    private final Write write = new JustWrite();

    @Test
    void write(@TempDir Path tmp) throws IOException {
        // given
        var file = tmp.resolve("write_test");
        assertTrue(Files.notExists(file));
        var content = "write content";
        var model = new WriteDtoImpl(file,
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)),
                true, false);

        // when
        write.write(model);

        // then
        var actual = Files.readString(file);
        assertEquals(content, actual);
    }

    @Test
    void append(@TempDir Path tmp) throws IOException {
        // given
        var file = Files.writeString(tmp.resolve("append"), "con");
        assertTrue(Files.exists(file));
        var dto = new WriteDtoImpl(file,
                new ByteArrayInputStream("tent".getBytes(StandardCharsets.UTF_8)),
                false, true);

        // when
        write.write(dto);

        // then
        var read = Files.readString(file);
        assertEquals("content", read);
    }

    @Test
    void failOnExist(@TempDir Path tmp) throws IOException {
        // given
        var path = tmp.resolve("file");
        var exist = Files.createFile(path);
        assertTrue(Files.exists(exist));
        boolean faeCaught;

        // when
        try {
            write.write(new WriteDtoImpl(path, mock(InputStream.class), false, false));
            faeCaught = false;
        } catch (FileAlreadyExistsException ignored) {
            faeCaught = true;
        }

        // then
        assertTrue(faeCaught);
    }

    @Test
    void replaceAndOverwriteExisted(@TempDir Path tmp) throws IOException {
        // given
        var path = tmp.resolve("file");
        Files.writeString(path, "old");
        assertEquals("old", Files.readString(path));

        // when
        write.write(new WriteDtoImpl(path,
                new ByteArrayInputStream("new".getBytes(StandardCharsets.UTF_8)),
                true,
                true));

        // then
        assertEquals("new", Files.readString(path));
    }
}
