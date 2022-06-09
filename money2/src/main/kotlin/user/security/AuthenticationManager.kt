package com.andreformento.money.user.security

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.role.OrganizationRole
import com.andreformento.money.organization.role.repository.OrganizationRoles
import com.andreformento.money.user.UserFacade
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import kotlinx.coroutines.runBlocking
import org.springframework.security.authentication.ReactiveAuthenticationManager
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

data class ValidatedUserIdentification(val email: String, val organizationId: OrganizationId?) {
    constructor(jws: Jws<Claims>, organizationId: OrganizationId?) : this(email = jws.body.subject, organizationId)
}

@Component
class Oauth2AuthenticationManager(
        private val userFacade: UserFacade,
        private val organizationRoles: OrganizationRoles,
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> = Mono
            .just(authentication)
//            .map { (it.principal as ValidatedUserIdentification) }
            .flatMap {
                runBlocking {
                    getAuthentication(email = "it.email", credentials = authentication.credentials as String, organizationId = null)
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
