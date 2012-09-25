package org.apromore.canoniser.yawl.yawl2cpf.patterns.controlflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternTest;
import org.apromore.cpf.DirectionType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.junit.Test;

public class WSInvokerTest extends BasePatternTest {

    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Others/WSInvokerTest.yawl");
    }

    @Test
    public void testWSInvoker() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);

        final TaskType nodeA = (TaskType) checkNode(rootNet, "A", TaskType.class, 1, 1);
        assertNotNull(nodeA);

        final TypeAttribute yawlService = findExtensionByName(nodeA, "http://www.yawlfoundation.org/yawlschema/yawlService");
        assertNotNull(yawlService);
        assertNotNull(yawlService.getAny());

        final String incMessageID = getOutgoingEdges(rootNet, nodeA.getId()).get(0).getTargetId();
        assertNotNull(incMessageID);
        final MessageType incMessage = (MessageType) checkNodeById(rootNet, incMessageID, MessageType.class, 1, 1);
        assertNotNull(incMessage);
        assertEquals(DirectionType.INCOMING, incMessage.getDirection());

        final String outMessageID = getIncomingEdges(rootNet, nodeA.getId()).get(0).getSourceId();
        assertNotNull(outMessageID);
        final MessageType outMessage = (MessageType) checkNodeById(rootNet, outMessageID, MessageType.class, 1, 1);
        assertNotNull(outMessage);
        assertEquals(DirectionType.OUTGOING, outMessage.getDirection());
    }

}
