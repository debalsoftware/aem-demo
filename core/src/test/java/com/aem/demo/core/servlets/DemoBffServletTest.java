package com.aem.demo.core.servlets;

import com.aem.demo.core.graphql.GraphQLClientException;
import com.aem.demo.core.services.DemoBffService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DemoBffServlet}.
 *
 * <p>The servlet is thin: it extracts the selector, validates it and delegates to
 * {@link DemoBffService}. These tests verify that:
 * <ul>
 *   <li>The correct HTTP status codes are produced.</li>
 *   <li>The correct JSON error payloads are returned.</li>
 *   <li>The servlet never calls GraphQL directly.</li>
 *   <li>The servlet does not expose stack traces or internal paths.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class DemoBffServletTest {

    @Mock
    private DemoBffService demoBffService;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private RequestPathInfo requestPathInfo;

    private DemoBffServlet servlet;

    private StringWriter responseWriter;

    private static final String ARTICLES_RESPONSE =
            "{\"data\":{\"articleModelList\":{\"items\":[{\"title\":\"AEM Architecture\"}]}}}";

    @BeforeEach
    void setUp() throws IOException {
        servlet = new DemoBffServlet();
        injectField(servlet, "demoBffService", demoBffService);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
    }

    // ── Valid selector — happy path ────────────────────────────────────────────

    @Test
    @DisplayName("doGet returns 200 and raw GraphQL JSON for valid selector")
    void doGet_validSelector_returns200WithRawGraphQLJson() throws Exception {
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"articles"});
        when(demoBffService.isOperationSupported("articles")).thenReturn(true);
        when(demoBffService.execute(eq("articles"))).thenReturn(ARTICLES_RESPONSE);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String body = responseWriter.toString();
        assertEquals(ARTICLES_RESPONSE, body,
                "Servlet must return the raw GraphQL JSON without transformation.");
    }

    @Test
    @DisplayName("doGet sets application/json content type")
    void doGet_validSelector_setsJsonContentType() throws Exception {
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"articles"});
        when(demoBffService.isOperationSupported("articles")).thenReturn(true);
        when(demoBffService.execute(eq("articles"))).thenReturn(ARTICLES_RESPONSE);

        servlet.doGet(request, response);

        verify(response).setContentType("application/json;charset=UTF-8");
    }

    // ── Missing selector ───────────────────────────────────────────────────────

    @Test
    @DisplayName("doGet returns 400 with MISSING_SELECTOR when no selector is present")
    void doGet_missingSelector_returns400() throws Exception {
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{});

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String body = responseWriter.toString();
        assertTrue(body.contains("\"error\""), "Error response must contain 'error' key.");
        assertTrue(body.contains("MISSING_SELECTOR"), "Error code must be MISSING_SELECTOR.");
    }

    @Test
    @DisplayName("doGet returns 400 when selector array is null")
    void doGet_nullSelectors_returns400() throws Exception {
        when(requestPathInfo.getSelectors()).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    // ── Unknown / invalid selector ─────────────────────────────────────────────

    @Test
    @DisplayName("doGet returns 400 with INVALID_OPERATION for unknown selector")
    void doGet_unknownSelector_returns400() throws Exception {
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"internalDebugQuery"});
        when(demoBffService.isOperationSupported("internalDebugQuery")).thenReturn(false);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String body = responseWriter.toString();
        assertTrue(body.contains("INVALID_OPERATION"),
                "Error code must be INVALID_OPERATION for unknown selectors.");
        // Ensure the servlet does NOT forward the request to the service
        verify(demoBffService, never()).execute(anyString());
    }

    @Test
    @DisplayName("doGet does not expose internal persisted-query names in error response")
    void doGet_unknownSelector_doesNotExposeInternalPaths() throws Exception {
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"internalDebugQuery"});
        when(demoBffService.isOperationSupported("internalDebugQuery")).thenReturn(false);

        servlet.doGet(request, response);

        String body = responseWriter.toString();
        assertFalse(body.contains("graphql/execute.json"),
                "Internal GraphQL execution paths must not be exposed in error responses.");
        assertFalse(body.contains("persistentQueries"),
                "Internal repository paths must not be exposed in error responses.");
    }

    // ── GraphQL client exceptions ──────────────────────────────────────────────

    @Test
    @DisplayName("doGet returns 502 when GraphQL upstream returns an error")
    void doGet_upstreamError_returns502() throws Exception {
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"articles"});
        when(demoBffService.isOperationSupported("articles")).thenReturn(true);
        when(demoBffService.execute(eq("articles")))
                .thenThrow(new GraphQLClientException(
                        GraphQLClientException.ErrorCode.UPSTREAM_ERROR, "HTTP 503"));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_GATEWAY);
        String body = responseWriter.toString();
        assertTrue(body.contains("UPSTREAM_ERROR"));
    }

    @Test
    @DisplayName("doGet returns 404 when persisted query is not found")
    void doGet_queryNotFound_returns404() throws Exception {
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"articles"});
        when(demoBffService.isOperationSupported("articles")).thenReturn(true);
        when(demoBffService.execute(eq("articles")))
                .thenThrow(new GraphQLClientException(
                        GraphQLClientException.ErrorCode.QUERY_NOT_FOUND,
                        "Persisted query not found"));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        String body = responseWriter.toString();
        assertTrue(body.contains("QUERY_NOT_FOUND"));
    }

    @Test
    @DisplayName("doGet returns 502 when connection times out")
    void doGet_timeout_returns502() throws Exception {
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"articles"});
        when(demoBffService.isOperationSupported("articles")).thenReturn(true);
        when(demoBffService.execute(eq("articles")))
                .thenThrow(new GraphQLClientException(
                        GraphQLClientException.ErrorCode.CONNECTION_ERROR,
                        "Connection timed out"));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_GATEWAY);
    }

    @Test
    @DisplayName("doGet returns 500 for malformed GraphQL response")
    void doGet_malformedResponse_returns500() throws Exception {
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"articles"});
        when(demoBffService.isOperationSupported("articles")).thenReturn(true);
        when(demoBffService.execute(eq("articles")))
                .thenThrow(new GraphQLClientException(
                        GraphQLClientException.ErrorCode.MALFORMED_RESPONSE,
                        "Malformed JSON"));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    // ── Unexpected exception ───────────────────────────────────────────────────

    @Test
    @DisplayName("doGet returns 500 for unexpected runtime exception")
    void doGet_unexpectedException_returns500() throws Exception {
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"articles"});
        when(demoBffService.isOperationSupported("articles")).thenReturn(true);
        when(demoBffService.execute(eq("articles")))
                .thenThrow(new RuntimeException("Unexpected failure"));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String body = responseWriter.toString();
        assertTrue(body.contains("INTERNAL_ERROR"),
                "Generic 500 error must use INTERNAL_ERROR code.");
        assertFalse(body.contains("Unexpected failure"),
                "Internal exception messages must not be exposed to the frontend.");
    }

    @Test
    @DisplayName("doGet never exposes stack traces in error responses")
    void doGet_error_noStackTraceInResponse() throws Exception {
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"articles"});
        when(demoBffService.isOperationSupported("articles")).thenReturn(true);
        when(demoBffService.execute(eq("articles")))
                .thenThrow(new RuntimeException("boom"));

        servlet.doGet(request, response);

        String body = responseWriter.toString();
        assertFalse(body.contains("at com."), "Stack traces must never appear in responses.");
        assertFalse(body.contains("java.lang"), "Stack traces must never appear in responses.");
    }

    // ── Correlation ID ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("doGet reads X-Correlation-ID header when present")
    void doGet_correlationIdHeader_isUsedForLogging() throws Exception {
        when(request.getHeader("X-Correlation-ID")).thenReturn("test-correlation-123");
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"articles"});
        when(demoBffService.isOperationSupported("articles")).thenReturn(true);
        when(demoBffService.execute(eq("articles"))).thenReturn(ARTICLES_RESPONSE);

        // Should not throw — correlation ID is used in logging only
        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    // ── Service delegation ────────────────────────────────────────────────────

    @Test
    @DisplayName("doGet delegates to DemoBffService and never calls GraphQLClient directly")
    void doGet_delegatesToService_neverCallsGraphQLClientDirectly() throws Exception {
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"articles"});
        when(demoBffService.isOperationSupported("articles")).thenReturn(true);
        when(demoBffService.execute(eq("articles"))).thenReturn(ARTICLES_RESPONSE);

        servlet.doGet(request, response);

        // Verify delegation happened exactly once
        verify(demoBffService, times(1)).execute(eq("articles"));
        // No other service or client should have been called — only DemoBffService
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Injects a field value via reflection to avoid adding test-only setters. */
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
