# Application

- Java 11
- Gradle
- Kotlin
- R2DBC (Postgres)
- Spring
- WebFlux
- coroutines

## Run

- Run app 
  ```shell
  make run
  ```

## API

- http://localhost:8080/docs-ui
- http://localhost:8080/actuator/health
- http://localhost:8080/actuator/health/liveness
- http://localhost:8080/actuator/health/readiness
- http://localhost:8080/actuator/prometheus

| URI pattern | Method | Description |
|-------------|--------|-------------|
| `/organizations` | `GET` | Get all organizations |
| `/organizations` | `POST` | Create a organization |
| `/organizations/{organization-id}` | `GET` | Get one organization |
| `/organizations/{organization-id}` | `PUT` | Update a organization |
| `/organizations/{organization-id}` | `DELETE` | Delete a organization |
| `/organizations/{organization-id}/roles` | `GET` | Roles from a organization |
| `/organizations/{organization-id}/roles/{role-id}` | `GET` | A role from a organization |

### Terminal example

```shell
curl -v -X POST 'http://localhost:8080/posts' \
  -H 'Content-Type: application/json' \
  --data-raw '{"title":"a new post", "content":"Blablabla"}'
```

Response header `Location` contains the new ID

## References
- https://spring.io/blog/2019/04/12/going-reactive-with-spring-coroutines-and-kotlin-flow
- https://github.com/hantsy/spring-kotlin-coroutines-sample/blob/master/data-r2dbc-fn/src/main/kotlin/com/example/demo/DemoApplication.kt
- https://www.tolkiana.com/introduction-to-spring-data-r2dbc-with-kotlin/
- https://github.com/tolkiana/liquibase-demo
- https://github.com/hantsy/spring-kotlin-coroutines-sample
- https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html
- https://hantsy.medium.com/reactive-accessing-rdbms-with-spring-data-r2dbc-d6e453f2837e
- https://docs.spring.io/spring-framework/docs/current/reference/html/languages.html#controllers
- https://www.baeldung.com/micrometer
- https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#actuator.metrics.export.prometheus

## TODO
- sec
- log
- unit test [Kotest](https://kotest.io/) + [Mockk](https://mockk.io/)
- integration test
- docker
- hex? entity, request, response, model
- kubernetes (Helm) deployments
- Spring Boot DevTools
- code formater [spotless](https://github.com/diffplug/spotless)
