package net.vanderkast.fs4r.service.fs.virtual;

import net.vanderkast.fs4r.domain.concurrent.ConcurrentWalk;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static org.mockito.Mockito.*;

class VirtualWalkTest {
    private final VirtualFileSystem fs = mock(VirtualFileSystem.class);
    private final ConcurrentWalk walk = mock(ConcurrentWalk.class);
    private final VirtualWalk virtualWalk = new VirtualWalk(fs, walk);

    @Test
    void root() throws IOException {
        // when
        virtualWalk.walkDir(VirtualConstants.VIRTUAL_ROOT);

        // then
        verify(fs).walkVirtualRoot();
        verifyNoInteractions(walk);
    }

    @Test
    void normal() throws IOException {
        // given
        var virtual = mock(Path.class);
        var real = mock(Path.class);
        doCallRealMethod().when(fs).mapOrThrow(virtual);
        doReturn(Optional.of(real)).when(fs).map(virtual);

        // when
        virtualWalk.walkDir(virtual);

        // then
        verify(fs).mapOrThrow(virtual);
        verify(walk).walkDir(real);
    }
}
