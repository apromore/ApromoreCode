package org.apromore.manager.service;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isNull;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.bind.JAXBElement;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.SerializationException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.model.DeployProcessInputMsgType;
import org.apromore.model.DeployProcessOutputMsgType;
import org.apromore.model.ObjectFactory;
import org.apromore.model.PluginProperties;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.service.CanoniserService;
import org.apromore.service.DeploymentService;
import org.apromore.service.RepositoryService;
import org.apromore.service.impl.CanoniserServiceImpl;
import org.apromore.service.impl.DeploymentServiceImpl;
import org.apromore.service.impl.RepositoryServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"
})
public class DeployProcessEndpointTest {


    private ManagerPortalEndpoint endpoint;

    private RepositoryService repositoryService;
    private DeploymentService deploymentService;
    private CanoniserService canoniserService;

    @Before
    public void setUp() throws Exception {
        repositoryService = createMock(RepositoryServiceImpl.class);
        deploymentService = createMock(DeploymentServiceImpl.class);
        canoniserService = createMock(CanoniserServiceImpl.class);
        endpoint = new ManagerPortalEndpoint();
        endpoint.setRepSrv(repositoryService);
        endpoint.setDeploymentService(deploymentService);
        endpoint.setCanoniserService(canoniserService);
    }

    @Test
    public void testDeployProces() throws DeploymentException, SerializationException, LockFailedException {

        DeployProcessInputMsgType msg = new DeployProcessInputMsgType();
        JAXBElement<DeployProcessInputMsgType> request = new ObjectFactory().createDeployProcessRequest(msg );

        String branchName = "test";
        request.getValue().setBranchName(branchName);
        String processName = "test";
        request.getValue().setProcessName(processName);
        String versionName = "1.0";
        request.getValue().setVersionName(versionName);
        String nativeType = "Test 2.1";
        request.getValue().setNativeType(nativeType);
        PluginProperties pluginProperties = new PluginProperties();
        request.getValue().setDeploymentProperties(pluginProperties);

        CPF cpf = new CPF();
        expect(repositoryService.getCurrentProcessModel(processName, false)).andReturn(cpf);
        expect(canoniserService.serializeCPF(cpf)).andReturn(new CanonicalProcessType());
        expect(deploymentService.deployProcess(eq(nativeType), anyObject(CanonicalProcessType.class), isNull(AnnotationsType.class), anyObject(HashSet.class))).andReturn(new ArrayList<PluginMessage>());

        replayAll();

        JAXBElement<DeployProcessOutputMsgType> result = endpoint.deployProcess(request);

        Assert.assertNotNull(result.getValue().getResult());
        Assert.assertEquals("Result Code Doesn't Match", result.getValue().getResult().getCode().intValue(), 0);

        assertNotNull(result.getValue().getMessage());

        verifyAll();
    }

}
