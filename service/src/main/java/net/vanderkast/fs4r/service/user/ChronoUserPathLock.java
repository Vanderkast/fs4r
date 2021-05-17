package net.vanderkast.fs4r.service.user;

import java.nio.file.Path;

public interface ChronoUserPathLock extends UserPathLock {
    boolean tryLock(User user, Path path, long forMillis);
}
