# --- Etapa 1: compilar con Maven ---
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Primero solo lo necesario para descargar dependencias (Docker cachea esta capa)
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
RUN ./mvnw -B -q -DskipTests package

# --- Etapa 2: imagen final, solo con el JRE y el .jar ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# No ejecutar la aplicación como root
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/target/originapi-*.jar app.jar
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
