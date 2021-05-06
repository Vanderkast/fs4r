package com.vanderkast.fs4r.lock;

import com.vanderkast.fs4r.domain.Read;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class LockedRead  implements Read {
    private final Logger logger;
    private final ReadLock lock;
    private final Read read;

    public LockedRead(Logger logger, ReadLock lock, Read read) {
        this.logger = logger;
        this.lock = lock;
        this.read = read;
    }

    @Override
    public Optional<Stream<Path>> readContains(Path dir) throws IOException {
        logger.logStart(LockedRead.class, dir);
        var lock = this.lock.forRead(dir);
        try {
            lock.lockInterruptibly();
            return read.readContains(dir);
        } catch (InterruptedException e) {
            logger.logInterrupted(LockedRead.class, dir);
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
            logger.logDone(LockedRead.class, dir);
        }
        return Optional.empty();
    }

    @FunctionalInterface
    public interface ReadLock {
        PathLock forRead(Path path);
    }
}
