package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Store;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class JustStore implements Store {

    @Override
    public void write(Path path, byte[] data) throws IOException {
        Files.write(path, data);
    }

    @Override
    public void append(Path path, byte[] data) throws IOException {
        Files.write(path, data, StandardOpenOption.APPEND);
    }
}
