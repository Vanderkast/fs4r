package net.vanderkast.fs4r.domain;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public interface Load {
    Optional<byte[]> load(Path path) throws IOException;
}
