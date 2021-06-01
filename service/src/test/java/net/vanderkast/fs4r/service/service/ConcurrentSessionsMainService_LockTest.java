package net.vanderkast.fs4r.service.service;

import net.vanderkast.fs4r.domain.concurrent.*;
import net.vanderkast.fs4r.dto.impl.MoveDtoImpl;
import net.vanderkast.fs4r.dto.impl.WriteDtoImpl;
import net.vanderkast.fs4r.extention.content.ConcurrentContentRead;
import net.vanderkast.fs4r.service.fs.attachment.AttachmentLoad;
import net.vanderkast.fs4r.service.fs.stamp_lock.ChronoStampPathLock;
import net.vanderkast.fs4r.service.fs.stamp_lock.ChronoStampPathLockImpl;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ConcurrentSessionsMainService_LockTest {
    private final ConcurrentWalk walk = mock(ConcurrentWalk.class);
    private final ConcurrentMove move = mock(ConcurrentMove.class);
    private final ConcurrentDelete delete = mock(ConcurrentDelete.class);
    private final ConcurrentContentRead read = mock(ConcurrentContentRead.class);
    @SuppressWarnings("unchecked")
    private final AttachmentLoad<HttpServletResponse> download = mock(AttachmentLoad.class);
    private final ConcurrentWrite write = mock(ConcurrentWrite.class);

    private final ChronoStampPathLock<UUID> locks = new ChronoStampPathLockImpl<>();

    private final ConcurrentSessionsMainService service = new ConcurrentSessionsMainService(locks, walk, move, delete, read, download, write);

    @Test
    void exclusiveLockBlocksOperationsWithoutStamp() {
        // given
        var stamp = UUID.randomUUID();
        var path = mock(Path.class);

        // when
        assertTrue(locks.tryExclusive(stamp, path));

        // then
        assertThrows(ResourceBusyException.class, () -> service.walkDir(path));
        assertThrows(ResourceBusyException.class, () -> service.read(path));
        assertThrows(ResourceBusyException.class, () -> service.delete(path));
        assertThrows(ResourceBusyException.class, () -> service.move(new MoveDtoImpl(path, mock(Path.class), true, true)));
        assertThrows(ResourceBusyException.class, () -> service.move(new MoveDtoImpl(mock(Path.class), path, true, true)));
        assertThrows(ResourceBusyException.class, () -> service.upload(new WriteDtoImpl(path, mock(InputStream.class), false, false)));
        assertThrows(ResourceBusyException.class, () -> service.download(path, mock(HttpServletResponse.class)));
        verifyNoInteractions(walk, move, delete, read, download, write);
    }

    @Test
    void exclusiveLockBlocksOperationsWithDifferentStamp() {
        // given
        var stamp = UUID.randomUUID();
        var path = mock(Path.class);
        var traitor = UUID.randomUUID();

        // when
        assertTrue(locks.tryExclusive(stamp, path));

        // then
        assertThrows(ResourceBusyException.class, () -> service.walkDir(path, traitor));
        assertThrows(ResourceBusyException.class, () -> service.read(path, traitor));
        assertThrows(ResourceBusyException.class, () -> service.delete(path, traitor));
        assertThrows(ResourceBusyException.class, () -> service.move(new MoveDtoImpl(path, mock(Path.class), true, true), traitor));
        assertThrows(ResourceBusyException.class, () -> service.move(new MoveDtoImpl(mock(Path.class), path, true, true), traitor));
        assertThrows(ResourceBusyException.class, () -> service.upload(new WriteDtoImpl(path, mock(InputStream.class), false, false), traitor));
        assertThrows(ResourceBusyException.class, () -> service.download(path, mock(HttpServletResponse.class), traitor));
        verifyNoInteractions(walk, move, delete, read, download, write);
    }

    @Test
    void concurrentLockBlocksWriteOperationsWithoutStamp() {
        // given
        var stamp = UUID.randomUUID();
        var path = mock(Path.class);

        // when
        assertTrue(locks.tryConcurrent(stamp, path));

        // then
        assertThrows(ResourceBusyException.class, () -> service.delete(path));
        assertThrows(ResourceBusyException.class, () -> service.move(new MoveDtoImpl(path, mock(Path.class), false, true)));
        assertThrows(ResourceBusyException.class, () -> service.move(new MoveDtoImpl(mock(Path.class), path, false, true)));
        assertThrows(ResourceBusyException.class, () -> service.upload(new WriteDtoImpl(path, mock(InputStream.class), false, false)));
        verifyNoInteractions(walk, move, delete, read, download, write);
    }

    @Test
    void concurrentLockAllowsReadOperationsWithoutStamp() throws IOException {
        // given
        var stamp = UUID.randomUUID();
        var path = mock(Path.class);


        // when
        assertTrue(locks.tryConcurrent(stamp, path));

        // on -> then
        doReturn(Optional.of(mock(Stream.class))).when(walk).tryNow(path);
        service.walkDir(path);
        verify(walk).tryNow(path);

        var out = mock(HttpServletResponse.class);
        doReturn(true).when(download).tryLoad(path, out);
        service.download(path, out);
        verify(download).tryLoad(path, out);

        var moveDto = new MoveDtoImpl(path, mock(Path.class), true, false);
        doReturn(Optional.of(VoidOk.OK)).when(move).tryNow(moveDto);
        service.move(moveDto);
        verify(move).tryNow(moveDto);

        doReturn(Optional.of("yeah")).when(read).tryNow(path);
        service.read(path);
        verify(read).tryNow(path);
    }

    @Test
    void concurrentLockAllowsWriteOperationsWithStamp() throws IOException {
        // given
        var stamp = UUID.randomUUID();
        var path = mock(Path.class);

        // when
        assertTrue(locks.tryConcurrent(stamp, path));

        // on -> then
        doReturn(Optional.of(VoidOk.OK)).when(delete).tryNow(path);
        service.delete(path, stamp);
        verify(delete).tryNow(path);

        var moveDto = new MoveDtoImpl(path, mock(Path.class), false, false);
        doReturn(Optional.of(VoidOk.OK)).when(move).tryNow(moveDto);
        service.move(moveDto, stamp);
        verify(move).tryNow(moveDto);

        var copyDto = new MoveDtoImpl(mock(Path.class), path, true, false);
        doReturn(Optional.of(VoidOk.OK)).when(move).tryNow(copyDto);
        service.move(copyDto, stamp);
        verify(move).tryNow(copyDto);

        var writeDto = new WriteDtoImpl(path, mock(InputStream.class), true, false);
        doReturn(Optional.of(VoidOk.OK)).when(write).tryNow(writeDto);
        service.upload(writeDto, stamp);
        verify(write).tryNow(writeDto);
    }
}
