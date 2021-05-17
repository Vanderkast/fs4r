package net.vanderkast.fs4r.service.virtual_fs;

import net.vanderkast.fs4r.domain.concurrent.ConcurrentWalk;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class VirtualWalk implements ConcurrentWalk {
    private final VirtualFileSystem fs;
    private final ConcurrentWalk walk;

    public VirtualWalk(VirtualFileSystem fs, ConcurrentWalk walk) {
        this.fs = fs;
        this.walk = walk;
    }

    @Override
    public Stream<Path> walkDir(Path virtualDir) throws IOException {
        if (virtualDir.equals(VirtualConstants.VIRTUAL_ROOT))
            return fs.walkVirtualRoot();
        var real = fs.mapOrThrow(virtualDir);
        return walk.walkDir(real);
    }

    @Override
    public Stream<Path> interruptibly(Path virtualDir) throws IOException, InterruptedException {
        if (virtualDir.equals(VirtualConstants.VIRTUAL_ROOT))
            return fs.walkVirtualRoot();
        var real = fs.mapOrThrow(virtualDir);
        return walk.interruptibly(real);
    }

    @Override
    public Optional<Stream<Path>> tryNow(Path virtualDir) throws IOException {
        if (virtualDir.equals(VirtualConstants.VIRTUAL_ROOT))
            return Optional.of(fs.walkVirtualRoot());
        var real = fs.mapOrThrow(virtualDir);
        return walk.tryNow(real);
    }
}
