package com.vanderkast.fs4r.simple;

import com.vanderkast.fs4r.domain.Move;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class JustMove implements Move {
    @Override
    public void move(Dto dto) throws IOException {
        if(dto.isCopy()) {
            if(dto.isFailOnTargetExists())
                Files.copy(dto.getOrigin(), dto.getTarget());
            else
                Files.copy(dto.getOrigin(), dto.getTarget(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            if(dto.isFailOnTargetExists())
                Files.move(dto.getOrigin(), dto.getTarget());
            else
                Files.move(dto.getOrigin(), dto.getTarget(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
