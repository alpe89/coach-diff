/**
 * DOMAIN LAYER - The heart of the application.
 *
 * <h2>Fundamental Rules</h2>
 * <ul>
 *   <li><b>ZERO framework dependencies</b>: No Spring, JPA, Jackson here</li>
 *   <li><b>Only pure Java</b>: POJO, record, interface</li>
 *   <li><b>Testable in isolation</b>: Pure JUnit without containers</li>
 * </ul>
 *
 * <h2>Structure</h2>
 * <pre>
 * domain/
 * ├── model/     → Entities and Value Objects (e.g., SummonerProfile, RankInfo)
 * ├── port/      → Interfaces (contracts) that define what the domain needs
 * │   ├── in/    → Inbound ports: use case interfaces called from outside
 * │   └── out/   → Outbound ports: repository/external interfaces the domain uses
 * └── service/   → Domain services: logic that doesn't belong to a single entity
 * </pre>
 *
 * <h2>Why this separation?</h2>
 * <p>
 * The domain is the "center" of the hexagon. It doesn't know (and shouldn't know) if:
 * <ul>
 *   <li>Data comes from PostgreSQL, MongoDB, or a file</li>
 *   <li>Requests arrive via REST, GraphQL, or CLI</li>
 *   <li>The AI is OpenAI, Anthropic, or a local model</li>
 * </ul>
 * </p>
 *
 * @see com.coachdiff.domain.model
 * @see com.coachdiff.domain.port
 * @see com.coachdiff.domain.service
 */
package com.coachdiff.domain;