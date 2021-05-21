package net.vanderkast.fs4r.service.fs.stamp_lock;

import java.nio.file.Path;

public interface StampPathLock<T> {
    boolean tryConcurrent(T stamp, Path path);

    boolean tryExclusive(T stamp, Path path);

    void unlock(T stamp, Path path);
}
