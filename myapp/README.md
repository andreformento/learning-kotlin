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

http://localhost:8080/docs-ui

| URI pattern | Method | Description |
|-------------|--------|-------------|
| `/posts` | `GET` | Get all posts |
| `/posts` | `POST` | Create a post |
| `/posts/{post-id}` | `GET` | Get one post |
| `/posts/{post-id}` | `PUT` | Update a post |
| `/posts/{post-id}` | `DELETE` | Delete a post |
| `/posts/{post-id}/comments` | `GET` | Comments from a post |
| `/posts/{post-id}/comments/{comment-id}` | `GET` | A comment from a post |

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

## TODO
- sec
- log
- unit test
- integration test
- docker
- hex? entity, request, response, model
