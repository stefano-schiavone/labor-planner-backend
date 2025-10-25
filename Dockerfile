# Image
FROM 192.168.120.135:5000/eclipse-temurin:21-jdk-jammy AS runtime
WORKDIR /app

# Copy the runnable jar produced by Gradle
# Adjust the jar file name if your project uses version suffixes. We'll expect one jar in build/libs.
COPY build/libs/*.jar app.jar

# Use an unprivileged user
# Docker Best Practice, creates a user group called `app` and a user called `app`. These have `--system` ensures it has no login shell and no home directory
# reducing security risks
RUN addgroup --system app && adduser --system --ingroup app app
# All future commands will be run using the secure `app` user
USER app

EXPOSE 8080

# JAVA_OPTS gives me runitme flags
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:-} -jar /app/app.jar"]
