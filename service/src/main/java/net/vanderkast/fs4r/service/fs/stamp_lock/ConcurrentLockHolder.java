package net.vanderkast.fs4r.service.fs.stamp_lock;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>LockHolder with many lock owners support.</p>
 * <p>Its dummy implementation, that keeps one deadline for every owner.
 * That leads to situations, when one owner takes lock for 1 min and another owner for 5 min, result deadline is 5 min.</p>
 *
 * @param <T> stamp type representing lock owners
 */
class ConcurrentLockHolder<T> extends ChronoLockHolder<T> {
    private final Set<T> holders;

    public static <T> ConcurrentLockHolder<T> of(T stamp, long deadline) {
        return new ConcurrentLockHolder<>(Set.of(stamp), deadline);
    }

    public ConcurrentLockHolder(long deadline) {
        super(deadline);
        holders = new HashSet<>();
    }

    public ConcurrentLockHolder(Set<T> holders, long deadline) {
        super(deadline);
        this.holders = new HashSet<>(holders);
    }

    @Override
    public boolean isOwner(T stamp) {
        return holders.contains(stamp) && !isInactive();
    }

    @Override
    public boolean isConcurrent() {
        return true;
    }

    @Override
    public boolean isInactive() {
        return holders.size() <= 0 || super.isInactive();
    }

    @Override
    public LockHolder<T> unlock(T stamp) {
        if (holders.contains(stamp)) {
            ConcurrentLockHolder<T> holder = new ConcurrentLockHolder<>(holders, super.deadline);
            holder.holders.remove(stamp);
            return holder;
        }
        return this;
    }

    @Override
    public LockHolder<T> lock(T stamp, long deadline) {
        if (holders.contains(stamp) && super.deadline > deadline)
            return this;
        ConcurrentLockHolder<T> holder = new ConcurrentLockHolder<>(holders,
                deadline == FOREVER ? FOREVER :  Math.max(super.deadline, deadline));
        holder.holders.add(stamp);
        return holder;
    }
}
