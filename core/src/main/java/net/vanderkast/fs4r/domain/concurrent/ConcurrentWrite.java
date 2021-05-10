package net.vanderkast.fs4r.domain.concurrent;

import net.vanderkast.fs4r.domain.Write;
import net.vanderkast.fs4r.domain.dto.WriteDto;

@ConcurrentDomain
public interface ConcurrentWrite extends Write, ConcurrentIo<WriteDto, VoidOk> {
}
