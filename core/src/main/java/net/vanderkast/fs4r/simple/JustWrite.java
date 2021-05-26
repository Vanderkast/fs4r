package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Write;
import net.vanderkast.fs4r.dto.WriteDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

public class JustWrite implements Write {
    @Override
    public void write(WriteDto data) throws IOException {
        if (data.isOverwrite()) {
            data.getInputStream().transferTo(Files.newOutputStream(data.getPath()));
        } else {
            data.getInputStream()
                    .transferTo(Files.newOutputStream(data.getPath(), StandardOpenOption.APPEND));
        }
    }
}
