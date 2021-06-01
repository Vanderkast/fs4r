package net.vanderkast.fs4r.dto.impl;

import net.vanderkast.fs4r.dto.WriteDto;

import java.io.InputStream;
import java.nio.file.Path;

public class WriteDtoImpl implements WriteDto {
    private final Path path;
    private final InputStream inputStream;
    private final boolean overwrite;
    private final boolean replace;

    public WriteDtoImpl(Path path, InputStream inputStream, boolean overwrite, boolean replace) {
        this.path = path;
        this.inputStream = inputStream;
        this.overwrite = overwrite;
        this.replace = replace;
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

    @Override
    public boolean isReplace() {
        return replace;
    }
}
