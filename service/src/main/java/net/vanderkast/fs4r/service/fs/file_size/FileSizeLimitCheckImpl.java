package net.vanderkast.fs4r.service.fs.file_size;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileSizeLimitCheckImpl implements FileSizeLimitCheck {
    private final long permittedSize; // bytes

    public FileSizeLimitCheckImpl(@Qualifier("load-files-size-limit") long permittedSize) {
        this.permittedSize = permittedSize;
    }

    @Override
    public long getLimit() {
        return permittedSize;
    }

    @Override
    public boolean check(Path path) throws IOException {
        var size = Files.size(path);
        return size <= permittedSize;
    }
}
