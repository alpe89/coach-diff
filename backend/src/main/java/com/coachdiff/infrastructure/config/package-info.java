/**
 * CONFIGURATION - Spring @Configuration classes.
 *
 * <h2>Role</h2>
 * <p>
 * Contains {@code @Configuration} classes that define beans and Spring settings.
 * </p>
 *
 * <h2>Planned Configurations</h2>
 *
 * <h3>RestClientConfig</h3>
 * <p>
 * Defines the two RestClients for Riot API:
 * </p>
 * <ul>
 *   <li>{@code @Qualifier("routingRestClient")}: for europe.api.riotgames.com</li>
 *   <li>{@code @Qualifier("platformRestClient")}: for euw1.api.riotgames.com</li>
 * </ul>
 *
 * <h3>RedisConfig</h3>
 * <p>
 * Configures RedisTemplate with JSON serialization for complex objects.
 * </p>
 *
 * <h3>OpenAIConfig</h3>
 * <p>
 * Configures RestClient for OpenAI API with Bearer token.
 * </p>
 *
 * <h3>CacheConfig</h3>
 * <p>
 * Enables {@code @Cacheable} and configures RedisCacheManager with default TTL.
 * </p>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * @Configuration
 * public class RestClientConfig {
 *
 *     @Bean
 *     @Qualifier("routingRestClient")
 *     RestClient routingRestClient(
 *             @Value("${coach-diff.riot.routing-urls.europe}") String baseUrl,
 *             @Value("${coach-diff.riot.api-key}") String apiKey) {
 *
 *         return RestClient.builder()
 *             .baseUrl(baseUrl)
 *             .defaultHeader("X-Riot-Token", apiKey)
 *             .build();
 *     }
 * }
 * }</pre>
 */
package com.coachdiff.infrastructure.config;
