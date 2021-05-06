package com.vanderkast.fs4r.domain;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public interface Read {
    /**
     * Returns files that lies in passed directory. If passed Path targets non-directory file returns Optional#empty.
     * @param dir that files from to return
     * @return files that lies in passed dir
     * @throws IOException if an I/O error occurs
     */
    Optional<Stream<Path>> readContains(Path dir) throws IOException;
}
