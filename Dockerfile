FROM maven:3-openjdk-11 as builder
WORKDIR /app
COPY . .

RUN mvn clean package

FROM adoptopenjdk/openjdk11
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=prod" ,"app.jar"]
