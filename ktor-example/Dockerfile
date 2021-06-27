# syntax = docker/dockerfile:experimental

#############
# Build App
FROM openjdk:15-jdk as build

ENV APP_HOME=/root/

WORKDIR $APP_HOME
ADD gradle gradle
ADD build.gradle.kts build.gradle.kts
ADD gradlew gradlew
ADD settings.gradle.kts settings.gradle.kts
ADD gradle.properties gradle.properties

ADD src src
ADD test test

RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=cache,target=/root/.gradle \
    ./gradlew build --no-daemon

# #############
# Final image
FROM openjdk:15-jdk

ENV APP_HOME=/root
WORKDIR $APP_HOME

ADD start.sh start.sh
COPY --from=build /root/build/libs/*shadow*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["/root/start.sh"]
