package net.vanderkast.fs4r.concurrent.lock;

import net.vanderkast.fs4r.concurrent.PathLock;

import java.util.concurrent.locks.Lock;

public class PathLockImpl implements PathLock {
    private final Lock lock;

    public PathLockImpl(Lock lock) {
        this.lock = lock;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock.lockInterruptibly();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }
}
