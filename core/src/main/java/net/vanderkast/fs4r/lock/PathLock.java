package net.vanderkast.fs4r.lock;

import java.nio.file.Path;
import java.util.concurrent.locks.Lock;

@FunctionalInterface
public interface PathLock {
    Lock on(Path path);
}
