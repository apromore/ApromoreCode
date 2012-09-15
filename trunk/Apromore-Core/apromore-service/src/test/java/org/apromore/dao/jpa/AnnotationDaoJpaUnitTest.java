package org.apromore.dao.jpa;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.transaction.annotation.Transactional;

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
 * Test the Annotation DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
@Transactional
public class AnnotationDaoJpaUnitTest {

    private AnnotationDaoJpa dao;
    private EntityManager manager;


    @Before
    public final void setUp() throws Exception {
        dao = new AnnotationDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManager(manager);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(AnnotationDaoJpa.class, "em");
    }


    @Test
    public final void testGetAllAnnotations() {
        int uri = 123;
        List<Annotation> anns = new ArrayList<Annotation>();
        anns.add(createAnnotation());

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_ANNOTATION_BY_URI)).andReturn(query);
        expect(query.setParameter("uri", uri)).andReturn(query);
        expect(query.getResultList()).andReturn(anns);

        replay(manager, query);

        List<Annotation> annotations = dao.findByUri(uri);

        verify(manager, query);

        assertThat(anns.size(), equalTo(annotations.size()));
    }

    @Test
    public final void testGetAllAnnotationsNonFound() {
        int uri = 123;
        List<Annotation> anns = new ArrayList<Annotation>();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_ANNOTATION_BY_URI)).andReturn(query);
        expect(query.setParameter("uri", uri)).andReturn(query);
        expect(query.getResultList()).andReturn(anns);

        replay(manager, query);

        List<Annotation> annotations = dao.findByUri(uri);

        verify(manager, query);

        assertThat(annotations, equalTo(anns));
    }

    @Test
    public final void testGetAnnotation() throws Exception {
        Integer processId = 123;
        String versionName = "123";
        String name = "annName";

        Annotation annotation = new Annotation();
        annotation.setContent("<XML/>");

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_ANNOTATION)).andReturn(query);
        expect(query.setParameter("processId", processId)).andReturn(query);
        expect(query.setParameter("versionName", versionName)).andReturn(query);
        expect(query.setParameter("name", name)).andReturn(query);
        expect(query.getSingleResult()).andReturn(annotation);

        replay(manager, query);

        Annotation annotationObj = dao.getAnnotation(processId, versionName, name);

        verify(manager, query);

        MatcherAssert.assertThat(annotation, Matchers.equalTo(annotationObj));
    }

    @Test
    public final void testGetAnnotationNotFound() throws Exception {
        Integer processId = 123;
        String versionName = "123";
        String name = "annName";

        String annotationXML = null;

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_ANNOTATION)).andReturn(query);
        expect(query.setParameter("processId", processId)).andReturn(query);
        expect(query.setParameter("versionName", versionName)).andReturn(query);
        expect(query.setParameter("name", name)).andReturn(query);
        expect(query.getSingleResult()).andReturn(annotationXML);

        replay(manager, query);

        dao.getAnnotation(processId, versionName, name);

        verify(manager, query);
    }


    @Test
    public final void testSaveAnnotation() {
        Annotation ann = createAnnotation();
        manager.persist(ann);
        replay(manager);
        dao.save(ann);
        verify(manager);
    }

    @Test
    public final void testUpdateAnnotation() {
        Annotation ann = createAnnotation();
        expect(manager.merge(ann)).andReturn(ann);
        replay(manager);
        dao.update(ann);
        verify(manager);
    }


    @Test
    public final void testDeleteAnnotation() {
        Annotation ann = createAnnotation();
        manager.remove(ann);
        replay(manager);
        dao.delete(ann);
        verify(manager);
    }


    private Annotation createAnnotation() {
        Annotation a = new Annotation();

        a.setContent("12345");
        a.setName("newCanonical");
        a.setId(12425535);
        a.setProcessModelVersion(new ProcessModelVersion());

        return a;
    }
}
