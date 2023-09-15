package org.simplesystem.todoservice.dto;

import java.time.Instant;
import lombok.*;
import org.simplesystem.todoservice.enums.Status;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {

  private Long id;

  private String description;
  private Status status;
  private Instant creationDate;
  private Instant dueDate;
  private Instant completionDate;
}
