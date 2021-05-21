package net.vanderkast.fs4r.service.fs.stamp_lock;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChronoStampPathLockImpl<T extends Comparable<T>> implements ChronoStampPathLock<T> {
    private final ConcurrentMap<Path, LockHolder<T>> pathLocks;

    public ChronoStampPathLockImpl() {
        pathLocks = new ConcurrentHashMap<>();
    }

    public ChronoStampPathLockImpl(Map<Path, LockHolder<T>> pathLocks) {
        this.pathLocks = new ConcurrentHashMap<>(pathLocks);
    }

    @Override
    public boolean tryExclusive(T stamp, Path path, long forMillis) {
        var current = pathLocks.get(path);
        if (current == null || current.isExpired() || current.isHolder(stamp)) {
            synchronized (this) {
                var dcHolder = pathLocks.get(path);
                if (dcHolder == null || dcHolder.isExpired() || dcHolder.isHolder(stamp)) {
                    pathLocks.put(path, new ExclusiveHolder<>(stamp, forMillis));
                    return true;
                }
            }
        }
        return pathLocks.get(path).isHolder(stamp);
    }

    @Override
    public boolean tryConcurrent(T stamp, Path path) {
        return tryExclusive(stamp, path, ChronoLockHolder.FOREVER);
    }

    @Override
    public boolean tryExclusive(T stamp, Path path) {
        return tryExclusive(stamp, path, ChronoLockHolder.FOREVER);
    }

    @Override
    public void unlock(T stamp, Path path) {
        pathLocks.computeIfPresent(path, (k, v) -> {
            if(v.isHolder(stamp))
                return new
        });
    }

    interface LockHolder<T> {
        boolean isHolder(T stamp);

        boolean isExpired();
    }

    static abstract class ChronoLockHolder<T> implements LockHolder<T> {
        private static final long FOREVER = -1;
        private final long deadline;

        protected ChronoLockHolder(long deadline) {
            this.deadline = deadline;
        }

        @Override
        public boolean isExpired() {
            return deadline != FOREVER
                    && deadline < System.currentTimeMillis();
        }
    }

    static class ParallelHolders<T> extends ChronoLockHolder<T> {
        private final Set<T> holders;

        ParallelHolders(Set<T> holders, long deadline) {
            super(deadline);
            this.holders = holders;
        }

        @Override
        public boolean isHolder(T stamp) {
            return holders.contains(stamp);
        }
    }

    static class ExclusiveHolder<T> extends ChronoLockHolder<T> {
        private final T holder;

        ExclusiveHolder(T holder, long deadline) {
            super(deadline);
            this.holder = holder;
        }

        @Override
        public boolean isHolder(T stamp) {
            return this.holder.equals(stamp);
        }
    }
}
