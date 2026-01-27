package com.coachdiff.domain.model;

/**
 * Represents a League of Legends summoner on a specific server.
 *
 * <h2>Summoner vs Account</h2>
 * <p>
 * A Riot Account can have multiple Summoners (one per server).
 * The Summoner is server-specific and contains:
 * </p>
 * <ul>
 *   <li><b>summonerId</b>: Server-specific ID (used for League-V4)</li>
 *   <li><b>puuid</b>: Global identifier (links to RiotAccount)</li>
 *   <li><b>summonerLevel</b>: Account level on this server</li>
 * </ul>
 *
 * <h2>Why do we need summonerId?</h2>
 * <p>
 * The League-V4 API (to get rank) requires summonerId, not PUUID.
 * So we must first call Summoner-V4 to get the ID, then League-V4.
 * </p>
 *
 * @param summonerId    Server-specific ID (for League-V4 API)
 * @param puuid         Global PUUID (same as RiotAccount)
 * @param profileIconId ID of the icon displayed in-game
 * @param summonerLevel Account level (1-500+)
 * @param revisionDate  Last update timestamp (epoch ms)
 */
public record Summoner(
        String summonerId,
        String puuid,
        int profileIconId,
        long summonerLevel,
        long revisionDate
) {
    public Summoner {
        if (summonerId == null || summonerId.isBlank()) {
            throw new IllegalArgumentException("Summoner ID cannot be null or blank");
        }
        if (puuid == null || puuid.isBlank()) {
            throw new IllegalArgumentException("PUUID cannot be null or blank");
        }
        if (summonerLevel < 1) {
            throw new IllegalArgumentException("Summoner level must be >= 1");
        }
    }
}