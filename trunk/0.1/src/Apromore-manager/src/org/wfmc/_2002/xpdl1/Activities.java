
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
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Activity" maxOccurs="unbounded" minOccurs="0"/>
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
    "activity"
})
@XmlRootElement(name = "Activities")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class Activities {

    @XmlElement(name = "Activity")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<Activity> activity;

    /**
     * Gets the value of the activity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the activity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActivity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Activity }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<Activity> getActivity() {
        if (activity == null) {
            activity = new ArrayList<Activity>();
        }
        return this.activity;
    }

}
