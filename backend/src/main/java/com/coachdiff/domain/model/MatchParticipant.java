package com.coachdiff.domain.model;

/**
 * A player's performance in a single match.
 *
 * <h2>Metrics Extracted</h2>
 * <p>
 * We extract metrics needed for coaching analysis:
 * </p>
 * <ul>
 *   <li>CS/min calculated from totalMinionsKilled + neutralMinionsKilled</li>
 *   <li>KDA from kills, deaths, assists</li>
 *   <li>Vision score for map awareness</li>
 * </ul>
 *
 * <h2>Team Position</h2>
 * <p>
 * Possible values: TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY (support)
 * </p>
 *
 * @param puuid                Player identifier
 * @param summonerName         Display name (may differ from Riot ID)
 * @param championName         Champion played (e.g., "Ahri")
 * @param championId           Numeric champion ID
 * @param teamPosition         Lane position (TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY)
 * @param win                  True if this player's team won
 * @param kills                Champion kills
 * @param deaths               Deaths
 * @param assists              Assists
 * @param totalMinionsKilled   Lane minions killed
 * @param neutralMinionsKilled Jungle camps killed
 * @param visionScore          Vision score
 * @param goldEarned           Total gold earned
 * @param totalDamageDealt     Total damage dealt to champions
 */
public record MatchParticipant(
        String puuid,
        String summonerName,
        String championName,
        int championId,
        String teamPosition,
        boolean win,
        int kills,
        int deaths,
        int assists,
        int totalMinionsKilled,
        int neutralMinionsKilled,
        int visionScore,
        int goldEarned,
        int totalDamageDealt
) {
    /**
     * Calculates KDA ratio.
     * <p>
     * Deaths of 0 are treated as 1 to avoid division by zero.
     * This is a standard convention in LoL stats.
     * </p>
     *
     * @return (Kills + Assists) / Deaths
     */
    public double kda() {
        int effectiveDeaths = deaths == 0 ? 1 : deaths;
        return (kills + assists) / (double) effectiveDeaths;
    }

    /**
     * Total CS (minions + jungle camps).
     *
     * @return Sum of lane and jungle minions killed
     */
    public int totalCs() {
        return totalMinionsKilled + neutralMinionsKilled;
    }

    /**
     * Calculates CS/min given game duration.
     *
     * @param gameDurationMinutes Game duration in minutes
     * @return CS per minute
     */
    public double csPerMin(double gameDurationMinutes) {
        return gameDurationMinutes > 0 ? totalCs() / gameDurationMinutes : 0;
    }

    /**
     * Calculates vision score per minute.
     *
     * @param gameDurationMinutes Game duration in minutes
     * @return Vision score per minute
     */
    public double visionPerMin(double gameDurationMinutes) {
        return gameDurationMinutes > 0 ? visionScore / gameDurationMinutes : 0;
    }
}