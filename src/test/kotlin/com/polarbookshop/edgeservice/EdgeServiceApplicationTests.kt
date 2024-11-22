package com.polarbookshop.edgeservice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class EdgeServiceApplicationTests {
	companion object {
		const val REDIS_PORT = 6379

		@Container
		@ServiceConnection
		val redis: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:7.0"))
			.withExposedPorts(REDIS_PORT)

		@MockBean
		var clientRegistrationRepository: ReactiveClientRegistrationRepository? = null

		@DynamicPropertySource
		fun redisProperties(registry: DynamicPropertyRegistry){
			registry.add("spring.data.redis.host"){ redis.host}
			registry.add("spring.data.redis.port"){ redis.getMappedPort(REDIS_PORT)}
		}
	}
	@Test
	fun contextLoads() {
	}

}
