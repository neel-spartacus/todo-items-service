package org.simplesystem.todoservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.simplesystem.todoservice.dto.ItemDto;
import org.simplesystem.todoservice.enums.Status;
import org.simplesystem.todoservice.exceptions.ValidationException;
import org.simplesystem.todoservice.model.Item;
import org.simplesystem.todoservice.repository.ItemRepository;

@ExtendWith(MockitoExtension.class)
public class ToDoItemsServiceTest {

  @Mock private ItemRepository repository;

  @Mock private ModelMapper modelMapper;

  @InjectMocks private ToDoItemsService toDoItemsService;

  @Test
  void shouldAddItem() {

    // Given
    Instant dueDate = Instant.now().plus(1, ChronoUnit.DAYS);
    Instant creationDate = Instant.now();
    ItemDto itemDto =
        ItemDto.builder().description("Item-1").dueDate(dueDate).status(Status.NOT_DONE).build();
    Item item =
        Item.builder()
            .id(1L)
            .description("Item-1")
            .dueDate(dueDate)
            .creationDate(creationDate)
            .status(Status.NOT_DONE)
            .build();
    ItemDto expectedItemDto =
        ItemDto.builder()
            .id(1L)
            .description("Item-1")
            .dueDate(dueDate)
            .creationDate(creationDate)
            .status(Status.NOT_DONE)
            .build();
    Mockito.when(repository.save(any(Item.class))).thenReturn(item);
    Mockito.when(modelMapper.map(eq(item), eq(ItemDto.class))).thenReturn(expectedItemDto);

    // When
    ItemDto itemDTo = toDoItemsService.addItem(itemDto);

    // Then
    Mockito.verify(repository, Mockito.times(1)).save(any(Item.class));
    assertEquals(1L, itemDTo.getId().longValue());
    assertNotNull(itemDTo.getCreationDate());
  }

  @Test
  void shouldThrowValidationExceptionWhenDueDateTimeIsBeforeCurrentDateTimeWhileAddingAnItem() {
    // Given
    Instant dueDate = Instant.now().minus(1, ChronoUnit.DAYS);
    ItemDto itemDto =
        ItemDto.builder().description("Item-1").dueDate(dueDate).status(Status.NOT_DONE).build();
    // When/Then
    Throwable exception =
        assertThrows(ValidationException.class, () -> toDoItemsService.addItem(itemDto));

    assertEquals("Due date time cannot be before current time", exception.getMessage());
  }

  @Test
  void shouldReturnAnItem() {
    // Given
    long itemId = 1L;
    Instant dueDate = Instant.now().plus(1, ChronoUnit.DAYS);
    Instant creationDate = Instant.now();
    ItemDto itemDto =
        ItemDto.builder()
            .id(1L)
            .description("Item-1")
            .dueDate(dueDate)
            .status(Status.NOT_DONE)
            .build();
    Item item =
        Item.builder()
            .id(1L)
            .description("Item-1")
            .dueDate(dueDate)
            .creationDate(creationDate)
            .status(Status.NOT_DONE)
            .build();
    Mockito.when(repository.findById(any(Long.class))).thenReturn(Optional.of(item));
    Mockito.when(modelMapper.map(eq(item), eq(ItemDto.class))).thenReturn(itemDto);

    // When
    itemDto = toDoItemsService.getItemById(itemId);

    // Then
    assertEquals(1L, itemDto.getId().longValue());
  }

  @Test
  void shouldReturnAllItems() {
    // Given
    Instant dueDate = Instant.now().plus(1, ChronoUnit.DAYS);
    Instant creationDate = Instant.now();
    Item item1 =
        Item.builder()
            .id(1L)
            .description("Item-1")
            .dueDate(dueDate)
            .creationDate(creationDate)
            .status(Status.NOT_DONE)
            .build();

    Item item2 =
        Item.builder()
            .id(2L)
            .description("Item-2")
            .dueDate(dueDate)
            .creationDate(Instant.now(Clock.systemUTC()))
            .status(Status.NOT_DONE)
            .build();

    ItemDto expectedItemDto1 =
        ItemDto.builder()
            .id(1L)
            .description("Item-1")
            .dueDate(dueDate)
            .creationDate(creationDate)
            .status(Status.NOT_DONE)
            .build();

    ItemDto expectedItemDto2 =
        ItemDto.builder()
            .id(2L)
            .description("Item-1")
            .dueDate(dueDate)
            .creationDate(creationDate)
            .status(Status.NOT_DONE)
            .build();

    Mockito.when(repository.findAll()).thenReturn(List.of(item1, item2));
    Mockito.when(modelMapper.map(eq(item1), eq(ItemDto.class))).thenReturn(expectedItemDto1);
    Mockito.when(modelMapper.map(eq(item2), eq(ItemDto.class))).thenReturn(expectedItemDto2);

    // When
    List<ItemDto> itemDtoList = toDoItemsService.getItems(true);

    // Then
    assertTrue(itemDtoList.size() == 2);
  }

  @Test
  void shouldReturnItemsWithStatusNotDone() {
    // Given
    Instant dueDate = Instant.now().plus(1, ChronoUnit.DAYS);
    Instant creationDate = Instant.now();
    Item item1 =
        Item.builder()
            .id(1L)
            .description("Item-1")
            .dueDate(dueDate)
            .creationDate(creationDate)
            .status(Status.NOT_DONE)
            .build();
    Item item2 =
        Item.builder()
            .id(1L)
            .description("Item-2")
            .dueDate(dueDate)
            .creationDate(creationDate)
            .status(Status.DONE)
            .build();

    ItemDto expectedItemDto1 =
        ItemDto.builder()
            .id(1L)
            .description("Item-1")
            .dueDate(dueDate)
            .creationDate(creationDate)
            .status(Status.NOT_DONE)
            .build();

    ItemDto expectedItemDto2 =
        ItemDto.builder()
            .id(2L)
            .description("Item-2")
            .dueDate(dueDate)
            .creationDate(creationDate)
            .status(Status.NOT_DONE)
            .build();

    Mockito.when(repository.findAll()).thenReturn(List.of(item1, item2));
    Mockito.when(repository.findItemByStatus(any(Status.class))).thenReturn(List.of(item1));
    Mockito.when(modelMapper.map(eq(item1), eq(ItemDto.class))).thenReturn(expectedItemDto1);
    Mockito.when(modelMapper.map(eq(item2), eq(ItemDto.class))).thenReturn(expectedItemDto2);

    // When
    List<ItemDto> allItems = toDoItemsService.getItems(true);
    List<ItemDto> itemsWithNotDone = toDoItemsService.getItems(false);

    // Then
    assertTrue(allItems.size() == 2);
    assertTrue(itemsWithNotDone.size() == 1);
  }

  @Test
  public void testUpdateStatusForPastDueItems() {

    // Given
    Instant now = Instant.now();
    Item item1 =
        Item.builder()
            .id(1L)
            .status(Status.NOT_DONE)
            .description("Item-1")
            .dueDate(now.minus(Duration.ofDays(1)))
            .build();
    Item item2 =
        Item.builder()
            .id(1L)
            .status(Status.NOT_DONE)
            .description("Item-2")
            .dueDate(now.plus(Duration.ofDays(1)))
            .build();
    Mockito.when(repository.findByStatusAndDueDateBefore(any(Status.class), any(Instant.class)))
        .thenReturn(Arrays.asList(item1));

    // When
    toDoItemsService.updateStatusForPastDueItems();

    // Then
    Mockito.verify(repository, Mockito.times(1)).save(item1);
    Mockito.verify(repository, Mockito.times(0)).save(item2);
  }
}
