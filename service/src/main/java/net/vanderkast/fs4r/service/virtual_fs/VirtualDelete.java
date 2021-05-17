package net.vanderkast.fs4r.service.virtual_fs;

import net.vanderkast.fs4r.domain.concurrent.ConcurrentDelete;
import net.vanderkast.fs4r.domain.concurrent.VoidOk;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class VirtualDelete implements ConcurrentDelete {
    private final VirtualFileSystem fs;
    private final ConcurrentDelete implementation;

    public VirtualDelete(VirtualFileSystem fs, ConcurrentDelete implementation) {
        this.fs = fs;
        this.implementation = implementation;
    }

    @Override
    public void delete(Path virtual) throws IOException {
        fs.verifyUnprotected(virtual);
        implementation.delete(fs.mapOrThrow(virtual));
    }

    @Override
    public VoidOk interruptibly(Path virtual) throws IOException, InterruptedException {
        fs.verifyUnprotected(virtual);
        return implementation.interruptibly(fs.mapOrThrow(virtual));
    }

    @Override
    public Optional<VoidOk> tryNow(Path virtual) throws IOException {
        fs.verifyUnprotected(virtual);
        return implementation.tryNow(fs.mapOrThrow(virtual));
    }
}
