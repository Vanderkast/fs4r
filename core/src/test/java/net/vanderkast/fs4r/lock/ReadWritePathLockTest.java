package net.vanderkast.fs4r.lock;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ReadWritePathLockTest {
    private final ReadWritePathLock locker = new ReadWritePathLock();

    @Test
    void sameLockOnEqualPaths() {
        // given
        var p1 = Path.of("the", "path");
        var p2 = Path.of("the", "path");
        assertEquals(p1, p2);

        // when
        var l1 = locker.onPath(p1);
        var l2 = locker.onPath(p2);

        // then
        assertSame(l1, l2);
    }

    @Test
    void differentLockOnDifferentPaths() {
        // given
        var p1 = mock(Path.class);
        var p2 = mock(Path.class);
        assertNotEquals(p1, p2);

        // when
        var l1 = locker.onPath(p1);
        var l2 = locker.onPath(p2);

        // then
        assertNotEquals(l1, l2);
    }

    @Test
    void readWriteLocksFromOneRwLockOnOnePath() {
        // given
        var path = mock(Path.class);
        var rwLock = locker.onPath(path);

        // when
        var readLock = locker.forRead(path);
        var writeLock = locker.forWrite(path);

        // then
        assertEquals(rwLock.readLock(), readLock);
        assertEquals(rwLock.writeLock(), writeLock);
    }
}
