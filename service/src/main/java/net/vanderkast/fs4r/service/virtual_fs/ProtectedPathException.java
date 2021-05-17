package net.vanderkast.fs4r.service.virtual_fs;

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
