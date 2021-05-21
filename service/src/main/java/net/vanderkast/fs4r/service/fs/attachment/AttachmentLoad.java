package net.vanderkast.fs4r.service.fs.attachment;

import net.vanderkast.fs4r.domain.concurrent.ConcurrentLoad;

import java.io.IOException;
import java.nio.file.Path;

public interface AttachmentLoad<T> extends ConcurrentLoad {
    boolean tryLoad(Path file, T output) throws IOException;
}
