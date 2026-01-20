/**
 * INFRASTRUCTURE LAYER - Adapters and Configuration.
 *
 * <h2>Role</h2>
 * <p>
 * This layer contains everything that is <b>framework and technology specific</b>:
 * </p>
 * <ul>
 *   <li>Spring MVC controllers</li>
 *   <li>JPA entities and repositories</li>
 *   <li>HTTP clients for external APIs</li>
 *   <li>Redis cache</li>
 *   <li>Spring configuration</li>
 * </ul>
 *
 * <h2>Structure</h2>
 * <pre>
 * infrastructure/
 * ├── adapter/
 * │   ├── in/        → IN adapters (receive requests)
 * │   │   └── rest/  → REST controllers
 * │   └── out/       → OUT adapters (call external services)
 * │       ├── persistence/  → JPA repositories
 * │       ├── external/     → API clients (Riot, OpenAI)
 * │       └── cache/        → Redis adapter
 * └── config/        → @Configuration classes
 * </pre>
 *
 * <h2>Rules</h2>
 * <ul>
 *   <li>IN adapters call IN ports (use cases)</li>
 *   <li>OUT adapters implement OUT ports</li>
 *   <li>You can use @RestController, @Entity, @Repository, etc. here</li>
 *   <li>Business logic does NOT go here: it belongs in the domain</li>
 * </ul>
 *
 * @see com.coachdiff.infrastructure.adapter
 * @see com.coachdiff.infrastructure.config
 */
package com.coachdiff.infrastructure;
