
package org.test_toolbox.manager.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadNativeTypesOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadNativeTypesOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/manager/model_portal}ResultType"/>
 *         &lt;element name="NativeTypes" type="{http://www.apromore.org/manager/model_portal}NativeTypesType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadNativeTypesOutputMsgType", propOrder = {
    "result",
    "nativeTypes"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-21T09:03:57+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
public class ReadNativeTypesOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-21T09:03:57+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected ResultType result;
    @XmlElement(name = "NativeTypes", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-21T09:03:57+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected NativeTypesType nativeTypes;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-21T09:03:57+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-21T09:03:57+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the nativeTypes property.
     * 
     * @return
     *     possible object is
     *     {@link NativeTypesType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-21T09:03:57+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public NativeTypesType getNativeTypes() {
        return nativeTypes;
    }

    /**
     * Sets the value of the nativeTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link NativeTypesType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-21T09:03:57+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setNativeTypes(NativeTypesType value) {
        this.nativeTypes = value;
    }

}
