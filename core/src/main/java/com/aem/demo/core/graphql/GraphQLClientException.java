package com.aem.demo.core.graphql;

/**
 * Exception thrown by {@link GraphQLClient} when a persisted-query execution fails.
 *
 * <p>The {@link ErrorCode} distinguishes between different failure categories so
 * that callers can map them to appropriate HTTP status codes without inspecting
 * exception messages.
 */
public class GraphQLClientException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Categorises the type of failure that occurred during GraphQL execution.
     */
    public enum ErrorCode {

        /**
         * The named persisted query was not found on the AEM GraphQL endpoint (HTTP 404).
         */
        QUERY_NOT_FOUND,

        /**
         * The AEM GraphQL endpoint returned a non-success HTTP status code.
         */
        UPSTREAM_ERROR,

        /**
         * A network-level connection or I/O error occurred.
         */
        CONNECTION_ERROR,

        /**
         * The GraphQL response body could not be parsed or was malformed.
         */
        MALFORMED_RESPONSE
    }

    private final ErrorCode errorCode;

    /**
     * Constructs a new exception with the given error code and detail message.
     *
     * @param errorCode categorised error type
     * @param message   human-readable detail (logged, not exposed to the frontend)
     */
    public GraphQLClientException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new exception with the given error code, detail message and cause.
     *
     * @param errorCode categorised error type
     * @param message   human-readable detail (logged, not exposed to the frontend)
     * @param cause     the underlying exception
     */
    public GraphQLClientException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the categorised error code that describes why the execution failed.
     *
     * @return error code; never {@code null}
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
