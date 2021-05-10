package net.vanderkast.fs4r.domain;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface Load {
    InputStream load(Path path) throws IOException;
}
