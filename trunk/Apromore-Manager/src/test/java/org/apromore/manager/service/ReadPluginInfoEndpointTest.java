package org.apromore.manager.service;

import org.apromore.manager.canoniser.ManagerCanoniserClient;
import org.apromore.model.ObjectFactory;
import org.apromore.model.PluginInfo;
import org.apromore.model.PluginInfoResult;
import org.apromore.model.ReadPluginInfoInputMsgType;
import org.apromore.model.ReadPluginInfoOutputMsgType;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.impl.DefaultPlugin;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.CanoniserService;
import org.apromore.service.ClusterService;
import org.apromore.service.DeploymentService;
import org.apromore.service.DomainService;
import org.apromore.service.FormatService;
import org.apromore.service.FragmentService;
import org.apromore.service.MergeService;
import org.apromore.service.PluginService;
import org.apromore.service.ProcessService;
import org.apromore.service.SessionService;
import org.apromore.service.SimilarityService;
import org.apromore.service.UserService;
import org.apromore.service.helper.UIHelper;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.impl.CanonicalConverterAdapter;
import org.apromore.service.impl.CanoniserServiceImpl;
import org.apromore.service.impl.ClusterServiceImpl;
import org.apromore.service.impl.DeploymentServiceImpl;
import org.apromore.service.impl.DomainServiceImpl;
import org.apromore.service.impl.FormatServiceImpl;
import org.apromore.service.impl.FragmentServiceImpl;
import org.apromore.service.impl.MergeServiceImpl;
import org.apromore.service.impl.PluginServiceImpl;
import org.apromore.service.impl.ProcessServiceImpl;
import org.apromore.service.impl.SessionServiceImpl;
import org.apromore.service.impl.SimilarityServiceImpl;
import org.apromore.service.impl.UserServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBElement;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

public class ReadPluginInfoEndpointTest {

    private ManagerPortalEndpoint endpoint;

    private DeploymentService deploymentService;
    private CanoniserService canoniserService;
    private PluginService pluginService;
    private FragmentService fragmentSrv;
    private ProcessService procSrv;
    private ClusterService clusterService;
    private FormatService frmSrv;
    private DomainService domSrv;
    private UserService userSrv;
    private SimilarityService simSrv;
    private MergeService merSrv;
    private SessionService sesSrv;
    private UserInterfaceHelper uiHelper;
    private CanonicalConverter convertor;
    private ManagerCanoniserClient caClient;

    @Before
    public void setUp() throws Exception {
        deploymentService = createMock(DeploymentServiceImpl.class);
        pluginService = createMock(PluginServiceImpl.class);
        fragmentSrv = createMock(FragmentServiceImpl.class);
        canoniserService = createMock(CanoniserServiceImpl.class);
        procSrv = createMock(ProcessServiceImpl.class);
        clusterService = createMock(ClusterServiceImpl.class);
        frmSrv = createMock(FormatServiceImpl.class);
        domSrv = createMock(DomainServiceImpl.class);
        userSrv = createMock(UserServiceImpl.class);
        simSrv = createMock(SimilarityServiceImpl.class);
        merSrv = createMock(MergeServiceImpl.class);
        sesSrv = createMock(SessionServiceImpl.class);
        uiHelper = createMock(UIHelper.class);
        convertor = createMock(CanonicalConverterAdapter.class);
        caClient = createMock(ManagerCanoniserClient.class);

        endpoint = new ManagerPortalEndpoint(deploymentService, pluginService, fragmentSrv, canoniserService, procSrv,
                clusterService, frmSrv, domSrv, userSrv, simSrv, merSrv, sesSrv, uiHelper, convertor, caClient);
    }


    @Test
    public void testReadPluginInfo() throws PluginNotFoundException {
        ReadPluginInfoInputMsgType msg = new ReadPluginInfoInputMsgType();
        msg.setPluginName("Test PLugin");
        msg.setPluginName("1.0");
        JAXBElement<ReadPluginInfoInputMsgType> request = new ObjectFactory().createReadPluginInfoRequest(msg);

        DefaultPlugin mockPlugin = createMock(DefaultPlugin.class);
        expect(mockPlugin.getName()).andReturn("Test Plugin");
        expect(mockPlugin.getVersion()).andReturn("1.0");
        expect(mockPlugin.getAuthor()).andReturn("Scott");
        expect(mockPlugin.getDescription()).andReturn("Beam me up");
        expect(mockPlugin.getType()).andReturn("Starship");
        expect(mockPlugin.getEMail()).andReturn("scott@enterprise.com");
        replay(mockPlugin);

        expect(pluginService.findByNameAndVersion(msg.getPluginName(), msg.getPluginVersion())).andReturn(mockPlugin);

        replay(pluginService);

        JAXBElement<ReadPluginInfoOutputMsgType> response = endpoint.readPluginInfo(request);
        PluginInfoResult infoResult = response.getValue().getPluginInfoResult();
        Assert.assertNotNull(infoResult);
        PluginInfo info = infoResult.getPluginInfo();
        Assert.assertNotNull(info);
        Assert.assertNull(infoResult.getMandatoryParameters());
        Assert.assertNull(infoResult.getOptionalParameters());
        Assert.assertEquals("Plugin name does not match", info.getName(), "Test Plugin");
        Assert.assertEquals("Plugin version does not match", info.getVersion(), "1.0");
        Assert.assertEquals("Plugin author does not match", info.getAuthor(), "Scott");
        Assert.assertEquals("Plugin descr does not match", info.getDescription(), "Beam me up");
        Assert.assertEquals("Plugin type does not match", info.getType(), "Starship");
        Assert.assertEquals("Plugin email does not match", info.getEmail(), "scott@enterprise.com");

        verify(pluginService);
        verify(mockPlugin);
    }

}
