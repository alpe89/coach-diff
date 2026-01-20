/**
 * DOMAIN MODEL - Entities and Value Objects.
 *
 * <h2>What goes here</h2>
 * <ul>
 *   <li><b>Entities</b>: Objects with identity (e.g., SummonerProfile identified by PUUID)</li>
 *   <li><b>Value Objects</b>: Immutable objects without identity (e.g., RankInfo, KDA)</li>
 *   <li><b>Aggregates</b>: Cluster of entities treated as a unit</li>
 * </ul>
 *
 * <h2>Best Practices</h2>
 * <ul>
 *   <li>Use Java <b>record</b> for value objects (immutable by default)</li>
 *   <li>Validate invariants in the constructor</li>
 *   <li>NO JPA annotations (@Entity, @Id) - those go in adapters</li>
 *   <li>NO Jackson annotations (@JsonProperty) - serialization is infrastructure</li>
 * </ul>
 *
 * <h2>Planned Models</h2>
 * <pre>
 * SummonerProfile   - Player profile with rank and metrics
 * RankInfo          - Tier, division, LP, winrate
 * ProfileMetrics    - CS/min, KDA, Vision, etc.
 * RankMetrics       - Medians for a given tier
 * MetricComparison  - Metric comparison vs median
 * ImprovementSuggestion - AI suggestion
 * MatchAnalysis     - Single match analysis
 * </pre>
 */
package com.coachdiff.domain.model;
