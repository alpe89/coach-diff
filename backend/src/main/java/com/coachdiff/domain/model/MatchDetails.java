package com.coachdiff.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Complete details of a ranked match.
 *
 * <h2>Queue ID 420</h2>
 * <p>
 * Queue ID 420 is Ranked Solo/Duo (Summoner's Rift, 5v5).
 * We only fetch this queue for coaching analysis.
 * </p>
 *
 * <h2>Match ID Format</h2>
 * <p>
 * Match IDs follow the format: {REGION}_{GAME_ID}
 * Example: "EUW1_1234567890"
 * </p>
 *
 * @param matchId             Unique match identifier (e.g., "EUW1_1234567890")
 * @param gameCreation        When the match started
 * @param gameDurationSeconds Total game duration in seconds
 * @param participants        All 10 players in the match
 */
public record MatchDetails(
        String matchId,
        Instant gameCreation,
        int gameDurationSeconds,
        List<MatchParticipant> participants
) {
    public MatchDetails {
        if (matchId == null || matchId.isBlank()) {
            throw new IllegalArgumentException("Match ID cannot be null or blank");
        }
        if (participants == null || participants.size() != 10) {
            throw new IllegalArgumentException("Match must have exactly 10 participants");
        }
        // Make the list immutable to preserve record semantics
        participants = List.copyOf(participants);
    }

    /**
     * Finds the participant matching the given PUUID.
     *
     * @param puuid Player's PUUID
     * @return The participant, or empty if not found
     */
    public Optional<MatchParticipant> findParticipant(String puuid) {
        return participants.stream()
                .filter(p -> p.puuid().equals(puuid))
                .findFirst();
    }

    /**
     * Returns game duration in minutes.
     *
     * @return Duration in minutes (decimal)
     */
    public double gameDurationMinutes() {
        return gameDurationSeconds / 60.0;
    }

    /**
     * Checks if the given player won this match.
     *
     * @param puuid Player's PUUID
     * @return true if player won, false if lost or not found
     */
    public boolean didPlayerWin(String puuid) {
        return findParticipant(puuid)
                .map(MatchParticipant::win)
                .orElse(false);
    }
}