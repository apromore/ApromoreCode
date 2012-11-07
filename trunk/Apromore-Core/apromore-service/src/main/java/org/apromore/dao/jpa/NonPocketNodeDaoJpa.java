package org.apromore.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apromore.dao.NonPocketNodeDao;
import org.apromore.dao.model.NonPocketNode;
import org.springframework.stereotype.Repository;

/**
 * Hibernate implementation of the org.apromore.dao.NonPocketNodeDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
public class NonPocketNodeDaoJpa implements NonPocketNodeDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.NonPocketNodeDao#save(org.apromore.dao.model.NonPocketNode)
     * {@inheritDoc}
     */
    @Override
    public void save(final NonPocketNode node) {
        em.persist(node);
    }

    /**
     * @see org.apromore.dao.NonPocketNodeDao#update(org.apromore.dao.model.NonPocketNode)
     * {@inheritDoc}
     */
    @Override
    public NonPocketNode update(final NonPocketNode node) {
        return em.merge(node);
    }

    /**
     * @see org.apromore.dao.NonPocketNodeDao#delete(org.apromore.dao.model.NonPocketNode)
     * {@inheritDoc}
     */
    @Override
    public void delete(final NonPocketNode node) {
        em.remove(node);
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working.
     * @param newEm the entitymanager
     */
    public void setEntityManager(final EntityManager newEm) {
        this.em = newEm;
    }

}
