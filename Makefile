# =============================================================================
# CoachDiff.ai - Makefile
# =============================================================================
#
# Commands to simplify development workflow.
#
# USAGE:
#   make help      → Show all available commands
#   make start     → Start PostgreSQL and Redis with Docker
#   make backend   → Start Spring Boot backend
#
# PREREQUISITES:
#   - Docker and Docker Compose
#   - Java 21 JDK
#   - Maven 3.9+ (or use the wrapper ./mvnw)
#
# =============================================================================

# Shell to use
SHELL := /bin/bash

# Variables
BACKEND_DIR := backend
MOBILE_DIR := mobile

# Colors for output (optional, improves readability)
GREEN := \033[0;32m
YELLOW := \033[0;33m
NC := \033[0m  # No Color

# =============================================================================
# HELP
# =============================================================================
# Default target: show help

.PHONY: help
help:
	@echo ""
	@echo "$(GREEN)CoachDiff.ai - Development Commands$(NC)"
	@echo "====================================="
	@echo ""
	@echo "$(YELLOW)Setup:$(NC)"
	@echo "  make install       Install dependencies (Maven + npm)"
	@echo "  make setup         Full setup (install + start + seed)"
	@echo ""
	@echo "$(YELLOW)Docker Services:$(NC)"
	@echo "  make start         Start PostgreSQL and Redis"
	@echo "  make stop          Stop Docker services"
	@echo "  make restart       Restart Docker services"
	@echo "  make logs          Show container logs"
	@echo "  make clean-docker  Stop and remove volumes (WARNING: loses data)"
	@echo ""
	@echo "$(YELLOW)Backend:$(NC)"
	@echo "  make backend       Start Spring Boot (requires 'make start' first)"
	@echo "  make backend-test  Run backend tests"
	@echo "  make backend-build Build backend (skip tests)"
	@echo ""
	@echo "$(YELLOW)Mobile:$(NC)"
	@echo "  make mobile        Start Expo dev server"
	@echo ""
	@echo "$(YELLOW)Database:$(NC)"
	@echo "  make db-migrate    Run Flyway migrations"
	@echo "  make db-info       Show migration status"
	@echo "  make db-psql       Open psql in PostgreSQL container"
	@echo ""
	@echo "$(YELLOW)Utility:$(NC)"
	@echo "  make clean         Clean build artifacts"
	@echo "  make status        Show service status"
	@echo ""

# =============================================================================
# SETUP
# =============================================================================

.PHONY: install
install:
	@echo "$(GREEN)Installing backend dependencies...$(NC)"
	cd $(BACKEND_DIR) && ./mvnw dependency:resolve -B
	@if [ -d "$(MOBILE_DIR)" ]; then \
		echo "$(GREEN)Installing mobile dependencies...$(NC)"; \
		cd $(MOBILE_DIR) && npm install; \
	fi
	@echo "$(GREEN)Dependencies installed!$(NC)"

.PHONY: setup
setup: install start
	@echo "$(GREEN)Waiting for services to be ready...$(NC)"
	@sleep 5
	@echo "$(GREEN)Setup complete! Run 'make backend' to start the application.$(NC)"

# =============================================================================
# DOCKER SERVICES
# =============================================================================

.PHONY: start
start:
	@echo "$(GREEN)Starting Docker services (PostgreSQL, Redis)...$(NC)"
	docker compose up -d
	@echo "Waiting for services to be healthy..."
	@sleep 3
	@docker compose ps
	@echo ""
	@echo "$(GREEN)Services started!$(NC)"
	@echo "PostgreSQL: localhost:5432"
	@echo "Redis: localhost:6379"

.PHONY: stop
stop:
	@echo "$(YELLOW)Stopping Docker services...$(NC)"
	docker compose down
	@echo "$(GREEN)Services stopped.$(NC)"

.PHONY: restart
restart: stop start

.PHONY: logs
logs:
	docker compose logs -f

.PHONY: clean-docker
clean-docker:
	@echo "$(YELLOW)WARNING: This will delete all data!$(NC)"
	@read -p "Are you sure? [y/N] " confirm && [ "$$confirm" = "y" ]
	docker compose down -v
	@echo "$(GREEN)Docker volumes removed.$(NC)"

# =============================================================================
# BACKEND
# =============================================================================

.PHONY: backend
backend:
	@echo "$(GREEN)Starting Spring Boot backend...$(NC)"
	@echo "Make sure Docker services are running (make start)"
	@echo ""
	cd $(BACKEND_DIR) && ./mvnw spring-boot:run

.PHONY: backend-test
backend-test:
	@echo "$(GREEN)Running backend tests...$(NC)"
	@echo "Note: Tests use Testcontainers (Docker required)"
	@echo ""
	cd $(BACKEND_DIR) && ./mvnw test

.PHONY: backend-build
backend-build:
	@echo "$(GREEN)Building backend (skip tests)...$(NC)"
	cd $(BACKEND_DIR) && ./mvnw package -DskipTests -B
	@echo "$(GREEN)Build complete: $(BACKEND_DIR)/target/*.jar$(NC)"

# =============================================================================
# MOBILE
# =============================================================================

.PHONY: mobile
mobile:
	@if [ -d "$(MOBILE_DIR)" ]; then \
		echo "$(GREEN)Starting Expo dev server...$(NC)"; \
		cd $(MOBILE_DIR) && npx expo start; \
	else \
		echo "$(YELLOW)Mobile directory not found. Phase 4 not implemented yet.$(NC)"; \
	fi

# =============================================================================
# DATABASE
# =============================================================================

.PHONY: db-migrate
db-migrate:
	@echo "$(GREEN)Running Flyway migrations...$(NC)"
	cd $(BACKEND_DIR) && ./mvnw flyway:migrate

.PHONY: db-info
db-info:
	@echo "$(GREEN)Flyway migration status:$(NC)"
	cd $(BACKEND_DIR) && ./mvnw flyway:info

.PHONY: db-psql
db-psql:
	@echo "$(GREEN)Connecting to PostgreSQL...$(NC)"
	@echo "Type \\q to exit"
	docker exec -it coachdiff-postgres psql -U dev -d coachdiff

# =============================================================================
# UTILITY
# =============================================================================

.PHONY: clean
clean:
	@echo "$(YELLOW)Cleaning build artifacts...$(NC)"
	cd $(BACKEND_DIR) && ./mvnw clean
	@if [ -d "$(MOBILE_DIR)" ]; then \
		cd $(MOBILE_DIR) && rm -rf node_modules .expo dist; \
	fi
	@echo "$(GREEN)Clean complete.$(NC)"

.PHONY: status
status:
	@echo "$(GREEN)Docker services status:$(NC)"
	@docker compose ps
	@echo ""
	@echo "$(GREEN)Checking backend health...$(NC)"
	@curl -s http://localhost:8080/actuator/health 2>/dev/null || echo "Backend not running"
