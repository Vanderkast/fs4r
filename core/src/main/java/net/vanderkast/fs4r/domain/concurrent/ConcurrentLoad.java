package net.vanderkast.fs4r.domain.concurrent;

import net.vanderkast.fs4r.domain.Load;

import java.io.InputStream;
import java.nio.file.Path;

@ConcurrentDomain
public interface ConcurrentLoad extends Load, ConcurrentIo<Path, InputStream> {
}
