package com.vanderkast.fs4r.lock;

import java.nio.file.Path;

public interface Logger {
    void logInterrupted(Class<?> who, Path resource);

    void logStart(Class<?> who, Path resource);

    void logDone(Class<?> who, Path resource);
}
