package org.apromore.service.impl;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.AnnotationRepository;
import org.apromore.dao.ContentRepository;
import org.apromore.dao.FragmentVersionDagRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.ProcessBranchRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.graph.canonical.Canonical;
import org.apromore.model.ExportFormatResultType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.CanoniserService;
import org.apromore.service.ComposerService;
import org.apromore.service.DecomposerService;
import org.apromore.service.FormatService;
import org.apromore.service.FragmentService;
import org.apromore.service.LockService;
import org.apromore.service.UserService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.DecanonisedProcess;
import org.easymock.EasyMock;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

/**
 * Unit test the UserService Implementation.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ProcessServiceImplUnitTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ProcessServiceImpl service;

    private AnnotationRepository annDao;
    private ContentRepository contDao;
    private NativeRepository natDao;
    private ProcessBranchRepository branchDao;
    private ProcessRepository proDao;
    private FragmentVersionRepository fvDao;
    private FragmentVersionDagRepository fvdDao;
    private ProcessModelVersionRepository pmvDao;
    private CanonicalConverter convertor;
    private CanoniserService canSrv;
    private FragmentService fSrv;
    private LockService lSrv;
    private UserService usrSrv;
    private FormatService fmtSrv;
    private ComposerService composerSrv;
    private DecomposerService decomposerSrv;
    private UserInterfaceHelper ui;

    @Before
    public final void setUp() throws Exception {
        annDao = createMock(AnnotationRepository.class);
        contDao = createMock(ContentRepository.class);
        natDao = createMock(NativeRepository.class);
        branchDao = createMock(ProcessBranchRepository.class);
        proDao = createMock(ProcessRepository.class);
        fvDao = createMock(FragmentVersionRepository.class);
        fvdDao = createMock(FragmentVersionDagRepository.class);
        pmvDao = createMock(ProcessModelVersionRepository.class);
        usrSrv = createMock(UserService.class);
        fmtSrv = createMock(FormatService.class);
        canSrv = createMock(CanoniserService.class);
        fSrv = createMock(FragmentService.class);
        lSrv = createMock(LockService.class);
        convertor = createMock(CanonicalConverter.class);
        composerSrv = createMock(ComposerService.class);
        decomposerSrv = createMock(DecomposerService.class);
        ui = createMock(UserInterfaceHelper.class);

        service = new ProcessServiceImpl(annDao, contDao, natDao, branchDao, proDao, fvDao, fvdDao, pmvDao, convertor, canSrv, fSrv, lSrv, usrSrv, fmtSrv, composerSrv, decomposerSrv, ui);
    }


//    @Test
//    public void testExportFormatGetCanonical() throws Exception {
//        Integer processId = 123;
//        String version = "1.2";
//        String name = "processName";
//        String format = "Canonical";
//
//        List<ProcessModelVersion> pmvs = new ArrayList<ProcessModelVersion>(1);
//        ProcessModelVersion pmv = new ProcessModelVersion();
//        FragmentVersion rootFragmentVersion = new FragmentVersion();
//        rootFragmentVersion.setId(1111);
//        rootFragmentVersion.setUri("1234567890");
//        ProcessBranch branch = new ProcessBranch();
//        branch.setId(9999);
//        pmv.setId(8888);
//        pmv.setRootFragmentVersion(rootFragmentVersion);
//        pmv.setProcessBranch(branch);
//        pmv.setVersionNumber(9.1d);
//        pmvs.add(pmv);
//
//        DataSource result = new ByteArrayDataSource("<xml/>", "text/xml");
//        DecanonisedProcess dp = new DecanonisedProcess();
//        dp.setNativeFormat(result.getInputStream());
//        CanonicalProcessType cpt = new CanonicalProcessType();
//        Canonical cpf = new Canonical();
//        Set<RequestParameterType<?>> optionalProperties = new HashSet<RequestParameterType<?>>(0);
//
//        expect(pmvDao.getCurrentProcessModelVersion(name, version)).andReturn(pmvs);
//        expect(composerSrv.compose(pmv.getRootFragmentVersion())).andReturn(cpf);
//        expect(convertor.convert(cpf)).andReturn(cpt);
//        expect(canSrv.CPFtoString(cpt)).andReturn("<xml/>");
//
//        replayAll();
//
//        ExportFormatResultType data = service.exportProcess(name, processId, version, format, "", false, optionalProperties);
//
//        verifyAll();
//
//        MatcherAssert.assertThat(data, Matchers.notNullValue());
//    }

    @Test
    public void testExportFormatGetAnnotation() throws Exception {
        Integer processId = 123;
        String version = "1.2";
        String name = "processName";
        String format = "Annotations-BPMN";
        String subStr = "MN";

        Canonical cpf = new Canonical();

        Native nat = new Native();
        nat.setContent("<xml/>");

        DataSource result = new ByteArrayDataSource("<xml/>", "text/xml");

        expect(natDao.getNative(processId, version, format)).andReturn(nat);
        //expect(rSrv.getCurrentProcessModel(name, version, false)).andReturn(cpf);
        //expect(annDao.getAnnotation(processId, version, subStr)).andReturn(annotation);

        replay(natDao);

        ExportFormatResultType data = service.exportProcess(name, processId, version, format, subStr, true, new HashSet<RequestParameterType<?>>());

        verify(natDao);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    //    @Test
    //    public void getAllProcessesWithSearchCriteria() {
    //        String searchExpression = "invoicing";
    //        List<Canonical> canonicals = new ArrayList<Canonical>();
    //        List<Object[]> processes = new ArrayList<Object[]>();
    //
    //        Object[] procSummary = new Object[2];
    //        Process process = createProcess();
    //        procSummary[0] = process;
    //        procSummary[1] = "2.0";
    //        processes.add(procSummary);
    //
    //        expect(proDao.getAllProcesses(CONDITION)).andReturn(processes);
    //        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
    //        replay(proDao, canDao);
    //
    //        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);
    //
    //        verify(proDao, canDao);
    //
    //        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
    //        assertThat(processSummary.getProcessSummary().get(0).getLastVersion(), equalTo(null));
    //    }
    //
    //    @Test
    //    public void getAllProcessesCanonicals() {
    //        String searchExpression = "";
    //
    //        // For the Processes
    //        List<Object[]> processes = new ArrayList<Object[]>();
    //        Object[] procSummary = new Object[2];
    //        Process process = createProcess();
    //        procSummary[0] = process;
    //        procSummary[1] = "2.0";
    //        processes.add(procSummary);
    //
    //        // For the Canonicals
    //        List<Canonical> canonicals = new ArrayList<Canonical>();
    //
    //        expect(proDao.getAllProcesses(searchExpression)).andReturn(processes);
    //        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
    //        replay(proDao, canDao);
    //
    //        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);
    //
    //        verify(proDao, canDao);
    //
    //        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
    //        assertThat(processSummary.getProcessSummary().get(0).getLastVersion(), equalTo(null));
    //    }
    //
    //    @Test
    //    public void getAllProcessesNatives() {
    //        String searchExpression = "";
    //
    //        // For the Processes
    //        List<Object[]> processes = new ArrayList<Object[]>();
    //        Object[] procSummary = new Object[2];
    //        Process process = createProcess();
    //        procSummary[0] = process;
    //        procSummary[1] = "2.0";
    //        processes.add(procSummary);
    //
    //        // For the Canonicals
    //        List<Canonical> canonicals = new ArrayList<Canonical>();
    //        canonicals.add(createCanonical());
    //
    //        // For the Natives
    //        List<Native> natives = new ArrayList<Native>();
    //
    //        expect(proDao.getAllProcesses(searchExpression)).andReturn(processes);
    //        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
    //        expect(natDao.findNativeByCanonical(Long.valueOf(process.getProcessId()).intValue(), "version")).andReturn(natives);
    //        replay(proDao, canDao, natDao);
    //
    //        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);
    //
    //        verify(proDao, canDao, natDao);
    //
    //        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
    //        assertThat(processSummary.getProcessSummary().get(0).getName(), equalTo("name"));
    //        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getName(), equalTo("version"));
    //    }
    //
    //    @Test
    //    public void getAllProcessesAnnotations() {
    //        String searchExpression = "";
    //
    //        // For the Processes
    //        List<Object[]> processes = new ArrayList<Object[]>();
    //        Object[] procSummary = new Object[2];
    //        Process process = createProcess();
    //        procSummary[0] = process;
    //        procSummary[1] = "2.0";
    //        processes.add(procSummary);
    //
    //        // For the Canonicals
    //        List<Canonical> canonicals = new ArrayList<Canonical>();
    //        canonicals.add(createCanonical());
    //
    //        // For the Natives
    //        List<Native> natives = new ArrayList<Native>();
    //        natives.add(createNative());
    //
    //        // For the Annotations
    //        List<Annotation> annotations = new ArrayList<Annotation>();
    //
    //        expect(proDao.getAllProcesses(searchExpression)).andReturn(processes);
    //        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
    //        expect(natDao.findNativeByCanonical(Long.valueOf(process.getProcessId()).intValue(), "version")).andReturn(natives);
    //        expect(annDao.findByUri(1234)).andReturn(annotations);
    //        replay(proDao, canDao, natDao, annDao);
    //
    //        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);
    //
    //        verify(proDao, canDao, natDao, annDao);
    //
    //        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
    //        assertThat(processSummary.getProcessSummary().get(0).getName(), equalTo("name"));
    //        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getName(), equalTo("version"));
    //        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getAnnotations().get(0).getNativeType(), equalTo("nat"));
    //    }
    //
    //    @Test
    //    public void getAllProcessesCompleteData() {
    //        String searchExpression = "";
    //
    //        // For the Processes
    //        List<Object[]> processes = new ArrayList<Object[]>();
    //        Object[] procSummary = new Object[2];
    //        Process process = createProcess();
    //        procSummary[0] = process;
    //        procSummary[1] = "2.0";
    //        processes.add(procSummary);
    //
    //        // For the Canonicals
    //        List<Canonical> canonicals = new ArrayList<Canonical>();
    //        canonicals.add(createCanonical());
    //
    //        // For the Natives
    //        List<Native> natives = new ArrayList<Native>();
    //        natives.add(createNative());
    //
    //        // For the Annotations
    //        List<Annotation> annotations = new ArrayList<Annotation>();
    //        annotations.add(createAnnotation());
    //
    //        expect(proDao.getAllProcesses(searchExpression)).andReturn(processes);
    //        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
    //        expect(natDao.findNativeByCanonical(Long.valueOf(process.getProcessId()).intValue(), "version")).andReturn(natives);
    //        expect(annDao.findByUri(1234)).andReturn(annotations);
    //
    //        replayAll();
    //
    //        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);
    //
    //        verifyAll();
    //
    //        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
    //        assertThat(processSummary.getProcessSummary().get(0).getName(), equalTo("name"));
    //        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getName(), equalTo("version"));
    //        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getAnnotations().get(0).getNativeType(), equalTo("nat"));
    //        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getAnnotations().get(0).getAnnotationName().get(0), equalTo("name1"));
    //    }
    //
    //    @Test
    //    public void testReadCanonicalAnfWithAnnotation() throws Exception {
    //        Integer processId = 123;
    //        String version = "1.2";
    //        String name = "Canonical";
    //        boolean isWith = true;
    //
    //        Canonical canonical = new Canonical();
    //        canonical.setContent("<xml/>");
    //        Annotation annotation = new Annotation();
    //        annotation.setContent("<xml/>");
    //
    //        expect(canDao.getCanonical(processId, version)).andReturn(canonical);
    //        expect(annDao.getAnnotation(processId, version, name)).andReturn(annotation);
    //
    //        replayAll();
    //
    //        Format data = service.getCanonicalAnf(processId, version, isWith, name);
    //
    //        verifyAll();
    //
    //        MatcherAssert.assertThat(data, Matchers.notNullValue());
    //        MatcherAssert.assertThat(data.getCpf(), Matchers.notNullValue());
    //        MatcherAssert.assertThat(data.getAnf(), Matchers.notNullValue());
    //    }
    //
    //    @Test
    //    public void testReadCanonicalAnfWithOutAnnotation() throws Exception {
    //        Integer processId = 123;
    //        String version = "1.2";
    //        String name = "Canonical";
    //        boolean isWith = false;
    //
    //        Canonical canonical = new Canonical();
    //        canonical.setContent("<xml/>");
    //
    //        expect(canDao.getCanonical(processId, version)).andReturn(canonical);
    //
    //        replayAll();
    //
    //        Format data = service.getCanonicalAnf(processId, version, isWith, name);
    //
    //        verifyAll();
    //
    //        assertThat(data, notNullValue());
    //        assertThat(data.getCpf(), notNullValue());
    //        assertThat(data.getAnf(), nullValue());
    //    }


    //    @Test
    //    public void testImportProcess() throws Exception {
    //        String username = "bubba";
    //        String processName = "TestProcess";
    //        String cpfURI = "112321234";
    //        String version = "1.2";
    //        String natType = "XPDL 2.1";
    //        String domain = "Airport";
    //        String created = "12/12/2011";
    //        String lastUpdate = "12/12/2011";
    //
    //        DataHandler stream = new DataHandler(new ByteArrayDataSource(TestData.XPDL.getBytes(), "text/xml"));
    //        User user = new User();
    //        user.setUsername(username);
    //
    //        NativeType nativeType = new NativeType();
    //        nativeType.setNatType(natType);
    //
    //        expect(usrSrv.findUser(username)).andReturn(user);
    //        expect(fmtSrv.findNativeType(natType)).andReturn(nativeType);
    //        proDao.save((Process) anyObject());
    //        expectLastCall().atLeastOnce();
    //        canDao.save((Canonical) anyObject());
    //        expectLastCall().atLeastOnce();
    //        natDao.save((Native) anyObject());
    //        expectLastCall().atLeastOnce();
    //        annDao.save((Annotation) anyObject());
    //        expectLastCall().atLeastOnce();
    //
    //        replayAll();
    //
    //        ProcessSummaryType procSum = service.importProcess(username, processName, cpfURI, version, natType, stream, domain, "", created, lastUpdate);
    //
    //        verifyAll();
    //
    //        assertThat(procSum, notNullValue());
    //    }
    //

    //    private Process createProcess() {
    //        Process process = new Process();
    //        process.setProcessId(1234);
    //        process.setDomain("domain");
    //        process.setName("name");
    //        process.setUser(createUser());
    //        process.setNativeType(createNativeType());
    //        return process;
    //    }
    //
    //    private NativeType createNativeType() {
    //        NativeType nat = new NativeType();
    //        nat.setExtension("ext");
    //        nat.setNatType("nat");
    //        return nat;
    //    }
    //
    //    private User createUser() {
    //        User usr = new User();
    //        usr.setFirstname("first");
    //        usr.setLastname("last");
    //        usr.setEmail("fl@domain.com");
    //        usr.setUsername("user");
    //        usr.setPasswd("pass");
    //        return usr;
    //    }
    //
    //    private Canonical createCanonical() {
    //        Canonical can = new Canonical();
    //        can.setAuthor("someone");
    //        can.setContent("content");
    //        can.setDocumentation("doco");
    //        can.setVersionName("version");
    //        return can;
    //    }
    //
    //    private Native createNative() {
    //        Native nat = new Native();
    //        nat.setUri(1234);
    //        nat.setNativeType(createNativeType());
    //        return nat;
    //    }
    //
    //    private Annotation createAnnotation() {
    //        Annotation ann = new Annotation();
    //        ann.setName("name1");
    //        ann.setUri(1234);
    //        return ann;
    //    }

}
