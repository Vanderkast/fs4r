package net.vanderkast.fs4r.service.fs.stamp_lock;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class ExclusiveLockHolder<T> extends ChronoLockHolder<T> {
    private final T holder;

    public ExclusiveLockHolder(@Nullable T holder, long deadline) {
        super(deadline);
        this.holder = holder;
    }

    @Override
    public boolean isOwner(T stamp) {
        return !isInactive() && holder.equals(stamp);
    }

    @Override
    public boolean isInactive() {
        return holder == null || super.isInactive();
    }

    @Override
    public boolean isConcurrent() {
        return false;
    }

    @Override
    public LockHolder<T> unlock(T stamp) {
        return isOwner(stamp) ? new ExclusiveLockHolder<>(null, 0) : this;
    }

    @Override
    public LockHolder<T> lock(T candidate, long deadline) {
        if (holder != null && holder.equals(candidate))
            return new ExclusiveLockHolder<>(candidate, deadline);
        return isInactive()
                ? new ExclusiveLockHolder<>(candidate, deadline)
                : this;
    }
}
