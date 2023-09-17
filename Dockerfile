# Use the official OpenJDK base image
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from your build target into the container
COPY target/ToDoServiceManager-1.0-SNAPSHOT.jar /app/ToDoServiceManager-1.0-SNAPSHOT.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Specify the command to run your Spring Boot application
CMD ["java", "-jar", "ToDoServiceManager-1.0-SNAPSHOT.jar"]
