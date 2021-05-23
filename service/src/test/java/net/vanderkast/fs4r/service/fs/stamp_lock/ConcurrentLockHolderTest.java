package net.vanderkast.fs4r.service.fs.stamp_lock;

import org.junit.jupiter.api.Test;

import static net.vanderkast.fs4r.service.fs.stamp_lock.ChronoLockHolder.FOREVER;
import static net.vanderkast.fs4r.service.fs.stamp_lock.ChronoLockHolder.deadlineAfter;
import static org.junit.jupiter.api.Assertions.*;

class ConcurrentLockHolderTest {
    @Test
    void multipleOwnersAllowedWithLockUpdate() {
        // given
        var first = "Samuel L. Jackson";
        ConcurrentLockHolder<String> holder = ConcurrentLockHolder.of(first, deadlineAfter(1000));
        assertTrue(holder.isOwner(first));

        // when
        var second = "Robin Hood";
        var updated = holder.lock(second, deadlineAfter(10000));

        // then
        assertTrue(updated.isOwner(second));
        assertTrue(updated.isOwner(first));
        assertTrue(((ChronoLockHolder<?>) updated).getDeadline() - holder.getDeadline() > 8000);
    }

    @Test
    void prolong() throws InterruptedException {
        // given
        var first = "Jerome Klapka Jerome";
        ConcurrentLockHolder<String> holder = ConcurrentLockHolder.of(first, deadlineAfter(5));
        assertTrue(holder.isOwner(first));
        var second = "Jim";

        // when
        var prolonged = holder.lock(second, deadlineAfter(10));
        Thread.sleep(6);

        // then
        assertTrue(prolonged.isOwner(second));
        assertTrue(prolonged.isOwner(first));
    }

    @Test
    void eternalLockAfterUpdate() {
        // given
        var first = "Moskpow";
        ConcurrentLockHolder<String> holder = ConcurrentLockHolder.of(first, deadlineAfter(5));
        assertTrue(holder.isOwner(first));
        var eternal = "Michigan";

        // when
        var updated = holder.lock(eternal, FOREVER);
        assertTrue(updated.isOwner(first));
        assertTrue(updated.isOwner(eternal));

        var _final = updated.unlock(eternal);

        // then
        assertEquals(FOREVER, ((ChronoLockHolder<?>) _final).getDeadline());
    }
}
