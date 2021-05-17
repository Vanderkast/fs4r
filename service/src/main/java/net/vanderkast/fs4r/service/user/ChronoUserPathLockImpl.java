package net.vanderkast.fs4r.service.user;

import lombok.Getter;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChronoUserPathLockImpl implements ChronoUserPathLock {
    private final ConcurrentMap<Path, LockHolder> pathLocks;

    public ChronoUserPathLockImpl() {
        pathLocks = new ConcurrentHashMap<>();
    }

    public ChronoUserPathLockImpl(Map<Path, LockHolder> pathLocks) {
        this.pathLocks = new ConcurrentHashMap<>(pathLocks);
    }

    @Override
    public boolean tryLock(User user, Path path, long forMillis) {
        var holder = pathLocks.get(path);
        if (holder == null || holder.isExpired()) {
            synchronized (this) {
                var dcHolder = pathLocks.get(path);
                if (dcHolder == null || dcHolder.isExpired()) {
                    pathLocks.put(path, new LockHolder(user, forMillis));
                    return true;
                }
            }
        }
        return user.equals(pathLocks.get(path).getUser());
    }

    @Override
    public boolean tryLock(User user, Path path) {
        return tryLock(user, path, LockHolder.NEVER);
    }

    @Override
    public void unlock(User user, Path path) {
        pathLocks.remove(path, LockHolder.of(user));
    }

    @Getter
    static class LockHolder {
        static final long NEVER = -1;

        private final User user;
        private final long deadline;

        LockHolder(User user, long forMillis) {
            this.user = user;
            this.deadline = forMillis == -1 ? NEVER : System.currentTimeMillis() + forMillis;
        }

        public static LockHolder of(User user) {
            return new LockHolder(user, -2);
        }

        boolean isExpired() {
            return deadline != NEVER && System.currentTimeMillis() > deadline;
        }

        @Override
        public int hashCode() {
            return user.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != LockHolder.class)
                return false;
            return user.equals(((LockHolder) obj).getUser());
        }
    }
}
