package net.vanderkast.fs4r.lock;

import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public interface ReadWritePathLock {
    ReadWriteLock onPath(Path path);

    Lock forRead(Path path);

    Lock forWrite(Path path);
}
