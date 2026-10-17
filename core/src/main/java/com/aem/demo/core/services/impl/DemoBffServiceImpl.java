package com.aem.demo.core.services.impl;

import com.aem.demo.core.graphql.GraphQLClient;
import com.aem.demo.core.graphql.GraphQLClientException;
import com.aem.demo.core.services.BffOperationRegistration;
import com.aem.demo.core.services.DemoBffService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * OSGi service implementation of {@link DemoBffService}.
 *
 * <h3>Configuration-Driven Operation Registry</h3>
 * <p>
 * This implementation collects all {@link BffOperationRegistration} services
 * published by factory instances of {@link BffOperationRegistrationImpl}. Each
 * {@code .cfg.json} factory file contributes one entry to the allow-list
 * registry.
 *
 * <p>
 * Adding a new business operation requires only a new configuration file — no
 * Java changes are needed.
 *
 * <h3>Allow-List Security</h3>
 * <p>
 * Only selectors explicitly registered via {@code BffOperationRegistration}
 * services are accepted. An unknown selector throws
 * {@link IllegalArgumentException}, causing the servlet to respond with HTTP
 * 400. Internal persisted queries without an explicit registration (e.g.
 * {@code internalDebugQuery}) are never reachable through the BFF.
 *
 * <h3>Multi-Query Orchestration</h3>
 * <p>
 * When an operation maps to more than one persisted query the raw GraphQL JSON
 * responses are combined into a single JSON object keyed by persisted-query
 * name. No transformation of the individual response shapes is performed.
 *
 * <pre>
 * {
 *   "articleById":      { "data": { ... } },
 *   "authorsByArticle": { "data": { ... } }
 * }
 * </pre>
 *
 * <h3>No Response Transformation</h3>
 * <p>
 * This service never maps, renames, flattens or converts the GraphQL JSON.
 */
@Component(service = DemoBffService.class, immediate = true)
public class DemoBffServiceImpl implements DemoBffService {

	private static final Logger LOG = LoggerFactory.getLogger(DemoBffServiceImpl.class);

	/** Thread-pool size for parallel persisted-query execution. */
	private static final int PARALLEL_POOL_SIZE = 5;

	/** Timeout (seconds) for the parallel executor to shut down on deactivation. */
	private static final int EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS = 5;

	// -------------------------------------------------------------------------
	// OSGi references
	// -------------------------------------------------------------------------

	@Reference
	private GraphQLClient graphQLClient;

	/**
	 * Dynamically collected set of all registered BFF operations.
	 *
	 * <p>
	 * Every {@link BffOperationRegistrationImpl} factory instance bound by OSGi DS
	 * calls {@link #bindOperation} / {@link #unbindOperation} here, keeping the
	 * {@link #operationRegistry} map in sync.
	 */
	@Reference(service = BffOperationRegistration.class, cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, bind = "bindOperation", unbind = "unbindOperation")
	private final List<BffOperationRegistration> registrations = Collections.synchronizedList(new ArrayList<>());

	/**
	 * Allow-list registry: businessSelector &rarr;
	 * {@link BffOperationRegistration}. Rebuilt on every bind / unbind.
	 */
	private final ConcurrentHashMap<String, BffOperationRegistration> operationRegistry = new ConcurrentHashMap<>();

	/** Thread pool used when {@code parallelExecution = true}. */
	private ExecutorService executorService;

	// -------------------------------------------------------------------------
	// Lifecycle
	// -------------------------------------------------------------------------

	@Activate
	protected void activate() {
		executorService = Executors.newFixedThreadPool(PARALLEL_POOL_SIZE);
		rebuildRegistry();
		LOG.info("DemoBffServiceImpl activated — {} operation(s) registered: {}", operationRegistry.size(),
				operationRegistry.keySet());
	}

	@Modified
	protected void modified() {
		rebuildRegistry();
		LOG.info("DemoBffServiceImpl modified — {} operation(s) registered: {}", operationRegistry.size(),
				operationRegistry.keySet());
	}

	@Deactivate
	protected void deactivate() {
		if (executorService != null) {
			executorService.shutdown();
			try {
				if (!executorService.awaitTermination(EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
					executorService.shutdownNow();
				}
			} catch (InterruptedException e) {
				executorService.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
		LOG.info("DemoBffServiceImpl deactivated.");
	}

	// -------------------------------------------------------------------------
	// Dynamic reference binding
	// -------------------------------------------------------------------------

	protected void bindOperation(BffOperationRegistration registration) {
		registrations.add(registration);
		rebuildRegistry();
		String persistedQueries = Arrays.toString(registration.getPersistedQueryNames());
		LOG.info("BffOperationRegistration bound — selector='{}', queries={}", registration.getBusinessSelector(),
				persistedQueries);
	}

	protected void unbindOperation(BffOperationRegistration registration) {
		registrations.remove(registration);
		rebuildRegistry();
		LOG.info("BffOperationRegistration unbound — selector='{}'", registration.getBusinessSelector());
	}

	// -------------------------------------------------------------------------
	// DemoBffService implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * <b>Single-query flow:</b>
	 * <ol>
	 * <li>Validate that {@code businessSelector} is in the allow-list.</li>
	 * <li>Resolve persisted-query name(s) from the operation registry.</li>
	 * <li>Delegate to {@link GraphQLClient#executePersistedQuery}.</li>
	 * <li>Return raw GraphQL JSON — no transformation.</li>
	 * </ol>
	 *
	 * <p>
	 * <b>Multi-query flow (parallel or sequential):</b> Same as above but for
	 * multiple queries; individual responses are wrapped as: {@code {
	 * "<queryName>": <rawGraphQLJson>, ... }}.
	 */
	@Override
	public String execute(String businessSelector)
			throws IllegalArgumentException, GraphQLClientException {

		if (businessSelector == null || businessSelector.trim().isEmpty()) {
			throw new IllegalArgumentException("Business selector must not be blank.");
		}

		BffOperationRegistration registration = operationRegistry.get(businessSelector);
		if (registration == null) {
			LOG.warn("Unknown business selector '{}' — not in allow-list. " + "Registered operations: {}",
					businessSelector, operationRegistry.keySet());
			throw new IllegalArgumentException("Unsupported business operation: " + businessSelector);
		}

		String[] persistedQueryNames = registration.getPersistedQueryNames();
		String persistedQueries = Arrays.toString(persistedQueryNames);
		if (persistedQueryNames == null || persistedQueryNames.length == 0) {
			LOG.error("Operation '{}' has no persisted query names configured.", businessSelector);
			throw new GraphQLClientException(GraphQLClientException.ErrorCode.MALFORMED_RESPONSE,
					"No persisted queries configured for operation: " + businessSelector);
		}

		LOG.info("Executing BFF operation '{}' — queries: {}, parallel: {}", businessSelector, persistedQueries,
				registration.isParallelExecution());

		if (persistedQueryNames.length == 1) {
			// Single-query path — return raw GraphQL JSON directly
			return graphQLClient.executePersistedQuery(persistedQueryNames[0]);
		}

		// Multi-query path — execute and combine without transformation
		return registration.isParallelExecution() ? executeParallel(persistedQueryNames)
				: executeSequential(persistedQueryNames);
	}

	@Override
	public boolean isOperationSupported(String businessSelector) {
		return businessSelector != null && operationRegistry.containsKey(businessSelector);
	}

	// -------------------------------------------------------------------------
	// Private helpers
	// -------------------------------------------------------------------------

	/**
	 * Executes multiple persisted queries sequentially and combines the raw JSON
	 * responses into a single JSON object keyed by persisted-query name. The
	 * GraphQL response structure under each key is preserved — no transformation.
	 */
	private String executeSequential(String[] queryNames) throws GraphQLClientException {

		StringBuilder combined = new StringBuilder("{");
		for (int i = 0; i < queryNames.length; i++) {
			String queryName = queryNames[i];
			String response = graphQLClient.executePersistedQuery(queryName);
			if (i > 0) {
				combined.append(",");
			}
			combined.append("\"").append(escapeJsonKey(queryName)).append("\":").append(response);
		}
		combined.append("}");
		return combined.toString();
	}

	/**
	 * Executes multiple persisted queries in parallel using the internal thread
	 * pool and combines the raw JSON responses keyed by persisted-query name.
	 *
	 * <p>
	 * Only safe when queries have no data dependencies. Controlled by
	 * {@link BffOperationRegistration#isParallelExecution()}.
	 */
	private String executeParallel(String[] queryNames) throws GraphQLClientException {

		List<Future<String[]>> futures = new ArrayList<>();

		for (String queryName : queryNames) {
			Callable<String[]> task = () -> {
				String response = graphQLClient.executePersistedQuery(queryName);
				return new String[] { queryName, response };
			};
			futures.add(executorService.submit(task));
		}

		StringBuilder combined = new StringBuilder("{");
		boolean first = true;
		for (Future<String[]> future : futures) {
			try {
				String[] result = future.get();
				if (!first) {
					combined.append(",");
				}
				combined.append("\"").append(escapeJsonKey(result[0])).append("\":").append(result[1]);
				first = false;
			} catch (ExecutionException e) {
				Throwable cause = e.getCause();
				if (cause instanceof GraphQLClientException) {
					throw (GraphQLClientException) cause;
				}
				throw new GraphQLClientException(GraphQLClientException.ErrorCode.UPSTREAM_ERROR,
						"Parallel query execution failed: " + cause.getMessage(), cause);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new GraphQLClientException(GraphQLClientException.ErrorCode.CONNECTION_ERROR,
						"Parallel query execution interrupted.", e);
			}
		}
		combined.append("}");
		return combined.toString();
	}

	/**
	 * Rebuilds the operation allow-list registry from currently bound
	 * {@link BffOperationRegistration} services. Entries with a blank selector are
	 * skipped with a warning.
	 */
	private void rebuildRegistry() {
		operationRegistry.clear();
		synchronized (registrations) {
			for (BffOperationRegistration reg : registrations) {
				String selector = reg.getBusinessSelector();
				if (selector != null && !selector.trim().isEmpty()) {
					operationRegistry.put(selector.trim(), reg);
				} else {
					LOG.warn("BffOperationRegistration with blank businessSelector skipped.");
				}
			}
		}
		LOG.debug("Operation registry rebuilt: {}", operationRegistry.keySet());
	}

	/**
	 * Minimal JSON key escaping — handles backslash and double-quote characters.
	 */
	private String escapeJsonKey(String key) {
		return key.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
