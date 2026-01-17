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

package com.aem.demo.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.Image;
import org.osgi.annotation.versioning.ConsumerType;


/**
 * Defines the {@code DemoLink} Sling Model used for the multifield in {@code demo/components/content/demo-comp-generator} component.
 * 
 */
@ConsumerType
public interface DemoLink
    extends ComponentExporter
{


    /**
     * Get the path.
     * 
     * @return String
     * 
     */
    String getPath();

    /**
     * Get the label.
     * 
     * @return String
     * 
     */
    String getLabel();

    /**
     * Get the linkIcon.
     * 
     * @return Image
     * 
     */
    Image getLinkIcon();

}
