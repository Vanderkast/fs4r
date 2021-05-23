package net.vanderkast.fs4r.lock;

import net.vanderkast.fs4r.extention.content.ConcurrentContentRead;
import net.vanderkast.fs4r.extention.content.ContentRead;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;

public class LockedContentRead extends LockedIo<Path, String> implements ConcurrentContentRead {
    private final ContentRead contentRead;
    private final PathLock lock;

    public LockedContentRead(ContentRead contentRead, PathLock lock) {
        this.contentRead = contentRead;
        this.lock = lock;
    }

    @Override
    public String read(Path path) throws IOException {
        return contentRead.read(path);
    }

    @Override
    protected Lock getLock(Path data) {
        return lock.on(data);
    }

    @Override
    protected String handle(Path data) throws IOException {
        return contentRead.read(data);
    }
}
