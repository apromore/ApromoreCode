
package org.wfmc._2002.xpdl1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}FormalParameter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "formalParameter"
})
@XmlRootElement(name = "FormalParameters")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class FormalParameters {

    @XmlElement(name = "FormalParameter")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<FormalParameter> formalParameter;

    /**
     * Gets the value of the formalParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the formalParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFormalParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FormalParameter }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<FormalParameter> getFormalParameter() {
        if (formalParameter == null) {
            formalParameter = new ArrayList<FormalParameter>();
        }
        return this.formalParameter;
    }

}
