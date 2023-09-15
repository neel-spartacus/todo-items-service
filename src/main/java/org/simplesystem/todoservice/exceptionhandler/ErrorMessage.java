package org.simplesystem.todoservice.exceptionhandler;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorMessage {
  private int statusCode;
  private LocalDateTime timestamp;
  private String message;
  private String description;
}
