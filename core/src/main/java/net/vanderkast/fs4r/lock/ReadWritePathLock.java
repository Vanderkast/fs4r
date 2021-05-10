package net.vanderkast.fs4r.lock;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWritePathLock {
    private final ConcurrentMap<Path, ReadWriteLock> locks;

    public ReadWritePathLock() {
        this.locks = new ConcurrentHashMap<>();
    }

    public ReadWritePathLock(Map<Path, ReadWriteLock> locks) {
        this.locks = new ConcurrentHashMap<>(locks);
    }

    ReadWriteLock onPath(Path path) {
        return locks.computeIfAbsent(path, p -> new ReentrantReadWriteLock());
    }

    public Lock forRead(Path path) {
        return onPath(path).readLock();
    }

    public Lock forWrite(Path path) {
        return onPath(path).writeLock();
    }
}
