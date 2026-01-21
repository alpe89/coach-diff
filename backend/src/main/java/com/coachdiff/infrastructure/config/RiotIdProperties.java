package com.coachdiff.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the player's Riot ID.
 *
 * <h2>MVP Approach</h2>
 * <p>
 * For the MVP, there is no search UI. The player's profile is configured
 * via environment variables. This simplifies development:
 * </p>
 * <ul>
 *   <li>No authentication needed</li>
 *   <li>No search endpoint to implement</li>
 *   <li>Fast iteration on core features</li>
 * </ul>
 *
 * <h2>YAML Mapping</h2>
 * <pre>
 * coach-diff:
 *   riot-id:
 *     game-name: ${COACHDIFF_RIOT_GAME_NAME}  → getGameName()
 *     tag-line: ${COACHDIFF_RIOT_TAG_LINE}    → getTagLine()
 *     region: ${COACHDIFF_RIOT_REGION:euw1}   → getRegion()
 * </pre>
 *
 * <h2>Riot ID Structure</h2>
 * <p>
 * A Riot ID has two parts: <b>GameName#TagLine</b>
 * </p>
 * <ul>
 *   <li><b>Game Name</b>: The display name (e.g., "Faker")</li>
 *   <li><b>Tag Line</b>: The unique identifier (e.g., "KR1", "EUW")</li>
 * </ul>
 * <p>
 * Together they form a globally unique identifier: "Faker#KR1"
 * </p>
 *
 * <h2>Region vs Platform</h2>
 * <p>
 * The region field maps to a platform (e.g., "euw1", "na1").
 * This determines which Riot server holds the player's data.
 * </p>
 *
 * @see RiotApiProperties for the API URLs configuration
 */
@ConfigurationProperties(prefix = "coach-diff.riot-id")
public class RiotIdProperties {

    /**
     * The player's Riot game name.
     * <p>
     * This is the name displayed in-game (e.g., "Faker", "Doublelift").
     * </p>
     */
    private String gameName;

    /**
     * The player's Riot tag line.
     * <p>
     * This is the unique suffix after # (e.g., "KR1", "EUW", "NA1").
     * </p>
     */
    private String tagLine;

    /**
     * The game region/platform.
     * <p>
     * Determines which server to query for summoner and rank data.
     * </p>
     * <p>
     * Examples: "euw1", "na1", "kr", "br1"
     * </p>
     * <p>
     * Defaults to "euw1" if not specified.
     * </p>
     */
    private String region = "euw1";

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}