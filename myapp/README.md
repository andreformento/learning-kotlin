# Application

- Java 11
- Gradle
- Kotlin
- R2DBC (Postgres)
- Spring
- WebFlux
- coroutines

## Run

- Create database _(Postgres)_
  ```shell
  make database
  ```

- Run app 
  ```shell
  make run
  ```

## API

| URI pattern | Method | Description |
|-------------|--------|-------------|
| `/posts` | `GET` | Get all posts |
| `/posts/{id}` | `GET` | Get one post |
| `/posts` | `POST` | Create a post |
| `/posts/{id}` | `PUT` | Update a post |
| `/posts/{id}` | `DELETE` | Delete a post |
| `/workspaces` | `GET` | Get all workspaces |
| `/workspaces/{id}` | `GET` | Get one workspace |
| `/workspaces` | `POST` | Create a workspace |
| `/workspaces/{id}` | `PUT` | Update a workspace |
| `/workspaces/{id}` | `DELETE` | Delete a workspace |

## References
- https://spring.io/blog/2019/04/12/going-reactive-with-spring-coroutines-and-kotlin-flow
- https://github.com/hantsy/spring-kotlin-coroutines-sample/blob/master/data-r2dbc-fn/src/main/kotlin/com/example/demo/DemoApplication.kt
- https://www.tolkiana.com/introduction-to-spring-data-r2dbc-with-kotlin/
- https://github.com/tolkiana/liquibase-demo
- https://github.com/hantsy/spring-kotlin-coroutines-sample
- https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html
- https://hantsy.medium.com/reactive-accessing-rdbms-with-spring-data-r2dbc-d6e453f2837e

## TODO
- swagger
- sec
