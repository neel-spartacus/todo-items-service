package org.simplesystem.todoservice.model;

import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.simplesystem.todoservice.enums.Status;

@Entity
@Table(name = "item")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  @EqualsAndHashCode.Include
  private Long id;

  @NotNull @EqualsAndHashCode.Include private String description;

  @Enumerated(EnumType.STRING)
  private Status status = Status.NOT_DONE;

  @CreationTimestamp private Instant creationDate;

  private Instant dueDate;

  private Instant completionDate;

  @Version private Long version;

  @Override
  public String toString() {
    return "Item{" + "id=" + id + ", description='" + description + '\'' + '}';
  }
}
