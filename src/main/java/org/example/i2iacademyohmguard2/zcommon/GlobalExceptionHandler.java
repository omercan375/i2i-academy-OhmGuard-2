package org.example.i2iacademyohmguard2.zcommon;

import io.jsonwebtoken.JwtException;
import org.example.i2iacademyohmguard2.zcommon.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(JwtException.class)
        public ResponseEntity<String> handleJwtException(JwtException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Oturum geçersiz veya süresi dolmuş.");
        }

        @ExceptionHandler(MissingRequestHeaderException.class)
        public ResponseEntity<String> handleMissingRequestHeaderException(MissingRequestHeaderException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bu işlem için giriş yapmanız gerekiyor.");
        }

        @ExceptionHandler(AlreadyExistsException.class)
        public ResponseEntity<String> handleAlreadyExistsException(AlreadyExistsException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }

        @ExceptionHandler(SaveException.class)
        public ResponseEntity<String> handleSaveException(SaveException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }

        @ExceptionHandler(InvalidResourceException.class)
        public ResponseEntity<String> handleInvalidResourceException(InvalidResourceException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException exception) {
            return ResponseEntity.badRequest().body(
                    exception.getBindingResult().getFieldError().getDefaultMessage()
            );
        }
        @ExceptionHandler(UpdateException.class)
        public ResponseEntity<String> handleUpdateException(UpdateException exception) {
            return ResponseEntity.badRequest().body(null);
        }
    }



