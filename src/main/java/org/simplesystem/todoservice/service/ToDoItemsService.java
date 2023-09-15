package org.simplesystem.todoservice.service;

import static org.simplesystem.todoservice.utils.DtoEntityUtils.*;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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

    item.setDescription(description);
    return convertEntityToDto(repository.save(item), modelMapper);
  }

  public ItemDto updateItemStatus(long itemId, String status) {

    Item item =
        repository
            .findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

    if (EnumUtils.isValidEnum(Status.class, status)
        && !Objects.equals(status, Status.PAST_DUE.name())
        && !Objects.equals(item.getStatus().name(), Status.PAST_DUE.name())) {
      item.setStatus(Status.valueOf(status));
      item.setCompletionDate(Instant.now(Clock.systemUTC()));
    } else {
      throw new ValidationException(
          "Invalid status value is used to update Item with id : " + itemId);
    }
    return convertEntityToDto(repository.save(item), modelMapper);
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
    List<Item> items = repository.findByStatusAndDueDateBefore(Status.NOT_DONE, Instant.now());
    items.forEach(
        item -> {
          item.setStatus(Status.PAST_DUE);
          repository.save(item);
        });
  }
}
