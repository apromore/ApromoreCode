/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.11.03 at 05:04:23 PM CET 
//

package org.yawlfoundation.yawlschema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

/**
 * <p>
 * Java class for WebServiceGatewayFactsType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="WebServiceGatewayFactsType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.yawlfoundation.org/yawlschema}DecompositionFactsType">
 *       &lt;sequence>
 *         &lt;element name="enablementParam" type="{http://www.yawlfoundation.org/yawlschema}InputParameterFactsType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="yawlService" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="documentation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;choice>
 *                     &lt;sequence>
 *                       &lt;element name="wsdlLocation" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                       &lt;element name="operationName" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN"/>
 *                     &lt;/sequence>
 *                     &lt;sequence>
 *                     &lt;/sequence>
 *                   &lt;/choice>
 *                 &lt;/sequence>
 *                 &lt;attribute name="id" use="required" type="{http://www.yawlfoundation.org/yawlschema}YAWLServiceIDType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="codelet" type="{http://www.w3.org/2001/XMLSchema}NCName" minOccurs="0"/>
 *         &lt;element name="externalInteraction" type="{http://www.yawlfoundation.org/yawlschema}ResourcingExternalInteractionType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WebServiceGatewayFactsType", propOrder = { "enablementParam",
		"yawlService", "codelet", "externalInteraction" })
public class WebServiceGatewayFactsType extends DecompositionFactsType {

	protected List<InputParameterFactsType> enablementParam;
	protected WebServiceGatewayFactsType.YawlService yawlService;
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "NCName")
	protected String codelet;
	protected ResourcingExternalInteractionType externalInteraction;
	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<QName, String>();

	/**
	 * Gets the value of the enablementParam property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the enablementParam property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getEnablementParam().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link InputParameterFactsType }
	 * 
	 * 
	 */
	public List<InputParameterFactsType> getEnablementParam() {
		if (enablementParam == null) {
			enablementParam = new ArrayList<InputParameterFactsType>();
		}
		return this.enablementParam;
	}

	/**
	 * Gets the value of the yawlService property.
	 * 
	 * @return possible object is {@link WebServiceGatewayFactsType.YawlService }
	 * 
	 */
	public WebServiceGatewayFactsType.YawlService getYawlService() {
		return yawlService;
	}

	/**
	 * Sets the value of the yawlService property.
	 * 
	 * @param value
	 *            allowed object is
	 *            {@link WebServiceGatewayFactsType.YawlService }
	 * 
	 */
	public void setYawlService(WebServiceGatewayFactsType.YawlService value) {
		this.yawlService = value;
	}

	/**
	 * Gets the value of the codelet property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodelet() {
		return codelet;
	}

	/**
	 * Sets the value of the codelet property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodelet(String value) {
		this.codelet = value;
	}

	/**
	 * Gets the value of the externalInteraction property.
	 * 
	 * @return possible object is {@link ResourcingExternalInteractionType }
	 * 
	 */
	public ResourcingExternalInteractionType getExternalInteraction() {
		return externalInteraction;
	}

	/**
	 * Sets the value of the externalInteraction property.
	 * 
	 * @param value
	 *            allowed object is {@link ResourcingExternalInteractionType }
	 * 
	 */
	public void setExternalInteraction(ResourcingExternalInteractionType value) {
		this.externalInteraction = value;
	}

	/**
	 * Gets a map that contains attributes that aren't bound to any typed
	 * property on this class.
	 * 
	 * <p>
	 * the map is keyed by the name of the attribute and the value is the string
	 * value of the attribute.
	 * 
	 * the map returned by this method is live, and you can add new attribute by
	 * updating the map directly. Because of this design, there's no setter.
	 * 
	 * 
	 * @return always non-null
	 */
	public Map<QName, String> getOtherAttributes() {
		return otherAttributes;
	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="documentation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;choice>
	 *           &lt;sequence>
	 *             &lt;element name="wsdlLocation" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
	 *             &lt;element name="operationName" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN"/>
	 *           &lt;/sequence>
	 *           &lt;sequence>
	 *           &lt;/sequence>
	 *         &lt;/choice>
	 *       &lt;/sequence>
	 *       &lt;attribute name="id" use="required" type="{http://www.yawlfoundation.org/yawlschema}YAWLServiceIDType" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "documentation", "wsdlLocation",
			"operationName" })
	public static class YawlService {

		protected String documentation;
		@XmlSchemaType(name = "anyURI")
		protected String wsdlLocation;
		@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
		@XmlSchemaType(name = "NMTOKEN")
		protected String operationName;
		@XmlAttribute(required = true)
		protected String id;

		/**
		 * Gets the value of the documentation property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDocumentation() {
			return documentation;
		}

		/**
		 * Sets the value of the documentation property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDocumentation(String value) {
			this.documentation = value;
		}

		/**
		 * Gets the value of the wsdlLocation property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getWsdlLocation() {
			return wsdlLocation;
		}

		/**
		 * Sets the value of the wsdlLocation property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setWsdlLocation(String value) {
			this.wsdlLocation = value;
		}

		/**
		 * Gets the value of the operationName property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getOperationName() {
			return operationName;
		}

		/**
		 * Sets the value of the operationName property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setOperationName(String value) {
			this.operationName = value;
		}

		/**
		 * Gets the value of the id property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getId() {
			return id;
		}

		/**
		 * Sets the value of the id property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setId(String value) {
			this.id = value;
		}

	}

}
