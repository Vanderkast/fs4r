package net.vanderkast.fs4r.service.configuration;

import net.vanderkast.fs4r.domain.Walk;
import net.vanderkast.fs4r.domain.concurrent.*;
import net.vanderkast.fs4r.extention.content.ConcurrentContentRead;
import net.vanderkast.fs4r.service.fs.attachment.AttachmentLoad;
import net.vanderkast.fs4r.service.fs.attachment.ServletVirtualAttachmentLoad;
import net.vanderkast.fs4r.service.fs.file_size.FileSizeLimitCheck;
import net.vanderkast.fs4r.service.fs.stamp_lock.ChronoStampPathLock;
import net.vanderkast.fs4r.service.fs.stamp_lock.ChronoStampPathLockImpl;
import net.vanderkast.fs4r.service.fs.virtual.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class VirtualFsConfiguration extends FsConfiguration implements ServiceConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(VirtualFsConfiguration.class);

    private final List<Path> publishedPaths;
    private final long fileLoadingSizeLimit;

    public VirtualFsConfiguration(@Nullable @Value("${fs4r.publish-dirs:}") String[] publishPaths,
                                  @Value("${fs4r.load-files-size-limit:3072}") long fileLoadingSizeLimit) {
        if (publishPaths == null || publishPaths.length == 0 || publishPaths[0] == null) {
            this.publishedPaths = List.of(Path.of("/"));
        } else {
            this.publishedPaths = Stream.of(publishPaths).map(Path::of).collect(Collectors.toList());
        }

        logger.info("Service publishes directories: {}", Arrays.toString(publishPaths));

        this.fileLoadingSizeLimit = fileLoadingSizeLimit;
        logger.info("Loading files size limit: {}", fileLoadingSizeLimit);
    }

    @Bean
    @Profile(Profiles.CONCURRENT_SESSIONS)
    ChronoStampPathLock<UUID> beanStampPathLock() {
        return new ChronoStampPathLockImpl<>();
    }

    @Bean
    public VirtualFileSystem virtualRoot(@Qualifier("core") Walk walk) {
        return publishedPaths.size() == 1
                ? new SingleRootVirtualFs(publishedPaths.get(0), walk)
                : new MultiRootVirtualFs(publishedPaths);
    }

    @Bean
    @Qualifier("virtual")
    ConcurrentWalk beanWalk(VirtualFileSystem fs, @Qualifier("core") ConcurrentWalk walk) {
        return new VirtualWalk(fs, walk);
    }

    @Bean
    @Qualifier("virtual")
    ConcurrentMove beanMove(VirtualFileSystem fs, @Qualifier("core") ConcurrentMove move) {
        return new VirtualMove(fs, move);
    }

    @Bean
    @Qualifier("virtual")
    ConcurrentDelete beanDelete(VirtualFileSystem fs, @Qualifier("core") ConcurrentDelete delete) {
        return new VirtualDelete(fs, delete);
    }

    @Bean
    @Qualifier("virtual")
    ConcurrentWrite beanVirtualWrite(VirtualFileSystem fs, @Qualifier("core") ConcurrentWrite write) {
        return new VirtualWrite(fs, write);
    }

    @Bean
    @Qualifier("virtual")
    AttachmentLoad<HttpServletResponse> beanAttachmentLoad(VirtualFileSystem fs, @Qualifier("core") ConcurrentLoad load) {
        return new ServletVirtualAttachmentLoad(fs, load, pathLock::forRead);
    }

    @Bean
    @Qualifier("virtual")
    ConcurrentContentRead beanConcurrentContentRead(VirtualFileSystem fs,
                                                    ConcurrentContentRead coreContentRead,
                                                    FileSizeLimitCheck verifier) {
        return new VirtualContentRead(fs, coreContentRead, verifier);
    }

    @Bean
    @Qualifier("load-files-size-limit")
    long beanLoadFilesSizeLimit() {
        return fileLoadingSizeLimit;
    }
}
