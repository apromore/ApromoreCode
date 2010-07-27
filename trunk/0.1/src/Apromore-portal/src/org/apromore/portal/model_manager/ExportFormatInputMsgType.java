
package org.apromore.portal.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExportFormatInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExportFormatInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="NativeType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ProcessId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="ProcessName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="VersionName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="AnnotationName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="withAnnotations" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="Owner" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExportFormatInputMsgType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class ExportFormatInputMsgType {

    @XmlAttribute(name = "NativeType")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String nativeType;
    @XmlAttribute(name = "ProcessId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Integer processId;
    @XmlAttribute(name = "ProcessName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String processName;
    @XmlAttribute(name = "VersionName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String versionName;
    @XmlAttribute(name = "AnnotationName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String annotationName;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Boolean withAnnotations;
    @XmlAttribute(name = "Owner")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String owner;

    /**
     * Gets the value of the nativeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getNativeType() {
        return nativeType;
    }

    /**
     * Sets the value of the nativeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setNativeType(String value) {
        this.nativeType = value;
    }

    /**
     * Gets the value of the processId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Integer getProcessId() {
        return processId;
    }

    /**
     * Sets the value of the processId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setProcessId(Integer value) {
        this.processId = value;
    }

    /**
     * Gets the value of the processName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setVersionName(String value) {
        this.versionName = value;
    }

    /**
     * Gets the value of the annotationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getAnnotationName() {
        return annotationName;
    }

    /**
     * Sets the value of the annotationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setAnnotationName(String value) {
        this.annotationName = value;
    }

    /**
     * Gets the value of the withAnnotations property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Boolean isWithAnnotations() {
        return withAnnotations;
    }

    /**
     * Sets the value of the withAnnotations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setWithAnnotations(Boolean value) {
        this.withAnnotations = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T12:09:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setOwner(String value) {
        this.owner = value;
    }

}
