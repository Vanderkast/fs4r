package net.vanderkast.fs4r.lock;

public interface PathLock {
    void lockInterruptibly() throws InterruptedException;

    void unlock();
}
