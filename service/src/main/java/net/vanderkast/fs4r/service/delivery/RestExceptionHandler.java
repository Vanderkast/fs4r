package net.vanderkast.fs4r.service.delivery;

import net.vanderkast.fs4r.service.delivery.service.ResourceBusyException;
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

    @ExceptionHandler(ResourceBusyException.class)
    public ResponseEntity<String> resourceBusyException(ResourceBusyException e) {
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

    @ExceptionHandler
    public ResponseEntity<String> ioException(IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
