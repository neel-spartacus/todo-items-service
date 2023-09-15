package org.simplesystem.todoservice.exceptionhandler;

import java.time.Clock;
import java.time.LocalDateTime;
import org.simplesystem.todoservice.exceptions.ResourceNotFoundException;
import org.simplesystem.todoservice.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = ResourceNotFoundException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public ErrorMessage resourceNotFoundException(ResourceNotFoundException ex) {
    return new ErrorMessage(
        HttpStatus.NOT_FOUND.value(),
        LocalDateTime.now(Clock.systemUTC()),
        ex.getMessage(),
        "Resource Not Found");
  }

  @ExceptionHandler(value = ValidationException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ErrorMessage validationException(ValidationException ex) {
    return new ErrorMessage(
        HttpStatus.BAD_REQUEST.value(),
        LocalDateTime.now(Clock.systemUTC()),
        ex.getMessage(),
        "Invalid input data");
  }
}
