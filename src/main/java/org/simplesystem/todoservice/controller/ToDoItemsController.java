package org.simplesystem.todoservice.controller;

import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.Valid;
import org.simplesystem.todoservice.dto.ItemDto;
import org.simplesystem.todoservice.enums.Status;
import org.simplesystem.todoservice.service.ToDoItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ToDoItemsController {

  @Autowired private ToDoItemsService toDoItemsService;

  @PostMapping("item")
  @ApiOperation(value = "Add an item")
  ResponseEntity<ItemDto> addItem(@RequestBody @Valid ItemDto itemDto) {
    itemDto = toDoItemsService.addItem(itemDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(itemDto);
  }

  @PatchMapping("items/{id}/description")
  @ApiOperation(value = "Update the description of an item")
  ResponseEntity<ItemDto> updateItemDescription(
      @PathVariable(name = "id") long itemId, @RequestBody @Valid String description) {
    ItemDto updatedItemDto = toDoItemsService.updateItemDescription(itemId, description);
    return ResponseEntity.status(HttpStatus.OK).body(updatedItemDto);
  }

  @PatchMapping("items/{id}/status")
  @ApiOperation(value = "Update the status of an item")
  ResponseEntity<ItemDto> updateItemStatus(
      @PathVariable(name = "id") long itemId, @RequestParam("status") @Valid Status status) {
    ItemDto updatedItemDto = toDoItemsService.updateItemStatus(itemId, status);
    return ResponseEntity.status(HttpStatus.OK).body(updatedItemDto);
  }

  @GetMapping("items/{id}")
  @ApiOperation(value = "Return an item details")
  ResponseEntity<ItemDto> getItem(@PathVariable(name = "id") long itemId) {
    ItemDto itemDto = toDoItemsService.getItemById(itemId);
    return itemDto != null
        ? ResponseEntity.status(HttpStatus.OK).body(itemDto)
        : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @GetMapping("items")
  @ApiOperation(value = "Return all the items or items which are marked not done")
  ResponseEntity<List<ItemDto>> getAllItemsOrByStatus(
      @RequestParam(value = "retrieveAll", required = false) boolean retrieveAll) {
    List<ItemDto> itemDtoList = toDoItemsService.getItems(retrieveAll);
    return !itemDtoList.isEmpty()
        ? ResponseEntity.status(HttpStatus.OK).body(itemDtoList)
        : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }
}
