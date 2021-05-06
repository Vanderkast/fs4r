package com.vanderkast.fs4r.simple;

import com.vanderkast.fs4r.domain.Delete;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JustDelete implements Delete {
    @Override
    public void delete(Path path) throws IOException {
        Files.delete(path);
    }
}
