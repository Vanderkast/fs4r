package net.vanderkast.fs4r.dto;

import java.nio.file.Path;

public interface MoveDto {
    Path getOrigin();

    Path getTarget();

    boolean isCopy();

    boolean isFailOnTargetExist();
}
