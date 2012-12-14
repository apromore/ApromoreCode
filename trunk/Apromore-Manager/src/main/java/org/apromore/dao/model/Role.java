package org.apromore.dao.model;

import org.springframework.beans.factory.annotation.Configurable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Stores the process in apromore.
 * @author Cameron James
 */
@Entity
@Table(name = "role",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id"}),
                @UniqueConstraint(columnNames = {"role_name"}),
                @UniqueConstraint(columnNames = {"row_guid"})
        }
)
@Configurable("role")
public class Role implements Serializable {

    private Integer id;
    private String rowGuid = UUID.randomUUID().toString();
    private String name;
    private String description;

    private Set<Permission> permissions = new HashSet<Permission>();
    private Set<User> users = new HashSet<User>();


    /**
     * Default Constructor.
     */
    public Role() {
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

    /**
     * Get the row unique identifier for the Object.
     * @return Returns the row unique identifier.
     */
    @Column(name = "row_guid", unique = true, nullable = false, length = 256)
    public String getRowGuid() {
        return rowGuid;
    }

    /**
     * Set the row unique identifier for the Object.
     * @param newRowGuid The row unique identifier to set.
     */
    public void setRowGuid(final String newRowGuid) {
        this.rowGuid = newRowGuid;
    }

    /**
     * Get the role name for the Object.
     * @return Returns the role name.
     */
    @Column(name = "role_name", unique = true, nullable = false, length = 45)
    public String getName() {
        return name;
    }

    /**
     * Set the role name for the Object.
     * @param newName The role name to set.
     */
    public void setName(final String newName) {
        this.name = newName;
    }

    /**
     * Get the role description for the Object.
     * @return Returns the role description.
     */
    @Column(name = "description", unique = false, nullable = true, length = 200)
    public String getDescription() {
        return description;
    }

    /**
     * Set the description for the Object.
     * @param newDescription The permission description to set.
     */
    public void setDescription(final String newDescription) {
        this.description = newDescription;
    }

    /**
     * Getter for the users collection.
     * @return Returns the users.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = { @JoinColumn(name = "roleId") },
            inverseJoinColumns = { @JoinColumn(name = "userId") }
    )
    public Set<User> getUsers() {
        return this.users;
    }

    /**
     * Setter for the users Collection.
     * @param newUsers The users to set.
     */
    public void setUsers(final Set<User> newUsers) {
        this.users = newUsers;
    }

    /**
     * Getter for the permission collection.
     * @return Returns the permissions.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permission",
            joinColumns = { @JoinColumn(name = "roleId") },
            inverseJoinColumns = { @JoinColumn(name = "permissionId") }
    )
    public Set<Permission> getPermissions() {
        return this.permissions;
    }

    /**
     * Setter for the permission collection.
     * @param newPermissions The permissions to set.
     */
    public void setPermissions(final Set<Permission> newPermissions) {
        this.permissions = newPermissions;
    }
}
