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
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route


@Configuration
class PostRouterConfiguration {

    @Bean
    fun postRoutes(postHandler: PostHandler, commentHandler: CommentHandler) = coRouter {
        accept(MediaType.APPLICATION_JSON).nest {
            "/posts".nest {
                GET("", postHandler::all)
//                route().GET("/a",postHandler::all, ops -> ops).
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

    suspend fun all(req: ServerRequest): ServerResponse {
        return ok().bodyAndAwait(this.posts.findAll())
    }

    suspend fun create(req: ServerRequest): ServerResponse {
        val body = req.awaitBody<Post>()
        val createdPost = this.posts.save(body)
        return created(URI.create("/posts/${createdPost.id}")).buildAndAwait()
    }

    suspend fun get(req: ServerRequest): ServerResponse {
        println("path variable::${req.pathVariable("post-id")}")
        val foundPost = this.posts.findOne(req.pathVariable("post-id").toLong())
        println("found post:$foundPost")
        return when {
            foundPost != null -> ok().bodyValueAndAwait(foundPost)
            else -> notFound().buildAndAwait()
        }
    }

    suspend fun update(req: ServerRequest): ServerResponse {
        val postId = req.pathVariable("post-id").toLong()
        val body = req.awaitBody<Post>()
        val updateResult = this.posts.update(id = postId, title = body.title!!, content = body.content!!)
        println("updateResult -> $updateResult")
        return noContent().buildAndAwait()
    }

    suspend fun delete(req: ServerRequest): ServerResponse {
        val deletedCount = this.posts.deleteById(req.pathVariable("id").toLong())
        println("$deletedCount posts deleted")
        return noContent().buildAndAwait()
    }
}

@Component
class CommentHandler(private val comments: CommentRepository) {

    suspend fun getCommentsByPost(req: ServerRequest): ServerResponse {
        val postId = req.pathVariable("post-id").toLong()
        println("get comments by post id $postId")
        return ok().bodyAndAwait(this.comments.getCommentsByPost(postId))
    }

    suspend fun getCommentByIdAndPost(req: ServerRequest): ServerResponse {
        val postId = req.pathVariable("post-id").toLong()
        val commentId = req.pathVariable("comment-id").toLong()
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
interface PostRepository : CoroutineCrudRepository<Post, Long> {

    @Query(
        """
        SELECT * FROM posts WHERE ID = :id
    """
    )
    suspend fun findOne(@Param("id") id: Long): Post?


    @Modifying
    @Query("update posts set title = :title, content = :content where id = :id")
    suspend fun update(
        @Param("id") id: Long,
        @Param("title") title: String,
        @Param("content") content: String
    ): Int
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
    suspend fun getCommentsByPost(@Param("postId") postId: Long): Flow<Comment>

    @Query(
        """
        SELECT * FROM comments WHERE id = :commentId AND post_id = :postId
    """
    )
    suspend fun getCommentByIdAndPost(@Param("postId") postId: Long, @Param("commentId") commentId: Long): Comment?

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
