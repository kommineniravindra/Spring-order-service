FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean install -DskipTests

CMD ["java", "-jar", "target/order-service-0.0.1-SNAPSHOT.jar"]

EXPOSE 8084
