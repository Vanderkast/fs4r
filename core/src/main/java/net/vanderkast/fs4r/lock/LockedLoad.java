package net.vanderkast.fs4r.lock;

import net.vanderkast.fs4r.domain.Load;
import net.vanderkast.fs4r.domain.concurrent.ConcurrentLoad;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;

public class LockedLoad extends LockedIo<Path, InputStream> implements ConcurrentLoad {
    private final PathLock pathLock;
    private final Load load;

    public LockedLoad(Load load, PathLock pathLock) {
        this.pathLock = pathLock;
        this.load = load;
    }

    @Override
    public InputStream load(Path path) throws IOException {
        return load.load(path);
    }

    @Override
    protected Lock getLock(Path path) {
        return pathLock.on(path);
    }

    @Override
    protected InputStream handle(Path path) throws IOException {
        return load.load(path);
    }
}
