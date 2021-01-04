/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.dao.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;


import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;
import com.google.common.base.Objects;

/**
 * ProcessModelVersion generated by hbm2java
 */
@Entity
@Table(name = "process_model_version")
@Configurable("processModelVersion")
@Cache(expiry = 180000, size = 5000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class ProcessModelVersion implements Serializable {

    private Integer id;
    private String originalId;
    private String versionNumber;
    private String createDate;
    private String lastUpdateDate;
    private Integer changePropagation;
    private Integer lockStatus;
    private Integer numVertices;
    private Integer numEdges;

    private Native nativeDocument;
    private ProcessBranch processBranch;
    private NativeType nativeType;
    private Set<ProcessBranch> currentProcessModelVersion = new HashSet<>();
    private Set<ProcessBranch> sourceProcessModelVersion = new HashSet<>();
    private Set<ProcessModelAttribute> processModelAttributes = new HashSet<>();


    /**
     * Default Constructor.
     */
    public ProcessModelVersion() {
    }


    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }


    @Column(name = "originalId")
    public String getOriginalId() {
        return this.originalId;
    }

    public void setOriginalId(final String newOriginalId) {
        this.originalId = newOriginalId;
    }

    @Column(name = "version_number")
    public String getVersionNumber() {
        return this.versionNumber;
    }

    public void setVersionNumber(final String newVersionNumber) {
        this.versionNumber = newVersionNumber;
    }

    @Column(name = "createDate")
    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(final String newCreationDate) {
        this.createDate = newCreationDate;
    }

    @Column(name = "lastUpdateDate")
    public String getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(final String newLastUpdate) {
        this.lastUpdateDate = newLastUpdate;
    }

    @Column(name = "change_propagation")
    public Integer getChangePropagation() {
        return this.changePropagation;
    }

    public void setChangePropagation(final Integer newChangePropagation) {
        this.changePropagation = newChangePropagation;
    }


    @Column(name = "lock_status")
    public Integer getLockStatus() {
        return this.lockStatus;
    }

    public void setLockStatus(final Integer newLockStatus) {
        this.lockStatus = newLockStatus;
    }


    @Column(name = "num_nodes")
    public Integer getNumVertices() {
        return this.numVertices;
    }

    public void setNumVertices(final Integer newNumVertices) {
        this.numVertices = newNumVertices;
    }


    @Column(name = "num_edges")
    public Integer getNumEdges() {
        return this.numEdges;
    }

    public void setNumEdges(final Integer newNumEdges) {
        this.numEdges = newNumEdges;
    }



    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nativeId", referencedColumnName = "id")
    public Native getNativeDocument() {
        return this.nativeDocument;
    }

    public void setNativeDocument(final Native newNative) {
        this.nativeDocument = newNative;
    }


    @ManyToOne
    @JoinColumn(name = "branchId")
    public ProcessBranch getProcessBranch() {
        return this.processBranch;
    }

    public void setProcessBranch(final ProcessBranch newProcessBranches) {
        this.processBranch = newProcessBranches;
    }

    @ManyToOne
    @JoinColumn(name = "nativeTypeId")
    public NativeType getNativeType() {
        return this.nativeType;
    }

    public void setNativeType(final NativeType newNativeType) {
        this.nativeType = newNativeType;
    }


//    @ManyToMany
//    @JoinTable(name = "process_fragment_map",
//            joinColumns = { @JoinColumn(name = "processModelVersionId") },
//            inverseJoinColumns = { @JoinColumn(name = "fragmentVersionId") }
//    )

    @OneToMany(mappedBy = "currentProcessModelVersion")
    public Set<ProcessBranch> getCurrentProcessModelVersion() {
        return this.currentProcessModelVersion;
    }

    public void setCurrentProcessModelVersion(final Set<ProcessBranch> newCurrentIds) {
        this.currentProcessModelVersion = newCurrentIds;
    }

    @OneToMany(mappedBy = "sourceProcessModelVersion")
    public Set<ProcessBranch> getSourceProcessModelVersion() {
        return this.sourceProcessModelVersion;
    }

    public void setSourceProcessModelVersion(final Set<ProcessBranch> newSourceIds) {
        this.sourceProcessModelVersion = newSourceIds;
    }


    @OneToMany(mappedBy = "processModelVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<ProcessModelAttribute> getProcessModelAttributes() {
        return this.processModelAttributes;
    }

    public void setProcessModelAttributes(Set<ProcessModelAttribute> processModelAttributes) {
        this.processModelAttributes = processModelAttributes;
    }


    @Override
    public int hashCode() {
      return Objects.hashCode(id,
          originalId,
          versionNumber,
          numVertices,
          numEdges);
    }

    @Override
    public boolean equals(java.lang.Object obj) {
      if (obj == null) {
        return false;
      }
      if (obj == this) {
        return true;
      }
      if (obj.getClass() != getClass()) {
        return false;
      }
      ProcessModelVersion rhs = (ProcessModelVersion) obj;
      return Objects.equal(id, rhs.id) &&
          Objects.equal(originalId, rhs.originalId) &&
          Objects.equal(versionNumber, rhs.versionNumber) &&
          Objects.equal(numVertices, rhs.numVertices) &&
          Objects.equal(numEdges, rhs.numEdges);
    }
    
    @Override
    public ProcessModelVersion clone() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String now = dateFormat.format(new Date());
        ProcessModelVersion pmv = new ProcessModelVersion();
        pmv.setProcessBranch(this.getProcessBranch());
        pmv.setProcessModelAttributes(this.getProcessModelAttributes());
        pmv.setVersionNumber(this.getVersionNumber());
        pmv.setOriginalId(null);
        pmv.setNativeType(this.getNativeType());
        pmv.setNativeDocument(this.getNativeDocument());
        pmv.setLockStatus(0);
        pmv.setLastUpdateDate(now);
        pmv.setCreateDate(now);
        return pmv;
    }
}


