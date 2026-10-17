package com.aem.demo.core.graphql;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * OSGi configuration for {@link GraphQLClient}.
 *
 * <h3>URL construction</h3>
 * <p>The full persisted-query execution path is assembled at runtime by combining
 * these three properties with the selector that arrives from the BFF service layer:
 * <pre>
 *   {graphqlBaseUrl}/{graphqlEndpoint}/{selector};path={damRootPath}{selector}
 *
 *   Example:
 *   /graphql/execute/AEM-Demo-SItes/articles;path=/content/dam/demo/articles
 * </pre>
 *
 * <h3>Properties</h3>
 * <ul>
 *   <li>{@code graphqlBaseUrl}  — relative base path for the AEM GraphQL execution servlet.</li>
 *   <li>{@code graphqlEndpoint} — the AEM Sites endpoint / configuration name.</li>
 *   <li>{@code damRootPath}     — root DAM path; a trailing slash is enforced at runtime.</li>
 * </ul>
 */
@ObjectClassDefinition(
        name = "AEM Demo — GraphQL Client Configuration",
        description = "Configuration for the AEM GraphQL persisted-query execution. "
                + "The three properties are combined with the business selector to produce "
                + "the full Sling-internal request path."
)
public @interface GraphQLClientConfig {

    @AttributeDefinition(
            name = "GraphQL Execution Base Path",
            description = "Relative base path of the AEM GraphQL execution servlet, "
                    + "without a trailing slash. "
                    + "Example: /graphql/execute"
    )
    String graphqlBaseUrl() default "/graphql/execute";

    @AttributeDefinition(
            name = "GraphQL Endpoint Name",
            description = "The AEM Sites GraphQL endpoint / configuration name. "
                    + "This value is placed between the base URL and the selector. "
                    + "Example: AEM-Demo-SItes"
    )
    String graphqlEndpoint() default "AEM-Demo-SItes";

    @AttributeDefinition(
            name = "DAM Root Path",
            description = "Root DAM path used as the 'path' query variable in persisted queries. "
                    + "A trailing slash is enforced at runtime before appending the selector. "
                    + "Example: /content/dam/demo/"
    )
    String damRootPath() default "/content/dam/demo/";
}
