package net.vanderkast.fs4r.service.rest;

import net.vanderkast.fs4r.service.fs.file_size.FileSizeLimitExceededException;
import net.vanderkast.fs4r.service.service.ResourceBusyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentException(IllegalArgumentException e) {
        if (e.getMessage().contains("UUID"))
            return ResponseEntity.badRequest().body(e.getMessage());
        throw new RuntimeException(e);
    }

    @ExceptionHandler(ResourceBusyException.class)
    public ResponseEntity<String> resourceBusyException(ResourceBusyException ignored) {
        return ResponseEntity.status(HttpStatus.LOCKED).build();
    }

    @ExceptionHandler({FileNotFoundException.class, NoSuchFileException.class})
    public ResponseEntity<String> fileNotFoundException(IOException e) {
        return ResponseEntity.badRequest().body("File not found " + e.getMessage());
    }

    @ExceptionHandler(NotDirectoryException.class)
    public ResponseEntity<String> notDirectoryException(NotDirectoryException e) {
        return ResponseEntity.badRequest().body("Expected target directory. " + e.getMessage());
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<String> fileAlreadyExistsException(FileAlreadyExistsException e) {
        return ResponseEntity.badRequest().body("File already exists. " + e.getMessage());
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public ResponseEntity<String> fileAlreadyExistsException(FileSizeLimitExceededException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> ioException(IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
