package org.apromore.manager.service;

import org.apromore.manager.canoniser.ManagerCanoniserClient;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ReadDomainsInputMsgType;
import org.apromore.model.ReadDomainsOutputMsgType;
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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the Read User method on the Manager Portal Endpoint WebService.
 */
public class ReadDomainsEndpointTest {

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
        convertor = createMock(CanonicalConverterAdapter.class);
        caClient = createMock(ManagerCanoniserClient.class);

        endpoint = new ManagerPortalEndpoint(deploymentService, pluginService, fragmentSrv, canoniserService, procSrv,
                clusterService, frmSrv, domSrv, userSrv, simSrv, merSrv, sesSrv, convertor, caClient);
    }


    @Test
    public void testInvokeReadDomains() throws Exception {
        ReadDomainsInputMsgType msg = new ReadDomainsInputMsgType();
        msg.setEmpty("");
        JAXBElement<ReadDomainsInputMsgType> request = new ObjectFactory().createReadDomainsRequest(msg);

        List<String> domains = new ArrayList<String>();
        expect(domSrv.findAllDomains()).andReturn(domains);

        replay(domSrv);

        JAXBElement<ReadDomainsOutputMsgType> response = endpoint.readDomains(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getDomains());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);
        Assert.assertEquals("UserNames should be empty", response.getValue().getDomains().getDomain().size(), 0);

        verify(domSrv);
    }

}

