package me.escoffier.spring.todo;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {
	@Bean
	@ServiceConnection
	public PostgreSQLContainer<?> postgres() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-bullseye"))
			.withDatabaseName("quarkus_test")
			.withUsername("quarkus_test")
			.withPassword("quarkus_test")
			.withExposedPorts(5432);
	}
}
