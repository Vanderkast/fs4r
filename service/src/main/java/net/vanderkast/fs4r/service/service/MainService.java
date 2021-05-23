package net.vanderkast.fs4r.service.service;

import net.vanderkast.fs4r.domain.concurrent.*;
import net.vanderkast.fs4r.dto.MoveDto;
import net.vanderkast.fs4r.dto.WriteDto;
import net.vanderkast.fs4r.extention.content.ConcurrentContentRead;
import net.vanderkast.fs4r.service.fs.attachment.AttachmentLoad;
import net.vanderkast.fs4r.service.model.FileWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static net.vanderkast.fs4r.service.configuration.Profiles.NOT_CONCURRENT_SESSIONS;

@Service
@Profile(NOT_CONCURRENT_SESSIONS)
public class MainService implements Fs4rMainService {
    private final ConcurrentWalk walk;
    private final ConcurrentMove move;
    private final ConcurrentDelete delete;
    private final ConcurrentWrite write;
    private final ConcurrentContentRead contentRead;
    private final AttachmentLoad<HttpServletResponse> download;

    @Autowired
    public MainService(@Qualifier("virtual") ConcurrentWalk walk,
                       @Qualifier("virtual") ConcurrentMove move,
                       @Qualifier("virtual") ConcurrentDelete delete,
                       @Qualifier("virtual") ConcurrentWrite write,
                       @Qualifier("virtual") ConcurrentContentRead contentRead,
                       @Qualifier("virtual") AttachmentLoad<HttpServletResponse> download) {
        this.walk = walk;
        this.move = move;
        this.delete = delete;
        this.write = write;
        this.contentRead = contentRead;
        this.download = download;
    }

    @Override
    public Stream<FileWalk> walkDir(Path virtualDir) throws IOException {
        Optional<Stream<Path>> content = walk.tryNow(virtualDir);
        if (content.isEmpty())
            content = walk.tryNow(virtualDir);
        return content.orElseThrow(ResourceBusyException::new).map(FileWalk::ofPath);
    }

    @Override
    public void download(Path virtual, HttpServletResponse response) throws IOException {
        var done = download.tryLoad(virtual, response);
        if (!done)
            done = download.tryLoad(virtual, response);
        if (!done)
            throw new ResourceBusyException();
    }

    @Override
    public void move(MoveDto dto) throws IOException {
        var moved = move.tryNow(dto);
        if (moved.isEmpty())
            moved = move.tryNow(dto);
        if (moved.isEmpty())
            throw new ResourceBusyException();
    }

    @Override
    public void delete(Path virtual) throws IOException {
        Optional<VoidOk> ok = delete.tryNow(virtual);
        if (ok.isEmpty())
            delete.tryNow(virtual).orElseThrow(ResourceBusyException::new);
    }

    @Override
    public String load(Path virtual) throws IOException {
        Optional<String> content = contentRead.tryNow(virtual);
        if (content.isEmpty())
            return contentRead.tryNow(virtual).orElseThrow(ResourceBusyException::new);
        return content.get();
    }

    @Override
    public void upload(WriteDto dto) throws IOException {
        Optional<VoidOk> done = write.tryNow(dto);
        if (done.isEmpty())
            write.tryNow(dto).orElseThrow(ResourceBusyException::new);
    }
}
