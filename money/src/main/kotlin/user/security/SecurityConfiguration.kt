package com.andreformento.money.user.security

import com.andreformento.money.organization.role.repository.OrganizationRoles
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.ReactiveAuthorizationManager
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.authorization.AuthorizationContext
import reactor.core.publisher.Mono


@Configuration
class SecurityConfiguration {

    val permittedGetPaths = arrayOf(
        // actuator
        "/actuator/health",
        "/actuator/health/liveness",
        "/actuator/health/readiness",
        "/actuator/prometheus",
    )

    val permittedPostPaths = arrayOf(
        // auth
        "/user/auth/signup",
        "/user/auth/login",
    )

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        jwtAuthenticationManager: ReactiveAuthenticationManager,
        jwtAuthenticationConverter: ServerAuthenticationConverter,
        organizationRoles: OrganizationRoles,
    ): SecurityWebFilterChain {
        val authenticationWebFilter = AuthenticationWebFilter(jwtAuthenticationManager)
        authenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter)

        val organizationAuthorizationManager =
            ReactiveAuthorizationManager { auth: Mono<Authentication>, _: AuthorizationContext ->
                auth.map { AuthorizationDecision(it is CurrentUserOrganizationAuthentication) }
            }

        return http
            .authorizeExchange()
            .pathMatchers(HttpMethod.GET, *permittedGetPaths).permitAll()
            .pathMatchers(HttpMethod.POST, *permittedPostPaths).permitAll()
            // https://docs.spring.io/spring-security/site/docs/current/reference/html5/#authorization
            .pathMatchers("/organizations/{organization-id}/**").access(organizationAuthorizationManager)
            .anyExchange().authenticated()
            .and()
            .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .httpBasic().disable()
            .csrf().disable()
            .formLogin().disable()
            .logout().disable()
            .build()
    }
}
