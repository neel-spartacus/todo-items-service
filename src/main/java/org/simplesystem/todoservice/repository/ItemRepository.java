package org.simplesystem.todoservice.repository;

import java.time.Instant;
import java.util.List;
import org.simplesystem.todoservice.enums.Status;
import org.simplesystem.todoservice.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

  List<Item> findItemByStatus(Status status);

  List<Item> findByStatusAndDueDateBefore(Status status, Instant instant);
}
