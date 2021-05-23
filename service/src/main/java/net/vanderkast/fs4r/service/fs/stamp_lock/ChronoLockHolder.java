package net.vanderkast.fs4r.service.fs.stamp_lock;

import lombok.Getter;

@Getter
abstract class ChronoLockHolder<T> implements LockHolder<T> {
    static final long FOREVER = -1;

    protected final long deadline;

    protected ChronoLockHolder(long deadline) {
        this.deadline = deadline;
    }

    @Override
    public boolean isInactive() {
        return deadline != FOREVER
                && deadline < System.currentTimeMillis();
    }

    public static long deadlineAfter(long millis) {
        return millis == FOREVER ? FOREVER : System.currentTimeMillis() + millis;
    }
}
