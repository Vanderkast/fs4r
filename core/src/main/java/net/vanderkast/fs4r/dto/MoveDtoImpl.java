package net.vanderkast.fs4r.dto;

import java.nio.file.Path;
import java.util.Objects;

public class MoveDtoImpl implements net.vanderkast.fs4r.domain.dto.MoveDto {
    private final Path origin;
    private final Path target;
    private final boolean copy;
    private final boolean failOnTargetExists;

    public MoveDtoImpl(Path origin, Path target, boolean copy, boolean failOnTargetExists) {
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
        MoveDtoImpl moveDtoImpl = (MoveDtoImpl) o;
        return copy == moveDtoImpl.copy
                && failOnTargetExists == moveDtoImpl.failOnTargetExists
                && origin.equals(moveDtoImpl.origin)
                && target.equals(moveDtoImpl.target);
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
