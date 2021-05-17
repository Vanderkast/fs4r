package net.vanderkast.fs4r.service.user;

import java.nio.file.Path;

public interface UserPathLock {
    boolean tryLock(User user, Path path);

    void unlock(User user, Path path);
}
