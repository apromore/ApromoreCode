
package org.apromore.manager.model_da;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadAllUsersOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadAllUsersOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/data_access/model_manager}ResultType"/>
 *         &lt;element name="Usernames" type="{http://www.apromore.org/data_access/model_manager}UsernamesType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadAllUsersOutputMsgType", propOrder = {
    "result",
    "usernames"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T10:25:31+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class ReadAllUsersOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T10:25:31+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected ResultType result;
    @XmlElement(name = "Usernames", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T10:25:31+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected UsernamesType usernames;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T10:25:31+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public ResultType getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T10:25:31+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the usernames property.
     * 
     * @return
     *     possible object is
     *     {@link UsernamesType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T10:25:31+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public UsernamesType getUsernames() {
        return usernames;
    }

    /**
     * Sets the value of the usernames property.
     * 
     * @param value
     *     allowed object is
     *     {@link UsernamesType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T10:25:31+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setUsernames(UsernamesType value) {
        this.usernames = value;
    }

}
