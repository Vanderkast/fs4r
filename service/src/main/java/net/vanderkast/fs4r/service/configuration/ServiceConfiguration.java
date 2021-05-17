package net.vanderkast.fs4r.service.configuration;

import net.vanderkast.fs4r.domain.*;

public interface ServiceConfiguration {
    Walk walk();

    Load load();

    Delete delete();

    Move move();

    Write write();
}
