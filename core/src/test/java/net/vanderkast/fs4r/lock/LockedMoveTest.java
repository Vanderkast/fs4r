package net.vanderkast.fs4r.lock;

import net.vanderkast.fs4r.domain.Move;
import net.vanderkast.fs4r.dto.MoveDtoImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;

import static org.mockito.Mockito.*;

class LockedMoveTest {
    private final PathLock pathLock = mock(PathLock.class);
    private final Move io = mock(Move.class);
    private final LockedMove lockedMove = new LockedMove(io, pathLock);

    @Test
    void deadlockOnInterruptibly() throws IOException, InterruptedException {
        // given
        var first = Path.of("here", "i", "am");
        var second = Path.of("here", "i", "go");
        var firstLock = mock(Lock.class);
        var secondLock = mock(Lock.class);
        doReturn(firstLock).when(pathLock).on(first);
        doReturn(secondLock).when(pathLock).on(second);

        // when firstLock.lock -> secondLock.lock
        lockedMove.interruptibly(new MoveDtoImpl(first, second, false, false));

        // then secondLock.unlock -> firstLock.unlock
        var fsOrder = inOrder(firstLock, secondLock, secondLock, firstLock);
        fsOrder.verify(firstLock).lockInterruptibly();
        fsOrder.verify(secondLock).lockInterruptibly();
        fsOrder.verify(secondLock).unlock();
        fsOrder.verify(firstLock).unlock();

        // when secondLock.lock -> firstLock.lock
        lockedMove.interruptibly(new MoveDtoImpl(second, first, false, false));

        // then firstLock.unlock -> secondLock.unlock
        var sfOrder = inOrder(firstLock, secondLock, secondLock, firstLock);
        sfOrder.verify(firstLock).lockInterruptibly();
        sfOrder.verify(secondLock).lockInterruptibly();
        sfOrder.verify(secondLock).unlock();
        sfOrder.verify(firstLock).unlock();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void deadlockOnTryNow() throws IOException {
        // given
        var first = Path.of("here", "i", "am");
        var second = Path.of("here", "i", "go");
        var firstLock = mock(Lock.class);
        doReturn(true).when(firstLock).tryLock();
        var secondLock = mock(Lock.class);
        doReturn(true).when(secondLock).tryLock();
        doReturn(firstLock).when(pathLock).on(first);
        doReturn(secondLock).when(pathLock).on(second);

        // when firstLock.lock -> secondLock.lock
        lockedMove.tryNow(new MoveDtoImpl(first, second, false, false));

        // then secondLock.unlock -> firstLock.unlock
        var fsOrder = inOrder(firstLock, secondLock, secondLock, firstLock);
        fsOrder.verify(firstLock).tryLock();
        fsOrder.verify(secondLock).tryLock();
        fsOrder.verify(secondLock).unlock();
        fsOrder.verify(firstLock).unlock();

        // when secondLock.lock -> firstLock.lock
        lockedMove.tryNow(new MoveDtoImpl(second, first, false, false));

        // then firstLock.unlock -> secondLock.unlock
        var sfOrder = inOrder(firstLock, secondLock, secondLock, firstLock);
        sfOrder.verify(firstLock).tryLock();
        sfOrder.verify(secondLock).tryLock();
        sfOrder.verify(secondLock).unlock();
        sfOrder.verify(firstLock).unlock();
    }
}
