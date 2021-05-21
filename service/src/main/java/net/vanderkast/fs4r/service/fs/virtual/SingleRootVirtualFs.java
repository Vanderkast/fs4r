package net.vanderkast.fs4r.service.fs.virtual;

import net.vanderkast.fs4r.domain.Walk;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static net.vanderkast.fs4r.service.fs.virtual.VirtualConstants.VIRTUAL_ROOT;

/**
 * <p>Publish single path.</p>
 */
public class SingleRootVirtualFs implements VirtualFileSystem {
    private final Path root;
    private final String rootString;
    private final Walk realWalk;

    public SingleRootVirtualFs(Path root, Walk realWalk) {
        this.root = root;
        this.realWalk = realWalk;
        rootString = root.toString();
    }

    @Override
    public Optional<Path> map(Path virtual) {
        return Optional.of(Path.of(rootString, virtual.toString()));
    }

    @Override
    public boolean isProtected(Path virtual) {
        return VIRTUAL_ROOT.equals(virtual);
    }

    @Override
    public Stream<Path> walkVirtualRoot() throws IOException {
        return realWalk.walkDir(root);
    }
}
