package net.vanderkast.fs4r.lock;

import net.vanderkast.fs4r.domain.Write;
import net.vanderkast.fs4r.domain.concurrent.ConcurrentWrite;
import net.vanderkast.fs4r.domain.concurrent.VoidOk;
import net.vanderkast.fs4r.domain.dto.WriteDto;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

public class LockedWrite extends LockedIo<WriteDto, VoidOk> implements ConcurrentWrite {
    private final PathLock pathLock;
    private final Write write;

    public LockedWrite(PathLock pathLock, Write write) {
        this.pathLock = pathLock;
        this.write = write;
    }

    @Override
    public void write(WriteDto data) throws IOException {
        write.write(data);
    }

    @Override
    protected Lock getLock(WriteDto data) {
        return pathLock.on(data.getPath());
    }

    @Override
    protected VoidOk handle(WriteDto data) throws IOException {
        write.write(data);
        return VoidOk.OK;
    }
}
