package net.vanderkast.fs4r.lock;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>Path lock mechanism based on {@link ReentrantReadWriteLock} and {@link ConcurrentMap}.</p>
 * <p>Locks only path endpoint. Example paths: A '/some', B '/some/path'; Locks on A and on B are handled separately.
 * Threads owning lock on A work in parallel with threads owning lock on B.</p>
 * <p>Delete and Move locks are mapped to write lock.
 * Read and Copy locks are mapped to read lock.
 * That means readers and copiers can run concurrently, but deletes and movers requires exclusive lock.</p>
 */
public class ReadWritePathLock implements LockedRead.ReadLock, LockedDelete.DeleteLock, LockedMove.MoveLock {
    private final ConcurrentMap<Path, ReadWriteLock> locks;

    public ReadWritePathLock() {
        locks = new ConcurrentHashMap<>();
    }

    ReadWritePathLock(ConcurrentMap<Path, ReadWriteLock> locks) {
        this.locks = new ConcurrentHashMap<>(locks);
    }

    /**
     * @param path resource to lock
     * @return read lock on passed path
     */
    @Override
    public PathLock forRead(Path path) {
        return new RwLock(locks.computeIfAbsent(path, (k) -> new ReentrantReadWriteLock()).readLock());
    }

    /**
     * @param path resource to lock
     * @return write lock on passed path
     */
    @Override
    public PathLock forDelete(Path path) {
        return new RwLock(locks.computeIfAbsent(path, (k) -> new ReentrantReadWriteLock()).writeLock());
    }

    /**
     * <p>No guarantees provided about target path.</p>
     * <p>If operation is Copy then returns read lock. If operation is Move then returns write lock.</p>
     * @param origin resource to lock
     * @param target doesn't play a role
     * @param copy if true returns read lock, write lock otherwise
     * @return Lock on origin path
     */
    @Override
    public PathLock forMove(Path origin, Path target, boolean copy) {
        var lock = locks.computeIfAbsent(origin, (k) -> new ReentrantReadWriteLock());
        return new RwLock(copy ? lock.readLock() : lock.writeLock());
    }

    static class RwLock implements PathLock {
        private final Lock lock;

        RwLock(Lock lock) {
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
}
