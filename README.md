# CoachDiff.ai

> "Find your diff, powered by AI"

AI-powered League of Legends virtual coach that transforms Riot API data into **3 actionable improvement priorities**.

## The Problem

Existing trackers (OP.GG, Mobalytics) show raw stats without practical guidance. Players see data but don't know what to improve.

## The Solution

Mobile-first app that:
1. Fetches your stats from the Riot API
2. Compares them against the **median** of players at your rank and one tier above
3. Identifies the most significant gaps (>10%)
4. Generates **3 personalized suggestions** with GPT-4o-mini

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 21, Spring Boot 4.0, PostgreSQL, Redis |
| Architecture | Hexagonal (Ports & Adapters) |
| Mobile | React Native 0.76, Expo 52, TypeScript 5.7 |
| AI | OpenAI GPT-4o-mini |
| DevOps | Docker Compose, Testcontainers, WireMock |

## Prerequisites

- Java 21 JDK ([Eclipse Temurin](https://adoptium.net/))
- Maven 3.9+
- Docker & Docker Compose
- Node.js 18+ (for mobile)
- [Riot API Key](https://developer.riotgames.com/)
- [OpenAI API Key](https://platform.openai.com/)

## Quick Start

```bash
# 1. Clone the repository
git clone <repo-url>
cd coach-diff

# 2. Copy and configure environment variables
cp .env.example .env
# Edit .env with your API keys

# 3. Start services (PostgreSQL + Redis)
make start

# 4. Start the backend
make backend

# 5. Verify it works
curl http://localhost:8080/actuator/health
# Response: {"status":"UP"}
```

## Available Commands

```bash
make help          # Show all commands
make install       # Install dependencies (Maven + npm)
make start         # Start Docker (PostgreSQL, Redis)
make stop          # Stop Docker
make backend       # Start Spring Boot
make backend-test  # Run tests
make mobile        # Start Expo (React Native)
make clean         # Clean build artifacts
```

## Project Structure

```
coach-diff/
├── backend/                    # Java Spring Boot
│   ├── src/main/java/
│   │   └── com/coachdiff/backend/
│   │       ├── domain/         # Business logic (no framework deps)
│   │       │   ├── model/      # Entities and value objects
│   │       │   ├── port/       # Interfaces (contracts)
│   │       │   └── service/    # Domain services
│   │       ├── application/    # Use cases
│   │       └── infrastructure/ # Adapters (REST, DB, external APIs)
│   └── src/test/
├── mobile/                     # React Native + Expo
├── docker-compose.yml          # PostgreSQL + Redis
└── Makefile                    # Development commands
```

## Hexagonal Architecture

```
                    ┌─────────────────────┐
                    │   REST Controllers  │ ◄── Primary Adapter (IN)
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │     Use Cases       │ ◄── Application Layer
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │   Domain Services   │ ◄── Core Business Logic
                    │   Domain Models     │     (ZERO framework deps)
                    └──────────┬──────────┘
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
┌─────────▼─────────┐ ┌────────▼────────┐ ┌────────▼────────┐
│   PostgreSQL      │ │     Redis       │ │   Riot/OpenAI   │
│   Repository      │ │     Cache       │ │   API Clients   │
└───────────────────┘ └─────────────────┘ └─────────────────┘
         ▲                    ▲                    ▲
         └── Secondary Adapters (OUT) ─────────────┘
```

**Why Hexagonal?**
- The **domain** doesn't depend on Spring, JPA, or other frameworks
- Easy to test: mock the ports (interfaces)
- Replaceable: change database or external API without touching the logic

## API Endpoints (MVP)

| Endpoint | Description |
|----------|-------------|
| `GET /api/profile` | Profile with rank and metrics |
| `GET /api/suggestions` | Top 3 AI priorities |
| `POST /api/suggestions/refresh` | Regenerate suggestions |
| `GET /api/matches` | Last 20 ranked matches |

## Tracked Metrics

| Metric | Description |
|--------|-------------|
| CS/min | Farm efficiency (most impactful) |
| KDA | (Kills + Assists) / Deaths |
| Vision Score/min | Map awareness |
| Kill Participation % | Team fight involvement |
| Deaths | Survivability |
| Gold diff @15 | Early game impact |

## License

This is a learning project focused on:
- Java 21 features (virtual threads, records, pattern matching)
- Spring Boot 4.0
- Hexagonal Architecture
- React Native with Expo
- AI integration (OpenAI)
