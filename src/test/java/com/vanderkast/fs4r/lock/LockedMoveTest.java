package com.vanderkast.fs4r.lock;

import com.vanderkast.fs4r.domain.Move;
import com.vanderkast.fs4r.dto.MoveDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

class LockedMoveTest {
    private final Logger logger = mock(Logger.class);
    private final LockedMove.MoveLock lock = mock(LockedMove.MoveLock.class);
    private final Move move = mock(Move.class);
    private final LockedMove lockedMove = new LockedMove(logger, lock, move);

    @Test
    void unlockOnExceptionThrown() throws IOException, InterruptedException {
        // given
        var origin = mock(Path.class);
        var target = mock(Path.class);
        doThrow(RuntimeException.class).when(move).move(any());
        PathLock lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forMove(origin, target);

        // when
        assertThrows(RuntimeException.class,
                () -> lockedMove.move(new MoveDto(origin, target, false, false)));

        // then
        verify(lock).lockInterruptibly();
        verify(lock).unlock();
    }

    @Test
    void unlockOnInterruptedExceptionThrown() throws InterruptedException, IOException {
        // given
        var origin = mock(Path.class);
        var target = mock(Path.class);
        PathLock lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forMove(origin, target);
        doThrow(InterruptedException.class).when(lock).lockInterruptibly();

        // when
        lockedMove.move(new MoveDto(origin, target, false, false));

        // then
        verify(lock).lockInterruptibly();
        verify(lock).unlock();
    }

    @Timeout(1)
    @Test
    void unlockOnThreadInterrupted() throws IOException, InterruptedException {
        // given
        var origin = mock(Path.class);
        var target = mock(Path.class);
        var dto = new MoveDto(origin, target, false, true);
        PathLock lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forMove(origin, target);
        doAnswer(i -> {
            Thread.sleep(50);
            return null;
        }).when(move).move(any());
        var worker = new Thread(() -> {
            try{
                lockedMove.move(dto);
            } catch (IOException e) {
                fail("Unexpected IOException caught!");
            }
        });

        // when
        worker.start();
        Thread.sleep(20);
        worker.interrupt();

        // then
        worker.join();
        verify(move).move(dto);
        verify(logger).logInterrupted(lockedMove.getClass(), origin);
        verify(lock).unlock();
    }

    @Test
    void name() throws IOException, InterruptedException {
        // given
        var origin = mock(Path.class);
        var target = mock(Path.class);
        var lock = mock(PathLock.class);
        var moveDto = new MoveDto(origin, target, true, true);
        doReturn(lock).when(this.lock).forMove(origin, target);

        // when
        lockedMove.move(moveDto);

        // then
        verify(lock).lockInterruptibly();
        verify(move).move(moveDto);
        verify(lock).unlock();
    }
}
