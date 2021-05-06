package com.vanderkast.fs4r.lock;

import com.vanderkast.fs4r.domain.Delete;

import java.io.IOException;
import java.nio.file.Path;

public class LockedDelete implements Delete {
    private final Logger logger;
    private final DeleteLock lock;
    private final Delete delete;

    public LockedDelete(Logger logger, DeleteLock lock, Delete delete) {
        this.logger = logger;
        this.lock = lock;
        this.delete = delete;
    }

    @Override
    public void delete(Path path) throws IOException {
        logger.logStart(this.getClass(), path);
        PathLock lock = this.lock.forDelete(path);
        try {
            lock.lockInterruptibly();
            delete.delete(path);
        } catch (InterruptedException e) {
            logger.logInterrupted(this.getClass(), path);
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
            logger.logDone(getClass(), path);
        }
    }

    @FunctionalInterface
    public interface DeleteLock {
        PathLock forDelete(Path path);
    }
}
