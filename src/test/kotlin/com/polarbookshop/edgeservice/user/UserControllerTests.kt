package com.polarbookshop.edgeservice.user

import com.polarbookshop.edgeservice.config.SecurityConfig
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.oidc.StandardClaimNames
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient

@WebFluxTest(UserController::class)
@Import(SecurityConfig::class)
class UserControllerTests {
    @Autowired
    lateinit var webClient: WebTestClient
    @MockBean
    lateinit var clientRegistrationRepository: ReactiveClientRegistrationRepository

    @Test
    fun `when not authenticated Then 401`(){
        webClient
            .get()
            .uri("/user")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `when authenticated then return user`() {
        val expectedUser = User("jon.snow", "Jon", "Snow", listOf("employee", "customer"))

        webClient
            .mutateWith(configureMockOidcLogin(expectedUser))
            .get()
            .uri("/user")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(User::class.java)
            .value {
                assertThat(it).isEqualTo(expectedUser)
            }

    }

    private fun configureMockOidcLogin(expectedUser: User) : SecurityMockServerConfigurers.OidcLoginMutator {
        return SecurityMockServerConfigurers.mockOidcLogin().idToken {
            builder ->
            builder.claim(StandardClaimNames.PREFERRED_USERNAME, expectedUser.username)
            builder.claim(StandardClaimNames.GIVEN_NAME, expectedUser.firstName)
            builder.claim(StandardClaimNames.FAMILY_NAME, expectedUser.lastName)
        }
    }
}