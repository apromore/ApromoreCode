
package org.apromore.manager.model_da;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WriteUserInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WriteUserInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="User" type="{http://www.apromore.org/data_access/model_manager}UserType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WriteUserInputMsgType", propOrder = {
    "user"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T02:47:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class WriteUserInputMsgType {

    @XmlElement(name = "User", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T02:47:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected UserType user;

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link UserType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T02:47:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public UserType getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T02:47:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setUser(UserType value) {
        this.user = value;
    }

}
