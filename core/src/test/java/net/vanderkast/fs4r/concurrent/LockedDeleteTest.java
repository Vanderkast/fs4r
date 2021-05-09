package net.vanderkast.fs4r.concurrent;

import net.vanderkast.fs4r.domain.Delete;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LockedDeleteTest {
    private final Logger logger = mock(Logger.class);
    private final LockedDelete.DeleteLock lock = mock(LockedDelete.DeleteLock.class);
    private final Delete delete = mock(Delete.class);
    private final LockedDelete lockedDelete = new LockedDelete(logger, lock, delete);

    @Test
    void unlockOnExceptionThrown() throws IOException {
        // given
        Path path = mock(Path.class);
        doThrow(RuntimeException.class).when(delete).delete(path);
        PathLock lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forDelete(path);

        // when
        assertThrows(
                RuntimeException.class,
                () -> lockedDelete.delete(path));

        // then
        verify(delete).delete(path);
        verify(lock).unlock();
    }

    @Test
    void unlockOnInterruptedException() throws IOException, InterruptedException {
        // given
        var path = mock(Path.class);
        PathLock lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forDelete(path);
        doThrow(InterruptedException.class).when(lock).lockInterruptibly();

        // when
        lockedDelete.delete(path);

        // then
        verify(logger).logInterrupted(lockedDelete.getClass(), path);
        verify(lock).unlock();
    }

    @Timeout(1)
    @Test
    void unlockOnThreadInterrupt() throws IOException, InterruptedException {
        // given
        var path = mock(Path.class);
        PathLock lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forDelete(path);
        doAnswer(i -> {
            Thread.sleep(50);
            return null;
        }).when(delete).delete(path);
        var worker = new Thread(() -> {
            try {
                lockedDelete.delete(path);
            } catch (IOException e) {
                throw new RuntimeException("Caught unexpected IOException!");
            }
        });

        // when
        worker.start();
        Thread.sleep(20);
        worker.interrupt();

        // then
        worker.join();
        verify(delete).delete(path);
        verify(logger).logInterrupted(lockedDelete.getClass(), path);
        verify(lock).unlock();
    }

    @Test
    void unlockOnNormal() throws IOException, InterruptedException {
        // given
        var path = mock(Path.class);
        var lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forDelete(path);

        // when
        lockedDelete.delete(path);

        // then
        verify(lock).lockInterruptibly();
        verify(delete).delete(path);
        verify(lock).unlock();
    }
}
