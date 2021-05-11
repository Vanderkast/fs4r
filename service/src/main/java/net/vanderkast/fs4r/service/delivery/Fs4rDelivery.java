package net.vanderkast.fs4r.service.delivery;

import net.vanderkast.fs4r.domain.Delete;
import net.vanderkast.fs4r.domain.Move;
import net.vanderkast.fs4r.domain.Walk;
import net.vanderkast.fs4r.service.model.FileMove;
import net.vanderkast.fs4r.service.model.FileWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.stream.Stream;

@RestController
@RequestMapping("api/v1/test")
@Profile("test")
public class Fs4rDelivery extends Delivery {
    private final Walk walk;
    private final Delete delete;
    private final Move move;

    @Autowired
    public Fs4rDelivery(@Qualifier("service-root-path") Path root, Walk walk, Delete delete, Move move) {
        super(root);
        this.walk = walk;
        this.delete = delete;
        this.move = move;
    }

    @GetMapping("/walk/{*path}")
    @ResponseBody
    public ResponseEntity<Stream<FileWalk>> walkDirectory(@PathVariable String path) throws IOException {
        var dir = fromServiceRoot(path);
        if (Files.isDirectory(dir))
            return ResponseEntity.ok(walk.walkDir(dir).map(FileWalk::ofPath));
        throw new NotDirectoryException(path);
    }

    @GetMapping(value = "/load/{*path}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> load(@PathVariable String path) {
        try {
            return ResponseEntity.ok(new FileUrlResource(fromServiceRoot(path).toUri().toURL()));
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed url passed!", e);
        }
    }

    @PostMapping("/move")
    @ResponseBody
    public void move(@RequestBody FileMove fileMove) throws IOException {
        move.move(fileMove.toMoveDto(root));
    }

    @DeleteMapping(value = "/delete/{*path}")
    public void delete(@PathVariable String path) throws IOException {
        delete.delete(fromServiceRoot(path));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> exceptionHandler(IOException e) {
        Class<?> exClass = e.getClass();
        if (exClass == FileNotFoundException.class || exClass == NoSuchFileException.class)
            return ResponseEntity.badRequest().body(String.format("No file found %s", e.getMessage()));
        if (exClass == NotDirectoryException.class)
            return ResponseEntity.badRequest().body(String.format("Is not a directory %s", e.getMessage()));
        if (exClass == FileAlreadyExistsException.class)
            return ResponseEntity.badRequest().body(String.format("File already exists %s", e.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
