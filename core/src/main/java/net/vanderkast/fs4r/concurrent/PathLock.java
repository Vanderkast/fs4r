package net.vanderkast.fs4r.concurrent;

public interface PathLock {
    void lockInterruptibly() throws InterruptedException;

    void unlock();
}
