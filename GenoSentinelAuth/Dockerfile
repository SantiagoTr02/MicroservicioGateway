# Dockerfile (versión que SÍ funciona siempre)
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Copiar solo lo necesario para aprovechar caché
COPY pom.xml .
COPY src ./src
# Si tienes mvnw (recomendado)
COPY mvnw .
COPY .mvn ./.mvn

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Etapa final: imagen ligera
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el JAR generado (el nombre exacto de tu proyecto)
COPY --from=builder /workspace/target/auth-0.0.1-SNAPSHOT.jar app.jar

# Seguridad: usuario no-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

# Recomendado para entornos containerizados
ENV JAVA_OPTS="-Xms512m -Xmx1024m" \
    SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]