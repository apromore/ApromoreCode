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
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for typeXOR complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="typeXOR">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.epml.de}tEpcElement">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="configurableConnector" type="{http://www.epml.de}typeCXOR"/>
 *         &lt;/choice>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="attribute" type="{http://www.epml.de}typeAttribute"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeXOR", propOrder = { "configurableConnector", "attribute" })
public class TypeXOR extends TEpcElement {

	protected TypeCXOR configurableConnector;
	protected List<TypeAttribute> attribute;

	/**
	 * Gets the value of the configurableConnector property.
	 * 
	 * @return possible object is {@link TypeCXOR }
	 * 
	 */
	public TypeCXOR getConfigurableConnector() {
		return configurableConnector;
	}

	/**
	 * Sets the value of the configurableConnector property.
	 * 
	 * @param value
	 *            allowed object is {@link TypeCXOR }
	 * 
	 */
	public void setConfigurableConnector(TypeCXOR value) {
		this.configurableConnector = value;
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

}
