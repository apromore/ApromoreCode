//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.03.23 at 09:25:45 AM EST 
//


package org.yawlfoundation.yawlschema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for CustomSchemaNamespaceMappingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomSchemaNamespaceMappingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="ncname" use="required" type="{http://www.yawlfoundation.org/yawlschema}NCNameType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomSchemaNamespaceMappingType")
@XmlSeeAlso({
    CustomSchemaNamespaceMappingFactsType.class
})
public class CustomSchemaNamespaceMappingType {

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String ncname;

    /**
     * Gets the value of the ncname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNcname() {
        return ncname;
    }

    /**
     * Sets the value of the ncname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNcname(String value) {
        this.ncname = value;
    }

}
