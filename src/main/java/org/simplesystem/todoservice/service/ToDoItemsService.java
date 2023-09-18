package org.simplesystem.todoservice.service;

import static org.simplesystem.todoservice.utils.DtoEntityUtils.*;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.modelmapper.ModelMapper;
import org.simplesystem.todoservice.dto.ItemDto;
import org.simplesystem.todoservice.enums.Status;
import org.simplesystem.todoservice.exceptions.ResourceNotFoundException;
import org.simplesystem.todoservice.exceptions.ValidationException;
import org.simplesystem.todoservice.model.Item;
import org.simplesystem.todoservice.repository.ItemRepository;
import org.simplesystem.todoservice.utils.DtoEntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class ToDoItemsService {

  private ModelMapper modelMapper;
  private ItemRepository repository;

  @Autowired
  public ToDoItemsService(ModelMapper modelMapper, ItemRepository repository) {
    this.modelMapper = modelMapper;
    this.repository = repository;
  }

  public ItemDto addItem(ItemDto itemDto) {
    if (Objects.nonNull(itemDto.getDueDate())
        && itemDto.getDueDate().isBefore(Instant.now(Clock.systemUTC()))) {
      throw new ValidationException("Due date time cannot be before current time");
    }
    Item newItem = new Item();
    newItem.setDescription(itemDto.getDescription());
    newItem.setStatus(Status.NOT_DONE);
    newItem.setCreationDate(Instant.now(Clock.systemUTC()));
    newItem.setDueDate(itemDto.getDueDate());
    newItem = repository.save(newItem);
    ItemDto newItemDto = convertEntityToDto(newItem, modelMapper);
    log.info("Item added with id: {}", newItemDto.getId());
    return newItemDto;
  }

  public ItemDto getItemById(long itemId) {
    Optional<Item> item = repository.findById(itemId);
    ItemDto itemDto = null;
    if (item.isPresent()) {
      itemDto = convertEntityToDto(item.get(), modelMapper);
    }
    return itemDto;
  }

  public ItemDto updateItemDescription(long itemId, String description) {

    Item item =
        repository
            .findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

    if (Objects.equals(item.getStatus(), Status.PAST_DUE)) {
      throw new ValidationException("Item with status as past due cannot be updated");
    }

    item.setDescription(description);
    try {
      return convertEntityToDto(repository.save(item), modelMapper);
    } catch (ObjectOptimisticLockingFailureException ex) {
      throw new OptimisticLockingFailureException(
          "Item with: " + itemId + " is already locked by another transaction");
    }
  }

  public ItemDto updateItemStatus(long itemId, Status status) {
    Item item =
        repository
            .findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));
    if (Objects.equals(item.getStatus(), Status.PAST_DUE)) {
      throw new ValidationException("Item with status as past due cannot be updated");
    }
    if (EnumUtils.isValidEnum(Status.class, status.name())
        && !Objects.equals(status, Status.PAST_DUE)) {
      item.setStatus(status);
      if (Objects.equals(item.getStatus(), Status.DONE)) {
        item.setCompletionDate(Instant.now(Clock.systemUTC()));
      }

    } else {
      throw new ValidationException(
          "Invalid status value is used to update Item with id : " + itemId);
    }
    try {
      return convertEntityToDto(repository.save(item), modelMapper);
    } catch (ObjectOptimisticLockingFailureException ex) {
      throw new OptimisticLockingFailureException(
          "Item with: " + itemId + " is already locked by another transaction");
    }
  }

  public List<ItemDto> getItems(boolean retrieveAll) {
    List<Item> items;
    if (retrieveAll) {
      items = repository.findAll();
    } else {
      items = repository.findItemByStatus(Status.NOT_DONE);
    }

    return items.stream()
        .map(item -> DtoEntityUtils.convertEntityToDto(item, modelMapper))
        .collect(Collectors.toList());
  }

  @Scheduled(fixedRate = 60000)
  public void updateStatusForPastDueItems() {
    List<Long> itemIds =
        repository.findItemIdsByStatusAndDueDateBefore(Status.NOT_DONE, Instant.now());

    List<Long> lockedItemsWithException = new ArrayList<>();
    for (Long itemId : itemIds) {
      try {
        Optional<Item> optionalItem = repository.findById(itemId);
        if (optionalItem.isPresent()) {
          Item item = optionalItem.get();
          item.setStatus(Status.PAST_DUE);
          repository.save(item);
        }
      } catch (ObjectOptimisticLockingFailureException ex) {
        log.error("Item with: " + itemId + " is already locked by another transaction");
        lockedItemsWithException.add(itemId);
      }
    }
    if (!lockedItemsWithException.isEmpty()) {
      String exceptionIds =
          lockedItemsWithException.stream()
              .map(itemId -> itemId.toString())
              .collect(Collectors.joining(","));
      throw new OptimisticLockingFailureException(
          "Item with ids: "
              + " [ "
              + exceptionIds
              + " ] "
              + " were not updated as they were locked by other transactions");
    }
  }
}
