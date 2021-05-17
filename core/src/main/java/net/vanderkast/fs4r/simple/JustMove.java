package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Move;
import net.vanderkast.fs4r.dto.MoveDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class JustMove implements Move {
    @Override
    public void move(MoveDto dto) throws IOException {
        if(dto.isCopy()) {
            if(dto.isFailOnTargetExist())
                Files.copy(dto.getOrigin(), dto.getTarget());
            else
                Files.copy(dto.getOrigin(), dto.getTarget(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            if(dto.isFailOnTargetExist())
                Files.move(dto.getOrigin(), dto.getTarget());
            else
                Files.move(dto.getOrigin(), dto.getTarget(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
