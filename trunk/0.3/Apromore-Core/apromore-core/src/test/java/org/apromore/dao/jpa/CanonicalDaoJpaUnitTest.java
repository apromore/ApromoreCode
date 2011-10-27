package org.apromore.dao.jpa;

import org.apromore.dao.model.Canonical;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.User;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the Canonical DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class CanonicalDaoJpaUnitTest {

    private CanonicalDaoJpa dao;
    private EntityManager manager;

    @Before
    public final void setUp() throws Exception {
        dao = new CanonicalDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManagerFactory(factory);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(CanonicalDaoJpa.class);
    }

    @Test
    public final void testGetAllProcesses() {
        int processId = 123;
        List<Canonical> cans = new ArrayList<Canonical>();
        cans.add(createCanonical());

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(Canonical.FIND_BY_PROCESS_ID)).andReturn(query);
        expect(query.setParameter("processId", processId)).andReturn(query);
        expect(query.getResultList()).andReturn(cans);

        replay(manager, query);

        List<Canonical> canonicals = dao.findByProcessId(processId);

        verify(manager, query);

        assertThat(cans.size(), equalTo(canonicals.size()));
    }

    @Test
    public final void testGetAllProcessesNonFound() {
        int processId = 123;
        List<Canonical> cans = new ArrayList<Canonical>();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(Canonical.FIND_BY_PROCESS_ID)).andReturn(query);
        expect(query.setParameter("processId", processId)).andReturn(query);
        expect(query.getResultList()).andReturn(cans);

        replay(manager, query);

        List<Canonical> canonicals = dao.findByProcessId(processId);

        verify(manager, query);

        assertThat(canonicals, equalTo(null));
    }




    @Test
    public final void testSaveProcess() {
        Canonical can = createCanonical();
        manager.persist(can);
        replay(manager);
        dao.save(can);
        verify(manager);
    }

    @Test
    public final void testUpdateUser() {
        Canonical can = createCanonical();
        expect(manager.merge(can)).andReturn(can);
        replay(manager);
        dao.update(can);
        verify(manager);
    }


    @Test
    public final void testDeleteProcess() {
        Canonical can = createCanonical();
        manager.remove(can);
        replay(manager);
        dao.delete(can);
        verify(manager);
    }


    private Canonical createCanonical() {
        Canonical c = new Canonical();

        c.setAuthor("bob");
        c.setContent("12345");
        c.setDocumentation("doc");
        c.setCreationDate("new date");
        c.setLastUpdate("new date");
        c.setRanking("2.0");
        c.setUri("12sad223123");

        return c;
    }
}
