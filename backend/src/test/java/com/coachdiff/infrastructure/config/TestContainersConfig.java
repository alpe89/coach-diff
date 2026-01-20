package com.coachdiff.infrastructure.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Testcontainers configuration for integration tests.
 *
 * <h2>What is Testcontainers?</h2>
 * <p>
 * Testcontainers starts real Docker containers during tests.
 * Instead of mocking the database, we use a real PostgreSQL in a container.
 * </p>
 *
 * <h2>Benefits</h2>
 * <ul>
 *   <li><b>Realistic tests</b>: same behavior as production</li>
 *   <li><b>Zero config</b>: containers start/stop automatically</li>
 *   <li><b>Isolation</b>: each test suite has clean database</li>
 *   <li><b>CI/CD ready</b>: works anywhere Docker is available</li>
 * </ul>
 *
 * <h2>@ServiceConnection</h2>
 * <p>
 * This Spring Boot (3.1+) annotation is "magic":
 * </p>
 * <ol>
 *   <li>Detects the container type (PostgreSQL, Redis, etc.)</li>
 *   <li>Automatically configures Spring ({@code spring.datasource.*})</li>
 *   <li>No more manual {@code @DynamicPropertySource}!</li>
 * </ol>
 *
 * <h2>Usage in tests</h2>
 * <pre>{@code
 * @SpringBootTest
 * @Import(TestContainersConfig.class)
 * class MyIntegrationTest {
 *     // PostgreSQL and Redis are already configured!
 *     @Autowired
 *     JdbcTemplate jdbc;  // Connected to PostgreSQL container
 * }
 * }</pre>
 *
 * <h2>Prerequisites</h2>
 * <ul>
 *   <li>Docker must be installed and running</li>
 *   <li>First run downloads images (may take time)</li>
 * </ul>
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

    /**
     * PostgreSQL container for tests.
     *
     * <p>
     * Uses the Alpine image (lighter: ~50MB vs ~150MB).
     * The container starts automatically when the bean is requested
     * and stops when the Spring context is closed.
     * </p>
     *
     * <p>
     * {@code @ServiceConnection} automatically configures:
     * </p>
     * <ul>
     *   <li>{@code spring.datasource.url}</li>
     *   <li>{@code spring.datasource.username}</li>
     *   <li>{@code spring.datasource.password}</li>
     * </ul>
     *
     * @return configured PostgreSQLContainer
     */
    @Bean
    @ServiceConnection
    @SuppressWarnings("resource")
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                // Optional configuration (defaults work fine for tests)
                .withDatabaseName("coachdiff_test")
                .withUsername("test")
                .withPassword("test");
    }

    /**
     * Redis container for tests.
     *
     * <p>
     * We use {@code GenericContainer} instead of a dedicated module
     * because Redis doesn't require special configuration.
     * </p>
     *
     * <p>
     * {@code @ServiceConnection(name = "redis")} tells Spring Boot
     * that this container is Redis and should configure:
     * </p>
     * <ul>
     *   <li>{@code spring.data.redis.host}</li>
     *   <li>{@code spring.data.redis.port}</li>
     * </ul>
     *
     * @return configured Redis GenericContainer
     */
    @Bean
    @ServiceConnection(name = "redis")
    @SuppressWarnings("resource")
    GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                .withExposedPorts(6379);  // Default Redis port
    }
}
