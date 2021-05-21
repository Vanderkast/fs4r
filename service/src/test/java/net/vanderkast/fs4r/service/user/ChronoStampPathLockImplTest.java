package net.vanderkast.fs4r.service.user;

import net.vanderkast.fs4r.service.fs.stamp_lock.ChronoStampPathLock;
import net.vanderkast.fs4r.service.fs.stamp_lock.ChronoStampPathLockImpl;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ChronoStampPathLockImplTest {
    private final ChronoStampPathLock<UUID> lock = new ChronoStampPathLockImpl<>();

    @Test
    void exclusiveness() {
        // given
        var first = UUID.randomUUID();
        var path = mock(Path.class);
        var firstHolds = lock.tryConcurrent(first, path);
        assertTrue(firstHolds);
        var second = UUID.randomUUID();

        // when
        var secondHolds = lock.tryConcurrent(second, path);
        // then
        assertFalse(secondHolds);
    }

    @Test
    void onlyHolderMayUnlock() {
        // given
        var first = UUID.randomUUID();
        var path = mock(Path.class);
        var firstHolds = lock.tryConcurrent(first, path);
        assertTrue(firstHolds);
        var second = UUID.randomUUID();

        // when
        lock.unlock(second, path);
        // then
        assertFalse(lock.tryConcurrent(second, path));
        assertTrue(lock.tryConcurrent(first, path));

        // when
        lock.unlock(first, path);

        // then
        assertTrue(lock.tryConcurrent(second, path));
    }

    @Test
    void holderUpdates() throws InterruptedException {
        // given
        var holder = UUID.randomUUID();
        var path = mock(Path.class);
        var spy = UUID.randomUUID();
        assertTrue(lock.tryExclusive(holder, path, 10));
        assertFalse(lock.tryConcurrent(spy, path));

        // when
        assertTrue(lock.tryExclusive(holder, path, 20));
        Thread.sleep(10);

        // then
        assertFalse(lock.tryConcurrent(spy, path));
        assertTrue(lock.tryConcurrent(holder, path));
    }

    @Test
    void expires() throws InterruptedException {
        // given
        var first = UUID.randomUUID();
        var path = mock(Path.class);
        var firstHolds = lock.tryExclusive(first, path, 10);
        assertTrue(firstHolds);
        var second = UUID.randomUUID();

        // when
        Thread.sleep(11);

        // then
        assertTrue(lock.tryConcurrent(second, path));
    }
}
