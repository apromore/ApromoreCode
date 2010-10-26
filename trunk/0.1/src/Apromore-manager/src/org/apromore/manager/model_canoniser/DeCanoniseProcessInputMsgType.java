
package org.apromore.manager.model_canoniser;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for DeCanoniseProcessInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeCanoniseProcessInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="processId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NativeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Cpf" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="Anf" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeCanoniseProcessInputMsgType", propOrder = {
    "processId",
    "version",
    "nativeType",
    "cpf",
    "anf"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class DeCanoniseProcessInputMsgType {

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected int processId;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String version;
    @XmlElement(name = "NativeType", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String nativeType;
    @XmlElement(name = "Cpf", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected DataHandler cpf;
    @XmlElement(name = "Anf")
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected DataHandler anf;

    /**
     * Gets the value of the processId property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public int getProcessId() {
        return processId;
    }

    /**
     * Sets the value of the processId property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setProcessId(int value) {
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the nativeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setNativeType(String value) {
        this.nativeType = value;
    }

    /**
     * Gets the value of the cpf property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public DataHandler getCpf() {
        return cpf;
    }

    /**
     * Sets the value of the cpf property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setCpf(DataHandler value) {
        this.cpf = value;
    }

    /**
     * Gets the value of the anf property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setAnf(DataHandler value) {
        this.anf = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
