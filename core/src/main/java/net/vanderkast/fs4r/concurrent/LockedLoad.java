package net.vanderkast.fs4r.concurrent;

import net.vanderkast.fs4r.domain.Load;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class LockedLoad implements Load {
    private final Logger logger;
    private final LoadLock loadLock;
    private final Load load;

    public LockedLoad(Logger logger, LoadLock loadLock, Load load) {
        this.logger = logger;
        this.loadLock = loadLock;
        this.load = load;
    }

    @Override
    public Optional<byte[]> load(Path path) throws IOException {
        logger.logStart(LockedLoad.class, path);
        var lock = loadLock.forLoad(path);
        try{
            lock.lockInterruptibly();
            return load.load(path);
        } catch (InterruptedException e) {
            logger.logInterrupted(LockedLoad.class, path);
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
            logger.logDone(LockedLoad.class, path);
        }
        return Optional.empty();
    }

    @FunctionalInterface
    public interface LoadLock {
        PathLock forLoad(Path path);
    }
}
