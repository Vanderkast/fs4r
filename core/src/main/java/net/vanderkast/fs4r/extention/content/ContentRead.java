package net.vanderkast.fs4r.extention.content;

import java.io.IOException;
import java.nio.file.Path;

public interface ContentRead {
    String read(Path path) throws IOException;
}
