package net.vanderkast.fs4r.domain;

import java.io.IOException;
import java.nio.file.Path;

public interface Store {
    void write(Path path, byte[] data) throws IOException;

    void append(Path path, byte[] data) throws IOException;
}
