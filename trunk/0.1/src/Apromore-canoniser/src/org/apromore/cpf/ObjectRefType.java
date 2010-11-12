
package org.apromore.cpf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for objectRefType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="objectRefType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="attribute" type="{http://www.apromore.org/CPF}typeAttribute" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="objectId" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="type" type="{http://www.apromore.org/CPF}InputOutputType" />
 *       &lt;attribute name="optional" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="consumed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objectRefType", propOrder = {
    "attribute"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ObjectRefType {

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	protected List<TypeAttribute> attribute;
    @XmlAttribute(name = "id")
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigInteger id;
    @XmlAttribute(name = "objectId")
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigInteger objectId;
    @XmlAttribute(name = "type")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected InputOutputType type;
    @XmlAttribute(name = "optional")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Boolean optional;
    @XmlAttribute(name = "consumed")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Boolean consumed;

    /**
     * Gets the value of the attribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeAttribute }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public List<TypeAttribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<TypeAttribute>();
        }
        return this.attribute;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public BigInteger getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Gets the value of the objectId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public BigInteger getObjectId() {
        return objectId;
    }

    /**
     * Sets the value of the objectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setObjectId(BigInteger value) {
        this.objectId = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link InputOutputType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public InputOutputType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link InputOutputType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setType(InputOutputType value) {
        this.type = value;
    }

    /**
     * Gets the value of the optional property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setOptional(Boolean value) {
        this.optional = value;
    }

    /**
     * Gets the value of the consumed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setConsumed(Boolean value) {
        this.consumed = value;
    }

	/**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
