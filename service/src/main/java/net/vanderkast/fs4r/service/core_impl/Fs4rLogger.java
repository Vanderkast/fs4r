package net.vanderkast.fs4r.service.core_impl;

import net.vanderkast.fs4r.concurrent.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Fs4rLogger implements Logger {
    private final org.slf4j.Logger logger;

    public Fs4rLogger(Class<?> forClass) {
        this(LoggerFactory.getLogger(forClass.getName()));
    }

    Fs4rLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void logInterrupted(Class<?> who, Path resource) {
        logger.warn("Was interrupted during handling {}", resource);
    }

    @Override
    public void logStart(Class<?> who, Path resource) {
        logger.info("Start locked handling resource {}", resource);
    }

    @Override
    public void logDone(Class<?> who, Path resource) {
        logger.info("Done locked handling resource {}", resource);
    }
}
