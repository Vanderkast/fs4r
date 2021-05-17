package net.vanderkast.fs4r.lock;

import net.vanderkast.fs4r.domain.Move;
import net.vanderkast.fs4r.dto.impl.MoveDtoImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LockedMoveTest {
    private final PathLock readLock = mock(PathLock.class);
    private final PathLock editLock = mock(PathLock.class);
    private final Move io = mock(Move.class);
    private final LockedMove lockedMove = new LockedMove(io, readLock, editLock);

    @Test
    void noActionWhenOriginEqualsTarget() throws IOException, InterruptedException {
        // given
        var origin = Path.of("E", "qual");
        var target = Path.of("E", "qual");

        // when
        lockedMove.interruptibly(new MoveDtoImpl(origin, target, false, true));
        lockedMove.interruptibly(new MoveDtoImpl(origin, target, true, true));
        lockedMove.tryNow(new MoveDtoImpl(origin, target, true, true));
        lockedMove.tryNow(new MoveDtoImpl(origin, target, false, true));

        // then
        verifyNoInteractions(readLock);
        verifyNoInteractions(editLock);
    }

    @Test
    void readLockOnCopy() throws IOException, InterruptedException {
        // given
        var origin = Path.of("a", "b");
        var target = Path.of("a", "c");
        assertTrue(origin.compareTo(target) < 0);
        var dto = new MoveDtoImpl(origin, target, true, false);
        var originLock = mock(Lock.class);
        doReturn(originLock).when(readLock).on(origin);
        var targetLock = mock(Lock.class);
        doReturn(targetLock).when(editLock).on(target);

        // when
        lockedMove.interruptibly(dto);

        // then
        var fsOrder = inOrder(originLock, targetLock, targetLock, originLock);
        fsOrder.verify(originLock).lockInterruptibly();
        fsOrder.verify(targetLock).lockInterruptibly();
        fsOrder.verify(targetLock).unlock();
        fsOrder.verify(originLock).unlock();
    }

    @Test
    void editLockOnMove() throws InterruptedException, IOException {
        // given
        var origin = Path.of("a", "b");
        var target = Path.of("a", "c");
        assertTrue(origin.compareTo(target) < 0);
        var dto = new MoveDtoImpl(origin, target, false, false);
        var originLock = mock(Lock.class);
        doReturn(originLock).when(editLock).on(origin);
        var targetLock = mock(Lock.class);
        doReturn(targetLock).when(editLock).on(target);

        // when
        lockedMove.interruptibly(dto);

        // then
        var fsOrder = inOrder(originLock, targetLock, targetLock, originLock);
        fsOrder.verify(originLock).lockInterruptibly();
        fsOrder.verify(targetLock).lockInterruptibly();
        fsOrder.verify(targetLock).unlock();
        fsOrder.verify(originLock).unlock();
    }

    @Test
    void deadlockOnInterruptibly() throws IOException, InterruptedException {
        // given
        var first = Path.of("here", "i", "am");
        var second = Path.of("here", "i", "go");
        var firstLock = mock(Lock.class);
        var secondLock = mock(Lock.class);
        doReturn(firstLock).when(editLock).on(first);
        doReturn(secondLock).when(editLock).on(second);

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
        var origin = Path.of("here", "i", "am");
        var target = Path.of("here", "i", "go");
        var read = mock(Lock.class);
        doReturn(true).when(read).tryLock();
        var edit = mock(Lock.class);
        doReturn(true).when(edit).tryLock();
        doReturn(read).when(readLock).on(origin);
        doReturn(edit).when(editLock).on(target);

        // when firstLock.lock -> secondLock.lock
        lockedMove.tryNow(new MoveDtoImpl(origin, target, true, false));

        // then secondLock.unlock -> firstLock.unlock
        var fsOrder = inOrder(read, edit, edit, read);
        fsOrder.verify(read).tryLock();
        fsOrder.verify(edit).tryLock();
        fsOrder.verify(edit).unlock();
        fsOrder.verify(read).unlock();

        // for copy target -> origin
        doReturn(edit).when(editLock).on(origin);
        doReturn(read).when(readLock).on(target);

        // when secondLock.lock -> firstLock.lock
        lockedMove.tryNow(new MoveDtoImpl(target, origin, true, false));

        // then firstLock.unlock -> secondLock.unlock
        var sfOrder = inOrder(read, edit, edit, read);
        sfOrder.verify(read).tryLock();
        sfOrder.verify(edit).tryLock();
        sfOrder.verify(edit).unlock();
        sfOrder.verify(read).unlock();
    }

    @Test
    void integrationWithRwLock() throws IOException, InterruptedException {
        // given
        var origin = Path.of("same");
        var target = Path.of("same");
        var rwLock = new ReentrantReadWriteLock();
        doReturn(rwLock.readLock()).when(readLock).on(origin);
        doReturn(rwLock.writeLock()).when(editLock).on(target);

        // when
        lockedMove.interruptibly(new MoveDtoImpl(origin, target, true, true));

        // then
        verifyNoInteractions(readLock);
        verifyNoInteractions(editLock);
    }
}
