package net.vanderkast.fs4r.concurrent;

import java.nio.file.Path;

public interface Logger {
    void logInterrupted(Class<?> who, Path resource);

    void logStart(Class<?> who, Path resource);

    void logDone(Class<?> who, Path resource);
}
