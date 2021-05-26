package net.vanderkast.fs4r.service.service;

import net.vanderkast.fs4r.domain.concurrent.ConcurrentDelete;
import net.vanderkast.fs4r.domain.concurrent.ConcurrentMove;
import net.vanderkast.fs4r.domain.concurrent.ConcurrentWalk;
import net.vanderkast.fs4r.domain.concurrent.ConcurrentWrite;
import net.vanderkast.fs4r.dto.MoveDto;
import net.vanderkast.fs4r.dto.WriteDto;
import net.vanderkast.fs4r.extention.content.ConcurrentContentRead;
import net.vanderkast.fs4r.service.fs.attachment.AttachmentLoad;
import net.vanderkast.fs4r.service.fs.stamp_lock.ChronoStampPathLock;
import net.vanderkast.fs4r.service.model.FileWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;

import static net.vanderkast.fs4r.service.configuration.Profiles.CONCURRENT_SESSIONS;

@Service
@Profile(CONCURRENT_SESSIONS)
public class ConcurrentSessionsMainService implements Fs4rMainService, Fs4rStampedMainService { // todo tests
    private final ChronoStampPathLock<UUID> sessionLocks;
    private final ConcurrentWalk walk;
    private final ConcurrentMove move;
    private final ConcurrentDelete delete;
    private final ConcurrentContentRead contentRead;
    private final AttachmentLoad<HttpServletResponse> download;
    private final ConcurrentWrite write;

    @Autowired
    public ConcurrentSessionsMainService(ChronoStampPathLock<UUID> sessionLocks,
                                         @Qualifier("virtual") ConcurrentWalk walk,
                                         @Qualifier("virtual") ConcurrentMove move,
                                         @Qualifier("virtual") ConcurrentDelete delete,
                                         @Qualifier("virtual") ConcurrentContentRead contentRead,
                                         @Qualifier("virtual") AttachmentLoad<HttpServletResponse> download,
                                         @Qualifier("virtual") ConcurrentWrite write) {
        this.sessionLocks = sessionLocks;
        this.walk = walk;
        this.move = move;
        this.delete = delete;
        this.contentRead = contentRead;
        this.download = download;
        this.write = write;
    }

    @Override
    public Stream<FileWalk> walkDir(Path virtualDir) throws IOException {
        UUID stamp = UUID.randomUUID();
        try {
            return walkDir(virtualDir, stamp);
        } finally {
            sessionLocks.unlock(stamp, virtualDir);
        }
    }

    @Override
    public Stream<FileWalk> walkDir(Path virtualDir, UUID stamp) throws IOException {
        if (!sessionLocks.tryConcurrent(stamp, virtualDir, 100))
            throw new ResourceBusyException();
        return walk.tryNow(virtualDir)
                .orElseThrow(ResourceBusyException::new)
                .map(FileWalk::ofPath);
    }

    @Override
    public void download(Path virtual, HttpServletResponse response) throws IOException {
        UUID stamp = UUID.randomUUID();
        try {
            download(virtual, response, stamp);
        } finally {
            sessionLocks.unlock(stamp, virtual);
        }
    }

    @Override
    public void download(Path virtual, HttpServletResponse response, UUID stamp) throws IOException {
        if (!sessionLocks.tryConcurrent(stamp, virtual, 100)
                || !download.tryLoad(virtual, response))
            throw new ResourceBusyException();
    }

    @Override
    public void move(MoveDto dto) throws IOException {
        var stamp = UUID.randomUUID();
        var origin = dto.getOrigin();
        var target = dto.getTarget();
        var lockOriginFirst = origin.compareTo(target) <= 0;
        try {
            move(dto, stamp);
        } finally {
            if (lockOriginFirst) {
                sessionLocks.unlock(stamp, target);
                sessionLocks.unlock(stamp, origin);
            } else {
                sessionLocks.unlock(stamp, origin);
                sessionLocks.unlock(stamp, target);
            }
        }
    }

    @Override
    public void move(MoveDto dto, UUID stamp) throws IOException {
        var origin = dto.getOrigin();
        var target = dto.getTarget();
        if (origin.compareTo(target) <= 0) {
            if (!tryLock(stamp, origin, dto.isCopy())
                    || !sessionLocks.tryExclusive(stamp, target, 100)
                    || move.tryNow(dto).isEmpty())
                throw new ResourceBusyException();
        } else {
            if (!sessionLocks.tryExclusive(stamp, target, 100)
                    || !tryLock(stamp, origin, dto.isCopy())
                    || move.tryNow(dto).isEmpty())
                throw new ResourceBusyException();
        }
    }

    private boolean tryLock(UUID stamp, Path origin, boolean concurrent) {
        if (concurrent)
            return sessionLocks.tryConcurrent(stamp, origin, 100);
        return sessionLocks.tryExclusive(stamp, origin, 100);
    }

    @Override
    public void delete(Path virtual) throws IOException {
        var stamp = UUID.randomUUID();
        try {
            delete(virtual, stamp);
        } finally {
            sessionLocks.unlock(stamp, virtual);
        }
    }

    @Override
    public void delete(Path virtual, UUID stamp) throws IOException {
        if (!sessionLocks.tryExclusive(stamp, virtual, 100)
                || delete.tryNow(virtual).isEmpty())
            throw new ResourceBusyException();
    }

    @Override
    public String read(Path path) throws IOException {
        var stamp = UUID.randomUUID();
        try {
            return read(path, stamp);
        } finally {
            sessionLocks.unlock(stamp, path);
        }
    }

    @Override
    public String read(Path path, UUID stamp) throws IOException {
        if (!sessionLocks.tryConcurrent(stamp, path, 100))
            throw new ResourceBusyException();
        return contentRead.tryNow(path).orElseThrow(ResourceBusyException::new);
    }

    @Override
    public void upload(WriteDto dto) throws IOException {
        var stamp = UUID.randomUUID();
        try {
            upload(dto, stamp);
        } finally {
            sessionLocks.unlock(stamp, dto.getPath());
        }
    }

    @Override
    public void upload(WriteDto writeDto, UUID stamp) throws IOException {
        if (!sessionLocks.tryExclusive(stamp, writeDto.getPath(), 100)
                || write.tryNow(writeDto).isEmpty())
            throw new ResourceBusyException();
    }
}
