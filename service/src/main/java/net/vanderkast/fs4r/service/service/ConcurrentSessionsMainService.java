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
public class ConcurrentSessionsMainService implements Fs4rMainService {
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
        var stamp = UUID.randomUUID();
        try {
            if (!sessionLocks.tryConcurrent(stamp, virtualDir, 100))
                throw new ResourceBusyException();
            return walk.tryNow(virtualDir)
                    .orElseThrow(ResourceBusyException::new)
                    .map(FileWalk::ofPath);
        } finally {
            sessionLocks.unlock(stamp, virtualDir);
        }
    }

    @Override
    public void download(Path virtual, HttpServletResponse response) throws IOException {
        var stamp = UUID.randomUUID();
        try {
            if (!sessionLocks.tryConcurrent(stamp, virtual, 100)
                    || !download.tryLoad(virtual, response))
                throw new ResourceBusyException();
        } finally {
            sessionLocks.unlock(stamp, virtual);
        }
    }

    @Override
    public void move(MoveDto dto) throws IOException { // todo test on deadlocks
        var stamp = UUID.randomUUID();
        var origin = dto.getOrigin();
        var target = dto.getTarget();
        var lockOriginFirst = origin.compareTo(target) <= 0;
        try {
            if (lockOriginFirst) {
                if (tryLock(stamp, origin, dto.isCopy())
                        || sessionLocks.tryConcurrent(stamp, target, 100)
                        || move.tryNow(dto).isEmpty())
                    throw new ResourceBusyException();
            } else {
                if (sessionLocks.tryConcurrent(stamp, target, 100)
                        || tryLock(stamp, origin, dto.isCopy())
                        || move.tryNow(dto).isEmpty())
                    throw new ResourceBusyException();
            }
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

    private boolean tryLock(UUID stamp, Path origin, boolean concurrent) {
        if (concurrent)
            return sessionLocks.tryConcurrent(stamp, origin, 100);
        return sessionLocks.tryExclusive(stamp, origin, 100);
    }

    @Override
    public void delete(Path virtual) throws IOException {
        var stamp = UUID.randomUUID();
        try {
            if (!sessionLocks.tryExclusive(stamp, virtual, 100)
                    || delete.tryNow(virtual).isEmpty())
                throw new ResourceBusyException();
        } finally {
            sessionLocks.unlock(stamp, virtual);
        }
    }

    @Override
    public String load(Path path) throws IOException {
        var stamp = UUID.randomUUID();
        try {
            if (!sessionLocks.tryConcurrent(stamp, path, 100))
                throw new ResourceBusyException();
            return contentRead.tryNow(path).orElseThrow(ResourceBusyException::new);
        } finally {
            sessionLocks.unlock(stamp, path);
        }
    }

    @Override
    public void upload(WriteDto writeDto) throws IOException {
        var stamp = UUID.randomUUID();
        try {
            if (!sessionLocks.tryConcurrent(stamp, writeDto.getPath(), 100)
                    || write.tryNow(writeDto).isEmpty())
                throw new ResourceBusyException();
        } finally {
            sessionLocks.unlock(stamp, writeDto.getPath());
        }
    }
}
