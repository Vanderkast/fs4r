package net.vanderkast.fs4r.service.fs.virtual;

import net.vanderkast.fs4r.domain.concurrent.ConcurrentWrite;
import net.vanderkast.fs4r.domain.concurrent.VoidOk;
import net.vanderkast.fs4r.dto.WriteDto;
import net.vanderkast.fs4r.dto.impl.WriteDtoImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public class VirtualWrite implements ConcurrentWrite {
    private final VirtualFileSystem fs;
    private final ConcurrentWrite implementation;

    public VirtualWrite(VirtualFileSystem fs, ConcurrentWrite implementation) {
        this.fs = fs;
        this.implementation = implementation;
    }

    @Override
    public void write(WriteDto data) throws IOException {
        fs.verifyUnprotected(data.getPath());
        implementation.write(map(data));
    }

    @Override
    public VoidOk interruptibly(WriteDto data) throws IOException, InterruptedException {
        fs.verifyUnprotected(data.getPath());
        return implementation.interruptibly(map(data));
    }

    @Override
    public Optional<VoidOk> tryNow(WriteDto data) throws IOException {
        fs.verifyUnprotected(data.getPath());
        return implementation.tryNow(map(data));
    }

    WriteDto map(WriteDto dto) throws FileNotFoundException {
        return new WriteDtoImpl(fs.mapOrThrow(dto.getPath()), dto.getInputStream(), dto.isOverwrite(), dto.isReplace());
    }
}
