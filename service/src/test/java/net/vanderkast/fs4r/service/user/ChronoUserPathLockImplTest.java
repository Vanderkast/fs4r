package net.vanderkast.fs4r.service.user;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ChronoUserPathLockImplTest {
    private final ChronoUserPathLock lock = new ChronoUserPathLockImpl();

    @Test
    void exclusiveness() {
        // given
        var first = mock(User.class);
        var path = mock(Path.class);
        var firstHolds = lock.tryLock(first, path);
        assertTrue(firstHolds);
        var second = mock(User.class);

        // when
        var secondHolds = lock.tryLock(second, path);
        // then
        assertFalse(secondHolds);
    }

    @Test
    void onlyHolderMayUnlock() {
        // given
        var first = mock(User.class);
        var path = mock(Path.class);
        var firstHolds = lock.tryLock(first, path);
        assertTrue(firstHolds);
        var second = mock(User.class);

        // when
        lock.unlock(second, path);
        // then
        assertFalse(lock.tryLock(second, path));
        assertTrue(lock.tryLock(first, path));

        // when
        lock.unlock(first, path);

        // then
        assertTrue(lock.tryLock(second, path));
    }

    @Test
    void expires() throws InterruptedException {
        // given
        var first = mock(User.class);
        var path = mock(Path.class);
        var firstHolds = lock.tryLock(first, path, 10);
        assertTrue(firstHolds);
        var second = mock(User.class);

        // when
        Thread.sleep(11);

        // then
        assertTrue(lock.tryLock(second, path));
    }
}
