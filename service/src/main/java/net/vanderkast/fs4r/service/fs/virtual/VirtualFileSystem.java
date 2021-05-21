package net.vanderkast.fs4r.service.fs.virtual;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * <p>Provides virtual file system.</p>
 */
public interface VirtualFileSystem {
    /**
     * Maps passed absolute virtual path to real path.
     *
     * @param virtual virtual absolute path that is mapped to real file system
     * @return {@link Optional#of} real path mapped to given virtual if its presented by virtual fs, {@link Optional#empty()} otherwise.
     */
    Optional<Path> map(Path virtual);

    default Path mapOrThrow(Path virtual) throws FileNotFoundException {
        return map(virtual).orElseThrow(FileNotFoundException::new);
    }

    /**
     * <p>Checks passed {@link Path} is protected.</p>
     * <p>In case path is protected it should not be modified by operations.</p>
     * <p><b>Contract:</b> Implementation should return true if {@link VirtualConstants#VIRTUAL_ROOT} passed.</p>
     *
     * @param virtual path to check protection
     * @return true if virtual path is marked as protected by virtual file system, false otherwise
     */
    boolean isProtected(Path virtual);

    default void verifyUnprotected(Path virtual) throws ProtectedPathException {
        if (isProtected(virtual))
            throw new ProtectedPathException(virtual);
    }

    /**
     * <p>Returns stream of virtual root content.</p>
     * <p>It's thread safe method guarded by {@link VirtualFileSystem#isProtected(Path)} contract.</p>
     * @return {@link Stream#of(Object)} of {@link Path} content lies in virtual root. 
     * @throws IOException if delegation on other {@link net.vanderkast.fs4r.domain.Walk} implementation used. 
     */
    Stream<Path> walkVirtualRoot() throws IOException;
}
