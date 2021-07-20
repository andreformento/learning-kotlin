package com.andreformento.myapp.infra

import com.andreformento.myapp.post.Post
import com.andreformento.myapp.post.repository.Posts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class PostgresHealthIndicator(private val posts: Posts) : HealthIndicator {
    // TODO this is the best way? I hope not!
    override fun health(): Health {
        return try {
            var firstPost: Post?
            runBlocking(Dispatchers.Default) {
                firstPost = posts.findAll().firstOrNull()
            }

            if (firstPost == null) {
                Health.down().withDetail("Cannot get any data from database", object {}).build()
            } else {
                println("firstPost $firstPost")
                Health.up().build()
            }
        } catch (e: Exception) {
            Health.down().withDetail("Cannot connect to database", e).build()
        }
    }
}
