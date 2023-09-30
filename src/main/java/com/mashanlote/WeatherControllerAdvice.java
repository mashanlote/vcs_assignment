package com.mashanlote;

import com.mashanlote.exceptions.BadRequestException;
import com.mashanlote.exceptions.ConflictException;
import com.mashanlote.exceptions.NotFoundException;
import com.mashanlote.model.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
