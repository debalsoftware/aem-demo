package com.aem.demo.core.services.impl;

import com.aem.demo.core.graphql.GraphQLClient;
import com.aem.demo.core.graphql.GraphQLClientException;
import com.aem.demo.core.services.BffOperationRegistration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DemoBffServiceImpl}.
 *
 * <p>All tests mock {@link GraphQLClient} to avoid real HTTP calls.
 * The service orchestration, allow-list enforcement, multi-query combination
 * and error handling are exercised in isolation.
 */
@ExtendWith(MockitoExtension.class)
class DemoBffServiceImplTest {

    @Mock
    private GraphQLClient graphQLClient;

    private DemoBffServiceImpl service;

    // ── Test fixtures ──────────────────────────────────────────────────────────

    private static final String ARTICLES_RESPONSE =
            "{\"data\":{\"articleModelList\":{\"items\":[{\"title\":\"AEM Architecture\"}]}}}";

    private static final String AUTHORS_RESPONSE =
            "{\"data\":{\"authorModelList\":{\"items\":[{\"name\":\"Jane Doe\"}]}}}";

    private static final String ARTICLE_BY_ID_RESPONSE =
            "{\"data\":{\"articleModelByPath\":{\"item\":{\"title\":\"AEM Architecture\"}}}}";

    private static final String AUTHORS_BY_ARTICLE_RESPONSE =
            "{\"data\":{\"authorModelList\":{\"items\":[{\"name\":\"Jane Doe\"}]}}}";

    // ── Setup ──────────────────────────────────────────────────────────────────

    @BeforeEach
    void setUp() {
        service = new DemoBffServiceImpl();
        // Inject the mock GraphQL client via field
        injectField(service, "graphQLClient", graphQLClient);
        // Activate lifecycle (creates executor service and empty registry)
        service.activate();
    }

    // ── isOperationSupported ───────────────────────────────────────────────────

    @Test
    @DisplayName("isOperationSupported returns false when no registrations present")
    void isOperationSupported_noRegistrations_returnsFalse() {
        assertFalse(service.isOperationSupported("articles"));
    }

    @Test
    @DisplayName("isOperationSupported returns true after binding a registration")
    void isOperationSupported_afterBind_returnsTrue() {
        // Only getBusinessSelector() is called during registry rebuild — use lenient
        // for the other stubs so this test does not fail with UnnecessaryStubbingException.
        service.bindOperation(mockRegistration("articles", new String[]{"articles"}, false));
        assertTrue(service.isOperationSupported("articles"));
    }

    @Test
    @DisplayName("isOperationSupported returns false after unbinding a registration")
    void isOperationSupported_afterUnbind_returnsFalse() {
        BffOperationRegistration reg = mockRegistration("articles", new String[]{"articles"}, false);
        service.bindOperation(reg);
        service.unbindOperation(reg);
        assertFalse(service.isOperationSupported("articles"));
    }

    @Test
    @DisplayName("isOperationSupported returns false for null selector")
    void isOperationSupported_nullSelector_returnsFalse() {
        assertFalse(service.isOperationSupported(null));
    }

    // ── Invalid / unknown selector ─────────────────────────────────────────────

    @Test
    @DisplayName("execute throws IllegalArgumentException for null selector")
    void execute_nullSelector_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.execute(null));
    }

    @Test
    @DisplayName("execute throws IllegalArgumentException for blank selector")
    void execute_blankSelector_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.execute("  "));
    }

    @Test
    @DisplayName("execute throws IllegalArgumentException for unknown selector (allow-list enforcement)")
    void execute_unknownSelector_throwsIllegalArgumentException() {
        // No registrations registered — 'internalDebugQuery' must never be reachable
        assertThrows(IllegalArgumentException.class,
                () -> service.execute("internalDebugQuery"));
    }

    // ── Single persisted-query execution ──────────────────────────────────────

    @Test
    @DisplayName("execute returns raw GraphQL JSON for single-query operation")
    void execute_singleQuery_returnsRawGraphQLJson() throws Exception {
        service.bindOperation(mockRegistration("articles", new String[]{"articles"}, false));
        when(graphQLClient.executePersistedQuery(eq("articles")))
                .thenReturn(ARTICLES_RESPONSE);

        String result = service.execute("articles");

        assertEquals(ARTICLES_RESPONSE, result,
                "Single-query response must be returned as-is — no transformation.");
        verify(graphQLClient, times(1)).executePersistedQuery(eq("articles"));
    }

    @Test
    @DisplayName("execute does not transform single-query GraphQL response")
    void execute_singleQuery_noResponseTransformation() throws Exception {
        service.bindOperation(mockRegistration("authors", new String[]{"authors"}, false));
        when(graphQLClient.executePersistedQuery(eq("authors")))
                .thenReturn(AUTHORS_RESPONSE);

        String result = service.execute("authors");

        // The response must contain the exact GraphQL structure — not a different shape
        assertTrue(result.contains("\"data\""), "Response must preserve GraphQL 'data' key.");
        assertTrue(result.contains("authorModelList"), "Response must preserve GraphQL field names.");
        assertFalse(result.contains("\"authors\":["), "Response must NOT be re-shaped into a flat array.");
    }

    // ── Multi-query sequential execution ──────────────────────────────────────

    @Test
    @DisplayName("execute combines two sequential queries into one JSON object keyed by query name")
    void execute_multiQuerySequential_combinesResponses() throws Exception {
        service.bindOperation(mockRegistration(
                "article-details",
                new String[]{"articleById", "authorsByArticle"},
                false /* sequential */
        ));
        when(graphQLClient.executePersistedQuery(eq("articleById")))
                .thenReturn(ARTICLE_BY_ID_RESPONSE);
        when(graphQLClient.executePersistedQuery(eq("authorsByArticle")))
                .thenReturn(AUTHORS_BY_ARTICLE_RESPONSE);

        String result = service.execute("article-details");

        assertNotNull(result);
        assertTrue(result.startsWith("{"), "Combined response must be a JSON object.");
        assertTrue(result.contains("\"articleById\":"), "Combined response must include 'articleById' key.");
        assertTrue(result.contains("\"authorsByArticle\":"), "Combined response must include 'authorsByArticle' key.");
        assertTrue(result.contains("articleModelByPath"), "Original GraphQL structure must be preserved.");
        verify(graphQLClient, times(1)).executePersistedQuery(eq("articleById"));
        verify(graphQLClient, times(1)).executePersistedQuery(eq("authorsByArticle"));
    }

    // ── GraphQL HTTP error ─────────────────────────────────────────────────────

    @Test
    @DisplayName("execute propagates GraphQLClientException when upstream returns error")
    void execute_graphqlUpstreamError_throwsGraphQLClientException() throws Exception {
        service.bindOperation(mockRegistration("articles", new String[]{"articles"}, false));
        when(graphQLClient.executePersistedQuery(eq("articles")))
                .thenThrow(new GraphQLClientException(
                        GraphQLClientException.ErrorCode.UPSTREAM_ERROR,
                        "HTTP 503 from GraphQL endpoint"));

        GraphQLClientException ex = assertThrows(GraphQLClientException.class,
                () -> service.execute("articles"));
        assertEquals(GraphQLClientException.ErrorCode.UPSTREAM_ERROR, ex.getErrorCode());
    }

    // ── Persisted query not found (404) ────────────────────────────────────────

    @Test
    @DisplayName("execute propagates QUERY_NOT_FOUND error code when persisted query is missing")
    void execute_queryNotFound_throwsQueryNotFoundException() throws Exception {
        service.bindOperation(mockRegistration("articles", new String[]{"articles"}, false));
        when(graphQLClient.executePersistedQuery(eq("articles")))
                .thenThrow(new GraphQLClientException(
                        GraphQLClientException.ErrorCode.QUERY_NOT_FOUND,
                        "Persisted query not found: articles (HTTP 404)"));

        GraphQLClientException ex = assertThrows(GraphQLClientException.class,
                () -> service.execute("articles"));
        assertEquals(GraphQLClientException.ErrorCode.QUERY_NOT_FOUND, ex.getErrorCode());
    }

    // ── Connection / timeout error ─────────────────────────────────────────────

    @Test
    @DisplayName("execute propagates CONNECTION_ERROR when network call times out")
    void execute_timeout_throwsConnectionError() throws Exception {
        service.bindOperation(mockRegistration("articles", new String[]{"articles"}, false));
        when(graphQLClient.executePersistedQuery(eq("articles")))
                .thenThrow(new GraphQLClientException(
                        GraphQLClientException.ErrorCode.CONNECTION_ERROR,
                        "Connection timed out"));

        GraphQLClientException ex = assertThrows(GraphQLClientException.class,
                () -> service.execute("articles"));
        assertEquals(GraphQLClientException.ErrorCode.CONNECTION_ERROR, ex.getErrorCode());
    }

    // ── GraphQL error in response body ────────────────────────────────────────

    @Test
    @DisplayName("execute returns raw JSON even when GraphQL response contains errors array")
    void execute_graphqlErrorsInResponse_returnsRawJson() throws Exception {
        String errorResponse = "{\"data\":null,\"errors\":[{\"message\":\"Not found\"}]}";
        service.bindOperation(mockRegistration("articles", new String[]{"articles"}, false));
        when(graphQLClient.executePersistedQuery(eq("articles")))
                .thenReturn(errorResponse);

        String result = service.execute("articles");

        // Must return the original GraphQL error response — not throw or transform
        assertEquals(errorResponse, result,
                "GraphQL error responses must be returned as-is to the caller.");
    }

    // ── Newly added operation via config (zero-Java-changes test) ─────────────

    @Test
    @DisplayName("newly registered 'categories' operation is immediately usable without Java changes")
    void execute_newCategoriesOperation_worksAfterBindingConfig() throws Exception {
        String categoriesResponse = "{\"data\":{\"categoryModelList\":{\"items\":[]}}}";
        // Simulate adding a new .cfg.json — no Java code change
        service.bindOperation(mockRegistration("categories", new String[]{"categories"}, false));
        when(graphQLClient.executePersistedQuery(eq("categories")))
                .thenReturn(categoriesResponse);

        assertTrue(service.isOperationSupported("categories"),
                "'categories' must be supported after config registration.");
        String result = service.execute("categories");
        assertEquals(categoriesResponse, result);
    }

    // ── Persisted query resolution ─────────────────────────────────────────────

    @Test
    @DisplayName("execute resolves correct persisted query name from business selector")
    void execute_resolvesCorrectPersistedQueryName() throws Exception {
        // 'article-details' maps to 'articleById' — NOT to 'article-details'
        service.bindOperation(mockRegistration(
                "article-details", new String[]{"articleById"}, false));
        when(graphQLClient.executePersistedQuery(eq("articleById")))
                .thenReturn(ARTICLE_BY_ID_RESPONSE);

        service.execute("article-details");

        // Verify that 'articleById' (the internal name) was used — not 'article-details'
        verify(graphQLClient, times(1)).executePersistedQuery(eq("articleById"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Creates a lenient mock for {@link BffOperationRegistration}.
     *
     * <p>All three stubs are declared as lenient so that tests which only exercise
     * {@code getBusinessSelector()} (e.g. the {@code isOperationSupported} tests) do
     * not fail with {@code UnnecessaryStubbingException}.
     */
    private BffOperationRegistration mockRegistration(String selector,
                                                       String[] queryNames,
                                                       boolean parallel) {
        BffOperationRegistration reg = mock(BffOperationRegistration.class);
        lenient().when(reg.getBusinessSelector()).thenReturn(selector);
        lenient().when(reg.getPersistedQueryNames()).thenReturn(queryNames);
        lenient().when(reg.isParallelExecution()).thenReturn(parallel);
        return reg;
    }

    /** Injects a field value via reflection (avoids adding test-only setters). */
    private void injectField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject field: " + fieldName, e);
        }
    }

    private java.lang.reflect.Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new RuntimeException("Field not found: " + fieldName);
    }
}
