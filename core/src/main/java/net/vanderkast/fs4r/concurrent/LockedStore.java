package net.vanderkast.fs4r.concurrent;

import net.vanderkast.fs4r.domain.Store;

import java.io.IOException;
import java.nio.file.Path;

public class LockedStore implements Store {
    private final Logger logger;
    private final WriteLock writeLock;
    private final AppendLock appendLock;
    private final Store store;
    private final Writer writer;
    private final Appender appender;

    public LockedStore(Logger logger, WriteLock writeLock, AppendLock appendLock, Store store) {
        this.logger = logger;
        this.writeLock = writeLock;
        this.appendLock = appendLock;
        this.store = store;
        writer = new Writer();
        appender = new Appender();
    }

    @Override
    public void write(Path path, byte[] data) throws IOException {
        writer.write(path, data);
    }

    @Override
    public void append(Path path, byte[] data) throws IOException {
        appender.append(path, data);
    }

    class Writer {
        public void write(Path path, byte[] data) throws IOException {
            logger.logStart(LockedStore.Writer.class, path);
            var lock = writeLock.forWrite(path);
            try {
                lock.lockInterruptibly();
                store.write(path, data);
            } catch (InterruptedException e) {
                logger.logInterrupted(LockedStore.Writer.class, path);
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    class Appender {
        public void append(Path path, byte[] data) throws IOException {
            logger.logStart(LockedStore.Appender.class, path);
            var lock = appendLock.forAppend(path);
            try {
                lock.lockInterruptibly();
                store.append(path, data);
            } catch (InterruptedException e) {
                logger.logInterrupted(LockedStore.Appender.class, path);
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    @FunctionalInterface
    public interface WriteLock {
        PathLock forWrite(Path path);
    }

    @FunctionalInterface
    public interface AppendLock {
        PathLock forAppend(Path path);
    }
}
