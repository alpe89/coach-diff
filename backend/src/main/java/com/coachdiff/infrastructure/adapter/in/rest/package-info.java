/**
 * REST CONTROLLERS - Primary Adapters (IN).
 *
 * <h2>Role</h2>
 * <p>
 * REST controllers are "inbound adapters": they receive HTTP requests
 * and translate them into calls to use cases.
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Parse HTTP parameters (path, query, body)</li>
 *   <li>Validate input ({@code @Valid})</li>
 *   <li>Call the appropriate use case</li>
 *   <li>Convert response to JSON</li>
 *   <li>Handle errors with correct HTTP status codes</li>
 * </ul>
 *
 * <h2>What NOT to do here</h2>
 * <ul>
 *   <li>Business logic: belongs in the domain</li>
 *   <li>Direct database access: go through use cases</li>
 *   <li>External API calls: use cases use OUT ports</li>
 * </ul>
 *
 * <h2>Planned Endpoints</h2>
 * <pre>
 * ProfileController
 *   GET /api/profile            → FetchProfilePort
 *   GET /api/profile/comparison → With comparison vs rank medians
 *
 * SuggestionsController
 *   GET /api/suggestions        → GetSuggestionsPort
 *   POST /api/suggestions/refresh → GenerateSuggestionsPort
 *
 * MatchController
 *   GET /api/matches            → GetMatchHistoryPort
 * </pre>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * @RestController
 * @RequestMapping("/api")
 * public class ProfileController {
 *
 *     private final FetchProfilePort fetchProfile;
 *
 *     @GetMapping("/profile")
 *     public SummonerProfile getProfile() {
 *         // Reads gameName/tagLine/region from configuration (MVP)
 *         return fetchProfile.fetchProfile(gameName, tagLine, region);
 *     }
 * }
 * }</pre>
 */
package com.coachdiff.infrastructure.adapter.in.rest;
