package org.apromore.dao.model;

import org.apromore.graph.canonical.ObjectTypeEnum;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Object generated by hbm2java
 */
@Entity
@Table(name = "object")
@Configurable("object")
public class Object implements java.io.Serializable {

    private Integer id;
    private String uri;
    private String name;
    private String netId;
    private Boolean configurable;

    private String softType;
    private ObjectTypeEnum type;

    private ProcessModelVersion processModelVersion;
    private Set<ObjectAttribute> objectAttributes = new HashSet<ObjectAttribute>(0);
    private Set<ObjectRef> objectRefs = new HashSet<ObjectRef>(0);


    /**
     * Public Constructor.
     */
    public Object() { }



    /**
     * returns the Id of this Object.
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the Id of this Object
     * @param id the new Id.
     */
    public void setId(final Integer id) {
        this.id = id;
    }


    @Column(name = "uri")
    public String getUri() {
        return this.uri;
    }

    public void setUri(final String newUri) {
        this.uri = newUri;
    }

    @Column(name = "name")
    public String getName() {
        return this.name;
    }

    public void setName(final String newName) {
        this.name = newName;
    }

    @Column(name = "type", length = 30)
    @Enumerated(EnumType.STRING)
        public ObjectTypeEnum getType() {
        return this.type;
    }

    public void setType(final ObjectTypeEnum newType) {
        this.type = newType;
    }

    @Column(name = "softType", length = 255)
    public String getSoftType() {
        return this.softType;
    }

    public void setSoftType(final String newType) {
        this.softType = newType;
    }

    @Column(name = "netId", length = 40)
    public String getNetId() {
        return this.netId;
    }

    public void setNetId(String newNetId) {
        this.netId = newNetId;
    }

    @Column(name = "configurable", length = 1)
    public Boolean getConfigurable() {
        return this.configurable;
    }

    public void setConfigurable(final Boolean newConfigurable) {
        this.configurable = newConfigurable;
    }



    @ManyToOne
    @JoinColumn(name = "processModelVersionId")
    public ProcessModelVersion getProcessModelVersion() {
        return this.processModelVersion;
    }

    public void setProcessModelVersion(final ProcessModelVersion newProcessModelVersion) {
        this.processModelVersion = newProcessModelVersion;
    }


    @OneToMany(mappedBy = "object", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<ObjectRef> getObjectRefs() {
        return this.objectRefs;
    }

    public void setObjectRefs(Set<ObjectRef> objectRefTypes) {
        this.objectRefs = objectRefTypes;
    }

    @OneToMany(mappedBy = "object", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<ObjectAttribute> getObjectAttributes() {
        return this.objectAttributes;
    }

    public void setObjectAttributes(Set<ObjectAttribute> objectTypeAttributes) {
        this.objectAttributes = objectTypeAttributes;
    }

}


