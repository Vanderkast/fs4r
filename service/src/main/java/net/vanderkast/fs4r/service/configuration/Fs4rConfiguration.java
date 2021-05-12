package net.vanderkast.fs4r.service.configuration;

import net.vanderkast.fs4r.domain.Delete;
import net.vanderkast.fs4r.domain.Load;
import net.vanderkast.fs4r.domain.Move;
import net.vanderkast.fs4r.domain.Walk;
import net.vanderkast.fs4r.lock.*;
import net.vanderkast.fs4r.simple.JustDelete;
import net.vanderkast.fs4r.simple.JustLoad;
import net.vanderkast.fs4r.simple.JustMove;
import net.vanderkast.fs4r.simple.JustWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import java.nio.file.Path;

@Configuration
@ConfigurationProperties(prefix = "fs4r")
public class Fs4rConfiguration implements ServiceConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(Fs4rConfiguration.class.getName());

    private final ReadWritePathLock readWritePathLock = new ReadWritePathLock();
    private final Path root;

    @Autowired
    protected Fs4rConfiguration(@Nullable @Value("${fs4r.root-dir:}") Path root) {
        this.root = root == null ? Path.of("/") : root;
        logger.info("Service starting with publish directory: {}", this.root.toString());
    }

    @Bean
    @Qualifier("service-root-path")
    @Override
    public Path getServiceRootPath() {
        return root;
    }

    @Bean
    Walk beanWalk() {
        return new LockedWalk(new JustWalk(), readWritePathLock::forRead);
    }

    @Bean
    Load beanLoad() {
        return new LockedLoad(new JustLoad(), readWritePathLock::forRead);
    }

    @Bean
    Delete beanDelete() {
        return new LockedDelete(new JustDelete(), readWritePathLock::forWrite);
    }

    @Bean
    Move beanMove() {
        return new LockedMove(new JustMove(), readWritePathLock::forWrite);
    }
}
