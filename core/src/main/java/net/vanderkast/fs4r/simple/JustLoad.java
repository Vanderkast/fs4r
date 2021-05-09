package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Load;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class JustLoad implements Load {
    /**
     * Reads all bytes from given file. Just wraps {@link Files}.readAllBytes() method.
     * @param path to read content
     * @return byte array content of given file
     * @throws IOException if IOException occurs or given path targets directory. Details in {@link Files}.readAllBytes()
     */
    @Override
    public Optional<byte[]> load(Path path) throws IOException {
        return Optional.of(Files.readAllBytes(path));
    }
}
