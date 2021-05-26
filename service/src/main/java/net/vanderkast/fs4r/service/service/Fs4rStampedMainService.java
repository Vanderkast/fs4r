package net.vanderkast.fs4r.service.service;

import net.vanderkast.fs4r.dto.MoveDto;
import net.vanderkast.fs4r.dto.WriteDto;
import net.vanderkast.fs4r.service.model.FileWalk;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;

public interface Fs4rStampedMainService extends Fs4rMainService {
    Stream<FileWalk> walkDir(Path dir, UUID stamp) throws IOException;

    void download(Path virtual, HttpServletResponse response, UUID stamp) throws IOException;

    void move(MoveDto moveDto, UUID stamp) throws IOException;

    void delete(Path path, UUID stamp) throws IOException;

    String read(Path path, UUID stamp) throws IOException;

    void upload(WriteDto writeDto, UUID stamp) throws IOException;
}
