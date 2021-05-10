package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Load;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class JustLoad implements Load {
    /**
     * Reads all bytes from given file. Just wraps {@link FileInputStream#FileInputStream(File)} method.
     *
     * @param path to read content
     * @return byte array content of given file
     * @throws IOException if I/O exception occurs or given path targets directory.
     */
    @Override
    public InputStream load(Path path) throws IOException {
        return new FileInputStream(path.toFile());
    }
}
