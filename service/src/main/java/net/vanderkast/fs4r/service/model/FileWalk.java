package net.vanderkast.fs4r.service.model;


import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

@Getter
public class FileWalk {
    private final String name;
    private final boolean dir;

    public FileWalk(String name, boolean dir) {
        this.name = name;
        this.dir = dir;
    }

    public static FileWalk ofPath(Path path) {
        var file = path.toFile();
        try {
            var attributes = Files.readAttributes(path, BasicFileAttributes.class);
            return new FileInfo(
                    file.getName(),
                    file.isDirectory(),
                    attributes.size(),
                    attributes.creationTime().toMillis(),
                    attributes.lastModifiedTime().toMillis());
        } catch (IOException e) {
            return new FileWalk(file.getName(), file.isDirectory());
        }
    }

    @Getter
    public static class FileInfo extends FileWalk {
        private final long size; // bytes
        private final long created; // millis
        private final long lastTimeModified; // millis

        public FileInfo(String name, boolean dir, long size, long created, long lastTimeModified) {
            super(name, dir);
            this.size = size;
            this.created = created;
            this.lastTimeModified = lastTimeModified;
        }
    }
}
