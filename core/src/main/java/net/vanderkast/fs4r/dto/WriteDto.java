package net.vanderkast.fs4r.dto;

import java.io.InputStream;
import java.nio.file.Path;

public interface WriteDto {
    Path getPath();

    InputStream getInputStream();

    /**
     * Affects only when {@link WriteDto#isReplace()} is false or target file doesn't exists.
     * @return true - overwrite existing file content, false - append to it
     */
    boolean isOverwrite();

    /**
     * If false means <i>"fail on target file exists already"</i>.
     * @return true if replaces existing file, false - if should fail.
     */
    boolean isReplace();
}
