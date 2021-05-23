package net.vanderkast.fs4r.service.fs.stamp_lock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChronoLockHolderTest {
    @Test
    void inactive() {
        // given
        var passedDeadline = System.currentTimeMillis() - 10;

        // when
        var holder = stub(passedDeadline);

        // then
        assertTrue(holder.isInactive());
    }

    @Test
    void foreverActive() {
        // when
        var holder = stub(ConcurrentLockHolder.FOREVER);

        // then
        assertFalse(holder.isInactive());
    }

    @Test
    void active() {
        // given
        var deadline = System.currentTimeMillis() + 100_000;

        // when
        var holder = stub(deadline);

        // then
        assertFalse(holder.isInactive());
    }

    ChronoLockHolder<?> stub(long deadline) {
        return new ChronoLockHolder<>(deadline) {
            @Override
            public boolean isOwner(Object stamp) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isConcurrent() {
                throw new UnsupportedOperationException();
            }

            @Override
            public LockHolder<Object> unlock(Object stamp) {
                throw new UnsupportedOperationException();
            }

            @Override
            public LockHolder<Object> lock(Object stamp, long deadline) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
