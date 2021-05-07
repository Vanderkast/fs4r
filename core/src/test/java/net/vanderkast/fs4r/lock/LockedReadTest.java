package net.vanderkast.fs4r.lock;

import net.vanderkast.fs4r.domain.Read;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LockedReadTest {
    private final Logger logger = mock(Logger.class);
    private final LockedRead.ReadLock lock = mock(LockedRead.ReadLock.class);
    private final Read read = mock(Read.class);
    private final LockedRead lockedRead = new LockedRead(logger, lock, read);

    @Test
    void unlockOnExceptionThrown() throws IOException, InterruptedException {
        // given
        var dir = mock(Path.class);
        doThrow(RuntimeException.class).when(read).readContains(dir);
        var lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forRead(dir);

        // when
        assertThrows(RuntimeException.class,
                () -> lockedRead.readContains(dir));

        // then
        verify(logger).logStart(LockedRead.class, dir);
        verify(lock).lockInterruptibly();
        verify(lock).unlock();
        verify(logger).logDone(LockedRead.class, dir);
    }

    @Test
    void unlockOnInterruptedException() throws InterruptedException, IOException {
        // given
        var dir = mock(Path.class);
        var lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forRead(dir);
        doThrow(InterruptedException.class).when(lock).lockInterruptibly();

        // when
        var result = lockedRead.readContains(dir);

        // then
        assertTrue(result.isEmpty());
        verify(logger).logStart(LockedRead.class, dir);
        verify(logger).logInterrupted(LockedRead.class, dir);
        verify(logger).logDone(LockedRead.class, dir);
        verify(lock).lockInterruptibly();
        verify(lock).unlock();
    }

    @Test
    void unlockOnThreadInterrupt() throws IOException, InterruptedException {
        // given
        var dir = mock(Path.class);
        PathLock lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forRead(dir);
        doAnswer(i -> {
            Thread.sleep(50);
            return null;
        }).when(read).readContains(dir);
        var reader = new Thread(() -> {
            try {
                var result = lockedRead.readContains(dir);
                // then
                assertTrue(result.isEmpty());
            } catch (IOException e) {
                fail("Unexpected IOException caught", e);
            }
        });

        // when
        reader.start();
        Thread.sleep(20);
        reader.interrupt();

        // then
        reader.join();
        verify(read).readContains(dir);
        verify(lock).lockInterruptibly();
        verify(lock).unlock();
        verify(logger).logInterrupted(LockedRead.class, dir);
    }

    @Test
    void unlockOnNormal() throws IOException, InterruptedException {
        // given
        var dir = mock(Path.class);
        var expectedResult = Optional.of(mock(Stream.class));
        doReturn(expectedResult).when(read).readContains(dir);
        var lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forRead(dir);

        // when
        var actual = lockedRead.readContains(dir);

        // then
        assertEquals(expectedResult, actual);
        verify(lock).lockInterruptibly();
        verify(lock).unlock();
    }
}
