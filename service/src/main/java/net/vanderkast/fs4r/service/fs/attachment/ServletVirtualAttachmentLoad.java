package net.vanderkast.fs4r.service.fs.attachment;

import net.vanderkast.fs4r.domain.concurrent.ConcurrentLoad;
import net.vanderkast.fs4r.lock.PathLock;
import net.vanderkast.fs4r.service.fs.virtual.VirtualFileSystem;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

public class ServletVirtualAttachmentLoad implements AttachmentLoad<HttpServletResponse> { // todo test
    private static final int BUFFER = 1024*3; // 3 Mb

    private final VirtualFileSystem fileSystem;
    private final ConcurrentLoad load;
    private final PathLock pathLock;

    public ServletVirtualAttachmentLoad(VirtualFileSystem fileSystem, ConcurrentLoad load, PathLock pathLock) {
        this.fileSystem = fileSystem;
        this.load = load;
        this.pathLock = pathLock;
    }

    @Override
    public InputStream load(Path virtual) throws IOException {
        return load.load(fileSystem.mapOrThrow(virtual));
    }

    @Override
    public InputStream interruptibly(Path virtual) throws IOException, InterruptedException {
        return load.interruptibly(fileSystem.mapOrThrow(virtual));
    }

    @Override
    public Optional<InputStream> tryNow(Path virtual) throws IOException {
        return load.tryNow(fileSystem.mapOrThrow(virtual));
    }

    @Override
    public boolean tryLoad(Path virtual, HttpServletResponse output) throws IOException { // todo refactor
        var file = fileSystem.mapOrThrow(virtual);
        var lock = pathLock.on(file);
        try {
            var locked = lock.tryLock();
            if (!locked)
                return false;
            var stream = load.tryNow(file);
            if(stream.isEmpty())
                return false;
            output.setHeader(
                    "Content-Disposition",
                    "attachment; filename=\"" + file.getFileName().toString() + "\"");
            try(var input = stream.get()) {
                var buffer = new byte[BUFFER];
                int length;
                while ((length = input.read(buffer)) > 0)
                    output.getOutputStream().write(buffer, 0, length);
            } finally {
                output.flushBuffer();
            }
            return true;
        } finally {
            lock.unlock();
        }
    }
}
