package com.aem.demo.core.services;

import com.aem.demo.core.graphql.GraphQLClientException;

/**
 * BFF (Backend-for-Frontend) service interface.
 *
 * <p>This service is the single orchestration point between the {@code DemoBffServlet}
 * and the {@code GraphQLClient}. It:
 * <ul>
 *   <li>Accepts a business selector from the servlet.</li>
 *   <li>Resolves the selector to one or more configured persisted-query names via the
 *       OSGi operation registry.</li>
 *   <li>Delegates HTTP execution to {@link com.aem.demo.core.graphql.GraphQLClient}.</li>
 *   <li>Returns raw GraphQL JSON — no transformation.</li>
 * </ul>
 *
 * <h3>Contract</h3>
 * <p>The servlet MUST NOT call {@code GraphQLClient} directly. All orchestration
 * goes through this service.
 *
 * <h3>Allow-list security</h3>
 * <p>Only explicitly configured business selectors are accepted. An unknown selector
 * results in an {@link IllegalArgumentException} so the servlet can respond 400.
 */
public interface DemoBffService {

    /**
     * Executes the BFF business operation identified by {@code businessSelector}.
     *
     * <p>For a single-query operation the raw GraphQL JSON is returned directly.
     * For a multi-query operation the responses are merged into a single JSON object
     * whose keys are the persisted-query names, preserving the original GraphQL
     * structure under each key.
     *
     * @param businessSelector the selector extracted from the Sling request
     *                         (e.g. {@code "articles"}, {@code "authors"},
     *                         {@code "article-details"})
     * @return raw GraphQL JSON string; never {@code null}
     * @throws IllegalArgumentException if the {@code businessSelector} is not
     *                                  configured (allow-list violation)
     * @throws GraphQLClientException   if the upstream GraphQL execution fails
     */
    String execute(String businessSelector)
            throws IllegalArgumentException, GraphQLClientException;

    /**
     * Returns {@code true} if the given business selector is registered in the
     * operation registry (i.e. it is an explicitly allowed operation).
     *
     * <p>This can be used by the servlet for a fast pre-check before invoking
     * {@link #execute(String)}.
     *
     * @param businessSelector selector to check
     * @return {@code true} if configured; {@code false} otherwise
     */
    boolean isOperationSupported(String businessSelector);
}
