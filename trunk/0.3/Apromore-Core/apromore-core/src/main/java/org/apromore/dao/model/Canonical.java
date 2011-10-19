package org.apromore.dao.model;

// Generated 17/10/2011 9:31:36 PM by Hibernate Tools 3.4.0.CR1

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Stores the canonical format in apromore.
 *
 * @author Cameron James
 */
@Entity
@Table(name = "canonicals",
       uniqueConstraints = {
               @UniqueConstraint(columnNames = { "processId", "version_name" })
       }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries( {
//        @NamedQuery(name = User.FIND_USER, query = "SELECT usr FROM User usr WHERE usr.username = :username"),
//        @NamedQuery(name = User.FIND_ALL_USERS, query = "SELECT usr FROM User usr")
})
@Configurable("canonical")
public class Canonical implements Serializable {

    /** Hard coded for interoperability. */
    private static final long serialVersionUID = -9072538404638485548L;

	private String uri;
	private String versionName;
	private String author;
	private String creationDate;
	private String lastUpdate;
	private String ranking;
	private String documentation;
	private String content;

    private Process process;
	private Set<Native> natives = new HashSet<Native>(0);

	private Set<Canonical> canonicalsForUriSource = new HashSet<Canonical>(0);
	private Set<Canonical> canonicalsForUriMerged = new HashSet<Canonical>(0);
//	private Set<Canonical> canonicalsForFkDerivedVersion1 = new HashSet<Canonical>(0);
//	private Set<Canonical> canonicalsForFkDerivedVersion2 = new HashSet<Canonical>(0);


    /**
     * Default Constructor.
     */
	public Canonical() { }


    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
	@Id @Column(name = "uri", unique = true, nullable = false, length = 40)
	public String getUri() {
		return this.uri;
	}

    /**
     * Set the Primary Key for the Object.
     * @param newUri The id to set.
     */
	public void setUri(final String newUri) {
		this.uri = newUri;
	}


    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "processId")
	public Process getProcess() {
		return this.process;
	}

	public void setProcess(final Process newProcess) {
		this.process = newProcess;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @Column(name = "version_name", length = 20)
	public String getVersionName() {
		return this.versionName;
	}

	public void setVersionName(final String newVersionName) {
		this.versionName = newVersionName;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @Column(name = "author", length = 40)
	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(final String newAuthor) {
		this.author = newAuthor;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @Column(name = "creation_date", length = 35)
	public String getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(final String newCreationDate) {
		this.creationDate = newCreationDate;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @Column(name = "last_update", length = 35)
	public String getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(final String newLastUpdate) {
		this.lastUpdate = newLastUpdate;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @Column(name = "ranking", length = 10)
	public String getRanking() {
		return this.ranking;
	}

	public void setRanking(final String newRanking) {
		this.ranking = newRanking;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @Column(name = "documentation", length = 65535)
	public String getDocumentation() {
		return this.documentation;
	}

	public void setDocumentation(final String newDocumentation) {
		this.documentation = newDocumentation;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @Column(name = "content")
	public String getContent() {
		return this.content;
	}

	public void setContent(final String newContent) {
		this.content = newContent;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "canonicals")
	public Set<Native> getNatives() {
		return this.natives;
	}

	public void setNatives(final Set<Native> newNatives) {
		this.natives = newNatives;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "merged_versions",
            joinColumns = { @JoinColumn(name = "uri_merged", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "uri_source", nullable = false, updatable = false) }
    )
	public Set<Canonical> getCanonicalsForUriSource() {
		return this.canonicalsForUriSource;
	}

	public void setCanonicalsForUriSource(final Set<Canonical> newCanonicalsForUriSource) {
		this.canonicalsForUriSource = newCanonicalsForUriSource;
	}

    /**
     * Get the process for the Object.
     * @return Returns the process.
     */
    @ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "merged_versions",
            joinColumns = { @JoinColumn(name = "uri_source", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "uri_merged", nullable = false, updatable = false) }
    )
	public Set<Canonical> getCanonicalsForUriMerged() {
		return this.canonicalsForUriMerged;
	}

	public void setCanonicalsForUriMerged(final Set<Canonical> newCanonicalsForUriMerged) {
		this.canonicalsForUriMerged = newCanonicalsForUriMerged;
	}


//    /**
//     * Get the process for the Object.
//     * @return Returns the process.
//     */
//    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "unresolved")
//	public Set<Canonical> getCanonicalsForFkDerivedVersion1() {
//		return this.canonicalsForFkDerivedVersion1;
//	}
//
//	public void setCanonicalsForFkDerivedVersion1(final Set<Canonical> newCanonicalsForFkDerivedVersion1) {
//		this.canonicalsForFkDerivedVersion1 = newCanonicalsForFkDerivedVersion1;
//	}
//
//    /**
//     * Get the process for the Object.
//     * @return Returns the process.
//     */
//    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "unresolved")
//	public Set<Canonical> getCanonicalsForFkDerivedVersion2() {
//		return this.canonicalsForFkDerivedVersion2;
//	}
//
//	public void setCanonicalsForFkDerivedVersion2(final  Set<Canonical> newCanonicalsForFkDerivedVersion2) {
//		this.canonicalsForFkDerivedVersion2 = newCanonicalsForFkDerivedVersion2;
//	}

}
