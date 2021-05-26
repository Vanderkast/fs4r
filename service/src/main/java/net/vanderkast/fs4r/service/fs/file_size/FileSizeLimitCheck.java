package net.vanderkast.fs4r.service.fs.file_size;

import java.io.IOException;
import java.nio.file.Path;

public interface FileSizeLimitCheck {
    long getLimit();

    boolean check(Path path) throws IOException;

    default void verify(Path path) throws IOException {
        if (!check(path))
            throw new FileSizeLimitExceededException(path, getLimit());
    }
}
