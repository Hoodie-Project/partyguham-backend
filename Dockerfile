FROM eclipse-temurin:21-jdk

LABEL maintainer="partyguham"
LABEL version="0.0.1"

WORKDIR /app
COPY build/libs/partyguham-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]