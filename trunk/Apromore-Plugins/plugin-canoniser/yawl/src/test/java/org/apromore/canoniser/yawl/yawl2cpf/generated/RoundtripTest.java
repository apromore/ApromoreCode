package org.apromore.canoniser.yawl.yawl2cpf.generated;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;

import org.apromore.canoniser.yawl.BaseYAWL2CPFTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RoundtripTest extends BaseYAWL2CPFTest {
    
    public static String MODEL_DIR = TestUtils.TEST_RESOURCES_DIRECTORY + "/YAWL/Roundtrip";

    @Parameters
    public static Collection<Object[]> getFiles() {
        final Collection<Object[]> params = new ArrayList<Object[]>();
        for (final File f : getYAWLFiles()) {
            final Object[] arr = new Object[] { f };
            params.add(arr);
        }
        return params;

    }

    private static File[] getYAWLFiles() {
        return new File(MODEL_DIR).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".yawl");
            }
        });
    }

    private final File yawlFile;

    public RoundtripTest(final File yawlFile) {
        super();
        this.yawlFile = yawlFile;
    }

    /* (non-Javadoc)
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return yawlFile;
    }

}
