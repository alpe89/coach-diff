/**
 * APPLICATION LAYER - Application Services.
 *
 * <h2>Role</h2>
 * <p>
 * This layer orchestrates the application flow:
 * </p>
 * <ol>
 *   <li>Receives requests from IN ports</li>
 *   <li>Coordinates domain services and OUT ports</li>
 *   <li>Returns results</li>
 * </ol>
 *
 * <h2>Characteristics</h2>
 * <ul>
 *   <li>Implements interfaces defined in {@code domain/port/in}</li>
 *   <li>Can use Spring's {@code @Service} (it's outside the pure domain)</li>
 *   <li>Manages transactions if needed ({@code @Transactional})</li>
 *   <li>Does NOT contain business logic: that belongs in the domain</li>
 * </ul>
 *
 * <h2>Planned Services</h2>
 * <pre>
 * FetchProfileService
 *   1. Call RiotApiPort to get account info
 *   2. Call RiotApiPort to get rank
 *   3. Call RiotApiPort to get match history
 *   4. Use MetricsCalculator to calculate metrics
 *   5. Persist with SummonerRepository
 *   6. Return SummonerProfile
 *
 * GenerateSuggestionsService
 *   1. Retrieve profile and metrics
 *   2. Retrieve RankMetrics for current and above tier
 *   3. Use RankComparator to find gaps
 *   4. Call SuggestionEnginePort (OpenAI) to generate suggestions
 *   5. Persist and return suggestions
 * </pre>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * @Service
 * public class FetchProfileService implements FetchProfilePort {
 *
 *     private final RiotApiPort riotApi;
 *     private final SummonerRepository repository;
 *     private final MetricsCalculator metricsCalculator;
 *
 *     @Override
 *     public SummonerProfile fetchProfile(String gameName, String tagLine, String region) {
 *         // 1. Fetch from Riot API
 *         var account = riotApi.getAccountByRiotId(gameName, tagLine);
 *
 *         // 2. Get matches and calculate metrics
 *         var matches = riotApi.getMatchIds(account.puuid(), 20).stream()
 *             .map(riotApi::getMatch)
 *             .toList();
 *         var metrics = metricsCalculator.calculate(matches);
 *
 *         // 3. Build and save profile
 *         var profile = new SummonerProfile(account, metrics);
 *         return repository.save(profile);
 *     }
 * }
 * }</pre>
 *
 * @see com.coachdiff.domain.port.in
 */
package com.coachdiff.application.service;