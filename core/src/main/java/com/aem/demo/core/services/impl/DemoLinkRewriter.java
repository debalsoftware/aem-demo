/*
 * package com.aem.demo.core.services.impl;
 * 
 * import java.util.Map;
 * 
 * import org.apache.sling.rewriter.Transformer; import
 * org.apache.sling.rewriter.TransformerFactory; import
 * org.osgi.service.component.ComponentContext; import
 * org.osgi.service.component.annotations.Activate; import
 * org.osgi.service.component.annotations.Component; import
 * org.osgi.service.component.annotations.Deactivate; import
 * org.osgi.service.component.annotations.Reference; import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 * 
 * import com.aem.demo.core.services.JcrUtility;
 * 
 * @Component(property = { "pipeline.type=linkrewriter" }, service = {
 * TransformerFactory.class }) public class DemoLinkRewriter implements
 * TransformerFactory {
 * 
 * 
 * 
 * private static final Logger log =
 * LoggerFactory.getLogger(DemoLinkRewriter.class);
 * 
 * @Activate protected void activate(Map<String, Object> properties) {
 * log.info("Activating service"); }
 * 
 * @Deactivate protected void deactivate(ComponentContext context) {
 * log.info("Deactivating service"); }
 * 
 * public Transformer createTransformer() { return new TransformerDemo(); }
 * 
 * }
 */