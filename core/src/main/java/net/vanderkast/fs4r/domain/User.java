package net.vanderkast.fs4r.domain;

import java.io.File;

public interface User {
    boolean canWatch(File file);

    boolean canTouch(File file);
}
