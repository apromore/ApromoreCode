package org.apromore.dao.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Stores the Annotation in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "annotation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"processModelVersionId", "name"}),
                @UniqueConstraint(columnNames = {"nativeId"})
        }
)
@Configurable("annotation")
@Cache(expiry = 180000, size = 5000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class Annotation implements Serializable {

    private Integer id;
    private String name;
    private String content;
    private String lastUpdateDate;

    private Native natve;
    private ProcessModelVersion processModelVersion;

    /**
     * Default Constructor.
     */
    public Annotation() { }



    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }


    @Column(name = "name", unique = false, nullable = true, length = 40)
    public String getName() {
        return name;
    }

    public void setName(final String newName) {
        this.name = newName;
    }


    @Lob
    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(final String newContent) {
        this.content = newContent;
    }

    @Column(name = "lastUpdateDate")
    public String getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(final String newLastUpdate) {
        this.lastUpdateDate = newLastUpdate;
    }



    @ManyToOne
    @JoinColumn(name = "nativeId")
    public Native getNatve() {
        return this.natve;
    }

    public void setNatve(final Native newNative) {
        this.natve = newNative;
    }

    @ManyToOne
    @JoinColumn(name = "processModelVersionId")
    public ProcessModelVersion getProcessModelVersion() {
        return this.processModelVersion;
    }

    public void setProcessModelVersion(final ProcessModelVersion newProcessModelVersion) {
        this.processModelVersion = newProcessModelVersion;
    }

}
