package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Issued Content Ids POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class IssuedContentIdsUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(IssuedContentIds.class);
    }

}
