package net.vanderkast.fs4r.service.fs.stamp_lock;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static net.vanderkast.fs4r.service.fs.stamp_lock.ChronoLockHolder.FOREVER;
import static net.vanderkast.fs4r.service.fs.stamp_lock.ChronoLockHolder.deadlineAfter;

public class ChronoStampPathLockImpl<T extends Comparable<T>> implements ChronoStampPathLock<T> {
    private final ConcurrentMap<Path, LockHolder<T>> pathLockOwners;

    public ChronoStampPathLockImpl() {
        pathLockOwners = new ConcurrentHashMap<>();
    }

    public ChronoStampPathLockImpl(Map<Path, LockHolder<T>> pathLockOwners) {
        this.pathLockOwners = new ConcurrentHashMap<>(pathLockOwners);
    }

    @Override
    public boolean tryConcurrent(T candidate, Path path, long forMillis) {
        LockHolder<T> current = pathLockOwners.get(path);
        if (current == null || current.isInactive() || current.isConcurrent() || current.isOwner(candidate)) {
            pathLockOwners.compute(path, (ignored, old) -> {
                if (old != null)
                    return old.lock(candidate, deadlineAfter(forMillis));
                return ConcurrentLockHolder.of(candidate, deadlineAfter(forMillis));
            });
        }
        return pathLockOwners.get(path).isOwner(candidate);
    }

    @Override
    public boolean tryExclusive(T candidate, Path path, long forMillis) {
        LockHolder<T> current = pathLockOwners.get(path);
        if (current == null || current.isInactive() || current.isOwner(candidate)) {
            pathLockOwners.compute(path, (ignored, old) -> {
                if (old != null)
                    return old.lock(candidate, deadlineAfter(forMillis));
                return new ExclusiveLockHolder<>(candidate, deadlineAfter(forMillis));
            });
        }
        return pathLockOwners.get(path).isOwner(candidate);
    }

    @Override
    public boolean tryConcurrent(T stamp, Path path) {
        return tryConcurrent(stamp, path, FOREVER);
    }

    @Override
    public boolean tryExclusive(T stamp, Path path) {
        return tryExclusive(stamp, path, FOREVER);
    }

    @Override
    public void unlock(T stamp, Path path) {
        pathLockOwners.computeIfPresent(path, (ignored, current) -> current.unlock(stamp));
    }

    @Override
    public boolean isOwning(T stamp, Path path) {
        var holder = pathLockOwners.get(path);
        return holder != null && holder.isOwner(stamp);
    }
}
