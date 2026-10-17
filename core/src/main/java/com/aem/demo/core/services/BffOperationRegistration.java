package com.aem.demo.core.services;

/**
 * OSGi service interface that represents a single registered BFF business operation.
 *
 * <p>Each factory instance of {@link com.aem.demo.core.services.impl.BffOperationRegistrationImpl}
 * publishes one service of this type. The {@link DemoBffService} implementation
 * collects all such services dynamically via {@code @Reference(cardinality=MULTIPLE)}.
 *
 * <p>This is the correct OSGi DS whiteboard pattern for collecting factory
 * configurations without hard-coding operation names in Java.
 */
public interface BffOperationRegistration {

    /**
     * The frontend-visible business selector (e.g. {@code "articles"}).
     *
     * @return business selector; never {@code null}
     */
    String getBusinessSelector();

    /**
     * The AEM GraphQL persisted-query names mapped to this selector
     * (e.g. {@code ["articles"]} or {@code ["articleById", "authorsByArticle"]}).
     *
     * @return array of persisted-query names; never {@code null}, length &ge; 1
     */
    String[] getPersistedQueryNames();

    /**
     * Whether the persisted queries for this operation should be executed in parallel.
     *
     * @return {@code true} for parallel execution; {@code false} for sequential
     */
    boolean isParallelExecution();
}
