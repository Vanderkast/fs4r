package net.vanderkast.fs4r.service.rest;

import net.vanderkast.fs4r.dto.impl.MoveDtoImpl;
import net.vanderkast.fs4r.dto.impl.WriteDtoImpl;
import net.vanderkast.fs4r.service.model.FileMove;
import net.vanderkast.fs4r.service.model.FileWalk;
import net.vanderkast.fs4r.service.service.Fs4rMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

@RestController
@RequestMapping(MainRestController.API_PATH)
public class MainRestController {
    public static final String API_PATH = "api/v1/main";

    private final Fs4rMainService service;

    @Autowired
    public MainRestController(Fs4rMainService service) {
        this.service = service;
    }

    @GetMapping("/walk/{*path}")
    @ResponseBody
    public ResponseEntity<Stream<FileWalk>> walkDirectory(@PathVariable String path) throws IOException {
        return ResponseEntity.ok(service.walkDir(Path.of(path)));
    }

    @GetMapping(value = "/download/{*path}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public void download(@PathVariable String path, HttpServletResponse response) throws IOException {
        service.download(Path.of(path), response);
    }

    @GetMapping(value = "/load/{*path}", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> load(@PathVariable String path) throws IOException {
        return ResponseEntity.ok(service.load(Path.of(path)));
    }

    @PostMapping(value = "/upload/{*path}")
    public void upload(@RequestParam("attachment") MultipartFile file,
                       @PathVariable("path") String path,
                       @RequestParam("overwrite") boolean overwrite) throws IOException {
        service.upload(new WriteDtoImpl(Path.of(path), file.getInputStream(), overwrite));
    }

    @PostMapping("/move/{*origin}")
    @ResponseBody
    public void move(@PathVariable("origin") String origin, @RequestBody FileMove fileMove) throws IOException {
        service.move(new MoveDtoImpl(
                Path.of(origin),
                Path.of(fileMove.getTarget()),
                fileMove.isCopy(),
                fileMove.isFailOnTargetExist()));
    }

    @DeleteMapping(value = "/delete/{*path}")
    public void delete(@PathVariable String path) throws IOException {
        service.delete(Path.of(path));
    }
}
