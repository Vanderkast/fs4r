package net.vanderkast.fs4r.service.rest;

import net.vanderkast.fs4r.dto.impl.MoveDtoImpl;
import net.vanderkast.fs4r.dto.impl.WriteDtoImpl;
import net.vanderkast.fs4r.service.model.FileMove;
import net.vanderkast.fs4r.service.model.FileWalk;
import net.vanderkast.fs4r.service.service.Fs4rStampedMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;

import static net.vanderkast.fs4r.service.configuration.Profiles.CONCURRENT_SESSIONS;

@RestController
@RequestMapping("api/v1/main")
@Profile(CONCURRENT_SESSIONS)
public class StampMainRestController {
    private final Fs4rStampedMainService service;

    @Autowired
    public StampMainRestController(Fs4rStampedMainService service) {
        this.service = service;
    }

    @GetMapping("/walk/{*path}")
    @ResponseBody
    public ResponseEntity<Stream<FileWalk>> walkDirectory(@PathVariable String path,
                                                          @RequestParam(required = false) String stamp)
            throws IOException {
        if (stamp == null)
            return ResponseEntity.ok(service.walkDir(Path.of(path)));
        return ResponseEntity.ok(service.walkDir(Path.of(path), UUID.fromString(stamp)));
    }

    @GetMapping(value = "/download/{*path}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public void download(@PathVariable String path,
                         @RequestParam(required = false) String stamp,
                         HttpServletResponse response) throws IOException {
        if (stamp == null)
            service.download(Path.of(path), response);
        else
            service.download(Path.of(path), response, UUID.fromString(stamp));
    }

    @GetMapping(value = "/load/{*path}", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> load(@PathVariable String path,
                                       @RequestParam(required = false) String stamp) throws IOException {
        if (stamp == null)
            return ResponseEntity.ok(service.read(Path.of(path)));
        return ResponseEntity.ok(service.read(Path.of(path), UUID.fromString(stamp)));
    }

    @PostMapping(value = "/upload/{*path}")
    public void upload(@RequestParam("attachment") MultipartFile file,
                       @PathVariable("path") String path,
                       @RequestParam(value = "overwrite", required = false) boolean overwrite,
                       @RequestParam(required = false) String stamp) throws IOException {
        var dto = new WriteDtoImpl(Path.of(path), file.getInputStream(), overwrite);
        if (stamp == null)
            service.upload(dto);
        else
            service.upload(dto, UUID.fromString(stamp));
    }

    @PostMapping("/move/{*origin}")
    @ResponseBody
    public void move(@PathVariable("origin") String origin,
                     @RequestBody FileMove fileMove,
                     @RequestParam(required = false) String stamp) throws IOException {
        var dto = new MoveDtoImpl(
                Path.of(origin),
                Path.of(fileMove.getTarget()),
                fileMove.isCopy(),
                fileMove.isFailOnTargetExist());
        if (stamp == null)
            service.move(dto);
        else
            service.move(dto, UUID.fromString(stamp));
    }

    @DeleteMapping(value = "/delete/{*path}")
    public void delete(@PathVariable String path, @RequestParam(required = false) String stamp) throws IOException {
        if (stamp == null)
            service.delete(Path.of(path));
        else
            service.delete(Path.of(path), UUID.fromString(stamp));
    }
}
