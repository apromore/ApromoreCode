
package org.apromore.canoniser.model_da;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WriteAnnotationInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WriteAnnotationInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Anf" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *       &lt;attribute name="EditSessionCode" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="AnnotationName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="IsNew" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="ProcessId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="Version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="CpfURI" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="NativeType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WriteAnnotationInputMsgType", propOrder = {
    "anf"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class WriteAnnotationInputMsgType {

    @XmlElement(name = "Anf", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected DataHandler anf;
    @XmlAttribute(name = "EditSessionCode")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Integer editSessionCode;
    @XmlAttribute(name = "AnnotationName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String annotationName;
    @XmlAttribute(name = "IsNew")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Boolean isNew;
    @XmlAttribute(name = "ProcessId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Integer processId;
    @XmlAttribute(name = "Version")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String version;
    @XmlAttribute(name = "CpfURI")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String cpfURI;
    @XmlAttribute(name = "NativeType")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String nativeType;

    /**
     * Gets the value of the anf property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public DataHandler getAnf() {
        return anf;
    }

    /**
     * Sets the value of the anf property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setAnf(DataHandler value) {
        this.anf = value;
    }

    /**
     * Gets the value of the editSessionCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setEditSessionCode(Integer value) {
        this.editSessionCode = value;
    }

    /**
     * Gets the value of the annotationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setAnnotationName(String value) {
        this.annotationName = value;
    }

    /**
     * Gets the value of the isNew property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public Boolean isIsNew() {
        return isNew;
    }

    /**
     * Sets the value of the isNew property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setIsNew(Boolean value) {
        this.isNew = value;
    }

    /**
     * Gets the value of the processId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setProcessId(Integer value) {
        this.processId = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the cpfURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getCpfURI() {
        return cpfURI;
    }

    /**
     * Sets the value of the cpfURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setCpfURI(String value) {
        this.cpfURI = value;
    }

    /**
     * Gets the value of the nativeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:50:40+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setNativeType(String value) {
        this.nativeType = value;
    }

}
