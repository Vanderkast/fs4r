package net.vanderkast.fs4r.domain;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface Walk {
    /**
     * Returns files that lies in passed directory.
     * @param dir that files from to return
     * @return files that lies in passed dir
     * @throws IOException if an I/O error occurs
     */
    Stream<Path> walkDir(Path dir) throws IOException;
}
