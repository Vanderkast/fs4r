package com.vanderkast.fs4r.dto;

import com.vanderkast.fs4r.domain.Move;

import java.nio.file.Path;
import java.util.Objects;

public class MoveDto implements Move.Dto {
    private final Path origin;
    private final Path target;
    private final boolean copy;
    private final boolean failOnTargetExists;

    public MoveDto(Path origin, Path target, boolean copy, boolean failOnTargetExists) {
        assert origin != null;
        assert target != null;

        this.origin = origin;
        this.target = target;
        this.copy = copy;
        this.failOnTargetExists = failOnTargetExists;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveDto moveDto = (MoveDto) o;
        return copy == moveDto.copy
                && failOnTargetExists == moveDto.failOnTargetExists
                && origin.equals(moveDto.origin)
                && target.equals(moveDto.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, target);
    }

    @Override
    public Path getOrigin() {
        return origin;
    }

    @Override
    public Path getTarget() {
        return target;
    }

    @Override
    public boolean isCopy() {
        return copy;
    }

    @Override
    public boolean isFailOnTargetExists() {
        return failOnTargetExists;
    }
}
