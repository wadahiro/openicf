/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License").  You may not use this file
 * except in compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://IdentityConnectors.dev.java.net/legal/license.txt
 * See the License for the specific language governing permissions and limitations
 * under the License.
 *
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at identityconnectors/legal/license.txt.
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package com.forgerock.openconnector.xml;

import org.identityconnectors.common.Assertions;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;

/**
 * Extends the {@link AbstractConfiguration} class to provide all the necessary
 * parameters to initialize the XML Connector.
 *
 */
public class XMLConfiguration extends AbstractConfiguration {

    private String xmlFilePath = null;

    private String xsdFilePath = null;

    private String xsdRIFilePath = null;
    /**
     * Constructor
     */
    public XMLConfiguration() {

    }

    /**
     * Accessor for the xml filepath property. Uses ConfigurationProperty annotation
     * to provide property metadata to the application.
     */
    @ConfigurationProperty(displayMessageKey="XML_FILEPATH_PROPERTY_DISPLAY", helpMessageKey="XML_FILEPATH_PROPERTY_HELP", confidential=false)
    public String getXmlFilePath() {
        return xmlFilePath;
    }

    /**
     * @param xmlFilePath the xmlFilePath to set
     */
    public void setXmlFilePath(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

        /**
     * Accessor for the xml filepath property. Uses ConfigurationProperty annotation
     * to provide property metadata to the application.
     */
    @ConfigurationProperty(displayMessageKey="XSD_ICF_FILEPATH_PROPERTY_DISPLAY", helpMessageKey="XSD_ICF_FILEPATH_PROPERTY_HELP", confidential=false)
    public String getXsdFilePath() {
        return this.xsdFilePath;
    }

    /**
     * @param xmlFilePath the xmlFilePath to set
     */
    public void setXsdFilePath(String xsdFilePath) {
        this.xsdFilePath = xsdFilePath;
    }

    @ConfigurationProperty(displayMessageKey="XSD_RI_FILEPATH_PROPERTY_DISPLAY", helpMessageKey="XSD_RI_FILEPATH_PROPERTY_HELP", confidential=false)
    public String getXsdRIFilePath() {
        return this.xsdRIFilePath;
    }

    public void setXsdRIFilePath(String xsdFilePath) {
        this.xsdRIFilePath = xsdFilePath;
    }

    /**
     * {@inheritDoc}
     */
    public void validate() {
        Assertions.blankCheck(xmlFilePath, "xmlFilePath");
        Assertions.blankCheck(xsdFilePath, "xsdFilePath");
        Assertions.blankCheck(xsdRIFilePath, "xsdRIFilePath");
    }
}