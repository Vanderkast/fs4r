package net.vanderkast.fs4r.extention.content;

import net.vanderkast.fs4r.domain.concurrent.ConcurrentDomain;
import net.vanderkast.fs4r.domain.concurrent.ConcurrentIo;

import java.nio.file.Path;

@ConcurrentDomain
public interface ConcurrentContentRead extends ContentRead, ConcurrentIo<Path, String> {
}
