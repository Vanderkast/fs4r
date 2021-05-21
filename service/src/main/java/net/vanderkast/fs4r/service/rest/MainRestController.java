package net.vanderkast.fs4r.service.rest;

import net.vanderkast.fs4r.dto.impl.MoveDtoImpl;
import net.vanderkast.fs4r.service.service.Fs4rMainService;
import net.vanderkast.fs4r.service.model.FileMove;
import net.vanderkast.fs4r.service.model.FileWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

@RestController
@RequestMapping("api/v1/main")
public class MainRestController {
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
