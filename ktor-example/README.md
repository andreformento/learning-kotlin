# Ktor example

## Requirements

- Java 8

## app
- run: `./gradlew run`
- [people endpoint](http://localhost:8081/v1/people)
- [metrics](http://localhost:8081/metrics)

## with docker
```shell
docker build -t andreformento/ktor-example .
docker run --rm \
           -p 8081:8081 \
           -v $(pwd)/resources:/root/resources:ro \
           -e CONFIG_FILE_PATH=/root/resources/application.conf \
           -e LOGGING_FILE_PATH=/root/resources/logback.xml \
           andreformento/ktor-example
```

## References

- https://start.ktor.io
- https://ktor.io/docs/gson.html#register_gson_converter
- https://ktor.io/docs/serialization.html#receive_data
- https://github.com/ktorio/ktor-documentation/blob/main/codeSnippets/snippets/gson/src/GsonApplication.kt
- https://github.com/lukas-krecan/JsonUnit#kotlin-support
- https://ktor.io/docs/features-locations.html#route-classes
- https://ktor.io/docs/micrometer-metrics.html
- https://medium.com/@math21/how-to-monitor-a-ktor-server-using-grafana-bab54a9ac0dc
