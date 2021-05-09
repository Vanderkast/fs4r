package net.vanderkast.fs4r.service.configuration;

import net.vanderkast.fs4r.concurrent.LockedRead;
import net.vanderkast.fs4r.concurrent.PathLock;
import net.vanderkast.fs4r.concurrent.lock.ReadWritePathLock;
import net.vanderkast.fs4r.domain.Read;
import net.vanderkast.fs4r.service.core_impl.Fs4rLogger;
import net.vanderkast.fs4r.simple.JustRead;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfiguration {

   @Bean
    Read beanRead(ReadWritePathLock lock) {
        return new LockedRead(new Fs4rLogger(LockedRead.class), lock, new JustRead());
    }

    @Bean
    ReadWritePathLock beanRwPathLock() {return new ReadWritePathLock();}

    PathLock beanPathLock() {
        return new PathLock() {
            private final org.slf4j.Logger logger = LoggerFactory.getLogger(PathLock.class.getName());

            @Override
            public void lockInterruptibly() {
                logger.info("PathLock acquire");
            }

            @Override
            public void unlock() {
                logger.info("PathLock release");
            }
        };
    }
}
