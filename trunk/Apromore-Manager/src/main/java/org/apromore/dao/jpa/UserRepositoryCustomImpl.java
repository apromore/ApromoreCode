package org.apromore.dao.jpa;

import org.apromore.dao.UserRepositoryCustom;
import org.apromore.dao.model.Membership;
import org.apromore.dao.model.Permission;
import org.apromore.dao.model.User;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * implementation of the org.apromore.dao.ProcessDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.UserRepositoryCustom#login(String, String)
     * {@inheritDoc}
     */
    @Override
    public User login(final String username, final String password) {
        Query query = em.createQuery("SELECT u FROM User u WHERE u.username = :username");
        query.setParameter("username", username);
        User user = (User) query.getSingleResult();

        if (user != null){
            Membership membership = user.getMembership();
            if (membership != null && membership.getPassword().trim().equals(password.trim())) {
                return user;
            }
        }

        return null;
    }

    /**
     * @see org.apromore.dao.UserRepositoryCustom#hasAccess(String, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean hasAccess(String userId, String permissionId) {
        Query query = em.createQuery("SELECT p FROM User usr JOIN usr.roles r JOIN r.permissions p WHERE usr.rowGuid = :userId " +
                "AND p.rowGuid = :permId");
        query.setParameter("userId", userId);
        query.setParameter("permId", permissionId);
        List<Permission> permissions = query.getResultList();

        return permissions != null && permissions.size() > 0;
    }
}
