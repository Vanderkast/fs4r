package net.vanderkast.fs4r.service.rest;

import net.vanderkast.fs4r.service.fs.stamp_lock.ChronoStampPathLock;
import net.vanderkast.fs4r.service.model.StampedLock;
import net.vanderkast.fs4r.service.service.Fs4rStampedMainService;
import net.vanderkast.fs4r.service.service.ResourceBusyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.UUID;

import static net.vanderkast.fs4r.service.configuration.Profiles.CONCURRENT_SESSIONS;

@RestController
@RequestMapping("api/v1/lock")
@Profile(CONCURRENT_SESSIONS)
public class StampLockRestController {
    private static final long MINUTE = 60_000;

    private final ChronoStampPathLock<UUID> stampPathLock;
    private final Fs4rStampedMainService service;

    @Autowired
    public StampLockRestController(ChronoStampPathLock<UUID> stampPathLock, Fs4rStampedMainService service) {
        this.stampPathLock = stampPathLock;
        this.service = service;
    }

    @GetMapping("/exclusive/{*path}")
    @ResponseBody
    public ResponseEntity<StampedLock<UUID>> lockExclusive(@PathVariable("path") String path) {
        var stamp = UUID.randomUUID();
        var deadline = System.currentTimeMillis() + MINUTE;
        if (stampPathLock.tryExclusive(stamp, Path.of(path), MINUTE))
            return ResponseEntity.ok(new StampedLock<>(stamp, deadline));
        throw new ResourceBusyException();
    }

    @GetMapping("/concurrent/{*path}")
    @ResponseBody
    public ResponseEntity<StampedLock<UUID>> lockConcurrent(@PathVariable("path") String path) {
        var stamp = UUID.randomUUID();
        var deadline = System.currentTimeMillis() + MINUTE;
        if (stampPathLock.tryConcurrent(stamp, Path.of(path), MINUTE))
            return ResponseEntity.ok(new StampedLock<>(stamp, deadline));
        throw new ResourceBusyException();
    }

    @GetMapping("/unlock/{*path}")
    @ResponseBody
    public void unlock(@PathVariable("path") String path, @RequestParam("stamp") String stamp) {
        stampPathLock.unlock(UUID.fromString(stamp), Path.of(path));
    }
}
