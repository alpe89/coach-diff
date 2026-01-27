package com.coachdiff.domain.model;

/**
 * Represents ranked information for a specific queue.
 *
 * <h2>Queue Types</h2>
 * <ul>
 *   <li><b>RANKED_SOLO_5x5</b>: Solo/Duo queue (our focus)</li>
 *   <li><b>RANKED_FLEX_SR</b>: Flex queue</li>
 * </ul>
 *
 * <h2>Tier System</h2>
 * <pre>
 * IRON → BRONZE → SILVER → GOLD → PLATINUM → EMERALD → DIAMOND → MASTER → GRANDMASTER → CHALLENGER
 * </pre>
 * <p>
 * IRON to DIAMOND have divisions I-IV. MASTER+ have no divisions.
 * </p>
 *
 * <h2>League Points (LP)</h2>
 * <p>
 * LP range from 0 to 100 for tiered ranks. For MASTER+ they are uncapped
 * and determine ladder positioning.
 * </p>
 *
 * @param queueType     Queue identifier (e.g., "RANKED_SOLO_5x5")
 * @param tier          Rank tier (e.g., "GOLD")
 * @param rank          Division within tier (e.g., "II") - empty for MASTER+
 * @param leaguePoints  LP in division (0-100, uncapped for MASTER+)
 * @param wins          Total wins this season
 * @param losses        Total losses this season
 * @param hotStreak     Currently on a winning streak
 * @param veteran       Has played 100+ games this season
 * @param freshBlood    Recently promoted to this tier
 * @param inactive      Marked for decay (MASTER+ only)
 */
public record LeagueEntry(
        String queueType,
        String tier,
        String rank,
        int leaguePoints,
        int wins,
        int losses,
        boolean hotStreak,
        boolean veteran,
        boolean freshBlood,
        boolean inactive
) {
    public LeagueEntry {
        if (queueType == null || queueType.isBlank()) {
            throw new IllegalArgumentException("Queue type cannot be null or blank");
        }
        if (tier == null || tier.isBlank()) {
            throw new IllegalArgumentException("Tier cannot be null or blank");
        }
        // rank can be null/empty for MASTER+
    }

    /**
     * Calculates win rate percentage.
     *
     * @return Win rate (0.0 - 100.0), or 0 if no games played
     */
    public double winRate() {
        int total = wins + losses;
        return total > 0 ? (wins * 100.0) / total : 0.0;
    }

    /**
     * Returns full rank string (e.g., "GOLD II" or "MASTER").
     *
     * @return String with tier and division
     */
    public String fullRank() {
        // MASTER+ have no divisions
        if (rank == null || rank.isBlank() ||
                "MASTER".equals(tier) || "GRANDMASTER".equals(tier) || "CHALLENGER".equals(tier)) {
            return tier;
        }
        return tier + " " + rank;
    }

    /**
     * Checks if this entry is for Solo/Duo queue.
     *
     * @return true if ranked solo queue
     */
    public boolean isSoloQueue() {
        return "RANKED_SOLO_5x5".equals(queueType);
    }

    /**
     * Returns total games played.
     *
     * @return wins + losses
     */
    public int totalGames() {
        return wins + losses;
    }
}