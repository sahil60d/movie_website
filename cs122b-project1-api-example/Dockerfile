# Maven Install
FROM maven:3.8.5-openjdk-11-slim AS builder

# Create app directory
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . .

# Build the app
RUN mvn clean package

# Tomcat Install
FROM tomcat:10-jdk11

# Create app directory
WORKDIR /app

# Copy the war file from the builder stage to the tomcat webapps directory
COPY --from=builder /app/target/cs122b-project1-api-example.war /usr/local/tomcat/webapps/cs122b-project1-api-example.war

# Expose port 8080 to the outside world
EXPOSE 8080

# Run the war file
CMD ["catalina.sh", "run"]