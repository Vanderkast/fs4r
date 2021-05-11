package net.vanderkast.fs4r.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;

@AllArgsConstructor
@Getter
public class FileWalk {
    private final String name;
    private final boolean dir;

    public static FileWalk ofPath(Path path) {
        return new FileWalk(
                path.getFileName().toString(),
                path.toFile().isDirectory());
    }
}
