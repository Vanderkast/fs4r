package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Write;
import net.vanderkast.fs4r.domain.dto.WriteDto;
import net.vanderkast.fs4r.dto.WriteDtoImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                true);

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
                false);

        // when
        write.write(dto);

        // then
        var read = Files.readString(file);
        assertEquals("content", read);
    }
}
