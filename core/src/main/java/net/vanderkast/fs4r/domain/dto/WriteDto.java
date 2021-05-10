package net.vanderkast.fs4r.domain.dto;

import java.io.InputStream;
import java.nio.file.Path;

public interface WriteDto {
    Path getPath();

    InputStream getInputStream();

    /**
     * @return true - overwrite exist data, false - append to exist data
     */
    boolean isOverwrite();
}
