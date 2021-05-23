package net.vanderkast.fs4r.service.configuration;

import net.vanderkast.fs4r.domain.*;
import net.vanderkast.fs4r.extention.content.ConcurrentContentRead;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

public interface ServiceConfiguration {
    Walk walk();

    Load load();

    Delete delete();

    Move move();

    Write write();

    ConcurrentContentRead contentRead();
}
