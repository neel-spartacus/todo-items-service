package org.simplesystem.todoservice.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.simplesystem.todoservice.enums.Status;
import org.simplesystem.todoservice.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long> {

  List<Item> findItemByStatus(Status status);

  @Query("SELECT i.id FROM Item i WHERE i.status = :status AND i.dueDate < :currentTime")
  List<Long> findItemIdsByStatusAndDueDateBefore(
      @Param("status") Status status, @Param("currentTime") Instant currentTime);

  @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
  Optional<Item> findById(Long id);
}
