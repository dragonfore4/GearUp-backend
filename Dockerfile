# Use OpenJDK 21
FROM openjdk:21-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/ecommerce-0.0.1-SNAPSHOT.jar app.jar

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
