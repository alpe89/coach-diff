/**
 * DOMAIN SERVICES - Business logic that doesn't belong to a single entity.
 *
 * <h2>When to use a Domain Service</h2>
 * <ul>
 *   <li>The logic involves <b>multiple entities</b></li>
 *   <li>The operation has no natural "owner"</li>
 *   <li>It's a domain policy or algorithm</li>
 * </ul>
 *
 * <h2>Characteristics</h2>
 * <ul>
 *   <li><b>Stateless</b>: No internal state</li>
 *   <li><b>No framework</b>: Pure Java, no @Service</li>
 *   <li><b>Uses only ports</b>: Doesn't know concrete implementations</li>
 * </ul>
 *
 * <h2>Planned Services</h2>
 * <pre>
 * MetricsCalculator  - Calculate aggregate metrics from match list
 *                      Input: List&lt;MatchAnalysis&gt;
 *                      Output: ProfileMetrics (CS/min, KDA, Vision/min, etc.)
 *
 * RankComparator     - Compare metrics with rank medians
 *                      Input: ProfileMetrics, RankMetrics (current), RankMetrics (above)
 *                      Output: List&lt;MetricComparison&gt; sorted by gap
 * </pre>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * public class MetricsCalculator {
 *
 *     public ProfileMetrics calculate(List<MatchAnalysis> matches) {
 *         double avgCsPerMin = matches.stream()
 *             .mapToDouble(m -> m.cs() / (m.durationSeconds() / 60.0))
 *             .average()
 *             .orElse(0.0);
 *         // ... other calculations
 *         return new ProfileMetrics(avgCsPerMin, avgKda, ...);
 *     }
 * }
 * }</pre>
 */
package com.coachdiff.domain.service;
