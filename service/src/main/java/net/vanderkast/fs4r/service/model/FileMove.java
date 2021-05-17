package net.vanderkast.fs4r.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class FileMove {
    private final String target;
    private final boolean copy;
    private final boolean failOnTargetExist;

    public FileMove(@JsonProperty(value = "target", required = true) String target,
                    @JsonProperty(value = "copy", defaultValue = "false") boolean copy,
                    @JsonProperty(value = "failOnTargetExist", defaultValue = "true") boolean failOnTargetExist) {
        this.target = target;
        this.copy = copy;
        this.failOnTargetExist = failOnTargetExist;
    }
}
