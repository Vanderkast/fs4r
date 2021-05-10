package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Walk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class JustWalk implements Walk {
    /**
     * <p>Because Files#list method returns lazy populated Stream,
     * removed from listed directory file during listing will be not handled.
     * You can observe it in JustReadTest#removeFileDuringListing test.</p>
     * @param dir that files from to return
     * @return files that lies in passed dir
     * @throws IOException if I/O exception occurs
     */
    @Override
    public Stream<Path> walkDir(Path dir) throws IOException {
        return Files.list(dir);
    }
}
