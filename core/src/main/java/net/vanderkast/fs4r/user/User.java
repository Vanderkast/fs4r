package net.vanderkast.fs4r.user;

import java.nio.file.Path;

public interface User {
    boolean canWatch(Path file);

    boolean canTouch(Path file);
}
