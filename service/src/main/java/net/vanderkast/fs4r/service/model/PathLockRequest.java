package net.vanderkast.fs4r.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.nio.file.Path;

@Getter
public class PathLockRequest {
    private final Path path;
    private final long forMillis;

    public PathLockRequest(@JsonProperty("path") String path,
                           @JsonProperty("forMillis") long forMillis) {
        this.path = Path.of(path);
        this.forMillis = forMillis;
    }
}
