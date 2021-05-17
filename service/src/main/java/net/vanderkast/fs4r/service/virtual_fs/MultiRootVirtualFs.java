package net.vanderkast.fs4r.service.virtual_fs;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.vanderkast.fs4r.service.virtual_fs.VirtualConstants.VIRTUAL_ROOT;

/**
 * <p>Provides feature to publish multiple files via service.</p>
 * <p>Wraps published paths with Virtual root directory.</p>
 */
public class MultiRootVirtualFs implements VirtualFileSystem {
    private final Map<Path, Path> virtualPublishPaths;

    public MultiRootVirtualFs(List<Path> publish) {
        virtualPublishPaths = publish.stream().collect(
                Collectors.toMap(
                        v -> VIRTUAL_ROOT.resolve(v.getFileName()),
                        p -> p));
    }

    @Override
    public Optional<Path> map(Path path) {
        if (VIRTUAL_ROOT.equals(path))
            return Optional.empty();
        var publishedPath = virtualPublishPaths.keySet()
                .stream()
                .filter(path::startsWith)
                .findFirst();
        return publishedPath.map(virtual -> virtualPublishPaths.get(virtual).resolve(virtual.relativize(path)));
    }

    @Override
    public boolean isProtected(Path virtual) {
        return VIRTUAL_ROOT.equals(virtual) || virtualPublishPaths.containsKey(virtual);
    }

    @Override
    public Stream<Path> walkVirtualRoot() {
        return virtualPublishPaths.values().stream();
    }
}
