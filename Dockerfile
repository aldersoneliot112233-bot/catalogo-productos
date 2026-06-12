FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Forzar UTF-8 en todo el contenedor
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

COPY . .
RUN chmod +x mvnw

# Forzar encoding UTF-8 en Maven explícitamente
RUN ./mvnw clean package -DskipTests -Dproject.build.sourceEncoding=UTF-8

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]