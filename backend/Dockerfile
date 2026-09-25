# =====================================================================
# Backend Spring Boot — build multi-stage
#   1) build : Maven compile et génère le jar
#   2) run   : JRE minimal, non-root, sans sources
# =====================================================================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Répertoire Maven de l'hôte fourni par le contexte nommé "m2" (cf. docker-compose.yml)
# : les dépendances déjà téléchargées sont réutilisées => build rapide, même hors-ligne.
COPY --from=m2 / /root/.m2/repository

COPY pom.xml .
COPY src ./src

# Mode hors-ligne d'abord (dépôt local complet), repli en ligne si un artefact manque
RUN mvn -B -q -o -DskipTests package || mvn -B -DskipTests package

# ------------------------------ image finale ------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app

COPY --from=build /app/target/*.jar app.jar

USER app
EXPOSE 8080

# Santé de l'application (endpoint Spring Actuator)
HEALTHCHECK --interval=15s --timeout=5s --start-period=45s --retries=5 \
    CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
