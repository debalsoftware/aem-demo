package com.aem.demo.core.services.impl;

import java.util.Arrays;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.demo.core.configurations.BffOperationConfig;
import com.aem.demo.core.services.BffOperationRegistration;

/**
 * Factory OSGi component that publishes one {@link BffOperationRegistration} service
 * per configured business operation.
 *
 * <h3>How it works</h3>
 * <ol>
 *   <li>Each {@code .cfg.json} file with PID
 *       {@code com.aem.demo.core.services.impl.BffOperationRegistrationImpl-<suffix>}
 *       causes OSGi to instantiate a new component instance.</li>
 *   <li>That instance reads its {@link BffOperationConfig} and registers itself as a
 *       {@link BffOperationRegistration} service in the OSGi service registry.</li>
 *   <li>{@link DemoBffServiceImpl} collects all registered {@code BffOperationRegistration}
 *       services dynamically and builds its allow-list registry.</li>
 * </ol>
 *
 * <h3>Adding a new operation — zero Java changes</h3>
 * <p>Create a file:
 * <pre>
 * ui.config/…/osgiconfig/config/
 *     com.aem.demo.core.services.impl.BffOperationRegistrationImpl-categories.cfg.json
 * </pre>
 * with content:
 * <pre>
 * {
 *   "businessSelector": "categories",
 *   "persistedQueryNames": ["categories"],
 *   "parallelExecution": false
 * }
 * </pre>
 * After deployment {@code GET /bin/demo/bff.categories.json} works automatically.
 *
 * <h3>Security / allow-list</h3>
 * <p>Only operations with an explicit factory configuration file are reachable.
 * Internal persisted queries that have no corresponding {@code .cfg.json} are
 * never exposed.
 */
@Component(
        service = BffOperationRegistration.class,
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        immediate = true
)
@Designate(ocd = BffOperationConfig.class, factory = true)
public class BffOperationRegistrationImpl implements BffOperationRegistration {

    private static final Logger LOG = LoggerFactory.getLogger(BffOperationRegistrationImpl.class);

    private String businessSelector;
    private String[] persistedQueryNames;
    private boolean parallelExecution;

    @Activate
    @Modified
    protected void activate(BffOperationConfig config) {
        this.businessSelector   = config.businessSelector();
        this.persistedQueryNames = config.persistedQueryNames();
        this.parallelExecution  = config.parallelExecution();
        String persistedQueries = Arrays.toString(persistedQueryNames);

        LOG.info("BffOperationRegistration activated — selector='{}', queries={}, parallel={}",
                businessSelector,
                persistedQueries,
                parallelExecution);
    }

    @Override
    public String getBusinessSelector() {
        return businessSelector;
    }

    @Override
    public String[] getPersistedQueryNames() {
        return persistedQueryNames != null ? persistedQueryNames.clone() : new String[0];
    }

    @Override
    public boolean isParallelExecution() {
        return parallelExecution;
    }
}
