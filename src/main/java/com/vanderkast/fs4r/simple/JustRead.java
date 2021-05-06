package com.vanderkast.fs4r.simple;

import com.vanderkast.fs4r.domain.Read;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class JustRead implements Read {
    /**
     * <p>Does not throws IOException if passed path does not exist. Returns Optional#empty instead.</p>
     * <p>Because Files#list method returns lazy populated Stream,
     * removed from listed directory file during listing will be not handled.
     * You can observe it in JustReadTest#removeFileDuringListing test.</p>
     * @param dir that files from to return
     * @return files that lies in passed dir
     * @throws IOException if I/O exception occurs
     */
    @Override
    public Optional<Stream<Path>> readContains(Path dir) throws IOException {
        if (Files.isDirectory(dir))
            return Optional.of(Files.list(dir));
        return Optional.empty();
    }
}
