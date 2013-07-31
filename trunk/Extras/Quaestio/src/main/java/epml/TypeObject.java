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
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.04.15 at 11:16:00 AM EST 
//

package epml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for typeObject complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="typeObject">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.epml.de}tEpcElement">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="configurableObject" type="{http://www.epml.de}typeCObject"/>
 *         &lt;/choice>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="attribute" type="{http://www.epml.de}typeAttribute"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="input"/>
 *             &lt;enumeration value="output"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="optional" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="consumed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="initial" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="final" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeObject", propOrder = { "configurableObject", "attribute" })
public class TypeObject extends TEpcElement {

	protected TypeCObject configurableObject;
	protected List<TypeAttribute> attribute;
	@XmlAttribute(required = true)
	protected String type;
	@XmlAttribute
	protected Boolean optional;
	@XmlAttribute
	protected Boolean consumed;
	@XmlAttribute
	protected Boolean initial;
	@XmlAttribute(name = "final")
	protected Boolean _final;

	/**
	 * Gets the value of the configurableObject property.
	 * 
	 * @return possible object is {@link TypeCObject }
	 * 
	 */
	public TypeCObject getConfigurableObject() {
		return configurableObject;
	}

	/**
	 * Sets the value of the configurableObject property.
	 * 
	 * @param value
	 *            allowed object is {@link TypeCObject }
	 * 
	 */
	public void setConfigurableObject(TypeCObject value) {
		this.configurableObject = value;
	}

	/**
	 * Gets the value of the attribute property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the attribute property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getAttribute().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link TypeAttribute }
	 * 
	 * 
	 */
	public List<TypeAttribute> getAttribute() {
		if (attribute == null) {
			attribute = new ArrayList<TypeAttribute>();
		}
		return this.attribute;
	}

	/**
	 * Gets the value of the type property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the value of the type property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setType(String value) {
		this.type = value;
	}

	/**
	 * Gets the value of the optional property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isOptional() {
		if (optional == null) {
			return false;
		} else {
			return optional;
		}
	}

	/**
	 * Sets the value of the optional property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setOptional(Boolean value) {
		this.optional = value;
	}

	/**
	 * Gets the value of the consumed property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isConsumed() {
		if (consumed == null) {
			return false;
		} else {
			return consumed;
		}
	}

	/**
	 * Sets the value of the consumed property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setConsumed(Boolean value) {
		this.consumed = value;
	}

	/**
	 * Gets the value of the initial property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isInitial() {
		if (initial == null) {
			return false;
		} else {
			return initial;
		}
	}

	/**
	 * Sets the value of the initial property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setInitial(Boolean value) {
		this.initial = value;
	}

	/**
	 * Gets the value of the final property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isFinal() {
		if (_final == null) {
			return false;
		} else {
			return _final;
		}
	}

	/**
	 * Sets the value of the final property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setFinal(Boolean value) {
		this._final = value;
	}

}
