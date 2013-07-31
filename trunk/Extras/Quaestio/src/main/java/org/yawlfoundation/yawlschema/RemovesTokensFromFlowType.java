/*******************************************************************************
 * Copyright © 2006-2011, www.processconfiguration.com
 *   
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *      Marcello La Rosa - initial API and implementation, subsequent revisions
 *      Florian Gottschalk - individualizer for YAWL
 *      Possakorn Pitayarojanakul - integration with Configurator and Individualizer
 ******************************************************************************/
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.11.03 at 05:04:23 PM CET 
//

package org.yawlfoundation.yawlschema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for RemovesTokensFromFlowType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="RemovesTokensFromFlowType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="flowSource" type="{http://www.yawlfoundation.org/yawlschema}ExternalNetElementType"/>
 *         &lt;element name="flowDestination" type="{http://www.yawlfoundation.org/yawlschema}ExternalNetElementType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemovesTokensFromFlowType", propOrder = { "flowSource",
		"flowDestination" })
public class RemovesTokensFromFlowType {

	@XmlElement(required = true)
	protected ExternalNetElementType flowSource;
	@XmlElement(required = true)
	protected ExternalNetElementType flowDestination;

	/**
	 * Gets the value of the flowSource property.
	 * 
	 * @return possible object is {@link ExternalNetElementType }
	 * 
	 */
	public ExternalNetElementType getFlowSource() {
		return flowSource;
	}

	/**
	 * Sets the value of the flowSource property.
	 * 
	 * @param value
	 *            allowed object is {@link ExternalNetElementType }
	 * 
	 */
	public void setFlowSource(ExternalNetElementType value) {
		this.flowSource = value;
	}

	/**
	 * Gets the value of the flowDestination property.
	 * 
	 * @return possible object is {@link ExternalNetElementType }
	 * 
	 */
	public ExternalNetElementType getFlowDestination() {
		return flowDestination;
	}

	/**
	 * Sets the value of the flowDestination property.
	 * 
	 * @param value
	 *            allowed object is {@link ExternalNetElementType }
	 * 
	 */
	public void setFlowDestination(ExternalNetElementType value) {
		this.flowDestination = value;
	}

}
