package org.simplesystem.todoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
public class TodoServiceApp {

  public static void main(String[] args) {
    SpringApplication.run(TodoServiceApp.class, args);
  }
}
