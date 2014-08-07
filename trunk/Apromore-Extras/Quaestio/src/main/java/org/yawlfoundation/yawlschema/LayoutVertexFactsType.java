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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for LayoutVertexFactsType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="LayoutVertexFactsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="startpoint" type="{http://www.yawlfoundation.org/yawlschema}LayoutPointType" minOccurs="0"/>
 *         &lt;element name="iconpath" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="attributes" type="{http://www.yawlfoundation.org/yawlschema}LayoutAttributesFactsType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LayoutVertexFactsType", propOrder = { "startpoint",
		"iconpath", "attributes" })
public class LayoutVertexFactsType {

	protected LayoutPointType startpoint;
	@XmlSchemaType(name = "anyURI")
	protected String iconpath;
	@XmlElement(required = true)
	protected LayoutAttributesFactsType attributes;
	@XmlAttribute
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "NCName")
	protected String id;

	/**
	 * Gets the value of the startpoint property.
	 * 
	 * @return possible object is {@link LayoutPointType }
	 * 
	 */
	public LayoutPointType getStartpoint() {
		return startpoint;
	}

	/**
	 * Sets the value of the startpoint property.
	 * 
	 * @param value
	 *            allowed object is {@link LayoutPointType }
	 * 
	 */
	public void setStartpoint(LayoutPointType value) {
		this.startpoint = value;
	}

	/**
	 * Gets the value of the iconpath property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIconpath() {
		return iconpath;
	}

	/**
	 * Sets the value of the iconpath property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIconpath(String value) {
		this.iconpath = value;
	}

	/**
	 * Gets the value of the attributes property.
	 * 
	 * @return possible object is {@link LayoutAttributesFactsType }
	 * 
	 */
	public LayoutAttributesFactsType getAttributes() {
		return attributes;
	}

	/**
	 * Sets the value of the attributes property.
	 * 
	 * @param value
	 *            allowed object is {@link LayoutAttributesFactsType }
	 * 
	 */
	public void setAttributes(LayoutAttributesFactsType value) {
		this.attributes = value;
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
