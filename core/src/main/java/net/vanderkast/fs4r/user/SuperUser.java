package net.vanderkast.fs4r.user;

import java.nio.file.Path;

public interface SuperUser extends User {
    @Override
    default boolean canWatch(Path file) {
        return true;
    }

    @Override
    default boolean canTouch(Path file) {
        return true;
    }
}
