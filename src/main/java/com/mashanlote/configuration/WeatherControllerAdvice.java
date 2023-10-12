package com.mashanlote.configuration;

import com.mashanlote.model.exceptions.*;
import com.mashanlote.model.weather.ErrorDetails;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class WeatherControllerAdvice {

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorDetails> handleBadRequest() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDetails("400", "There was an error in user inputs."));
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorDetails> handleNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDetails("404", "Requested city not found"));
    }

    @ExceptionHandler({ConflictException.class})
    public ResponseEntity<ErrorDetails> handleConflict() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorDetails("409", "There was a conflict adding a new resource."));
    }

    @ExceptionHandler({
            AuthorizationException.class,
            AuthenticationException.class,
            DateTimeParseException.class
    })
    public ResponseEntity<ErrorDetails> handleAuthOrInternal() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDetails("500", "The server encountered an error while fetching the data."));
    }

    @ExceptionHandler({RequestNotPermitted.class})
    public ResponseEntity<ErrorDetails> handleTooManyRequests() {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ErrorDetails("429", "Quit spamming our API."));
    }

}
