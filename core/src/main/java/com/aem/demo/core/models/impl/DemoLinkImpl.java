/*
 * ***********************************************************************
 * Debal Das CONFIDENTIAL
 * ___________________
 *
 * Copyright 2024 Debal Das.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property
 * of Debal Das and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to Debal Das
 * and its suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Debal Das.
 * ***********************************************************************
 */

package com.aem.demo.core.models.impl;

import com.adobe.acs.commons.models.injectors.annotation.ChildResourceFromRequest;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Image;
import com.aem.demo.core.models.DemoLink;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = {
    SlingHttpServletRequest.class
}, adapters = {
    DemoLink.class,
    ComponentExporter.class
})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class DemoLinkImpl
    implements DemoLink
{

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String path;
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String label;
    @ChildResourceFromRequest(injectionStrategy = InjectionStrategy.OPTIONAL)
    private Image linkIcon;
    @SlingObject
    private Resource resource;

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Image getLinkIcon() {
        return linkIcon;
    }

    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

}
