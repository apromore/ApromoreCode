
package org.test_toolbox.manager.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadDomainsInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadDomainsInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="Empty" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadDomainsInputMsgType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-21T09:03:57+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
public class ReadDomainsInputMsgType {

    @XmlAttribute(name = "Empty")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-21T09:03:57+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String empty;

    /**
     * Gets the value of the empty property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-21T09:03:57+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public String getEmpty() {
        return empty;
    }

    /**
     * Sets the value of the empty property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-21T09:03:57+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setEmpty(String value) {
        this.empty = value;
    }

}
