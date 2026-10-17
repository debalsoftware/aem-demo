package com.aem.demo.core.configurations;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * OSGi factory configuration for a single BFF business operation.
 *
 * <p>Each instance of this configuration maps one <em>business selector</em> (the name
 * the frontend knows) to one or more <em>persisted query names</em> (the internal AEM
 * GraphQL persisted-query identifiers).
 *
 * <h3>Why a factory configuration?</h3>
 * <p>Using {@code configurationPolicy = ConfigurationPolicy.REQUIRE} on the consumer
 * service together with {@code @Designate(ocd = BffOperationConfig.class, factory = true)}
 * means every new business operation can be added purely by dropping a new
 * {@code .cfg.json} file under
 * {@code ui.config/…/osgiconfig/config/com.aem.demo.core.services.impl.DemoBffServiceImpl-<name>.cfg.json}
 * — no Java changes are needed.
 *
 * <h3>Example — articles</h3>
 * <pre>
 * businessSelector = "articles"
 * persistedQueryNames = ["articles"]
 * parallelExecution = false
 * </pre>
 *
 * <h3>Example — article-details (multi-query)</h3>
 * <pre>
 * businessSelector = "article-details"
 * persistedQueryNames = ["articleById", "authorsByArticle"]
 * parallelExecution = true
 * </pre>
 *
 * <h3>Adding a new operation without Java changes</h3>
 * <p>Create:
 * {@code com.aem.demo.core.configurations.BffOperationConfig-categories.cfg.json}
 * with content:
 * <pre>
 * {
 *   "businessSelector": "categories",
 *   "persistedQueryNames": ["categories"]
 * }
 * </pre>
 * Then {@code GET /bin/demo/bff.categories.json} is immediately available.
 */
@ObjectClassDefinition(
        name = "AEM Demo — BFF Operation Configuration",
        description = "Maps a frontend-visible business selector to one or more AEM GraphQL "
                + "persisted-query names. Create one factory instance per business operation."
)
public @interface BffOperationConfig {

    @AttributeDefinition(
            name = "Business Selector",
            description = "The selector the frontend uses. E.g. 'articles', 'authors', 'article-details'. "
                    + "Must match the Sling selector in /bin/demo/bff.<selector>.json."
    )
    String businessSelector() default "";

    @AttributeDefinition(
            name = "Persisted Query Names",
            description = "One or more AEM GraphQL persisted-query names (without the "
                    + "'/graphql/execute.json/AEM-Demo-SItes/' prefix). "
                    + "E.g. ['articles'] or ['articleById', 'authorsByArticle']."
    )
    String[] persistedQueryNames() default {};

    @AttributeDefinition(
            name = "Parallel Execution",
            description = "When true and multiple persisted queries are configured, execute them "
                    + "in parallel using a thread pool. Set to false if queries have data dependencies."
    )
    boolean parallelExecution() default false;
}
