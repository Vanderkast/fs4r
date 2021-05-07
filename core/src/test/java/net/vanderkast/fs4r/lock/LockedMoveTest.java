package net.vanderkast.fs4r.lock;

import net.vanderkast.fs4r.domain.Move;
import net.vanderkast.fs4r.dto.MoveDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class LockedMoveTest {
    private final Logger logger = mock(Logger.class);
    private final LockedMove.MoveLock lock = mock(LockedMove.MoveLock.class);
    private final Move move = mock(Move.class);
    private final LockedMove lockedMove = new LockedMove(logger, lock, move);

    @Test
    void unlockOnExceptionThrown() throws IOException, InterruptedException {
        // given
        Move.Dto dto = new MoveDto(mock(Path.class), mock(Path.class), true, false);
        doThrow(RuntimeException.class).when(move).move(any());
        PathLock lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forMove(dto.getOrigin(), dto.getTarget(), dto.isCopy());

        // when
        assertThrows(RuntimeException.class,
                () -> lockedMove.move(dto));

        // then
        verify(lock).lockInterruptibly();
        verify(lock).unlock();
    }

    @Test
    void unlockOnInterruptedExceptionThrown() throws InterruptedException, IOException {
        // given
        Move.Dto dto = new MoveDto(mock(Path.class), mock(Path.class), false, false);
        PathLock lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forMove(dto.getOrigin(), dto.getTarget(), dto.isCopy());
        doThrow(InterruptedException.class).when(lock).lockInterruptibly();

        // when
        lockedMove.move(dto);

        // then
        verify(lock).lockInterruptibly();
        verify(lock).unlock();
    }

    @Timeout(1)
    @Test
    void unlockOnThreadInterrupted() throws IOException, InterruptedException {
        // given
        var dto = new MoveDto(mock(Path.class), mock(Path.class), false, true);
        PathLock lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forMove(dto.getOrigin(), dto.getTarget(), dto.isCopy());
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
        verify(logger).logInterrupted(lockedMove.getClass(), dto.getOrigin());
        verify(lock).unlock();
    }

    @Test
    void name() throws IOException, InterruptedException {
        // given
        var dto = new MoveDto(mock(Path.class), mock(Path.class), false, true);
        var lock = mock(PathLock.class);
        doReturn(lock).when(this.lock).forMove(dto.getOrigin(), dto.getTarget(), dto.isCopy());

        // when
        lockedMove.move(dto);

        // then
        verify(lock).lockInterruptibly();
        verify(move).move(dto);
        verify(lock).unlock();
    }
}
