package net.vanderkast.fs4r.service.virtual_fs;

import org.springframework.lang.Nullable;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Optional;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;

public class FsStub {
    private FsStub() {
    }

    public static void setProtected(VirtualFileSystem fs, Path path) throws ProtectedPathException {
        doReturn(true).when(fs).isProtected(path);
        doCallRealMethod().when(fs).verifyUnprotected(path);
    }

    public static void setUnprotected(VirtualFileSystem fs, Path path) throws ProtectedPathException {
        doReturn(false).when(fs).isProtected(path);
        doCallRealMethod().when(fs).verifyUnprotected(path);
    }

    public static void setMapping(VirtualFileSystem fs, Path virtual, @Nullable Path real) throws FileNotFoundException {
        doReturn(Optional.ofNullable(real)).when(fs).map(virtual);
        doCallRealMethod().when(fs).mapOrThrow(virtual);
    }

    public static void setUnmapped(VirtualFileSystem fs, Path virtual) throws FileNotFoundException {
        setMapping(fs, virtual, null);
    }
}
