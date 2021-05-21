package net.vanderkast.fs4r.service.fs.virtual;

import java.io.IOException;
import java.nio.file.Path;

public class ProtectedPathException extends IOException {
    public ProtectedPathException() {
        super();
    }

    public ProtectedPathException(Path path) {
        super(path.toString() + " is protected and can't be modified.");
    }
}
