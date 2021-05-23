package net.vanderkast.fs4r.service.fs.file_size;

import java.io.IOException;
import java.nio.file.Path;

public class FileSizeLimitExceededException extends IOException {
    public FileSizeLimitExceededException(Path path, long limit) {
        super(String.format("Target file %s exceeds size limit %s bytes", path, limit));
    }
}
