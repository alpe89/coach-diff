# coachdiff.ai

## Project Overview

**Name**: CoachDiff.ai  
**Tagline**: "Find your diff, powered by AI"  
**Type**: AI-powered League of Legends virtual coach

**Problem**: Existing trackers (OP.GG, Mobalytics) show raw stats without actionable guidance. Players see data but don't know what to improve.

**Solution**: Mobile-first app that transforms Riot API data into **3 actionable improvement priorities** by comparing your metrics against median values from players at your rank and one tier above, with AI-generated coaching advice.

**Core Differentiation**:
- Simplicity: Max 3 improvement areas (not 8+ like Mobalytics)
- Rank-relative context: "Your CS/min is 12% below the average Gold player"
- AI-powered suggestions: GPT-4o-mini generates personalized coaching
- Time-efficient: Quick glance between games

---

## Tech Stack

**Backend**: Java 21 LTS, Spring Boot 4.0, PostgreSQL, Redis, Maven  
**Architecture**: Hexagonal (Ports & Adapters)  
**Mobile**: React 19, React Native 0.76.6, Expo ~52.0, TypeScript 5.7  
**AI**: OpenAI GPT-4o-mini  
**DevOps**: Docker Compose, Testcontainers, WireMock  
**Monorepo**: Maven parent POM aggregator

---

## Project Structure

```
coach-diff/
├── pom.xml                     # Parent aggregator
├── backend/
│   ├── pom.xml                 # Backend module
│   ├── src/
│   │   ├── main/java/com/coachdiff/
│   │   │   ├── Application.java
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── SummonerProfile
│   │   │   │   │   ├── RankInfo
│   │   │   │   │   ├── ProfileMetrics
│   │   │   │   │   ├── RankMetrics
│   │   │   │   │   ├── MetricComparison
│   │   │   │   │   ├── ImprovementSuggestion
│   │   │   │   │   └── MatchAnalysis
│   │   │   │   ├── port/
│   │   │   │   │   ├── in/    # Inbound ports (use cases)
│   │   │   │   │   └── out/   # Outbound ports (repositories, APIs)
│   │   │   │   └── service/
│   │   │   │       ├── MetricsCalculator
│   │   │   │       └── RankComparator
│   │   │   ├── application/
│   │   │   │   └── service/
│   │   │   │       ├── FetchProfileService
│   │   │   │       └── GenerateSuggestionsService
│   │   │   └── infrastructure/
│   │   │       ├── adapter/
│   │   │       │   ├── in/rest/       # REST controllers
│   │   │       │   └── out/
│   │   │       │       ├── persistence/
│   │   │       │       ├── external/
│   │   │       │       └── cache/
│   │   │       └── config/
│   │   └── test/
│   └── Dockerfile
├── mobile/
│   ├── package.json
│   ├── app/
│   │   ├── (tabs)/
│   │   │   ├── index.tsx       # Profile dashboard
│   │   │   ├── suggestions.tsx # AI suggestions
│   │   │   └── matches.tsx     # Match history
│   │   └── _layout.tsx
│   ├── api/
│   │   ├── client.ts
│   │   └── types.ts
│   └── components/
├── docs/
│   └── technical-spec.md
├── .clinerules
├── .env.example
├── .gitignore
├── docker-compose.yml
├── Makefile
└── README.md
```

---

## Core Features (MVP)

| Feature           | Description                                         | Priority |
|-------------------|-----------------------------------------------------|----------|
| Profile Dashboard | Winrate, KDA, CS/min, Vision Score, main role       | P0       |
| Rank Comparison   | Compare metrics vs median at your rank & rank above | P0       |
| AI Suggestions    | GPT-4o-mini top 3 priorities based on biggest gaps  | P0       |
| Match History     | Last 20 ranked games with W/L, KDA, CS              | P1       |

**MVP Scope**: Single profile mode via env vars (RIOT_GAME_NAME, RIOT_TAG_LINE, RIOT_REGION). No search UI.

---

## Key Metrics

### Tracked & Compared
1. **CS/min** - Farm efficiency (most impactful)
2. **KDA** - (K+A)/D ratio
3. **Vision Score/min** - Map awareness
4. **Kill Participation %** - Team fight involvement
5. **Death count** - Survivability
6. **Gold diff @15** - Early game impact (nullable)

### Comparison Logic
- Calculate a user's rolling 20-game average per metric
- Compare against **median** (not mean) for their tier
- Compare against tier above median
- Flag metrics where gap > 10% as priorities
- Rank by gap size (the biggest = top priority)

**Wording**: Use "average player" in UI (technically median, user-friendly wording)

---

## Riot API Integration

### Endpoints Used

**ACCOUNT-V1** (Regional routing: europe.api.riotgames.com)
- `GET /riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}`

**SUMMONER-V4** (Platform: euw1.api.riotgames.com)
- `GET /lol/summoner/v4/summoners/by-puuid/{puuid}`

**LEAGUE-V4** (Platform)
- `GET /lol/league/v4/entries/by-summoner/{summonerId}`

**MATCH-V5** (Regional routing)
- `GET /lol/match/v5/matches/by-puuid/{puuid}/ids?queue=420&count=20`
- `GET /lol/match/v5/matches/{matchId}`

### Rate Limits
- Dev key: 20 req/sec, 100 req/2min (expires every 24h)
- Cache heavily: 24h profiles, 1h match lists, 24h match details

---

## OpenAI Integration

**Model**: gpt-4o-mini (~$0.15/1M input tokens)

**Prompt Template**:
```
You are a League of Legends coach. Analyze this player:

Rank: Gold III
Metrics (vs average Gold player):
- CS/min: 5.8 (-12% vs 6.6 avg)
- KDA: 2.8 (-7% vs 3.0 avg)
- Vision/min: 1.2 (+8% vs 1.1 avg)

Generate 3 concise, actionable improvement priorities. Format:
1. [Priority] - [Why it matters] - [How to improve]

Keep each under 50 words. Focus on biggest gaps first.
```

**Caching**: Cache by profile_hash (rank + metrics) for 24h

---

## Database Schema

```sql
-- Summoner profiles
CREATE TABLE summoner_profiles (
    puuid VARCHAR(78) PRIMARY KEY,
    game_name VARCHAR(16) NOT NULL,
    tag_line VARCHAR(5) NOT NULL,
    region VARCHAR(4) NOT NULL,
    tier VARCHAR(20),
    division VARCHAR(4),
    league_points INTEGER,
    wins INTEGER,
    losses INTEGER,
    main_role VARCHAR(10),
    cs_per_min DECIMAL(4,2),
    kda DECIMAL(4,2),
    vision_per_min DECIMAL(4,2),
    kill_participation DECIMAL(5,2),
    avg_deaths DECIMAL(4,2),
    gold_diff_at_15 INTEGER,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rank median metrics (seeded data)
CREATE TABLE rank_metrics (
    tier VARCHAR(20) PRIMARY KEY,
    median_cs_per_min DECIMAL(4,2),
    median_kda DECIMAL(4,2),
    median_vision_per_min DECIMAL(4,2),
    median_kill_participation DECIMAL(5,2),
    median_deaths DECIMAL(4,2),
    median_gold_diff_at_15 INTEGER,
    sample_size INTEGER,
    last_updated DATE
);

-- Match history
CREATE TABLE match_analyses (
    match_id VARCHAR(20) PRIMARY KEY,
    puuid VARCHAR(78) REFERENCES summoner_profiles(puuid),
    played_at TIMESTAMP NOT NULL,
    champion_name VARCHAR(50),
    win BOOLEAN,
    kills INTEGER,
    deaths INTEGER,
    assists INTEGER,
    cs INTEGER,
    game_duration_seconds INTEGER,
    vision_score INTEGER,
    gold_diff_at_15 INTEGER,
    INDEX idx_puuid_played (puuid, played_at DESC)
);

-- AI suggestions (cached)
CREATE TABLE suggestions (
    id BIGSERIAL PRIMARY KEY,
    puuid VARCHAR(78) REFERENCES summoner_profiles(puuid),
    profile_hash VARCHAR(32) UNIQUE,
    priority_1_title TEXT,
    priority_1_reason TEXT,
    priority_1_action TEXT,
    priority_2_title TEXT,
    priority_2_reason TEXT,
    priority_2_action TEXT,
    priority_3_title TEXT,
    priority_3_reason TEXT,
    priority_3_action TEXT,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Seed Data
```sql
INSERT INTO rank_metrics (tier, median_cs_per_min, median_kda, median_vision_per_min, median_kill_participation, median_deaths, median_gold_diff_at_15) VALUES
('IRON', 4.2, 2.1, 0.8, 52.0, 6.5, -200),
('BRONZE', 4.8, 2.3, 0.9, 54.0, 6.0, -100),
('SILVER', 5.5, 2.6, 1.0, 57.0, 5.5, 0),
('GOLD', 6.2, 3.0, 1.1, 61.0, 5.0, 100),
('PLATINUM', 7.2, 3.3, 1.2, 63.0, 4.5, 200),
('EMERALD', 7.8, 3.6, 1.3, 65.0, 4.2, 300),
('DIAMOND', 8.5, 4.0, 1.4, 68.0, 3.8, 400),
('MASTER', 9.0, 4.5, 1.5, 70.0, 3.5, 500),
('GRANDMASTER', 9.5, 5.0, 1.6, 72.0, 3.2, 600),
('CHALLENGER', 10.0, 5.5, 1.7, 75.0, 3.0, 700);
```

---

## REST API Endpoints

**GET /api/profile**
- Returns: SummonerProfile with metrics

**GET /api/suggestions**
- Returns: Top 3 AI-generated improvement priorities

**POST /api/suggestions/refresh**
- Clears cache, regenerates suggestions

**GET /api/matches**
- Returns: Last 20 ranked matches

---

## Configuration

### application.yml
```yaml
spring:
  application:
    name: coach-diff-backend
  datasource:
    url: jdbc:postgresql://localhost:5432/coachdiff
    username: dev
    password: dev
  data:
    redis:
      host: localhost
      port: 6379
  threads:
    virtual:
      enabled: true

coach-diff:
  riot:
    api-key: ${COACHDIFF_RIOT_API_KEY}
    routing-urls:
      europe: https://europe.api.riotgames.com
    platform-urls:
      euw1: https://euw1.api.riotgames.com
  riot-id:
    game-name: ${COACHDIFF_RIOT_GAME_NAME}
    tag-line: ${COACHDIFF_RIOT_TAG_LINE}
    region: ${COACHDIFF_RIOT_REGION:euw1}

openai:
  api-key: ${COACHDIFF_OPENAI_API_KEY}
  model: gpt-4o-mini
```

### .env.example
```bash
# Riot API
COACHDIFF_RIOT_API_KEY=RGAPI-your-dev-key
COACHDIFF_RIOT_GAME_NAME=YourSummonerName
COACHDIFF_RIOT_TAG_LINE=EUW
COACHDIFF_RIOT_REGION=euw1

# OpenAI
COACHDIFF_OPENAI_API_KEY=sk-your-openai-key

# Database
POSTGRES_DB=coachdiff
POSTGRES_USER=dev
POSTGRES_PASSWORD=dev
```

---

## Development Plan (8 Weeks)

### Phase 0: Setup (Week 1)
- Spring Boot 4.0 + Java 21 skeleton
- Hexagonal structure setup
- Maven parent POM + backend module
- Docker Compose (PostgreSQL + Redis)
- Riot & OpenAI API key configuration
- Seed rank_metrics table

### Phase 1: Core API (Week 2-3)
- RiotApiAdapter (dual RestClient: routing + platform)
- FetchProfileService
- MetricsCalculator service
- REST endpoints: /api/profile, /api/matches
- Redis caching
- Unit + integration tests (Testcontainers + WireMock)

### Phase 2: Analysis (Week 4)
- RankComparator service
- MetricComparison calculations
- REST endpoint: /api/profile/comparison
- Tests for comparison logic

### Phase 3: AI Suggestions (Week 5)
- OpenAISuggestionAdapter
- Prompt engineering
- GenerateSuggestionsService
- Suggestion caching by profile_hash
- REST endpoint: /api/suggestions
- Error handling for OpenAI failures

### Phase 4: Mobile (Week 6-7)
- React Native + Expo setup
- TanStack Query + TypeScript API client
- Profile Dashboard screen (rank card, metric comparisons)
- Suggestions screen (top 3 priorities)
- Match History screen (FlatList)
- Navigation, error states, styling

### Phase 5: Polish (Week 8)
- Error handling improvements
- Better loading states
- Backend deploy (Railway/Render)
- E2E testing
- Documentation

---

## Success Criteria (MVP)

- [ ] App loads profile (via env vars) in < 3s
- [ ] Shows rank (tier, division, LP, winrate)
- [ ] Displays metrics with vs. rank avg % delta
- [ ] Generates 3 AI suggestions
- [ ] Shows last 20 ranked matches
- [ ] Works on iOS/Android via Expo Go
- [ ] Metrics match OP.GG within 5% variance
- [ ] Backend deployed
- [ ] 70%+ test coverage on domain

---

## Key Technical Decisions

### Architecture
- **Hexagonal**: Domain has zero framework dependencies
- **Dual RestClient**: @Qualifier("routingRestClient") for Account/Match, @Qualifier("platformRestClient") for Summoner/League
- **Virtual Threads**: Enabled in Spring Boot 4.0 for parallel match fetching

### Metrics
- **Median not mean**: Use median for rank comparisons (outlier-resistant)
- **Wording**: Call it "average player" in UI for user-friendliness
- **Gap threshold**: > 10% deviation = improvement priority

### Caching
- Profile: 24h TTL
- Match list: 1h TTL
- Match details: 24h TTL
- AI suggestions: 24h TTL by profile_hash

### AI
- **Model**: gpt-4o-mini (cost-effective)
- **Prompt**: Max 50 words per suggestion
- **Cache**: By profile_hash to minimize API costs

---

## Risks & Mitigations

| Risk                 | Mitigation                                     |
|----------------------|------------------------------------------------|
| Riot API rate limits | Aggressive caching (Redis), 24h for profiles   |
| OpenAI costs         | GPT-4o-mini, cache by profile_hash, ~$5-10 MVP |
| No rank data         | Hardcode community medians initially           |
| Scope creep          | Strict MVP: P0 only                            |
| Timeline             | 2-3 hours/week, 8 weeks                        |

---

## Out of Scope (Post-MVP)

- Account search UI
- Multi-account support
- Live game tracking
- Champion builds
- Social features
- Replay analysis
- Trend charts

---

## Getting Started

### Prerequisites
- Java 21 JDK
- Maven 3.9+
- Node.js 18+
- Docker & Docker Compose
- Riot API key (https://developer.riotgames.com/)
- OpenAI API key (https://platform.openai.com/)

### Quick Start
```bash
# Clone and setup
git clone <repo-url>
cd coach-diff
cp .env.example .env
# Edit .env with your API keys

# Install dependencies
make install

# Start services
make start

# Run backend (separate terminal)
make backend

# Run mobile (separate terminal)
make mobile
```

### IntelliJ IDEA
1. File → Open → Select coach-diff/ root (or backend/pom.xml for backend only)
2. IntelliJ auto-imports Maven modules
3. Run → Edit Configurations → Spring Boot
4. Module: coach-diff-backend
5. Main class: com.coachdiff.Application

---

## References

- **Riot API Docs**: https://developer.riotgames.com/apis
- **Spring Boot 4.0**: https://docs.spring.io/spring-boot/docs/4.0.0/reference/html/
- **OpenAI API**: https://platform.openai.com/docs/api-reference
- **React Native**: https://reactnative.dev/
- **Expo**: https://docs.expo.dev/
- **Hexagonal Architecture**: https://alistair.cockburn.us/hexagonal-architecture/

---

## Notes

- This is a learning project focused on Java 21, Spring Boot 4.0, Hexagonal Architecture, and React Native
- AI integration adds modern skill (GPT-4o-mini)
- Mobile-first approach for quick glances between games
- Median-based comparisons provide more accurate rank benchmarks than means