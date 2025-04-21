# Etapa de construcción
FROM gradle:8.5-jdk21-alpine AS builder
WORKDIR /app

# Copia los archivos de configuración de gradle y el wrapper
COPY gradlew build.gradle settings.gradle /app/
COPY gradle /app/gradle

# Descarga dependencias
RUN ./gradlew build --no-daemon -x test || return 0

# Copia el resto del proyecto
COPY . /app

# Construye el jar sin ejecutar tests
RUN ./gradlew bootJar --no-daemon -x test

# Imagen final
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Carpeta para logs
RUN mkdir ./logs

# Copia el jar construido desde la imagen anterior
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Expone el puerto
EXPOSE 8080

# Comando para ejecutar el jar
ENTRYPOINT ["java", "-jar", "app.jar"]