package net.vanderkast.fs4r.domain.concurrent;

import net.vanderkast.fs4r.domain.Walk;

import java.nio.file.Path;
import java.util.stream.Stream;

@ConcurrentDomain
public interface ConcurrentWalk extends Walk, ConcurrentIo<Path, Stream<Path>> {
}
