package net.vanderkast.fs4r.service.service;

import net.vanderkast.fs4r.domain.concurrent.ConcurrentDelete;
import net.vanderkast.fs4r.domain.concurrent.ConcurrentMove;
import net.vanderkast.fs4r.domain.concurrent.ConcurrentWalk;
import net.vanderkast.fs4r.domain.concurrent.VoidOk;
import net.vanderkast.fs4r.dto.impl.MoveDtoImpl;
import net.vanderkast.fs4r.service.fs.attachment.AttachmentLoad;
import net.vanderkast.fs4r.service.model.FileWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class Fs4rMainServiceImpl implements Fs4rMainService {
    private final ConcurrentWalk walk;
    private final ConcurrentMove move;
    private final ConcurrentDelete delete;
    private final AttachmentLoad<HttpServletResponse> load;

    @Autowired
    public Fs4rMainServiceImpl(@Qualifier("virtual") ConcurrentWalk walk,
                               @Qualifier("virtual") ConcurrentMove move,
                               @Qualifier("virtual") ConcurrentDelete delete,
                               @Qualifier("virtual") AttachmentLoad<HttpServletResponse> load) {
        this.walk = walk;
        this.move = move;
        this.delete = delete;
        this.load = load;
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
        var done = load.tryLoad(virtual, response);
        if (!done)
            done = load.tryLoad(virtual, response);
        if (!done)
            throw new ResourceBusyException();
    }

    @Override
    public void move(MoveDtoImpl moveDto) throws IOException {
        var moved = move.tryNow(moveDto);
        if (moved.isEmpty())
            moved = move.tryNow(moveDto);
        if (moved.isEmpty())
            throw new ResourceBusyException();
    }

    @Override
    public void delete(Path path) throws IOException {
        Optional<VoidOk> ok = delete.tryNow(path);
        if(ok.isEmpty())
            delete.tryNow(path).orElseThrow(ResourceBusyException::new);
    }
}
