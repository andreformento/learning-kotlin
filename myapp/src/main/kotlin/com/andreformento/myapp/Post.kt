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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import java.net.URI
import java.util.*

@RestController
@RequestMapping("posts")
class PostController(private val posts: PostRepository) {

    fun Any.toUUID(): UUID {
        return UUID.fromString(this.toString())
    }

    @GetMapping
    suspend fun all(): Flow<Post> {
        return this.posts.findAll()
    }

}

@Configuration
class PostRouterConfiguration {

    @Bean
    fun postRoutes(postHandler: PostHandler, commentHandler: CommentHandler) = coRouter {
        accept(MediaType.APPLICATION_JSON).nest {
            "/posts".nest {
                POST("", postHandler::create)
                "/{post-id}".nest {
                    GET("", postHandler::get)
                    PUT("", postHandler::update)
                    DELETE("", postHandler::delete)
                    "/comments".nest {
                        GET("", commentHandler::getCommentsByPost)
                        GET("/{comment-id}", commentHandler::getCommentByIdAndPost)
                    }
                }
            }
        }
    }
}

@Component
class PostHandler(private val posts: PostRepository) {

    fun Any.toUUID(): UUID {
        return UUID.fromString(this.toString())
    }

    suspend fun create(req: ServerRequest): ServerResponse {
        val body = req.awaitBody<Post>()
        val createdPost = this.posts.save(body)
        return created(URI.create("/posts/${createdPost.id}")).buildAndAwait()
    }

    suspend fun get(req: ServerRequest): ServerResponse {
        println("path variable::${req.pathVariable("post-id")}")
        val foundPost = this.posts.findOne(req.pathVariable("post-id").toUUID())
        println("found post:$foundPost")
        return when {
            foundPost != null -> ok().bodyValueAndAwait(foundPost)
            else -> notFound().buildAndAwait()
        }
    }

    suspend fun update(req: ServerRequest): ServerResponse {
        val postId = req.pathVariable("post-id").toUUID()
        val body = req.awaitBody<Post>()
        val updateResult = this.posts.update(id = postId, title = body.title!!, content = body.content!!)
        println("updateResult -> $updateResult")
        return noContent().buildAndAwait()
    }

    suspend fun delete(req: ServerRequest): ServerResponse {
        val deletedCount = this.posts.deleteById(req.pathVariable("id").toUUID())
        println("$deletedCount posts deleted")
        return noContent().buildAndAwait()
    }
}

@Component
class CommentHandler(private val comments: CommentRepository) {

    fun Any.toUUID(): UUID {
        return UUID.fromString(this.toString())
    }

    suspend fun getCommentsByPost(req: ServerRequest): ServerResponse {
        val postId = req.pathVariable("post-id").toUUID()
        println("get comments by post id $postId")
        return ok().bodyAndAwait(this.comments.getCommentsByPost(postId))
    }

    suspend fun getCommentByIdAndPost(req: ServerRequest): ServerResponse {
        val postId = req.pathVariable("post-id").toUUID()
        val commentId = req.pathVariable("comment-id").toUUID()
        println("get comment id $commentId from post id $postId")
        val foundEntity = this.comments.getCommentByIdAndPost(postId, commentId)
        println("found entity:$foundEntity")
        return when {
            foundEntity != null -> ok().bodyValueAndAwait(foundEntity)
            else -> notFound().buildAndAwait()
        }
    }
}

@Component
interface PostRepository : CoroutineCrudRepository<Post, UUID> {

    @Query(
        """
        SELECT * FROM post WHERE ID = :id
    """
    )
    suspend fun findOne(@Param("id") id: UUID): Post?


    @Modifying
    @Query("update post set title = :title, content = :content where id = :id")
    suspend fun update(
        @Param("id") id: UUID,
        @Param("title") title: String,
        @Param("content") content: String
    ): Int
}

@Component
interface CommentRepository : CoroutineCrudRepository<Comment, UUID> {

    @Query(
        """
        SELECT COUNT(*) FROM comment WHERE post_id = :postId
    """
    )
    suspend fun countByPostId(@Param("postId") postId: UUID): UUID

    @Query(
        """
        SELECT * FROM comment WHERE post_id = :postId
    """
    )
    suspend fun getCommentsByPost(@Param("postId") postId: UUID): Flow<Comment>

    @Query(
        """
        SELECT * FROM comment WHERE id = :commentId AND post_id = :postId
    """
    )
    suspend fun getCommentByIdAndPost(@Param("postId") postId: UUID, @Param("commentId") commentId: UUID): Comment?

}

@Table("comment")
data class Comment(
    @Id val id: UUID? = null,
    @Column("content") val content: String? = null,
    @Column("post_id") val postId: UUID? = null
)

@Table("post")
data class Post(
    @Id val id: UUID? = null,
    @Column("title") val title: String? = null,
    @Column("content") val content: String? = null
)
