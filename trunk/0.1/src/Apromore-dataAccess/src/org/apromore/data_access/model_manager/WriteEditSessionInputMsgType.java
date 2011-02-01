
package org.apromore.data_access.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for WriteEditSessionInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WriteEditSessionInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EditSession" type="{http://www.apromore.org/data_access/model_manager}EditSessionType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WriteEditSessionInputMsgType", propOrder = {
    "editSession"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-08T04:50:04-08:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class WriteEditSessionInputMsgType {

    @XmlElement(name = "EditSession", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-08T04:50:04-08:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected EditSessionType editSession;

    /**
     * Gets the value of the editSession property.
     * 
     * @return
     *     possible object is
     *     {@link EditSessionType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-08T04:50:04-08:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public EditSessionType getEditSession() {
        return editSession;
    }

    /**
     * Sets the value of the editSession property.
     * 
     * @param value
     *     allowed object is
     *     {@link EditSessionType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-08T04:50:04-08:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setEditSession(EditSessionType value) {
        this.editSession = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
//    @Override
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:53:11+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
