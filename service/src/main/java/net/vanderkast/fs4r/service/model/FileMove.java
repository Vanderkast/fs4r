package net.vanderkast.fs4r.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import net.vanderkast.fs4r.domain.dto.MoveDto;
import net.vanderkast.fs4r.dto.MoveDtoImpl;

import java.nio.file.Path;

@Getter
public class FileMove {
    private final String origin;
    private final String target;
    private final boolean copy;
    private final boolean failOnTargetExist;

    public FileMove(@JsonProperty(value = "origin", required = true) String origin,
                    @JsonProperty(value = "target", required = true) String target,
                    @JsonProperty(value = "copy",defaultValue = "false") boolean copy,
                    @JsonProperty(value = "failOnTargetExist", defaultValue = "true") boolean failOnTargetExist) {
        this.origin = origin;
        this.target = target;
        this.copy = copy;
        this.failOnTargetExist = failOnTargetExist;
    }

    public MoveDto toMoveDto(String root) {
        return new MoveDtoImpl(Path.of(root, origin), Path.of(root, target), copy, failOnTargetExist);
    }
}
