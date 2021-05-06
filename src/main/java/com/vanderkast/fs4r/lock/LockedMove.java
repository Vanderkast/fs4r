package com.vanderkast.fs4r.lock;

import com.vanderkast.fs4r.domain.Move;

import java.io.IOException;
import java.nio.file.Path;

public class LockedMove implements Move {
    private final Logger logger;
    private final MoveLock lock;
    private final Move move;

    public LockedMove(Logger logger, MoveLock lock, Move move) {
        this.logger = logger;
        this.lock = lock;
        this.move = move;
    }

    @Override
    public void move(Move.Dto dto) throws IOException {
        logger.logStart(this.getClass(), dto.getOrigin());
        PathLock lock = this.lock.forMove(dto.getOrigin(), dto.getTarget());
        try {
            lock.lockInterruptibly();
            move.move(dto);
        } catch (InterruptedException e) {
            logger.logInterrupted(this.getClass(), dto.getOrigin());
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
            logger.logDone(this.getClass(), dto.getTarget());
        }
    }

    @FunctionalInterface
    public interface MoveLock {
        PathLock forMove(Path origin, Path target); // todo maybe add boolean copy argument?
    }
}
