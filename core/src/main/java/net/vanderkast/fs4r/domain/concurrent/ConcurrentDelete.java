package net.vanderkast.fs4r.domain.concurrent;

import net.vanderkast.fs4r.domain.Delete;

import java.nio.file.Path;

@ConcurrentDomain
public interface ConcurrentDelete extends Delete, ConcurrentIo<Path, VoidOk> {
}
