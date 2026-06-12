# Etapa de construcción
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# CONFIGURACIÓN DE IDIOMA PARA EVITAR ERRORES DE CARACTERES
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Etapa de ejecución (imagen ligera)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# TAMBIÉN AQUÍ POR SEGURIDAD
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]