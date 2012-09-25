package org.apromore.canoniser.yawl.yawl2cpf.reallife;

import java.io.File;

import org.apromore.canoniser.yawl.BaseYAWL2CPFTest;
import org.apromore.canoniser.yawl.utils.TestUtils;

public class OrderFulfilmentTest extends BaseYAWL2CPFTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Orderfulfillment/orderfulfillment.yawl");
    }

    @Override
    protected File getYAWLOrgDataFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Orderfulfillment/orderfulfillment.ybkp");
    }

}
