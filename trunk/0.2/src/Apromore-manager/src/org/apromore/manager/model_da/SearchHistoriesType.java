
package org.apromore.manager.model_da;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SearchHistoriesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SearchHistoriesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="search" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="num" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchHistoriesType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T11:11:11+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class SearchHistoriesType {

    @XmlAttribute(name = "search")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T11:11:11+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String search;
    @XmlAttribute(name = "num")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T11:11:11+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Integer num;

    /**
     * Gets the value of the search property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T11:11:11+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getSearch() {
        return search;
    }

    /**
     * Sets the value of the search property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T11:11:11+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setSearch(String value) {
        this.search = value;
    }

    /**
     * Gets the value of the num property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T11:11:11+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public Integer getNum() {
        return num;
    }

    /**
     * Sets the value of the num property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T11:11:11+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setNum(Integer value) {
        this.num = value;
    }

}
