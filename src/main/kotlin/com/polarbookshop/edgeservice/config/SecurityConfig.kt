package com.polarbookshop.edgeservice.config

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository
import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity,
                                  clientRegistrationRepository: ReactiveClientRegistrationRepository) : SecurityWebFilterChain{
        return http
            .authorizeExchange { exchange ->
                exchange
                    .pathMatchers("/", "/*.css", "/*.js", "/favicon.ico")
                    .permitAll()
                    .pathMatchers(HttpMethod.GET, "/books/**")
                    .permitAll()
                    .anyExchange().authenticated()
            }
            .exceptionHandling { exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
            }
//            .formLogin(Customizer.withDefaults())
            .oauth2Login(Customizer.withDefaults())
            .logout { it.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))}
            .csrf { csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()) }
            .build()

    }

    private fun oidcLogoutSuccessHandler(clientRegistrationRepository: ReactiveClientRegistrationRepository) : ServerLogoutSuccessHandler {
        val oidcLogoutSuccessHandler = OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}")
        return oidcLogoutSuccessHandler;
    }

    @Bean
    fun csrfWebFilter(): WebFilter {
        // Required because of https://github.com/spring-projects/spring-security/issues/5766
        return WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
            exchange.response.beforeCommit {
                Mono.defer<Void> {
                    val csrfToken: Mono<CsrfToken>? =
                        exchange.getAttribute<Mono<CsrfToken>>(
                            CsrfToken::class.java.getName()
                        )
                    csrfToken?.then() ?: Mono.empty<Void>()
                }
            }
            chain.filter(exchange)
        }
    }


}