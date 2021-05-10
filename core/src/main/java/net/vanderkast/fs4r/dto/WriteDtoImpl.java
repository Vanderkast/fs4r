package net.vanderkast.fs4r.dto;

import java.io.InputStream;
import java.nio.file.Path;

public class WriteDtoImpl implements net.vanderkast.fs4r.domain.dto.WriteDto {
    private final Path path;
    private final InputStream   inputStream;
    private final boolean overwrite;

    public WriteDtoImpl(Path path, InputStream inputStream, boolean overwrite) {
        this.path = path;
        this.inputStream = inputStream;
        this.overwrite = overwrite;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public boolean isOverwrite() {
        return overwrite;
    }
}
