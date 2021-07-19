package com.andreformento.myapp.infra

import com.andreformento.myapp.post.Post
import com.andreformento.myapp.post.repository.Posts
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.runBlocking
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class PostgresHealthIndicator(private val posts: Posts): HealthIndicator {
    override fun health(): Health {
        return try {
//            val posts = GlobalScope.async { posts.findAll() }
            val a: Flow<Post> = runBlocking { posts.findAll() }

//            println("posts ${posts.}")
            Health.up().build();
        } catch (e: Exception) {
            Health.down().withDetail("Cannot connect to database", 503).build();
        }
    }
}
