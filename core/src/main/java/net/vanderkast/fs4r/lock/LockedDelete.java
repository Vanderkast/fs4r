package net.vanderkast.fs4r.lock;

import net.vanderkast.fs4r.domain.Delete;
import net.vanderkast.fs4r.domain.concurrent.ConcurrentDelete;
import net.vanderkast.fs4r.domain.concurrent.VoidOk;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;

public class LockedDelete extends LockedIo<Path, VoidOk> implements ConcurrentDelete {
    private final Delete delete;
    private final PathLock pathLock;

    public LockedDelete(Delete delete, PathLock pathLock) {
        this.delete = delete;
        this.pathLock = pathLock;
    }

    @Override
    public void delete(Path path) throws IOException {
        delete.delete(path);
    }

    @Override
    protected Lock getLock(Path path) {
        return pathLock.on(path);
    }

    @Override
    protected VoidOk handle(Path path) throws IOException {
        delete.delete(path);
        return VoidOk.OK;
    }
}
