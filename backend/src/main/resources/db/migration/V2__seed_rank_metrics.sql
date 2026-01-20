-- =============================================================================
-- V2__seed_rank_metrics.sql
-- =============================================================================
-- Populates rank_metrics table with estimated medians for each tier.
--
-- DATA SOURCE:
-- These values are estimates based on public LoL community data:
-- - League of Graphs (leagueofgraphs.com)
-- - OP.GG global stats
-- - Reddit r/leagueoflegends discussions
--
-- NOTE: These are conservative estimates. In production, they should be
-- calculated from a real match dataset.
--
-- INTERPRETATION:
-- - CS/min: From ~4.2 (Iron) to ~10.0 (Challenger) - most impactful skill
-- - KDA: From ~2.1 (Iron) to ~5.5 (Challenger)
-- - Vision/min: From ~0.8 (Iron) to ~1.7 (Challenger)
-- - Kill Participation: From ~52% (Iron) to ~75% (Challenger)
-- - Deaths: From ~6.5 (Iron) to ~3.0 (Challenger) - dying less = climbing
-- - Gold diff @15: From -200 (Iron) to +700 (Challenger)
-- =============================================================================

INSERT INTO rank_metrics (
    tier,
    median_cs_per_min,
    median_kda,
    median_vision_per_min,
    median_kill_participation,
    median_deaths,
    median_gold_diff_at_15,
    sample_size,
    last_updated
) VALUES
    -- IRON: Newest players or those with mechanical difficulties
    -- Low CS (~4.2): miss many last hits
    -- Many deaths (~6.5): poor positioning, don't watch minimap
    ('IRON', 4.2, 2.1, 0.8, 52.0, 6.5, -200, 100000, CURRENT_DATE),

    -- BRONZE: Understand basics but inconsistent execution
    -- Slight improvement in all areas
    ('BRONZE', 4.8, 2.3, 0.9, 54.0, 6.0, -100, 200000, CURRENT_DATE),

    -- SILVER: Majority of players (median distribution)
    -- Gold diff ~0: games are balanced on average
    ('SILVER', 5.5, 2.6, 1.0, 57.0, 5.5, 0, 300000, CURRENT_DATE),

    -- GOLD: Above average, good game understanding
    -- CS/min 6.2: know how to farm decently
    -- KDA 3.0: smarter trades
    ('GOLD', 6.2, 3.0, 1.1, 61.0, 5.0, 100, 250000, CURRENT_DATE),

    -- PLATINUM: Good mechanics, starting to understand macro
    -- Vision/min 1.2: use more wards
    ('PLATINUM', 7.2, 3.3, 1.2, 63.0, 4.5, 200, 150000, CURRENT_DATE),

    -- EMERALD: New tier (Season 13+), between Platinum and Diamond
    -- Good balance of all skills
    ('EMERALD', 7.8, 3.6, 1.3, 65.0, 4.2, 300, 100000, CURRENT_DATE),

    -- DIAMOND: Top ~2% of players
    -- CS/min 8.5: efficient farming
    -- Fewer deaths (3.8): know when to trade/escape
    ('DIAMOND', 8.5, 4.0, 1.4, 68.0, 3.8, 400, 50000, CURRENT_DATE),

    -- MASTER: Top ~0.5%
    -- Excellent at everything, solid macro game
    ('MASTER', 9.0, 4.5, 1.5, 70.0, 3.5, 500, 10000, CURRENT_DATE),

    -- GRANDMASTER: Top ~0.1%
    -- Near-perfect mechanics, great decision making
    ('GRANDMASTER', 9.5, 5.0, 1.6, 72.0, 3.2, 600, 2000, CURRENT_DATE),

    -- CHALLENGER: Top ~0.01% - Best players on the server
    -- CS/min ~10: nearly perfect
    -- Very few deaths (3.0): know their limits exactly
    -- Gold diff +700: dominate lane
    ('CHALLENGER', 10.0, 5.5, 1.7, 75.0, 3.0, 700, 500, CURRENT_DATE);

-- Verify insertion
-- SELECT tier, median_cs_per_min, median_kda FROM rank_metrics ORDER BY median_cs_per_min;
