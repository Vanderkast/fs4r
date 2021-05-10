package net.vanderkast.fs4r.lock;

import net.vanderkast.fs4r.domain.concurrent.ConcurrentIo;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

/**
 * <p>Encapsulates locking logic for Locked {@link ConcurrentIo} implementations.</p>
 *
 * @param <T> input type
 * @param <R> output type
 */
public abstract class LockedIo<T, R> implements ConcurrentIo<T, R> {

    @Override
    public R interruptibly(T data) throws IOException, InterruptedException {
        var lock = getLock(data);
        try {
            lock.lockInterruptibly();
            return handle(data);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<R> tryNow(T data) throws IOException {
        var lock = getLock(data);
        try {
            if (lock.tryLock())
                return Optional.of(handle(data));
            return Optional.empty();
        } finally {
            lock.unlock();
        }
    }

    protected abstract Lock getLock(T data);

    protected abstract R handle(T data) throws IOException;
}
