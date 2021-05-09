package net.vanderkast.fs4r.service.delivery;

import net.vanderkast.fs4r.domain.Read;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

@RestController
@RequestMapping("api/v1/test")
public class Fs4rDelivery {
    private final Read read;

    @Autowired
    public Fs4rDelivery(Read read) {
        this.read = read;
    }

    @GetMapping("/dir/{*path}")
    @ResponseBody
    public ResponseEntity<Stream<String>> readDirectory(@PathVariable String path) {
        try {
            return ResponseEntity.of(read.readContains(Path.of(path)).map(s -> s.map(Path::getFileName).map(Path::toString)));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An I/O Exception occur!", e);
        }
    }
}
