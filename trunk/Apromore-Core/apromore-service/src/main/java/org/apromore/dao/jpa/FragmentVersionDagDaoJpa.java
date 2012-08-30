package org.apromore.dao.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.FragmentVersionDagDao;
import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate implementation of the org.apromore.dao.FragmentVersionDagDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class FragmentVersionDagDaoJpa implements FragmentVersionDagDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.FragmentVersionDagDao#findFragmentVersionDag(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FragmentVersionDag findFragmentVersionDag(final String vertexId) {
        return em.find(FragmentVersionDag.class, vertexId);
    }


    /**
     * @see org.apromore.dao.FragmentVersionDagDao#getChildMappings(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FragmentVersionDag> getChildMappings(final String fragmentId) {
        Query query = em.createNamedQuery(NamedQueries.GET_CHILD_MAPPINGS);
        query.setParameter("fragVersionId", fragmentId);
        return (List<FragmentVersionDag>) query.getResultList();
    }

    /**
     * @see org.apromore.dao.FragmentVersionDagDao#getAllParentChildMappings()
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getAllParentChildMappings() {
        Query query = em.createNamedQuery(NamedQueries.GET_ALL_PARENT_CHILD_MAPPINGS);
        List<FragmentVersionDag> mappings = (List<FragmentVersionDag>) query.getResultList();

        Map<String, List<String>> parentChildMap = new HashMap<String, List<String>>();
        for (FragmentVersionDag mapping : mappings) {
            String pid = mapping.getId().getFragmentVersionId();
            String cid = mapping.getId().getChildFragmentVersionId();
            if (parentChildMap.containsKey(pid)) {
                parentChildMap.get(pid).add(cid);
            } else {
                List<String> childIds = new ArrayList<String>();
                childIds.add(cid);
                parentChildMap.put(pid, childIds);
            }
        }
        return parentChildMap;
    }

    /**
     * @see org.apromore.dao.FragmentVersionDagDao#getAllChildParentMappings()
     *  {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getAllChildParentMappings() {
        Query query = em.createNamedQuery(NamedQueries.GET_ALL_PARENT_CHILD_MAPPINGS);
        List<FragmentVersionDag> mappings = (List<FragmentVersionDag>) query.getResultList();

        Map<String, List<String>> childParentMap = new HashMap<String, List<String>>();
        for (FragmentVersionDag mapping : mappings) {
            String pid = mapping.getId().getFragmentVersionId();
            String cid = mapping.getId().getChildFragmentVersionId();
            if (childParentMap.containsKey(cid)) {
                childParentMap.get(cid).add(pid);
            } else {
                List<String> parentIds = new ArrayList<String>();
                parentIds.add(pid);
                childParentMap.put(cid, parentIds);
            }
        }
        return childParentMap;
    }

    /**
     * @see org.apromore.dao.FragmentVersionDagDao#getChildFragmentsByFragmentVersion(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FragmentVersion> getChildFragmentsByFragmentVersion(final String fragmentVersionId) {
        Query query = em.createNamedQuery(NamedQueries.GET_CHILD_FRAGMENTS_BY_FRAGMENT_VERSION);
        query.setParameter("fragVersionId", fragmentVersionId);
        return (List<FragmentVersion>) query.getResultList();
    }

    /**
     * @see org.apromore.dao.FragmentVersionDagDao#getAllDAGEntries(int)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FragmentVersionDag> getAllDAGEntries(int minimumChildFragmentSize) {
        Query query = em.createNamedQuery(NamedQueries.GET_ALL_DAGS_WITH_SIZE);
        query.setParameter("minSize", minimumChildFragmentSize);
        return (List<FragmentVersionDag>) query.getResultList();
    }



    /**
     * @see org.apromore.dao.FragmentVersionDagDao#save(org.apromore.dao.model.FragmentVersionDag)
     * {@inheritDoc}
     */
    @Override
    public void save(final FragmentVersionDag fragmentVersionDag) {
        em.persist(fragmentVersionDag);
    }

    /**
     * @see org.apromore.dao.FragmentVersionDagDao#update(org.apromore.dao.model.FragmentVersionDag)
     * {@inheritDoc}
     */
    @Override
    public FragmentVersionDag update(final FragmentVersionDag fragmentVersionDag) {
        return em.merge(fragmentVersionDag);
    }

    /**
     * @see org.apromore.dao.FragmentVersionDagDao#delete(org.apromore.dao.model.FragmentVersionDag)
     *  {@inheritDoc}
     */
    @Override
    public void delete(final FragmentVersionDag fragmentVersionDag) {
        em.remove(fragmentVersionDag);
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working.
     * @param newEm the entitymanager
     */
    public void setEntityManager(final EntityManager newEm) {
        this.em = newEm;
    }

}
