-- =============================================================================
-- V1__initial_schema.sql
-- =============================================================================
-- First migration: creates all tables needed for CoachDiff.ai
--
-- TABLES:
-- 1. rank_metrics      → Median stats for each tier (reference data)
-- 2. summoner_profiles → Player profiles with rank and aggregated metrics
-- 3. match_analyses    → Match history with individual stats
-- 4. suggestions       → Cached AI suggestions per profile
--
-- NOTE: We use snake_case for table/column names (PostgreSQL convention)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- RANK_METRICS: Median stats per tier
-- -----------------------------------------------------------------------------
-- This table contains "median" stat values for each rank.
-- We use MEDIAN instead of MEAN because it's more resistant to outliers.
-- Example: if 1 player has 20 CS/min (smurf), mean goes up, median doesn't.
--
-- Data is "seeded" (manually inserted) based on community estimates.
-- In the future, could be calculated from real data.

CREATE TABLE rank_metrics (
    -- Tier is the PRIMARY KEY: IRON, BRONZE, SILVER, GOLD, PLATINUM, EMERALD, DIAMOND, MASTER, GRANDMASTER, CHALLENGER
    tier VARCHAR(20) PRIMARY KEY,

    -- CS (Creep Score) per minute
    -- IRON ~4.2, CHALLENGER ~10.0
    -- Most impactful metric for climbing
    median_cs_per_min DECIMAL(4,2) NOT NULL,

    -- KDA: (Kills + Assists) / Deaths
    -- Typical value: 2.0 (low) - 5.0+ (high)
    median_kda DECIMAL(4,2) NOT NULL,

    -- Vision Score per minute
    -- Measures how much the player contributes to vision (wards, sweeper)
    median_vision_per_min DECIMAL(4,2) NOT NULL,

    -- Kill Participation: % of team kills you participated in
    -- (Kills + Assists) / Team Total Kills * 100
    median_kill_participation DECIMAL(5,2) NOT NULL,

    -- Average deaths per game
    -- Fewer deaths = more time alive = more farm/XP/impact
    median_deaths DECIMAL(4,2) NOT NULL,

    -- Gold difference at 15 minutes (vs lane opponent)
    -- Positive = ahead, Negative = behind
    -- Nullable because not always available (surrender, remake)
    median_gold_diff_at_15 INTEGER,

    -- Metadata
    sample_size INTEGER,  -- How many players were analyzed
    last_updated DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Table comment (in-DB documentation)
COMMENT ON TABLE rank_metrics IS 'Median stats per rank tier, used for comparisons';
COMMENT ON COLUMN rank_metrics.median_cs_per_min IS 'CS (minion kills) per minute - most impactful metric';
COMMENT ON COLUMN rank_metrics.median_gold_diff_at_15 IS 'Gold diff vs opponent at 15 min (can be NULL)';

-- -----------------------------------------------------------------------------
-- SUMMONER_PROFILES: Player profiles
-- -----------------------------------------------------------------------------
-- Contains player data fetched from Riot API plus aggregated metrics
-- calculated from the last 20 games.

CREATE TABLE summoner_profiles (
    -- PUUID (Player Universally Unique ID): Riot global identifier
    -- Format: 78 characters, stable across name/region changes
    puuid VARCHAR(78) PRIMARY KEY,

    -- Riot ID (game_name#tag_line), e.g.: "Faker#KR1"
    game_name VARCHAR(16) NOT NULL,
    tag_line VARCHAR(5) NOT NULL,

    -- Server region (euw1, na1, kr, etc.)
    region VARCHAR(10) NOT NULL,

    -- Summoner ID: legacy identifier used by some endpoints
    summoner_id VARCHAR(63),

    -- Rank info
    tier VARCHAR(20),      -- IRON, BRONZE, ..., CHALLENGER
    division VARCHAR(4),   -- I, II, III, IV
    league_points INTEGER, -- 0-100 LP
    wins INTEGER DEFAULT 0,
    losses INTEGER DEFAULT 0,

    -- Main role (calculated from games)
    main_role VARCHAR(10),  -- TOP, JUNGLE, MID, ADC, SUPPORT

    -- Aggregated metrics (average of last 20 ranked games)
    cs_per_min DECIMAL(4,2),
    kda DECIMAL(4,2),
    vision_per_min DECIMAL(4,2),
    kill_participation DECIMAL(5,2),
    avg_deaths DECIMAL(4,2),
    gold_diff_at_15 INTEGER,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for common queries
CREATE INDEX idx_summoner_region ON summoner_profiles(region);
CREATE INDEX idx_summoner_tier ON summoner_profiles(tier);

COMMENT ON TABLE summoner_profiles IS 'Player profiles with rank and aggregated metrics';
COMMENT ON COLUMN summoner_profiles.puuid IS 'Player UUID - Riot global unique identifier';

-- -----------------------------------------------------------------------------
-- MATCH_ANALYSES: Match history
-- -----------------------------------------------------------------------------
-- Contains details of each analyzed match.
-- Used to calculate aggregated profile metrics.

CREATE TABLE match_analyses (
    -- Match ID: format "REGION_GAMEID", e.g.: "EUW1_1234567890"
    match_id VARCHAR(20) PRIMARY KEY,

    -- Foreign key to profile
    puuid VARCHAR(78) NOT NULL REFERENCES summoner_profiles(puuid) ON DELETE CASCADE,

    -- When the game was played
    played_at TIMESTAMP NOT NULL,

    -- Champion played
    champion_name VARCHAR(50) NOT NULL,

    -- Result
    win BOOLEAN NOT NULL,

    -- Game stats
    kills INTEGER NOT NULL DEFAULT 0,
    deaths INTEGER NOT NULL DEFAULT 0,
    assists INTEGER NOT NULL DEFAULT 0,
    cs INTEGER NOT NULL DEFAULT 0,  -- Total minion + monster kills
    game_duration_seconds INTEGER NOT NULL,
    vision_score INTEGER NOT NULL DEFAULT 0,
    gold_diff_at_15 INTEGER,  -- Nullable (not always available)

    -- Metadata
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for "last N games for a player" queries
-- DESC order on played_at optimizes ORDER BY played_at DESC LIMIT 20
CREATE INDEX idx_match_puuid_played ON match_analyses(puuid, played_at DESC);

COMMENT ON TABLE match_analyses IS 'Match history with individual stats';
COMMENT ON COLUMN match_analyses.cs IS 'Total Creep Score (minions + jungle monsters)';

-- -----------------------------------------------------------------------------
-- SUGGESTIONS: Cached AI suggestions
-- -----------------------------------------------------------------------------
-- Cache of suggestions generated by OpenAI to avoid repeated calls.
-- profile_hash allows invalidating cache when metrics change.

CREATE TABLE suggestions (
    id BIGSERIAL PRIMARY KEY,

    -- Foreign key to profile
    puuid VARCHAR(78) NOT NULL REFERENCES summoner_profiles(puuid) ON DELETE CASCADE,

    -- Profile hash: MD5 of (tier + metrics)
    -- If metrics change, hash changes and we regenerate
    profile_hash VARCHAR(32) NOT NULL UNIQUE,

    -- Suggestion 1 (highest priority)
    priority_1_title TEXT NOT NULL,   -- E.g.: "Improve CS/min"
    priority_1_reason TEXT NOT NULL,  -- E.g.: "Your CS is 15% below Gold average"
    priority_1_action TEXT NOT NULL,  -- E.g.: "Practice last-hitting in training mode"

    -- Suggestion 2
    priority_2_title TEXT,
    priority_2_reason TEXT,
    priority_2_action TEXT,

    -- Suggestion 3
    priority_3_title TEXT,
    priority_3_reason TEXT,
    priority_3_action TEXT,

    -- When it was generated
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for fast profile lookup
CREATE INDEX idx_suggestions_puuid ON suggestions(puuid);

COMMENT ON TABLE suggestions IS 'Cached AI suggestions, invalidated when profile_hash changes';
COMMENT ON COLUMN suggestions.profile_hash IS 'MD5(tier + metrics) for cache invalidation';
