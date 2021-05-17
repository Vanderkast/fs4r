package net.vanderkast.fs4r.service.delivery.service;

import net.vanderkast.fs4r.dto.impl.MoveDtoImpl;
import net.vanderkast.fs4r.service.model.FileWalk;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface Fs4rMainService {
    Stream<FileWalk> walkDir(Path dir) throws IOException;

    void download(Path virtual, HttpServletResponse response) throws IOException;

    void move(MoveDtoImpl moveDto) throws IOException;

    void delete(Path path) throws IOException;
}
