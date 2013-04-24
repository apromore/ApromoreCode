package org.apromore.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;

/**
 * Stores the process in apromore.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "fragment_user")
@Cache(expiry = 180000, size = 1000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class FragmentUser implements Serializable {

    private Integer id;
    private boolean hasRead;
    private boolean hasWrite;
    private boolean hasOwnership;

    private User user;
    private Fragment fragment;


    /**
     * Default Constructor.
     */
    public FragmentUser() {
    }



    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    /**
     * Set the id for the Object.
     * @param newId The role name to set.
     */
    public void setId(final Integer newId) {
        this.id = newId;
    }


    @ManyToOne
    @JoinColumn(name = "userId")
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "fragmentId")
    public Fragment getFragment() {
        return this.fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }


    @Column(name = "has_read")
    public boolean isHasRead() {
        return this.hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }


    @Column(name = "has_write")
    public boolean isHasWrite() {
        return this.hasWrite;
    }

    public void setHasWrite(boolean hasWrite) {
        this.hasWrite = hasWrite;
    }


    @Column(name = "has_ownership")
    public boolean isHasOwnership() {
        return this.hasOwnership;
    }

    public void setHasOwnership(boolean hasOwnership) {
        this.hasOwnership = hasOwnership;
    }
}
