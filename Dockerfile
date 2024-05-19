FROM openjdk:23-slim

# Set the working directory inside the container
WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven

# Copy the pom.xml and run Maven to install dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy the source code (excluding the target directory)
COPY src ./src

# Build the application using Maven
RUN mvn package

# Expose the port your application uses
EXPOSE 8080

# Mapeia o volume de imagens
VOLUME /app/imagespet
# Run the Spring Boot application as a JAR
CMD ["java", "-jar", "target/buscapet-0.0.1-SNAPSHOT.jar"]