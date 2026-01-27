package com.coachdiff.domain.model;

/**
 * Represents a Riot Games account.
 *
 * <h2>PUUID (Player Universally Unique Identifier)</h2>
 * <p>
 * The PUUID is the most stable identifier for a player:
 * </p>
 * <ul>
 *   <li>Never changes, even if the player renames their account</li>
 *   <li>Same across all Riot games (LoL, Valorant, TFT)</li>
 *   <li>78 characters, cryptographically secure</li>
 * </ul>
 *
 * <h2>Riot ID</h2>
 * <p>
 * A Riot ID has two parts: <b>GameName#TagLine</b>
 * </p>
 * <ul>
 *   <li><b>Game Name</b>: Display name (e.g., "Faker")</li>
 *   <li><b>Tag Line</b>: Unique identifier after # (e.g., "KR1", "EUW")</li>
 * </ul>
 *
 * @param puuid    Unique player identifier (78 characters)
 * @param gameName Display name (e.g., "Faker")
 * @param tagLine  Tag after # (e.g., "KR1")
 */
public record RiotAccount(
        String puuid,
        String gameName,
        String tagLine
) {
    /**
     * Compact constructor with validation.
     * <p>
     * Java records allow adding validation in the constructor
     * without redeclaring the parameters.
     * </p>
     */
    public RiotAccount {
        if (puuid == null || puuid.isBlank()) {
            throw new IllegalArgumentException("PUUID cannot be null or blank");
        }
        if (gameName == null || gameName.isBlank()) {
            throw new IllegalArgumentException("Game name cannot be null or blank");
        }
        if (tagLine == null || tagLine.isBlank()) {
            throw new IllegalArgumentException("Tag line cannot be null or blank");
        }
    }

    /**
     * Returns the full Riot ID (e.g., "Faker#KR1").
     *
     * @return Riot ID in format gameName#tagLine
     */
    public String fullRiotId() {
        return gameName + "#" + tagLine;
    }
}