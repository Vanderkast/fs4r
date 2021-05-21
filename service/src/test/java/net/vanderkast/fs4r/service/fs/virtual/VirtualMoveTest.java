package net.vanderkast.fs4r.service.fs.virtual;

import net.vanderkast.fs4r.domain.concurrent.ConcurrentMove;
import net.vanderkast.fs4r.dto.impl.MoveDtoImpl;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import static net.vanderkast.fs4r.service.fs.virtual.FsStub.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VirtualMoveTest {
    private final VirtualFileSystem fs = mock(VirtualFileSystem.class);
    private final ConcurrentMove move = mock(ConcurrentMove.class);
    private final VirtualMove virtualMove = new VirtualMove(fs, move);

    @Test
    void originProtected() throws IOException {
        // given
        var dto = new MoveDtoImpl(mock(Path.class), mock(Path.class), false, false);
        setProtected(fs, dto.getOrigin());
        boolean protectedPathCaught;

        // when
        try {
            virtualMove.move(dto);
            protectedPathCaught = false;
        } catch (ProtectedPathException ignored) {
            protectedPathCaught = true;
        }

        // then
        assertTrue(protectedPathCaught);
        verifyNoInteractions(move);
        verify(fs).isProtected(dto.getOrigin());
    }

    @Test
    void originUnprotected_TargetProtected() throws IOException {
        // given
        var dto = new MoveDtoImpl(mock(Path.class), mock(Path.class), false, true);
        setUnprotected(fs, dto.getOrigin());
        setProtected(fs, dto.getTarget());
        boolean protectedPathCaught;

        // when
        try {
            virtualMove.move(dto);
            protectedPathCaught = false;
        } catch (ProtectedPathException e) {
            protectedPathCaught = true;
        }

        // then
        assertTrue(protectedPathCaught);
        verifyNoInteractions(move);
        verify(fs).isProtected(dto.getOrigin());
        verify(fs).isProtected(dto.getTarget());
    }

    @Test
    void originNotMapped() throws IOException {
        // given
        var dto = new MoveDtoImpl(mock(Path.class), mock(Path.class), false, true);
        setUnprotected(fs, dto.getOrigin());
        setUnprotected(fs, dto.getTarget());
        setUnmapped(fs, dto.getOrigin());
        setMapping(fs, mock(Path.class), dto.getTarget());
        boolean fileNotFoundCaught;

        // when
        try {
            virtualMove.move(dto);
            fileNotFoundCaught = false;
        } catch (FileNotFoundException ignored){
            fileNotFoundCaught = true;
        }

        // then
        assertTrue(fileNotFoundCaught);
        verifyNoInteractions(move);
        verify(fs, atLeastOnce()).map(dto.getOrigin());
        verify(fs, atMostOnce()).map(dto.getTarget());
    }

    @Test
    void targetNotMapped() throws IOException {
        // given
        var dto = new MoveDtoImpl(mock(Path.class), mock(Path.class), false, true);
        setUnprotected(fs, dto.getOrigin());
        setUnprotected(fs, dto.getTarget());
        setMapping(fs, mock(Path.class), dto.getOrigin());
        setUnmapped(fs, dto.getTarget());
        boolean fileNotFoundCaught;

        // when
        try {
            virtualMove.move(dto);
            fileNotFoundCaught = false;
        } catch (FileNotFoundException ignored){
            fileNotFoundCaught = true;
        }

        // then
        assertTrue(fileNotFoundCaught);
        verifyNoInteractions(move);
        verify(fs, atMostOnce()).map(dto.getOrigin());
        verify(fs, atLeastOnce()).map(dto.getTarget());
    }

    @Test
    void unprotected_AllMapped_Move() throws IOException {
        // given
        var dto = new MoveDtoImpl(mock(Path.class), mock(Path.class), false, false);
        setUnprotected(fs, dto.getOrigin());
        setUnprotected(fs, dto.getTarget());
        var realOrigin = mock(Path.class);
        var realTarget = mock(Path.class);
        setMapping(fs, dto.getOrigin(), realOrigin);
        setMapping(fs, dto.getTarget(), realTarget);
        boolean protectedPathCaught;

        // when
        try {
            virtualMove.move(dto);
            protectedPathCaught = false;
        } catch (ProtectedPathException ignored) {
            protectedPathCaught = true;
        }

        // then
        assertFalse(protectedPathCaught);
        verify(fs).isProtected(dto.getOrigin());
        verify(fs).isProtected(dto.getTarget());
        verify(move).move(argThat(argument -> {
            assertEquals(dto.isCopy(), argument.isCopy());
            assertEquals(dto.isFailOnTargetExist(), argument.isFailOnTargetExist());
            assertEquals(realOrigin, argument.getOrigin());
            assertEquals(realTarget, argument.getTarget());
            return true;
        }));
    }

    @Test
    void originProtected_TargetUnprotected_AllMapped_Copy() throws IOException {
        // given
        var dto = new MoveDtoImpl(mock(Path.class), mock(Path.class), true, false);
        setProtected(fs, dto.getOrigin());
        setUnprotected(fs, dto.getTarget());
        var realOrigin = mock(Path.class);
        var realTarget = mock(Path.class);
        setMapping(fs, dto.getOrigin(), realOrigin);
        setMapping(fs, dto.getTarget(), realTarget);
        boolean protectedPathCaught;

        // when
        try {
            virtualMove.move(dto);
            protectedPathCaught = false;
        } catch (ProtectedPathException ignored) {
            protectedPathCaught = true;
        }

        // then
        assertFalse(protectedPathCaught);
        verify(fs, times(0)).isProtected(dto.getOrigin());
        verify(fs).isProtected(dto.getTarget());
        verify(move).move(argThat(argument -> {
            assertEquals(dto.isCopy(), argument.isCopy());
            assertEquals(dto.isFailOnTargetExist(), argument.isFailOnTargetExist());
            assertEquals(realOrigin, argument.getOrigin());
            assertEquals(realTarget, argument.getTarget());
            return true;
        }));
    }
}
