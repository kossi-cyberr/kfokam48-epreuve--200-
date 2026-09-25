# =====================================================================
# Backend Spring Boot — build multi-stage
#   1) build : Maven compile et génère le jar (cache .m2 persistant BuildKit)
#   2) run   : JRE minimal, non-root, sans sources
# =====================================================================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Cache de dépendances monté pendant le build : les téléchargements sont
# conservés d'une construction à l'autre, même si la couche est interrompue.
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn -B -q -DskipTests package

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
