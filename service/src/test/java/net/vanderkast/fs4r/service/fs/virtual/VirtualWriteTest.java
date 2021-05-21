package net.vanderkast.fs4r.service.fs.virtual;

import net.vanderkast.fs4r.domain.concurrent.ConcurrentWrite;
import net.vanderkast.fs4r.dto.impl.WriteDtoImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class VirtualWriteTest {
    private final VirtualFileSystem fs = mock(VirtualFileSystem.class);
    private final ConcurrentWrite write = mock(ConcurrentWrite.class);
    private final VirtualWrite virtualWrite = new VirtualWrite(fs, write);

    @Test
    void writeProtected() throws IOException {
        // given
        var path = mock(Path.class);
        doReturn(true).when(fs).isProtected(path);
        doCallRealMethod().when(fs).verifyUnprotected(path);
        boolean caughtProtected;

        // when
        try {
            virtualWrite.write(new WriteDtoImpl(path, mock(InputStream.class), false));
            caughtProtected = false;
        } catch (ProtectedPathException ignored) {
            caughtProtected = true;
        }

        // then
        assertTrue(caughtProtected);
        verify(fs).isProtected(path);
        verifyNoInteractions(write);
    }

    @Test
    void writeUnprotected() throws IOException {
        // given
        var path = mock(Path.class);
        doReturn(false).when(fs).isProtected(path);
        doCallRealMethod().when(fs).verifyUnprotected(path);
        var dto = new WriteDtoImpl(path, mock(InputStream.class), false);
        boolean caughtProtected;

        // when
        try {
            virtualWrite.write(dto);
            caughtProtected = false;
        } catch (ProtectedPathException ignored) {
            caughtProtected = true;
        }

        // then
        assertFalse(caughtProtected);
        verify(fs).isProtected(path);
        verify(write).write(dto);
    }
}
