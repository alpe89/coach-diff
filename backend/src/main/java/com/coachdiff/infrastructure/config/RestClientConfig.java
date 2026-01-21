package com.coachdiff.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration for Riot API RestClient beans.
 *
 * <h2>Why TWO RestClients?</h2>
 * <p>
 * The Riot API has a distributed architecture with two types of URLs:
 * </p>
 *
 * <h3>1. Routing URLs (Regional)</h3>
 * <p>
 * For endpoints NOT tied to a specific game server:
 * </p>
 * <ul>
 *   <li><b>Account-V1</b>: Riot accounts are global (you can play on EUW or NA with the same account)</li>
 *   <li><b>Match-V5</b>: Match history is archived regionally</li>
 * </ul>
 * <pre>
 * europe.api.riotgames.com  →  Account, Match history
 * americas.api.riotgames.com
 * asia.api.riotgames.com
 * </pre>
 *
 * <h3>2. Platform URLs (Server-specific)</h3>
 * <p>
 * For endpoints tied to a specific game server:
 * </p>
 * <ul>
 *   <li><b>Summoner-V4</b>: Summoner profile exists on a specific server (EUW1)</li>
 *   <li><b>League-V4</b>: Rank is server-specific</li>
 * </ul>
 * <pre>
 * euw1.api.riotgames.com  →  Summoner profile, Rank info
 * na1.api.riotgames.com
 * kr.api.riotgames.com
 * </pre>
 *
 * <h2>What is @Qualifier?</h2>
 * <p>
 * When Spring finds TWO beans of the same type (RestClient), it doesn't know
 * which one to inject. @Qualifier resolves this ambiguity:
 * </p>
 * <pre>{@code
 * // Without @Qualifier - Spring: "Which RestClient? There are 2!" ❌
 * @Autowired RestClient restClient;
 *
 * // With @Qualifier - Spring: "Ok, you want 'routingRestClient'" ✅
 * @Autowired @Qualifier("routingRestClient") RestClient routingClient;
 * }</pre>
 *
 * <h2>RestClient (Spring Boot 4.0)</h2>
 * <p>
 * RestClient is the modern replacement for RestTemplate:
 * </p>
 * <ul>
 *   <li>Fluent API: method chaining like {@code .get().uri("/path").retrieve()}</li>
 *   <li>Type-safe: automatic JSON deserialization</li>
 *   <li>Works with Virtual Threads for async performance with sync code</li>
 * </ul>
 *
 * @see RiotApiProperties for the configuration values
 * @see RiotIdProperties for the player's region
 */
@Configuration
public class RestClientConfig {

    /**
     * RestClient for regional routing endpoints (Account-V1, Match-V5).
     * <p>
     * Uses europe.api.riotgames.com as base URL.
     * </p>
     * <p>
     * Inject with: {@code @Qualifier("routingRestClient") RestClient restClient}
     * </p>
     *
     * <h3>Usage in adapter</h3>
     * <pre>{@code
     * // Account-V1: Get account by Riot ID
     * AccountDto account = routingRestClient.get()
     *     .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}",
     *          "Faker", "KR1")
     *     .retrieve()
     *     .body(AccountDto.class);
     *
     * // Match-V5: Get match IDs
     * List<String> matchIds = routingRestClient.get()
     *     .uri("/lol/match/v5/matches/by-puuid/{puuid}/ids?queue=420&count=20",
     *          puuid)
     *     .retrieve()
     *     .body(new ParameterizedTypeReference<List<String>>() {});
     * }</pre>
     *
     * @param riotApiProperties Riot API configuration (injected by Spring)
     * @return configured RestClient for routing endpoints
     */
    @Bean
    @Qualifier("routingRestClient")
    public RestClient routingRestClient(RiotApiProperties riotApiProperties) {
        String europeUrl = riotApiProperties.getRoutingUrls().get("europe");

        return RestClient.builder()
                .baseUrl(europeUrl)
                .defaultHeader("X-Riot-Token", riotApiProperties.getApiKey())
                .build();
    }

    /**
     * RestClient for platform-specific endpoints (Summoner-V4, League-V4).
     * <p>
     * Uses the platform URL matching the player's region (e.g., euw1.api.riotgames.com).
     * </p>
     * <p>
     * Inject with: {@code @Qualifier("platformRestClient") RestClient restClient}
     * </p>
     *
     * <h3>Usage in adapter</h3>
     * <pre>{@code
     * // Summoner-V4: Get summoner by PUUID
     * SummonerDto summoner = platformRestClient.get()
     *     .uri("/lol/summoner/v4/summoners/by-puuid/{puuid}", puuid)
     *     .retrieve()
     *     .body(SummonerDto.class);
     *
     * // League-V4: Get rank by summoner ID
     * Set<LeagueEntryDto> entries = platformRestClient.get()
     *     .uri("/lol/league/v4/entries/by-summoner/{summonerId}", summonerId)
     *     .retrieve()
     *     .body(new ParameterizedTypeReference<Set<LeagueEntryDto>>() {});
     * }</pre>
     *
     * @param riotApiProperties Riot API configuration (API key, platform URLs)
     * @param riotIdProperties  Player configuration (region)
     * @return configured RestClient for platform endpoints
     */
    @Bean
    @Qualifier("platformRestClient")
    public RestClient platformRestClient(
            RiotApiProperties riotApiProperties,
            RiotIdProperties riotIdProperties) {

        String region = riotIdProperties.getRegion();
        String platformUrl = riotApiProperties.getPlatformUrls().get(region);

        return RestClient.builder()
                .baseUrl(platformUrl)
                .defaultHeader("X-Riot-Token", riotApiProperties.getApiKey())
                .build();
    }
}