/**
 * INBOUND PORTS - Use Case Interfaces.
 *
 * <h2>What they are</h2>
 * <p>
 * Inbound ports define <b>what the application can do</b>.
 * They are interfaces representing the system's use cases.
 * </p>
 *
 * <h2>Who implements them?</h2>
 * <p>
 * Implementations go in the {@code application/usecase} layer.
 * </p>
 *
 * <h2>Who uses them?</h2>
 * <p>
 * IN adapters (e.g., REST controllers) call these interfaces.
 * The controller doesn't know (and shouldn't know) the implementation.
 * </p>
 *
 * <h2>Planned Ports</h2>
 * <pre>
 * FetchProfileUseCase        - Fetch player profile
 * GenerateSuggestionsUseCase - Generate AI suggestions
 * GetMatchHistoryUseCase     - Fetch match history
 * </pre>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * public interface FetchProfileUseCase {
 *     SummonerProfile fetchProfile(String gameName, String tagLine, String region);
 * }
 * }</pre>
 *
 * @see com.coachdiff.application.usecase
 */
package com.coachdiff.domain.port.in;
