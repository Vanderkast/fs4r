package net.vanderkast.fs4r.service.fs.stamp_lock;

import org.junit.jupiter.api.Test;

import static net.vanderkast.fs4r.service.fs.stamp_lock.ChronoLockHolder.deadlineAfter;
import static org.junit.jupiter.api.Assertions.*;

class ExclusiveLockHolderTest {

    /**
     * Given:
     * <p>{@link LockHolder<String>} <code>holder</code> with 1000s time and null {@link ExclusiveLockHolder#holder}.
     * That means <code>holder</code> should be inactive.</p>
     *
     * When:
     * <p><code>holder</code> is asked is some foolish stamp holds lock.</p>
     *
     * Then:
     * <p>No {@link NullPointerException} should not be caught.</p>
     */
    @Test
    void whenNullStampHolds_isHolder() {
        // given
        LockHolder<String> holder
                = new ExclusiveLockHolder<>(null, deadlineAfter(1_000_000));
        boolean nleCaught;
        assertTrue(holder.isInactive());

        // when
        try {
            holder.isOwner("foolish");
            nleCaught = false;
        } catch (NullPointerException e) {
            nleCaught = true;
        }

        // then
        assertFalse(nleCaught);
    }

    @Test
    void whenNullStampHolds_lock() {
        // given
        LockHolder<String> holder
                = new ExclusiveLockHolder<>(null, deadlineAfter(1_000_000));
        assertTrue(holder.isInactive());
        var candidate = "Michael Jordan";

        // when
        var current = holder.lock(candidate, deadlineAfter(10_000_000));

        // then
        assertNotNull(current);
        assertFalse(current.isInactive());
        assertTrue(current.isOwner(candidate));
    }

    @Test
    void onlyOwnerMayUpdateLock() {
        // given
        var owner = "Derrick Rose";
        ExclusiveLockHolder<String> holder
                = new ExclusiveLockHolder<>(owner, deadlineAfter(1000));
        assertFalse(holder.isInactive());
        var traitor = "fool";

        // when traitor locks
        var current = holder.lock(traitor, deadlineAfter(10_000_000));
        // then
        assertNotNull(current);
        assertSame(current, holder);

        // when
        var updated = holder.lock(owner, deadlineAfter(1_000_000));

        // then
        assertTrue(updated.isOwner(owner));
        assertFalse(holder.isInactive());
        assertTrue(((ChronoLockHolder<?>) updated).getDeadline() > holder.getDeadline() + 998_000);
    }

    @Test
    void onlyHolderMayUnlock() {
        // given
        var stamp = "Larry Bird";
        ExclusiveLockHolder<String> holder
                = new ExclusiveLockHolder<>(stamp, deadlineAfter(1_000_000));
        var traitor = "amogus";

        // when traitor unlocks
        var notHolderUnlocked = holder.unlock(traitor);
        // then
        assertSame(holder, notHolderUnlocked);
        assertFalse(holder.isInactive());
        assertFalse(holder.isOwner(traitor));

        // when holder unlocks
        var holderUnlocked = holder.unlock(stamp);
        // then
        assertTrue(holderUnlocked.isInactive());
        assertFalse(holderUnlocked.isOwner(stamp));
    }
}
