package net.vanderkast.fs4r.service.service;

import net.vanderkast.fs4r.dto.MoveDto;
import net.vanderkast.fs4r.dto.WriteDto;
import net.vanderkast.fs4r.service.model.FileWalk;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface Fs4rMainService {
    Stream<FileWalk> walkDir(Path dir) throws IOException;

    void download(Path virtual, HttpServletResponse response) throws IOException;

    void move(MoveDto moveDto) throws IOException;

    void delete(Path path) throws IOException;

    String read(Path path) throws IOException;

    void upload(WriteDto writeDto) throws IOException;
}
