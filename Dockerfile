# Use Java 21 runtime
FROM eclipse-temurin:21-jdk-jammy AS runtime
WORKDIR /app

# Copy the runnable jar produced by Gradle
# Adjust the jar file name if your project uses version suffixes. We'll expect one jar in build/libs.
COPY build/libs/*.jar app.jar

# Use an unprivileged user (optional)
RUN addgroup --system app && adduser --system --ingroup app app
USER app

EXPOSE 8080

# Use JAVA_OPTS pattern for runtime flags
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:-} -jar /app/app.jar"]
