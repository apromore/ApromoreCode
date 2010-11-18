
package org.apromore.portal.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for MergeProcessesInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MergeProcessesInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProcessName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="VersionName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProcessVersion_ids" type="{http://www.apromore.org/manager/model_portal}ProcessVersion_idsType"/>
 *         &lt;element name="Algorithm" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Parameters" type="{http://www.apromore.org/manager/model_portal}ParametersType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MergeProcessesInputMsgType", propOrder = {
    "processName",
    "versionName",
    "username",
    "processVersionIds",
    "algorithm",
    "parameters"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class MergeProcessesInputMsgType {

    @XmlElement(name = "ProcessName", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String processName;
    @XmlElement(name = "VersionName", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String versionName;
    @XmlElement(name = "Username", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String username;
    @XmlElement(name = "ProcessVersion_ids", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ProcessVersionIdsType processVersionIds;
    @XmlElement(name = "Algorithm", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String algorithm;
    @XmlElement(name = "Parameters", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ParametersType parameters;

    /**
     * Gets the value of the processName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setVersionName(String value) {
        this.versionName = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the processVersionIds property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessVersionIdsType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public ProcessVersionIdsType getProcessVersionIds() {
        return processVersionIds;
    }

    /**
     * Sets the value of the processVersionIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessVersionIdsType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setProcessVersionIds(ProcessVersionIdsType value) {
        this.processVersionIds = value;
    }

    /**
     * Gets the value of the algorithm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the value of the algorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setAlgorithm(String value) {
        this.algorithm = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link ParametersType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public ParametersType getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParametersType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setParameters(ParametersType value) {
        this.parameters = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
