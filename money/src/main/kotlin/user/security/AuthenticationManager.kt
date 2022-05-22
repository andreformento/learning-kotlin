package com.andreformento.money.user.security

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.role.OrganizationRole
import com.andreformento.money.organization.role.repository.OrganizationRoles
import com.andreformento.money.user.UserFacade
import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseCookie
import org.springframework.http.server.RequestPath
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

data class CurrentUserAuthentication(
        private val currentUser: CurrentUser,
        private val credentials: String,
        private val authorities: MutableCollection<out GrantedAuthority>
) : Authentication {
    override fun getName() = currentUser.name

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities

    override fun getCredentials(): String = credentials

    //    TODO Stores additional details about the authentication request. These might be an IP address, certificate serial number etc.
    override fun getDetails(): Any = TODO("Not yet implemented")

    override fun getPrincipal(): CurrentUser = this.currentUser

    override fun isAuthenticated() = true

    override fun setAuthenticated(isAuthenticated: Boolean) {}

}

data class CurrentUserOrganizationAuthentication constructor(
        private val organizationRole: OrganizationRole,
        private val credentials: String,
        private val authorities: MutableCollection<out GrantedAuthority>
) : Authentication {

    override fun getName() = organizationRole.user.name

    override fun getAuthorities() = authorities

    override fun getCredentials() = credentials

    override fun getDetails(): Any = TODO("Not yet implemented")

    override fun getPrincipal(): OrganizationRole = organizationRole

    override fun isAuthenticated() = true

    override fun setAuthenticated(isAuthenticated: Boolean) {}
}

@Component
class TokenCookieHandler(
        private val tokenSigner: TokenSigner,
) {

    private val cookieName = "X-Auth"

    fun extractOrganizationId(path: RequestPath): OrganizationId? = when {
        path.elements().size >= 4 && path.subPath(1, 2).value() == "organizations" -> OrganizationId.fromString(path.subPath(3).value())
        else -> null
    }

    fun extractFromCookie(request: ServerHttpRequest): UsernamePasswordAuthenticationToken? = request
            .cookies[cookieName]
            ?.takeIf { it.isNotEmpty() }
            ?.let { it[0].value }
            ?.let { rawToken ->
                tokenSigner
                        .validateIdentification(rawToken, extractOrganizationId(request.path))
                        ?.let { validatedEmail ->
                            UsernamePasswordAuthenticationToken(
                                    validatedEmail,
                                    rawToken
                            )
                        }
            }

    fun createCookie(signedToken: SignedToken) =
            ResponseCookie
                    .fromClientResponse(cookieName, signedToken.token)
                    .maxAge(3600)
                    .httpOnly(true)
                    .path("/")
                    .secure(false) // TODO should be true in production
                    .build()

}

@Component
class TokenAuthenticationManager(
        private val userFacade: UserFacade,
        private val organizationRoles: OrganizationRoles,
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> = Mono
            .just(authentication)
            .map {
                (it.principal as ValidatedUserIdentification)
            }
            .flatMap {
                runBlocking {
                    getAuthentication(email = it.email, credentials = authentication.credentials as String, organizationId = it.organizationId)
                }.toMono()
            }

    suspend fun getAuthentication(email: String, credentials: String, organizationId: OrganizationId?): Authentication? =
            if (organizationId == null) {
                userFacade
                        .findByEmail(email)
                        ?.let { user ->
                            CurrentUserAuthentication(
                                    currentUser = user,
                                    credentials = credentials,
                                    authorities = mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
                            )
                        }
            } else {
                organizationRoles
                        .getUnsafeUserOrganization(userEmail = email, organizationId = organizationId)
                        ?.let { organizationRole ->
                            CurrentUserOrganizationAuthentication(
                                    organizationRole = organizationRole,
                                    credentials = credentials,
                                    authorities = mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
                            )
                        }
            }

}
