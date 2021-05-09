package net.vanderkast.fs4r.concurrent;

import java.util.Optional;

public interface Lockable<T, R> {
    Optional<R> interruptibly(T input) throws InterruptedException;

    Optional<R> tryProceed(T input);
}
