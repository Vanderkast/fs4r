package net.vanderkast.fs4r.domain;

import net.vanderkast.fs4r.dto.MoveDto;

import java.io.IOException;

public interface Move {
    void move(MoveDto dto) throws IOException;
}
