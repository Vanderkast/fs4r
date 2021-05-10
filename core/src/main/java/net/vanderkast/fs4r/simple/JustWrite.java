package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Write;
import net.vanderkast.fs4r.domain.dto.WriteDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

public class JustWrite implements Write {
    @Override
    public void write(WriteDto data) throws IOException {
        data.getInputStream()
                .transferTo(Files.newOutputStream(data.getPath(), getWriteOptions(data.isOverwrite())));
    }

    private OpenOption[] getWriteOptions(boolean overwrite) {
        if (overwrite)
            return new OpenOption[]{
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE};
        return new OpenOption[]{StandardOpenOption.APPEND};
    }
}
