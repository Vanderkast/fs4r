package net.vanderkast.fs4r.lock;

import net.vanderkast.fs4r.domain.Walk;
import net.vanderkast.fs4r.domain.concurrent.ConcurrentWalk;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;

public class LockedWalk extends LockedIo<Path, Stream<Path>> implements ConcurrentWalk {
    private final Walk walk;
    private final PathLock pathLock;

    public LockedWalk(Walk walk, PathLock pathLock) {
        this.walk = walk;
        this.pathLock = pathLock;
    }

    @Override
    public Stream<Path> walkDir(Path dir) throws IOException {
        return walk.walkDir(dir);
    }

    @Override
    protected Lock getLock(Path path) {
        return pathLock.on(path);
    }

    @Override
    protected Stream<Path> handle(Path path) throws IOException {
        return walk.walkDir(path);
    }
}
