package net.vanderkast.fs4r.service.configuration;

import net.vanderkast.fs4r.domain.concurrent.*;
import net.vanderkast.fs4r.extention.content.ConcurrentContentRead;
import net.vanderkast.fs4r.lock.*;
import net.vanderkast.fs4r.simple.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties(prefix = "fs4r")
public abstract class FsConfiguration implements ServiceConfiguration {
    protected final ReadWritePathLockImpl pathLock = new ReadWritePathLockImpl();

    @Override
    @Bean
    @Qualifier("core")
    public ConcurrentWalk walk() {
        return new LockedWalk(new JustWalk(), pathLock::forRead);
    }

    @Override
    @Bean
    @Qualifier("core")
    public ConcurrentLoad load() {
        return new LockedLoad(new JustLoad(), pathLock::forRead);
    }

    @Override
    @Bean
    @Qualifier("core")
    public ConcurrentDelete delete() {
        return new LockedDelete(new JustDelete(), pathLock::forWrite);
    }

    @Override
    @Bean
    @Qualifier("core")
    public ConcurrentMove move() {
        return new LockedMove(new JustMove(), pathLock::forRead, pathLock::forWrite);
    }

    @Override
    @Bean
    @Qualifier("core")
    public ConcurrentWrite write() {
        return new LockedWrite(new JustWrite(), pathLock::forWrite);
    }

    @Override
    @Bean
    @Qualifier("core")
    public ConcurrentContentRead contentRead() {
        return new LockedContentRead(new JustContentRead(), pathLock::forRead);
    }
}
