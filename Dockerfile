# ==========================================
# ETAPA 1: Build (Compilación y Empaquetado)
# ==========================================
FROM maven:3.9.8-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copiar configuración de dependencias para aprovechar el caché de capas de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y empaquetar omitiendo los tests en el build de la imagen
COPY src ./src
RUN mvn package -DskipTests

# ==========================================
# ETAPA 2: Runtime (Ejecución de la app)
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario sin privilegios para ejecutar la aplicación de forma segura
RUN addgroup -S wmsgroup && adduser -S wmsuser -G wmsgroup
USER wmsuser:wmsgroup

# Copiar el artefacto generado desde la etapa de compilación
COPY --from=builder /app/target/*.jar app.jar

# Puerto configurado para Spring Boot
EXPOSE 8080

# Parámetros óptimos de memoria para contenedores Java y ejecución
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]