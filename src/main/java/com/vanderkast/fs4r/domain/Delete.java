package com.vanderkast.fs4r.domain;

import java.io.IOException;
import java.nio.file.Path;

public interface Delete {
    void delete(Path path) throws IOException;
}
