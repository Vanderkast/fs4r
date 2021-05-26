package net.vanderkast.fs4r.service.fs.stamp_lock;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class ExclusiveLockHolder<T> extends ChronoLockHolder<T> {
    private final T owner;

    public ExclusiveLockHolder(@Nullable T owner, long deadline) {
        super(deadline);
        this.owner = owner;
    }

    @Override
    public boolean isOwner(T stamp) {
        return !isInactive() && owner.equals(stamp);
    }

    @Override
    public boolean isInactive() {
        return owner == null || super.isInactive();
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
        if (owner != null && owner.equals(candidate))
            return new ExclusiveLockHolder<>(candidate, deadline);
        if (isInactive())
            return new ExclusiveLockHolder<>(candidate, deadline);
        if (this.deadline < deadline)
            return new ExclusiveLockHolder<>(owner, deadline);
        return this;
    }
}
