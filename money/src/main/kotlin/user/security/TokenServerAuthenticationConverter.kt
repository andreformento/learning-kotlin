package com.andreformento.money.user.security

import kotlinx.coroutines.runBlocking
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class TokenServerAuthenticationConverter(
    private val tokenCookieHandler: TokenCookieHandler
) : ServerAuthenticationConverter {

    override fun convert(exchange: ServerWebExchange?): Mono<Authentication> = Mono
        .justOrEmpty(exchange)
        .flatMap { runBlocking { tokenCookieHandler.extractFromCookie(it.request) }.toMono() }

}
