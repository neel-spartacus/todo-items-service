package org.simplesystem.todoservice.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.simplesystem.todoservice.dto.ItemDto;
import org.simplesystem.todoservice.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ToDoItemsControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldAddAndReturnItem() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/item")
                .content(
                    asJsonString(
                        ItemDto.builder()
                            .description("item")
                            .dueDate(Instant.now().plus(1, ChronoUnit.DAYS))
                            .build()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists());

    mockMvc
        .perform(MockMvcRequestBuilders.get("/items/{id}", 1).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description").value("item"));
  }

  @Test
  public void shouldUpdateItemDescriptionAndStatus() throws Exception {

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/item")
                .content(
                    asJsonString(
                        ItemDto.builder()
                            .description("item")
                            .dueDate(Instant.now().plus(1, ChronoUnit.DAYS))
                            .build()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists());

    String newDescription = "item-modified";
    Status newStatus = Status.DONE;

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/items/{id}/description", 1)
                .content(newDescription)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.description").value("item-modified"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/items/{id}/status", 1)
                .param("status", String.valueOf(newStatus))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(Status.DONE.name()));
  }

  @Test
  public void shouldReturnAllItems() throws Exception {

    List<String> items = List.of("item-1", "item-2");
    items.stream()
        .forEach(
            item -> {
              try {
                mockMvc
                    .perform(
                        MockMvcRequestBuilders.post("/item")
                            .content(
                                asJsonString(
                                    ItemDto.builder()
                                        .description("item")
                                        .dueDate(Instant.now().plus(1, ChronoUnit.DAYS))
                                        .build()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists());
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            });
    boolean retrieveAll = true;
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/items")
                .param("retrieveAll", String.valueOf(retrieveAll))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(2));
  }

  public static String asJsonString(final Object obj) {
    try {
      ObjectMapper mapper =
          JsonMapper.builder()
              .addModule(new ParameterNamesModule())
              .addModule(new Jdk8Module())
              .addModule(new JavaTimeModule())
              .build();
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
