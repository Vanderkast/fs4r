package net.vanderkast.fs4r.domain;

import net.vanderkast.fs4r.dto.WriteDto;

import java.io.IOException;

public interface Write {
    void write(WriteDto data) throws IOException;
}
