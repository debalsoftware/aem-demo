package com.aem.demo.core.servlets;

import com.aem.demo.core.graphql.GraphQLClientException;
import com.aem.demo.core.services.DemoBffService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Sling servlet that exposes the BFF (Backend-for-Frontend) endpoint.
 *
 * <h3>Endpoint pattern</h3>
 * <pre>
 * GET /bin/demo/bff.&lt;businessSelector&gt;.json
 * </pre>
 * Examples:
 * <ul>
 *   <li>{@code GET /bin/demo/bff.articles.json}</li>
 *   <li>{@code GET /bin/demo/bff.authors.json}</li>
 *   <li>{@code GET /bin/demo/bff.article-details.json}</li>
 * </ul>
 *
 * <h3>Selector design rationale</h3>
 * <p>The servlet is registered on path {@code /bin/demo/bff} with extension {@code json}.
 * The <em>first</em> Sling selector is treated as the business operation name.
 * This means:
 * <ul>
 *   <li>The frontend is decoupled from AEM GraphQL internals — it only knows
 *       {@code articles}, {@code authors}, {@code article-details}.</li>
 *   <li>Adding a new operation requires no servlet change — just a new
 *       OSGi factory configuration.</li>
 *   <li>The URL is clean and Dispatcher-cacheable by selector.</li>
 * </ul>
 *
 * <h3>Servlet responsibilities (intentionally thin)</h3>
 * <ul>
 *   <li>Extract the business selector from the Sling request.</li>
 *   <li>Perform basic validation (selector present, non-blank).</li>
 *   <li>Delegate to {@link DemoBffService}.</li>
 *   <li>Write the JSON response and set the correct HTTP status code.</li>
 * </ul>
 *
 * <h3>What this servlet does NOT do</h3>
 * <ul>
 *   <li>Call {@code GraphQLClient} directly.</li>
 *   <li>Know individual persisted-query names or URLs.</li>
 *   <li>Contain GraphQL orchestration logic.</li>
 *   <li>Perform response transformation.</li>
 * </ul>
 *
 * <h3>Security</h3>
 * <p>The servlet is registered under {@code /bin/} which should be protected by
 * Dispatcher rules to prevent unauthenticated access in production.  The
 * {@link DemoBffService} enforces an explicit allow-list so only registered
 * operations are reachable — internal GraphQL persisted queries are never
 * automatically exposed.
 */
@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/demo/bff",
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=json"
        }
)
public class DemoBffServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(DemoBffServlet.class);

    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    @Reference
    private DemoBffService demoBffService;

    // -------------------------------------------------------------------------
    // GET handler
    // -------------------------------------------------------------------------

    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) throws IOException {

        String requestId = extractRequestId(request);
        String businessSelector = extractBusinessSelector(request);

        LOG.info("[{}] BFF request received — selector='{}'", requestId, businessSelector);

        response.setContentType(CONTENT_TYPE_JSON);
        response.setCharacterEncoding("UTF-8");

        // ── 1. Validate selector presence ─────────────────────────────────────
        if (businessSelector == null || businessSelector.trim().isEmpty()) {
            LOG.warn("[{}] Missing business selector.", requestId);
            writeError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "MISSING_SELECTOR", "A business operation selector is required. "
                            + "Use /bin/demo/bff.<operation>.json");
            return;
        }

        // ── 2. Check allow-list (fast pre-check — avoids upstream call) ────────
        if (!demoBffService.isOperationSupported(businessSelector)) {
            LOG.warn("[{}] Unknown business selector '{}' — rejected by allow-list.",
                    requestId, businessSelector);
            writeError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "INVALID_OPERATION", "Unsupported business operation: " + businessSelector);
            return;
        }

        // ── 3. Execute via BFF service ─────────────────────────────────────────
        try {
            String graphqlResponse = demoBffService.execute(businessSelector);
            LOG.info("[{}] BFF operation '{}' completed successfully.", requestId, businessSelector);
            response.setStatus(HttpServletResponse.SC_OK);
            writeBody(response, graphqlResponse);

        } catch (IllegalArgumentException e) {
            // Should not normally reach here after the isOperationSupported check,
            // but handled defensively.
            LOG.warn("[{}] Invalid operation '{}': {}", requestId, businessSelector, e.getMessage());
            writeError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "INVALID_OPERATION", "Unsupported business operation.");

        } catch (GraphQLClientException e) {
            handleGraphQLClientException(response, requestId, businessSelector, e);

        } catch (Exception e) {
            LOG.error("[{}] Unexpected internal error for selector '{}': {}",
                    requestId, businessSelector, e.getMessage(), e);
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "INTERNAL_ERROR", "An internal error occurred.");
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Extracts the first Sling selector from the request as the business operation name.
     *
     * <p>For {@code /bin/demo/bff.articles.json} the selectors string is {@code "articles"},
     * so {@code getSelectors()[0]} returns {@code "articles"}.
     *
     * <p>For {@code /bin/demo/bff.article-details.json} it returns {@code "article-details"}.
     */
    private String extractBusinessSelector(SlingHttpServletRequest request) {
        String[] selectors = request.getRequestPathInfo().getSelectors();
        if (selectors != null && selectors.length > 0) {
            return selectors[0].trim();
        }
        return null;
    }

    /**
     * Attempts to extract a correlation / request ID for log tracing.
     * Falls back to the request hash if no header is present.
     */
    private String extractRequestId(SlingHttpServletRequest request) {
        String correlationId = request.getHeader("X-Correlation-ID");
        if (correlationId != null && !correlationId.trim().isEmpty()) {
            return correlationId.trim();
        }
        return "req-" + Integer.toHexString(request.hashCode());
    }

    /**
     * Maps a {@link GraphQLClientException} error code to an appropriate HTTP status
     * and writes a safe JSON error response.
     */
    private void handleGraphQLClientException(SlingHttpServletResponse response,
                                               String requestId,
                                               String businessSelector,
                                               GraphQLClientException e) throws IOException {
        GraphQLClientException.ErrorCode errorCode = e.getErrorCode();
        LOG.error("[{}] GraphQL execution failure for operation '{}': code={}, message={}",
                requestId, businessSelector, errorCode, e.getMessage());

        switch (errorCode) {
            case QUERY_NOT_FOUND:
                writeError(response, HttpServletResponse.SC_NOT_FOUND,
                        "QUERY_NOT_FOUND", "The requested operation could not be resolved.");
                break;
            case UPSTREAM_ERROR:
            case CONNECTION_ERROR:
                writeError(response, HttpServletResponse.SC_BAD_GATEWAY,
                        "UPSTREAM_ERROR", "The upstream GraphQL service is unavailable.");
                break;
            case MALFORMED_RESPONSE:
            default:
                writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "INTERNAL_ERROR", "An internal error occurred.");
                break;
        }
    }

    /**
     * Writes the response body to the HTTP response writer.
     */
    private void writeBody(SlingHttpServletResponse response, String body) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.write(body);
        writer.flush();
    }

    /**
     * Writes a structured JSON error payload to the HTTP response.
     *
     * <p>Error format:
     * <pre>
     * {
     *   "error":   "ERROR_CODE",
     *   "message": "Human-readable description."
     * }
     * </pre>
     *
     * <p>Stack traces, internal paths, credentials and sensitive data are never
     * included in the error response.
     */
    private void writeError(SlingHttpServletResponse response,
                             int statusCode,
                             String errorCode,
                             String message) throws IOException {
        response.setStatus(statusCode);
        String errorJson = "{\"error\":\"" + escapeJsonString(errorCode) + "\","
                + "\"message\":\"" + escapeJsonString(message) + "\"}";
        writeBody(response, errorJson);
    }

    /** Minimal JSON string escaping for error payloads. */
    private String escapeJsonString(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
