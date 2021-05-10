package net.vanderkast.fs4r.domain.concurrent;

import net.vanderkast.fs4r.domain.Move;
import net.vanderkast.fs4r.domain.dto.MoveDto;

@ConcurrentDomain
public interface ConcurrentMove extends Move, ConcurrentIo<MoveDto, VoidOk> {
}
