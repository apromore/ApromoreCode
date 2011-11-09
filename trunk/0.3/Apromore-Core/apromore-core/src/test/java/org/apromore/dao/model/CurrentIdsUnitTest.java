package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Current Ids POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CurrentIdsUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(CurrentIds.class);
    }

}
