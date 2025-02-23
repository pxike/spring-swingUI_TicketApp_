# Use an official Java runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file into the container
COPY target/Tickets-0.0.1-SNAPSHOT.jar /app/Tickets-0.0.1-SNAPSHOT.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/Tickets-0.0.1-SNAPSHOT.jar"]

# Expose the port the app runs on
EXPOSE 8080
