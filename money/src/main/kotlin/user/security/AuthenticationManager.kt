package com.andreformento.money.user.security

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.role.OrganizationRole
import com.andreformento.money.user.User
import com.andreformento.money.user.UserFacade
import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseCookie
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
    override fun getDetails(): Any {
        TODO("Not yet implemented")
    }

    override fun getPrincipal(): CurrentUser = this.currentUser

    override fun isAuthenticated() = true

    override fun setAuthenticated(isAuthenticated: Boolean) {
    }

}

data class CurrentUserOrganizationAuthentication constructor(
    private val currentUserAuthentication: CurrentUserAuthentication,
    private val organizationRole: OrganizationRole,
) : Authentication {

    override fun getName() = currentUserAuthentication.getName()

    override fun getAuthorities() = currentUserAuthentication.getAuthorities()

    override fun getCredentials() = currentUserAuthentication.getCredentials()

    override fun getDetails() = currentUserAuthentication.getDetails()

    override fun getPrincipal(): OrganizationRole = organizationRole

    override fun isAuthenticated() = currentUserAuthentication.isAuthenticated()

    override fun setAuthenticated(isAuthenticated: Boolean) = currentUserAuthentication.setAuthenticated(isAuthenticated)
}

@Component
class TokenCookieHandler(
    private val tokenSigner: TokenSigner,
) {

    private val cookieName = "X-Auth"

    fun extractFromCookie(request: ServerHttpRequest): UsernamePasswordAuthenticationToken? = request
        .cookies[cookieName]
        ?.takeIf { it.isNotEmpty() }
        ?.let { it[0].value }
        ?.let { rawToken ->
            tokenSigner
                .validateToken(rawToken)
                ?.let { validatedUserIdentification ->
                    UsernamePasswordAuthenticationToken(
                        validatedUserIdentification,
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
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> = Mono
        .just(authentication)
        .map {(it.principal as ValidatedUserIdentification).email}
        .flatMap { findByEmail(it) }
        .map {
            CurrentUserAuthentication(
                currentUser = it,
                credentials = authentication.credentials as String,
                authorities = mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
            )
        }

    private fun findByEmail(email: String): Mono<User> =
        runBlocking { userFacade.findByEmail(email) }.toMono()

}
