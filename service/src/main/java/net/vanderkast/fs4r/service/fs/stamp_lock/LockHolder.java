package net.vanderkast.fs4r.service.fs.stamp_lock;

/**
 * Declares structure for stamped lock holding.
 * <p><b>Contract:</b> behavior on <code>null</code> stamps is undefined!</p>
 *
 * @param <T> type of stamps used to identify holders
 */
interface LockHolder<T> {
    /**
     * @param stamp that is tested on lock holding.
     * @return true is stamp holds lock, false otherwise.
     */
    boolean isOwner(T stamp);

    /**
     * @return true if multiple lock holders allowed, false otherwise ~ lock is holt in exclusive way.
     */
    boolean isConcurrent();

    /**
     * @return if no stamps are holding lock, or deadline passed.
     */
    boolean isInactive();

    /**
     * <p><b>Contract:</b> Only owner may unlock active lock.</p>
     *
     * @param stamp that should no longer hold lock.
     * @return {@link LockHolder <T>} doesn't contain passed stamp as lock holder.
     */
    LockHolder<T> unlock(T stamp);

    /**
     * <p><b>Contract:</b> Only owner may update exclusive active lock with new deadline.</p>
     *
     * @param stamp    that is holding lock.
     * @param deadline when stamp will stop holding lock.
     * @return {@link LockHolder <T>} contains passed stamp as lock holder.
     */
    LockHolder<T> lock(T stamp, long deadline);
}
