package net.vanderkast.fs4r.service.fs.virtual;

import net.vanderkast.fs4r.extention.content.ConcurrentContentRead;
import net.vanderkast.fs4r.service.fs.file_size.FileSizeLimitCheck;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class VirtualContentRead implements ConcurrentContentRead {
    private final VirtualFileSystem fs;
    private final ConcurrentContentRead contentRead;
    private final FileSizeLimitCheck fileSizeLimit;

    public VirtualContentRead(VirtualFileSystem fs, ConcurrentContentRead contentRead, FileSizeLimitCheck fileSizeLimit) {
        this.fs = fs;
        this.contentRead = contentRead;
        this.fileSizeLimit = fileSizeLimit;
    }

    private Path mapAndVerify(Path virtual) throws IOException {
        var real = fs.mapOrThrow(virtual);
        fileSizeLimit.verify(real);
        return real;
    }

    @Override
    public String interruptibly(Path virtual) throws IOException, InterruptedException {
        return contentRead.interruptibly(mapAndVerify(virtual));
    }

    @Override
    public Optional<String> tryNow(Path virtual) throws IOException {
        return contentRead.tryNow(mapAndVerify(virtual));
    }

    @Override
    public String read(Path virtual) throws IOException {
        return contentRead.read(mapAndVerify(virtual));
    }
}
