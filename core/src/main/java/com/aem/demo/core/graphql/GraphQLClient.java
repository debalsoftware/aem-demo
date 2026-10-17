package com.aem.demo.core.graphql;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.builder.Builders;
import org.apache.sling.api.request.builder.SlingHttpServletRequestBuilder;
import org.apache.sling.api.request.builder.SlingHttpServletResponseResult;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.demo.core.services.JcrUtility;

/**
 * GraphQL Client responsible for communicating with the AEM GraphQL
 * persisted-query execution endpoint using the Sling-internal
 * {@link SlingRequestProcessor} together with the {@link Builders}
 * request/response builder API (AEM as a Cloud Service).
 *
 * <p>
 * Executing persisted queries via the Sling request pipeline avoids any network
 * round-trip: the request is dispatched directly through the Sling engine,
 * resolved by AEM's own GraphQL servlet, and the response body is captured via
 * an in-memory {@link SlingHttpServletResponseResult}.
 *
 * <h3>Path construction</h3>
 * <p>
 * The full Sling-internal execution path is assembled from three OSGi
 * configuration properties plus the selector supplied by the caller:
 * 
 * <pre>
 *   {graphqlBaseUrl}/{graphqlEndpoint}/{selector};path={damRootPath}{selector}
 *
 *   Example (selector = "articles"):
 *   /graphql/execute/AEM-Demo-SItes/articles;path=/content/dam/demo/articles
 * </pre>
 *
 * <h3>Service-user authentication</h3>
 * <p>
 * A {@link ResourceResolver} obtained for the {@code demo-graphql-service}
 * sub-service is required by {@link Builders#newRequestBuilder(Resource)}. The
 * mapping is provided by
 * {@code ServiceUserMapperImpl.amended-demo-graphql.cfg.json}.
 *
 * <h3>Persisted-query repository vs execution endpoint</h3>
 * <ul>
 * <li>{@code /conf/AEM-Demo-SItes/settings/graphql/persistentQueries} — JCR
 * <em>storage</em> location; this client does NOT read from that path.</li>
 * <li>{@code /graphql/execute/AEM-Demo-SItes/&lt;selector&gt;} — runtime
 * <em>execution</em> endpoint targeted by every request.</li>
 * </ul>
 *
 * <h3>What this client does NOT contain</h3>
 * <ul>
 * <li>Business selector logic</li>
 * <li>Business operation mapping</li>
 * <li>Response transformation</li>
 * <li>Article- or Author-specific logic</li>
 * </ul>
 */
@Component(service = GraphQLClient.class, immediate = true)
@Designate(ocd = GraphQLClientConfig.class)
public class GraphQLClient {

	private static final Logger LOG = LoggerFactory.getLogger(GraphQLClient.class);

	// -------------------------------------------------------------------------
	// Configuration fields (from OSGi)
	// -------------------------------------------------------------------------

	/**
	 * Relative base path of the AEM GraphQL execution servlet, e.g.
	 * {@code /graphql/execute}.
	 */
	private String graphqlBaseUrl;

	/**
	 * AEM Sites GraphQL endpoint / configuration name, e.g. {@code AEM-Demo-SItes}.
	 * Placed between {@code graphqlBaseUrl} and the selector in the path.
	 */
	private String graphqlEndpoint;

	/**
	 * Root DAM path used as the {@code path} query variable, e.g.
	 * {@code /content/dam/demo/}. A trailing slash is enforced by
	 * {@link #normalizeTrailingSlash(String)} at activation.
	 */
	private String damRootPath;

	// -------------------------------------------------------------------------
	// OSGi references
	// -------------------------------------------------------------------------

	@Reference
	private SlingRequestProcessor slingRequestProcessor;

	@Reference
	private JcrUtility jcrUtility;

	// -------------------------------------------------------------------------
	// Lifecycle
	// -------------------------------------------------------------------------

	@Activate
	@Modified
	protected void activate(GraphQLClientConfig config) {
		this.graphqlBaseUrl = trimTrailingSlash(config.graphqlBaseUrl());
		this.graphqlEndpoint = config.graphqlEndpoint() != null ? config.graphqlEndpoint().trim() : "";
		this.damRootPath = normalizeTrailingSlash(config.damRootPath());

		validateConfig();

		LOG.info("GraphQLClient activated — graphqlBaseUrl='{}', graphqlEndpoint='{}', " + "damRootPath='{}'.",
				graphqlBaseUrl, graphqlEndpoint, damRootPath);
	}

	@Deactivate
	protected void deactivate() {
		LOG.info("GraphQLClient deactivated.");
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Executes an AEM GraphQL persisted query via the Sling internal request
	 * pipeline using {@link SlingRequestProcessor} and the {@link Builders} builder
	 * API.
	 *
	 * <p>
	 * The full execution path is assembled dynamically as:
	 * 
	 * <pre>
	 *   {graphqlBaseUrl}/{graphqlEndpoint}/{selector};path={damRootPath}{selector}
	 *
	 *   e.g. /graphql/execute.json/AEM-Demo-SItes/articles;path=/content/dam/demo/articles
	 * </pre>
	 *
	 * <p>
	 * Additional {@code variables} (if any) are passed as individual Sling request
	 * parameters via
	 * {@link SlingHttpServletRequestBuilder#withParameter(String, String)}.
	 *
	 * <p>
	 * A service {@link ResourceResolver} for the {@code demo-graphql-service}
	 * sub-service is opened, used to build the synthetic Sling GET request, and
	 * closed in a {@code finally} block to prevent resource leaks.
	 *
	 * @param selector the persisted-query / business selector (e.g.
	 *                 {@code "articles"}, {@code "authors"})
	 * @return raw GraphQL JSON response string; never {@code null}
	 * @throws GraphQLClientException if the service resolver cannot be obtained,
	 *                                the Sling request processing fails, the
	 *                                endpoint returns a non-200 status, or the
	 *                                response body is empty
	 */
	public String executePersistedQuery(String selector) throws GraphQLClientException {

		long startTime = System.currentTimeMillis();

		String persistedQuerySuffix = buildPersistedQuerySuffix(selector);

		LOG.info("Selector Details {}", selector);

		ResourceResolver resourceResolver = null;
		try {
			// ----------------------------------------------------------------
			// 1. Obtain a service-user ResourceResolver
			// ----------------------------------------------------------------
			resourceResolver = jcrUtility.getResourceResolver();

			// ----------------------------------------------------------------
			// 2. Resolve the GraphQL execution path to a Sling Resource.
			// ResourceResolver.resolve() always returns a Resource (possibly
			// a NonExistingResource) — safe for servlet-served virtual paths.
			// ----------------------------------------------------------------
			Resource graphqlResource = resourceResolver.resolve(graphqlBaseUrl);

			// ----------------------------------------------------------------
			// 3. Build a synthetic SlingHttpServletRequest (HTTP GET).
			// Additional variables are passed as individual request parameters.
			// ----------------------------------------------------------------
			SlingHttpServletRequestBuilder requestBuilder = Builders.newRequestBuilder(graphqlResource)
					.withRequestMethod("GET");

			SlingHttpServletRequestBuilder slingHttpServletRequestBuilder = requestBuilder.withExtension("json")
					.withSuffix(persistedQuerySuffix);
			SlingHttpServletRequest slingRequest = slingHttpServletRequestBuilder.build();

			// ----------------------------------------------------------------
			// 4. Build an in-memory SlingHttpServletResponse to capture output
			// ----------------------------------------------------------------
			SlingHttpServletResponseResult slingResponse = Builders.newResponseBuilder().build();

			// ----------------------------------------------------------------
			// 5. Dispatch through the Sling engine — ResourceResolver is
			// the third argument per SlingRequestProcessor API.
			// ----------------------------------------------------------------
			slingRequestProcessor.processRequest(slingRequest, slingResponse, resourceResolver);

			int httpStatus = slingResponse.getStatus();
			long duration = System.currentTimeMillis() - startTime;
			LOG.info("Persisted query '{}' returned HTTP {} in {}ms.", selector, httpStatus, duration);

			// ----------------------------------------------------------------
			// 6. Evaluate HTTP status
			// ----------------------------------------------------------------
			if (httpStatus == 404) {
				throw new GraphQLClientException(GraphQLClientException.ErrorCode.QUERY_NOT_FOUND,
						"Persisted query not found: " + selector + " (HTTP 404)");
			}

			if (httpStatus >= 500) {
				throw new GraphQLClientException(GraphQLClientException.ErrorCode.UPSTREAM_ERROR,
						"GraphQL endpoint returned HTTP " + httpStatus + " for query: " + selector);
			}

			if (httpStatus != 200) {
				throw new GraphQLClientException(GraphQLClientException.ErrorCode.UPSTREAM_ERROR,
						"Unexpected HTTP status " + httpStatus + " for query: " + selector);
			}

			// ----------------------------------------------------------------
			// 7. Read and validate response body
			// ----------------------------------------------------------------
			String responseBody = slingResponse.getOutputAsString();

			if (responseBody == null || responseBody.isEmpty()) {
				throw new GraphQLClientException(GraphQLClientException.ErrorCode.MALFORMED_RESPONSE,
						"Empty response body for query: " + selector);
			}

			validateGraphQLErrors(selector, responseBody);
			return responseBody;

		} catch (GraphQLClientException e) {
			throw e;
		} catch (ServletException | IOException e) {
			long duration = System.currentTimeMillis() - startTime;
			LOG.error("Error processing persisted query '{}' after {}ms: {}", selector, duration, e.getMessage(), e);
			throw new GraphQLClientException(GraphQLClientException.ErrorCode.CONNECTION_ERROR,
					"Sling request processing error for query: " + selector, e);
		} finally {
			if (resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
			}
		}
	}

	// -------------------------------------------------------------------------
	// Private helpers
	// -------------------------------------------------------------------------

	/**
	 * Builds the persisted-query suffix using the configured endpoint, the supplied
	 * selector, and the configured DAM root path.
	 *
	 * <p>
	 * Pattern:
	 * 
	 * <pre>
	 *   /{graphqlEndpoint}/{selector};path={damRootPath}{selector}
	 * </pre>
	 *
	 * <p>
	 * Example (selector = "articles"):
	 * 
	 * <pre>
	 *   /AEM-Demo-SItes/articles;path=/content/dam/demo/articles
	 * </pre>
	 *
	 * <p>
	 * This suffix is then prepended with {@code graphqlBaseUrl} to form the full
	 * path:
	 * 
	 * <pre>
	 *   /graphql/execute.json/AEM-Demo-SItes/articles;path=/content/dam/demo/articles
	 * </pre>
	 *
	 * @param selector the persisted-query / business selector (e.g.
	 *                 {@code "articles"})
	 * @return the assembled suffix string; never {@code null}
	 */
	private String buildPersistedQuerySuffix(String selector) {
		// normalizedDamRootPath is guaranteed to end with exactly one slash
		// so appending selector directly gives /content/dam/demo/articles
		return "/" + graphqlEndpoint + "/" + selector + ";path=" + damRootPath + selector;
	}

	/**
	 * Validates that the required configuration values are present and non-blank.
	 * Logs a warning for each missing value so operators can diagnose
	 * misconfiguration quickly without needing to trigger a request.
	 */
	private void validateConfig() {
		if (graphqlBaseUrl == null || graphqlBaseUrl.isEmpty()) {
			LOG.warn("GraphQLClient: 'graphqlBaseUrl' is not configured. " + "Persisted-query execution will fail.");
		}
		if (graphqlEndpoint == null || graphqlEndpoint.isEmpty()) {
			LOG.warn("GraphQLClient: 'graphqlEndpoint' is not configured. "
					+ "The AEM Sites GraphQL endpoint name must be set.");
		}
		if (damRootPath == null || damRootPath.isEmpty()) {
			LOG.warn("GraphQLClient: 'damRootPath' is not configured. "
					+ "The DAM root path must be set for the 'path' query variable.");
		}
	}

	/**
	 * Ensures the given path ends with exactly one trailing slash. Returns
	 * {@code "/"} if the input is {@code null} or blank.
	 */
	private String normalizeTrailingSlash(String value) {
		if (value == null || value.trim().isEmpty()) {
			return "/";
		}
		String trimmed = value.trim();
		return trimmed.endsWith("/") ? trimmed : trimmed + "/";
	}

	/**
	 * Removes a trailing slash from the given string, if present. Returns an empty
	 * string if the input is {@code null}.
	 */
	private String trimTrailingSlash(String value) {
		if (value == null) {
			return "";
		}
		String trimmed = value.trim();
		return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
	}

	/**
	 * Logs a warning if the GraphQL response body contains a top-level
	 * {@code "errors"} array. Does not throw — partial data may still be useful and
	 * the raw JSON is returned unchanged to the caller.
	 */
	private void validateGraphQLErrors(String selector, String responseBody) {
		if (responseBody.contains("\"errors\"")) {
			LOG.warn("GraphQL response for query '{}' contains an 'errors' array. "
					+ "The raw response is returned as-is.", selector);
		}
	}
}
