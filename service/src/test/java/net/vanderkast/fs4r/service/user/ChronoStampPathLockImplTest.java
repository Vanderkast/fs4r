package net.vanderkast.fs4r.service.user;

import net.vanderkast.fs4r.service.fs.stamp_lock.ChronoStampPathLock;
import net.vanderkast.fs4r.service.fs.stamp_lock.ChronoStampPathLockImpl;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ChronoStampPathLockImplTest { // todo rewrite for rw logic
    private final ChronoStampPathLock<UUID> lock = new ChronoStampPathLockImpl<>();

    @Test
    void exclusiveness() {
        // given
        var owner = UUID.randomUUID();
        var sharedResource = mock(Path.class);
        var traitor = UUID.randomUUID();

        // when
        assertTrue(lock.tryExclusive(owner, sharedResource, 4000));

        // then
        assertFalse(lock.tryConcurrent(traitor, sharedResource, 300));
        assertFalse(lock.tryExclusive(traitor, sharedResource, 300));
    }

    @Test
    void oneOwnerManyLocks() {
        // given
        var owner = UUID.randomUUID();
        var sharedResource = mock(Path.class);

        // when
        var firstLocked = lock.tryExclusive(owner, sharedResource, 300);
        var secondLocked = lock.tryConcurrent(owner, sharedResource, 400);

        // then
        assertTrue(firstLocked);
        assertTrue(secondLocked);
    }

    @Test
    void concurrent() {
        // given
        var firstConcurrent = UUID.randomUUID();
        var secondConcurrent = UUID.randomUUID();
        var exclusive = UUID.randomUUID();
        var sharedResource = mock(Path.class);

        // when
        assertTrue(lock.tryConcurrent(firstConcurrent, sharedResource));
        assertTrue(lock.tryConcurrent(secondConcurrent, sharedResource));

        // then
        assertFalse(lock.tryExclusive(exclusive, sharedResource, 200));
    }

    @Test
    void expire() throws InterruptedException {
        // given
        var sharedResource = mock(Path.class);
        var concurrent = UUID.randomUUID();

        // when
        assertTrue(lock.tryExclusive(UUID.randomUUID(), sharedResource, 10));
        assertFalse(lock.tryConcurrent(concurrent, sharedResource));
        Thread.sleep(11);

        // then
        assertTrue(lock.tryConcurrent(concurrent, sharedResource));
    }

    @Test
    void prolong() throws InterruptedException {
        // given
        var resource = mock(Path.class);
        var owner = UUID.randomUUID();

        // when
        assertTrue(lock.tryConcurrent(owner, resource, 5));
        assertFalse(lock.tryExclusive(UUID.randomUUID(), resource, 5));
        assertTrue(lock.tryConcurrent(owner, resource, 10));

        Thread.sleep(6);

        // then
        assertFalse(lock.tryExclusive(UUID.randomUUID(), resource));
    }
}
