//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.09.15 at 04:54:36 PM EST 
//


package de.epml;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.w3c.dom.Element;


/**
 * <p>Java class for typeEPC complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeEPC">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.epml.de}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="configurationRequirement" type="{http://www.epml.de}typeCReq"/>
 *           &lt;element name="configurationGuideline" type="{http://www.epml.de}typeCReq"/>
 *           &lt;element name="configurationOrder" type="{http://www.epml.de}typeCOrder"/>
 *         &lt;/choice>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="event" type="{http://www.epml.de}typeEvent" minOccurs="0"/>
 *           &lt;element name="function" type="{http://www.epml.de}typeFunction" minOccurs="0"/>
 *           &lt;element name="role" type="{http://www.epml.de}typeRole" minOccurs="0"/>
 *           &lt;element name="object" type="{http://www.epml.de}typeObject" minOccurs="0"/>
 *           &lt;element name="processInterface" type="{http://www.epml.de}typeProcessInterface" minOccurs="0"/>
 *           &lt;element name="and" type="{http://www.epml.de}typeAND" minOccurs="0"/>
 *           &lt;element name="or" type="{http://www.epml.de}typeOR" minOccurs="0"/>
 *           &lt;element name="xor" type="{http://www.epml.de}typeXOR" minOccurs="0"/>
 *           &lt;element name="range" type="{http://www.epml.de}typeRANGE" minOccurs="0"/>
 *           &lt;element name="arc" type="{http://www.epml.de}typeArc" minOccurs="0"/>
 *           &lt;any/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="epcId" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEPC", propOrder = {
    "configurationRequirementOrConfigurationGuidelineOrConfigurationOrder",
    "eventOrFunctionOrRole"
})
public class TypeEPC
    extends TExtensibleElements
{

    @XmlElementRefs({
        @XmlElementRef(name = "configurationGuideline", type = JAXBElement.class),
        @XmlElementRef(name = "configurationOrder", type = JAXBElement.class),
        @XmlElementRef(name = "configurationRequirement", type = JAXBElement.class)
    })
    protected List<JAXBElement<?>> configurationRequirementOrConfigurationGuidelineOrConfigurationOrder;
    /*@XmlElementRefs({
    @XmlElementRef(name = "arc", type = JAXBElement.class),
    @XmlElementRef(name = "range", type = JAXBElement.class),
    @XmlElementRef(name = "and", type = JAXBElement.class),
    @XmlElementRef(name = "role", type = JAXBElement.class),
    @XmlElementRef(name = "or", type = JAXBElement.class),
    @XmlElementRef(name = "object", type = JAXBElement.class),
    @XmlElementRef(name = "function", type = JAXBElement.class),
    @XmlElementRef(name = "xor", type = JAXBElement.class),
    @XmlElementRef(name = "processInterface", type = JAXBElement.class),
    @XmlElementRef(name = "event", type = JAXBElement.class)
    })
    @XmlAnyElement(lax = true)*/
    
    @XmlElements({
        @XmlElement(name = "arc", type = TypeArc.class),
        @XmlElement(name = "range", type = TypeRANGE.class),
        @XmlElement(name = "and", type = TypeAND.class),
        @XmlElement(name = "role", type = TypeRole.class),
        @XmlElement(name = "or", type = TypeOR.class),
        @XmlElement(name = "object", type = TypeObject.class),
        @XmlElement(name = "function", type = TypeFunction.class),
        @XmlElement(name = "xor", type = TypeXOR.class),
        @XmlElement(name = "processInterface", type = TypeProcessInterface.class),
        @XmlElement(name = "event", type = TypeEvent.class)
    })
    protected List<Object> eventOrFunctionOrRole;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger epcId;
    @XmlAttribute(required = true)
    protected String name;

    /**
     * Gets the value of the configurationRequirementOrConfigurationGuidelineOrConfigurationOrder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the configurationRequirementOrConfigurationGuidelineOrConfigurationOrder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConfigurationRequirementOrConfigurationGuidelineOrConfigurationOrder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link TypeCReq }{@code >}
     * {@link JAXBElement }{@code <}{@link TypeCReq }{@code >}
     * {@link JAXBElement }{@code <}{@link TypeCOrder }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getConfigurationRequirementOrConfigurationGuidelineOrConfigurationOrder() {
        if (configurationRequirementOrConfigurationGuidelineOrConfigurationOrder == null) {
            configurationRequirementOrConfigurationGuidelineOrConfigurationOrder = new ArrayList<JAXBElement<?>>();
        }
        return this.configurationRequirementOrConfigurationGuidelineOrConfigurationOrder;
    }

    /**
     * Gets the value of the eventOrFunctionOrRole property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eventOrFunctionOrRole property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEventOrFunctionOrRole().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link TypeRANGE }{@code >}
     * {@link JAXBElement }{@code <}{@link TypeArc }{@code >}
     * {@link JAXBElement }{@code <}{@link TypeAND }{@code >}
     * {@link JAXBElement }{@code <}{@link TypeOR }{@code >}
     * {@link JAXBElement }{@code <}{@link TypeRole }{@code >}
     * {@link JAXBElement }{@code <}{@link TypeObject }{@code >}
     * {@link JAXBElement }{@code <}{@link TypeFunction }{@code >}
     * {@link Object }
     * {@link Element }
     * {@link JAXBElement }{@code <}{@link TypeXOR }{@code >}
     * {@link JAXBElement }{@code <}{@link TypeProcessInterface }{@code >}
     * {@link JAXBElement }{@code <}{@link TypeEvent }{@code >}
     * 
     * 
     */
    public List<Object> getEventOrFunctionOrRole() {
        if (eventOrFunctionOrRole == null) {
            eventOrFunctionOrRole = new ArrayList<Object>();
        }
        return this.eventOrFunctionOrRole;
    }

    /**
     * Gets the value of the epcId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getEpcId() {
        return epcId;
    }

    /**
     * Sets the value of the epcId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setEpcId(BigInteger value) {
        this.epcId = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
