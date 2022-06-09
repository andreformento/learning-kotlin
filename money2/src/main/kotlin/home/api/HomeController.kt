package com.andreformento.money.home.api

import com.andreformento.money.user.api.LoggedUserResponse
import com.andreformento.money.user.security.CurrentUserAuthentication
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class HomeController {

    @GetMapping
    suspend fun home(@AuthenticationPrincipal principal: OAuth2User): Any {
        return object {val name = principal.attributes["name"]}
    }

}
