package net.vanderkast.fs4r.dto;

import java.io.InputStream;
import java.nio.file.Path;

public interface WriteDto {
    Path getPath();

    InputStream getInputStream();

    /**
     * Affects only when {@link WriteDto#isReplace()} is false.
     * @return true - overwrite exist data, false - append to exist data
     */
    boolean isOverwrite();

    /**
     * @return true if replaces existing file, false - if should fail.
     */
    boolean isReplace();
}
