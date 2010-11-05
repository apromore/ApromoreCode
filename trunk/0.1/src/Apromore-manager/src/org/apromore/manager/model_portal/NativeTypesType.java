
package org.apromore.manager.model_portal;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for NativeTypesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NativeTypesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NativeType" type="{http://www.apromore.org/manager/model_portal}FormatType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NativeTypesType", propOrder = {
    "nativeType"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-05T05:28:49+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class NativeTypesType {

    @XmlElement(name = "NativeType")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-05T05:28:49+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<FormatType> nativeType;

    /**
     * Gets the value of the nativeType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nativeType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNativeType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FormatType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-05T05:28:49+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public List<FormatType> getNativeType() {
        if (nativeType == null) {
            nativeType = new ArrayList<FormatType>();
        }
        return this.nativeType;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-05T05:28:49+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
