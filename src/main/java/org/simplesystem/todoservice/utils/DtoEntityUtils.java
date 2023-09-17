package org.simplesystem.todoservice.utils;

import org.modelmapper.ModelMapper;
import org.simplesystem.todoservice.dto.ItemDto;
import org.simplesystem.todoservice.model.Item;

public class DtoEntityUtils {
  public static ItemDto convertEntityToDto(Item item, ModelMapper modelMapper) {
    return modelMapper.map(item, ItemDto.class);
  }
}
