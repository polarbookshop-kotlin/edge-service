package com.polarbookshop.edgeservice.user

import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class UserController {
//    @GetMapping("user")
//    fun getUser() : Mono<User> {
//        return ReactiveSecurityContextHolder.getContext()
//            .map { it.authentication }
//            .map { it: Authentication ->
//                val oidcUser: OidcUser = it.principal as OidcUser
//                User(oidcUser.preferredUsername,
//                    oidcUser.givenName,
//                    oidcUser.familyName,
//                    listOf("employee", "customer"))
//            }
//    }

    @GetMapping("user")
    fun getUser(@AuthenticationPrincipal oidcUser: OidcUser) : Mono<User> {
       return Mono.just(User(oidcUser.preferredUsername,
            oidcUser.givenName,
            oidcUser.familyName,
            listOf("employee", "customer")
        ))
    }

}