package com.andreformento.myapp

import kotlinx.coroutines.flow.Flow
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import java.net.URI

@Configuration
class RouterConfiguration {

    @Bean
    fun routes(postHandler: PostHandler) = coRouter {
        accept(MediaType.APPLICATION_JSON).nest {
            "/posts".nest {
                GET("", postHandler::all)
                GET("/{id}", postHandler::get)
                POST("", postHandler::create)
                PUT("/{id}", postHandler::update)
                DELETE("/{id}", postHandler::delete)
            }
        }
    }
}

@Component
class PostHandler(private val posts: PostRepository) {

    suspend fun all(req: ServerRequest): ServerResponse {
        return ok().bodyAndAwait(this.posts.findAll())
    }

    suspend fun create(req: ServerRequest): ServerResponse {
        val body = req.awaitBody<Post>()
        val createdPost = this.posts.save(body)
        return created(URI.create("/posts/$createdPost")).buildAndAwait()
    }

    suspend fun get(req: ServerRequest): ServerResponse {
        println("path variable::${req.pathVariable("id")}")
        val foundPost = this.posts.findOne(req.pathVariable("id").toLong())
        println("found post:$foundPost")
        return when {
            foundPost != null -> ok().bodyValueAndAwait(foundPost)
            else -> notFound().buildAndAwait()
        }
    }

    suspend fun update(req: ServerRequest): ServerResponse {
        val foundPost = this.posts.findOne(req.pathVariable("id").toLong())
        val body = req.awaitBody<Post>()
        return when {
            foundPost != null -> {
                this.posts.update(id = body.id!!, title = body.title!!, content = body.content!!)
                noContent().buildAndAwait()
            }
            else -> notFound().buildAndAwait()
        }

    }

    suspend fun delete(req: ServerRequest): ServerResponse {
        val deletedCount = this.posts.deleteById(req.pathVariable("id").toLong())
        println("$deletedCount posts deleted")
        return noContent().buildAndAwait()
    }
}

@Component
interface PostRepository : CoroutineCrudRepository<Post, Long> {

    @Query(
        """
        SELECT posts FROM WHERE ID = :id
    """
    )
    suspend fun findOne(@Param("id") id: Long): Post?


    @Modifying
    @Query("update posts set title = :title, content = :content where id = :id")
    suspend fun update(@Param("id") id: Long,
                       @Param("title") title: String,
                       @Param("content") content: String): Int
}

@Component
interface CommentRepository : CoroutineCrudRepository<Comment, Long> {

    @Query(
        """
        SELECT COUNT(*) FROM comments WHERE post_id = :postId
    """
    )
    suspend fun countByPostId(@Param("postId") postId: Long): Long

    @Query(
        """
        SELECT * FROM comments WHERE post_id = :postId
    """
    )
    suspend fun findByPostId(@Param("postId") postId: Long): Flow<Comment>

}

@Table("comments")
data class Comment(
    @Id val id: Long? = null,
    @Column("content") val content: String? = null,
    @Column("post_id") val postId: Long? = null
)

@Table("posts")
data class Post(
    @Id val id: Long? = null,
    @Column("title") val title: String? = null,
    @Column("content") val content: String? = null
)
