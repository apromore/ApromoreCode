
package org.oryx.model_portal;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for WriteNewProcessInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WriteNewProcessInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Native" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *       &lt;attribute name="EditSessionCode" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="ProcessName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="VersionName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WriteNewProcessInputMsgType", propOrder = {
    "_native"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-08T05:27:55+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class WriteNewProcessInputMsgType {

    @XmlElement(name = "Native", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-08T05:27:55+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected DataHandler _native;
    @XmlAttribute(name = "EditSessionCode")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-08T05:27:55+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Integer editSessionCode;
    @XmlAttribute(name = "ProcessName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-08T05:27:55+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String processName;
    @XmlAttribute(name = "VersionName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-08T05:27:55+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String versionName;

    /**
     * Gets the value of the native property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-08T05:27:55+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public DataHandler getNative() {
        return _native;
    }

    /**
     * Sets the value of the native property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-08T05:27:55+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setNative(DataHandler value) {
        this._native = value;
    }

    /**
     * Gets the value of the editSessionCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-08T05:27:55+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public Integer getEditSessionCode() {
        return editSessionCode;
    }

    /**
     * Sets the value of the editSessionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-08T05:27:55+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setEditSessionCode(Integer value) {
        this.editSessionCode = value;
    }

    /**
     * Gets the value of the processName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-08T05:27:55+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getProcessName() {
        return processName;
    }

    /**
     * Sets the value of the processName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-08T05:27:55+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setProcessName(String value) {
        this.processName = value;
    }

    /**
     * Gets the value of the versionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-08T05:27:55+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getVersionName() {
        return versionName;
    }

    /**
     * Sets the value of the versionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-08T05:27:55+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setVersionName(String value) {
        this.versionName = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-08T05:27:55+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
