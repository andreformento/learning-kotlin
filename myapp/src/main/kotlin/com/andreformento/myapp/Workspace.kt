package com.andreformento.myapp

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
import java.util.*

@Configuration
class WorkspaceRouterConfiguration {

    @Bean
    fun workspaceRoutes(workspaceHandler: WorkspaceHandler) = coRouter {
        accept(MediaType.APPLICATION_JSON).nest {
            "/workspaces".nest {
                GET("", workspaceHandler::all)
                GET("/{id}", workspaceHandler::get)
                POST("", workspaceHandler::create)
                PUT("/{id}", workspaceHandler::update)
                DELETE("/{id}", workspaceHandler::delete)
            }
        }
    }
}

@Component
class WorkspaceHandler(private val workspaces: WorkspaceRepository) {

    fun Any.toUUID(): UUID {
        return UUID.fromString(this.toString())
    }

    suspend fun all(req: ServerRequest): ServerResponse {
        return ok().bodyAndAwait(this.workspaces.findAll())
    }

    suspend fun create(req: ServerRequest): ServerResponse {
        val body = req.awaitBody<Workspace>()
        val createdWorkspace = this.workspaces.save(body)
        return created(URI.create("/workspaces/$createdWorkspace")).buildAndAwait()
    }

    suspend fun get(req: ServerRequest): ServerResponse {
        println("path variable::${req.pathVariable("id")}")
        val foundWorkspace = this.workspaces.findOne(req.pathVariable("id").toUUID())
        println("found workspace:$foundWorkspace")
        return when {
            foundWorkspace != null -> ok().bodyValueAndAwait(foundWorkspace)
            else -> notFound().buildAndAwait()
        }
    }

    suspend fun update(req: ServerRequest): ServerResponse {
        val foundWorkspace = this.workspaces.findOne(req.pathVariable("id").toUUID())
        val body = req.awaitBody<Workspace>()
        return when {
            foundWorkspace != null -> {
                this.workspaces.update(id = body.id!!, description = body.description!!)
                noContent().buildAndAwait()
            }
            else -> notFound().buildAndAwait()
        }

    }

    suspend fun delete(req: ServerRequest): ServerResponse {
        val deletedCount = this.workspaces.deleteById(req.pathVariable("id").toUUID())
        println("$deletedCount posts deleted")
        return noContent().buildAndAwait()
    }
}

@Component
interface WorkspaceRepository : CoroutineCrudRepository<Workspace, UUID> {

    @Query(
        """
        SELECT workspace FROM WHERE ID = :id
    """
    )
    suspend fun findOne(@Param("id") id: UUID): Workspace?


    @Modifying
    @Query("update posts set description = :description where id = :id")
    suspend fun update(
        @Param("id") id: UUID,
        @Param("description") description: String
    ): Int

}

@Table("workspace")
data class Workspace(
    @Id val id: UUID? = null,
    @Column("description") val description: String? = null
)
