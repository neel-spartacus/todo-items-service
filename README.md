# Todo-items-service
Solution for a problem statement for managing toto items

Pre-conditions:

**System requirements:**

1)OpenJDK for Java 11
2)Maven
3)Junit
4)Project Lombok: https://projectlombok.org
5)OpenApi Swagger
6)Spring Boot
7)Docker

**Building the project:**

To build the JAR and run some tests:
** mvn clean install**

Run mvn spotless:check to check for violations.

Run mvn spotless:apply to fix violations.

To run the application:
**java -jar target/ToDoServiceManager-1.0-SNAPSHOT.jar**
or
Running as SpringBoot application : TodoServiceApp.java

Swagger UI: http://localhost:8080/swagger-ui/#

To run as a dockerized service:

Install a docker engine.I used Docker for Windows: https://docs.docker.com/engine/install/

Build docker image: docker build -t todoitems-service-image .
Run the container: docker-compose up -d

Notes/Assumptions about the assignment:

I have added few services to add an item,modify an item and return item/items.
When an item is created, by default the status is NOT_DONE.Also, the api forbids any status update of
an item to PAST_DUE through the status update api exposed,only can be done by the scheduler.
There is also a scheduled api which runs every 60 seconds and retrieves the items which have
status NOT_DONE and past due the due date and updates their status as PAST_DUE.

In my opinion,there might be race condition when the above scheduled api retrieves certain items
to update and in the same time,another transaction updates an item which was supposed to be in the scheduler list,
in that case an optimistic lock will be used for whichever transactions queries for the item first.