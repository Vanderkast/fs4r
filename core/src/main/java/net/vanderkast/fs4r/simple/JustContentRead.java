package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.extention.content.ContentRead;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JustContentRead implements ContentRead {
    @Override
    public String read(Path path) throws IOException {
        return Files.readString(path);
    }
}
