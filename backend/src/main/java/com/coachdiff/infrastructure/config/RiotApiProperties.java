package com.coachdiff.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Type-safe configuration properties for Riot Games API.
 *
 * <h2>Why @ConfigurationProperties instead of @Value?</h2>
 * <ul>
 *   <li><b>Type-safety</b>: Compile-time errors instead of runtime errors</li>
 *   <li><b>IDE support</b>: Autocomplete and refactoring work</li>
 *   <li><b>Validation</b>: Can add @NotBlank, @URL annotations</li>
 *   <li><b>Testability</b>: Easy to mock/construct in tests</li>
 * </ul>
 *
 * <h2>YAML Mapping</h2>
 * <pre>
 * coach-diff:
 *   riot:
 *     api-key: ${COACHDIFF_RIOT_API_KEY}              → getApiKey()
 *     ranked-solo-queue-id: ${...:420}                → getRankedSoloQueueId()
 *     routing-urls:                                    → getRoutingUrls()
 *       europe: https://europe.api...
 *     platform-urls:                                   → getPlatformUrls()
 *       euw1: https://euw1.api...
 * </pre>
 *
 * <p>
 * Spring Boot automatically converts kebab-case (routing-urls) to camelCase (routingUrls).
 * </p>
 *
 * <h2>Two types of Riot API URLs</h2>
 *
 * <h3>Routing URLs (Regional)</h3>
 * <p>
 * Used for endpoints that aren't tied to a specific game server:
 * </p>
 * <ul>
 *   <li><b>Account-V1</b>: Riot accounts are global</li>
 *   <li><b>Match-V5</b>: Match history is stored regionally</li>
 * </ul>
 *
 * <h3>Platform URLs (Server-specific)</h3>
 * <p>
 * Used for endpoints tied to a specific game server:
 * </p>
 * <ul>
 *   <li><b>Summoner-V4</b>: Summoner profile exists on EUW1 server</li>
 *   <li><b>League-V4</b>: Rank is specific to the server</li>
 * </ul>
 *
 * @see RestClientConfig for how these properties create RestClient beans
 */
@ConfigurationProperties(prefix = "coach-diff.riot")
public class RiotApiProperties {

    /**
     * Riot API key.
     * <p>
     * Get one from: <a href="https://developer.riotgames.com/">developer.riotgames.com</a>
     * </p>
     * <p>
     * Dev keys expire every 24 hours. Production keys require approval.
     * </p>
     */
    private String apiKey;

    /**
     * Queue ID for Ranked Solo/Duo.
     * <p>
     * Default is 420 (Ranked Solo/Duo on Summoner's Rift).
     * Configurable in case Riot ever changes the queue ID.
     * </p>
     *
     * @see <a href="https://static.developer.riotgames.com/docs/lol/queues.json">Queue IDs</a>
     */
    private int rankedSoloQueueId = 420;

    /**
     * Regional routing URLs for Account-V1 and Match-V5.
     * <p>
     * Example entries:
     * </p>
     * <ul>
     *   <li>europe → https://europe.api.riotgames.com</li>
     *   <li>americas → https://americas.api.riotgames.com</li>
     *   <li>asia → https://asia.api.riotgames.com</li>
     * </ul>
     */
    private Map<String, String> routingUrls;

    /**
     * Platform URLs for Summoner-V4 and League-V4.
     * <p>
     * Example entries:
     * </p>
     * <ul>
     *   <li>euw1 → https://euw1.api.riotgames.com</li>
     *   <li>na1 → https://na1.api.riotgames.com</li>
     *   <li>kr → https://kr.api.riotgames.com</li>
     * </ul>
     */
    private Map<String, String> platformUrls;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getRankedSoloQueueId() {
        return rankedSoloQueueId;
    }

    public void setRankedSoloQueueId(int rankedSoloQueueId) {
        this.rankedSoloQueueId = rankedSoloQueueId;
    }

    public Map<String, String> getRoutingUrls() {
        return routingUrls;
    }

    public void setRoutingUrls(Map<String, String> routingUrls) {
        this.routingUrls = routingUrls;
    }

    public Map<String, String> getPlatformUrls() {
        return platformUrls;
    }

    public void setPlatformUrls(Map<String, String> platformUrls) {
        this.platformUrls = platformUrls;
    }
}