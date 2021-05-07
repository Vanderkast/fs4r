package net.vanderkast.fs4r.domain;

import java.io.IOException;
import java.nio.file.Path;

public interface Move {
    interface Dto {
        Path getOrigin();

        Path getTarget();

        boolean isCopy();

        boolean isFailOnTargetExists();
    }

    void move(Dto dto) throws IOException;
}
