package net.vanderkast.fs4r.service.fs.stamp_lock;

import java.nio.file.Path;

public interface ChronoStampPathLock<T> extends StampPathLock<T> {
    boolean tryConcurrent(T stamp, Path path, long forMillis);

    boolean tryExclusive(T stamp, Path path, long forMillis);
}
