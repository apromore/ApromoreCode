package org.apromore.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBException;

import org.apromore.common.Constants;
import org.apromore.dao.AnnotationDao;
import org.apromore.dao.NativeDao;
import org.apromore.dao.ProcessBranchDao;
import org.apromore.dao.ProcessDao;
import org.apromore.dao.ProcessModelVersionDao;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.exception.CanoniserException;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.exception.UpdateProcessException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.service.CanoniserService;
import org.apromore.service.FormatService;
import org.apromore.service.ProcessService;
import org.apromore.service.RepositoryService;
import org.apromore.service.UserService;
import org.apromore.service.helper.UIHelper;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.search.SearchExpressionBuilder;
import org.apromore.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wfmc._2008.xpdl2.PackageType;
import org.xml.sax.SAXException;

/**
 * Implementation of the UserService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("ProcessService")
@Transactional(propagation = Propagation.REQUIRED)
public class ProcessServiceImpl implements ProcessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessServiceImpl.class);

    @Autowired @Qualifier("AnnotationDao")
    private AnnotationDao annDao;
    @Autowired @Qualifier("NativeDao")
    private NativeDao natDao;
    @Autowired @Qualifier("ProcessDao")
    private ProcessDao proDao;
    @Autowired @Qualifier("ProcessModelVersionDao")
    private ProcessModelVersionDao pmvDao;
    @Autowired @Qualifier("ProcessBranchDao")
    private ProcessBranchDao pbDao;

    @Autowired @Qualifier("CanoniserService")
    private CanoniserService canSrv;
    @Autowired @Qualifier("UserService")
    private UserService usrSrv;
    @Autowired @Qualifier("FormatService")
    private FormatService fmtSrv;
    @Autowired @Qualifier("RepositoryService")
    private RepositoryService rSrv;
    @Autowired @Qualifier("UIHelper")
    private UIHelper uiSrv;


    /**
     * @see org.apromore.service.ProcessService#readProcessSummaries(String)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessSummariesType readProcessSummaries(String searchExpression) {
        ProcessSummariesType processSummaries = null;

        try {
            // Firstly, do we need to use the searchExpression
            SearchExpressionBuilder seb = new SearchExpressionBuilder();
            String conditions = seb.buildSearchConditions(searchExpression);
            LOGGER.debug("Search Expression Builder output: " + conditions);

            // Now... Build the Object tree from this list of processes.
            processSummaries = uiSrv.buildProcessSummaryList(conditions, null);
        } catch (UnsupportedEncodingException usee) {
            LOGGER.error("Failed to get Process Summaries: " + usee.toString());
        }

        return processSummaries;
    }


    /**
     * @see org.apromore.service.ProcessService#importProcess(String, String, String, String, String, DataHandler, String, String, String, String)
     * {@inheritDoc}
     */
    @Override
    public ProcessSummaryType importProcess(String username, String processName, String cpfURI, String version, String natType,
            DataHandler cpf, String domain, String documentation, String created, String lastUpdate) throws ImportException {
        LOGGER.info("Executing operation canoniseProcess");
        ProcessSummaryType pro;

        try {
            CanonisedProcess cp = canSrv.canonise(natType, cpfURI, cpf.getInputStream());

            User user = usrSrv.findUser(username);
            NativeType nativeType = fmtSrv.findNativeType(natType);
            CPF pg = canSrv.deserializeCPF(cp.getCpt());

            ProcessModelVersion pmv = rSrv.addProcessModel(processName, version, user.getUsername(), cpfURI, nativeType.getNatType(),
                    domain, documentation, created, lastUpdate, pg);
            fmtSrv.storeNative(processName, version, pmv, cpf.getInputStream(), created, lastUpdate, user, nativeType, cp);
            pro = uiSrv.createProcessSummary(processName, pmv.getProcessModelVersionId(), version, nativeType.getNatType(), domain,
                    created, lastUpdate, user.getUsername());
        } catch (Exception e) {
            LOGGER.error("Canonisation Process Failed: " + e.toString());
            throw new ImportException(e);
        }

        return pro;
    }

    /**
     * @see org.apromore.service.ProcessService#exportFormat(String, Integer, String, String, String, boolean)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public DataSource exportFormat(final String name, final Integer processId, final String version, final String format,
            final String annName, boolean withAnn) throws ExportFormatException {
        DataSource ds;
        try {
            CPF cpf = rSrv.getCurrentProcessModel(name, version, false);

            if ((withAnn && format.startsWith(Constants.INITIAL_ANNOTATION)) || format.startsWith(Constants.ANNOTATIONS)) {
                ds = new ByteArrayDataSource(natDao.getNative(processId, version, format).getContent(), "text/xml");
            } else if (format.equals(Constants.CANONICAL)) {
                ds = new ByteArrayDataSource(canSrv.CPFtoString(canSrv.serializeCPF(cpf)), "text/xml");
            } else {
                if (withAnn) {
                    String annotation = annDao.getAnnotation(processId, version, annName).getContent();
                    DataSource anf = new ByteArrayDataSource(annotation, "text/xml");
                    ds = canSrv.deCanonise(processId, version, format, canSrv.serializeCPF(cpf), anf);
                } else {
                    ds = canSrv.deCanonise(processId, version, format, canSrv.serializeCPF(cpf), null);
                }
            }
        } catch (Exception e) {
            throw new ExportFormatException(e.getMessage(), e.getCause());
        }
        return ds;
    }


    /**
     * @see org.apromore.service.ProcessService#updateProcessMetaData(Integer, String, String, String, String, String, String)
     * {@inheritDoc}
     */
    @Override
    public void updateProcessMetaData(final Integer processId, final String processName, final String domain, final String username,
            final String preVersion, final String newVersion, final String ranking) throws UpdateProcessException {
        LOGGER.info("Executing operation update process meta data.");
        try {
            Process process = proDao.findProcess(processId.toString());
            process.setDomain(domain);
            process.setName(processName);
            process.setUser(usrSrv.findUser(username));

            ProcessModelVersion processModelVersion = pmvDao.getCurrentProcessModelVersion(processId, preVersion);
            processModelVersion.setVersionName(newVersion);

            ProcessBranch branch = processModelVersion.getProcessBranch();
            branch.setRanking(ranking);

            updateNativeRecords(processModelVersion.getNatives(), processName, username, newVersion);

            proDao.update(process);
            pmvDao.update(processModelVersion);
            pbDao.update(branch);
        } catch (Exception e) {
            throw new UpdateProcessException(e.getMessage(), e.getCause());
        }
    }


    /**
     * @see org.apromore.service.ProcessService#addProcessModelVersion(ProcessBranch, String, int, String, int, int)
     * {@inheritDoc}
     */
    @Override
    public ProcessModelVersion addProcessModelVersion(ProcessBranch branch, String rootFragmentVersionId, int versionNumber,
                String versionName, int numVertices, int numEdges) throws ExceptionDao {
        ProcessModelVersion pmv = new ProcessModelVersion();

        pmv.setProcessBranch(branch);
        pmv.setRootFragmentVersionId(rootFragmentVersionId);
        pmv.setVersionNumber(versionNumber);
        pmv.setVersionName(versionName);
        pmv.setNumVertices(numVertices);
        pmv.setNumEdges(numEdges);

        pmvDao.save(pmv);
        return pmv;
    }



    /* Update a list of native process models with this new meta data, */
    private void updateNativeRecords(final Set<Native> natives, final String processName, final String username, final String version) {
        try {
            for (Native n : natives) {
                String natType = n.getNativeType().getNatType();
                InputStream inStr = new ByteArrayInputStream(n.getContent().getBytes());
                CanonisedProcess cp = canSrv.canonise(natType, n.getUri().toString(), inStr);

                if (natType.compareTo(Constants.XPDL_2_1) == 0) {
                    PackageType pakType = StreamUtil.unmarshallXPDL(inStr);
                    StreamUtil.copyParam2XPDL(pakType, processName, version, username, null, null);
                    n.setContent(StreamUtil.marshallXPDL(pakType));
                }
            }
        } catch (CanoniserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }







    /**
     * Set the Annotation DAO object for this class. Mainly for spring tests.
     * @param annDAOJpa the Annotation Dao.
     */
    public void setAnnotationDao(AnnotationDao annDAOJpa) {
        annDao = annDAOJpa;
    }

    /**
     * Set the Process DAO object for this class. Mainly for spring tests.
     * @param proDAOJpa the process
     */
    public void setProcessDao(ProcessDao proDAOJpa) {
        proDao = proDAOJpa;
    }

    /**
     * Set the Process Model Version DAO object for this class. Mainly for spring tests.
     * @param pmvDAOJpa the process model version
     */
    public void setProcessModelVersionDao(ProcessModelVersionDao pmvDAOJpa) {
        pmvDao = pmvDAOJpa;
    }

    /**
     * Set the Process branch Version DAO object for this class. Mainly for spring tests.
     * @param pbDAOJpa the process branch
     */
    public void setProcessBranchDao(ProcessBranchDao pbDAOJpa) {
        pbDao = pbDAOJpa;
    }


    /**
     * Set the Native DAO object for this class. Mainly for spring tests.
     * @param natDAOJpa the Native Dao.
     */
    public void setNativeDao(NativeDao natDAOJpa) {
        natDao = natDAOJpa;
    }


    /**
     * Set the Canoniser Service for this class. Mainly for spring tests.
     * @param canSrv the service
     */
    public void setCanoniserService(CanoniserService canSrv) {
        this.canSrv = canSrv;
    }

    /**
     * Set the User Service for this class. Mainly for spring tests.
     * @param usrSrv the service
     */
    public void setUserService(UserService usrSrv) {
        this.usrSrv = usrSrv;
    }

    /**
     * Set the Format Service for this class. Mainly for spring tests.
     * @param fmtSrv the service
     */
    public void setFormatService(FormatService fmtSrv) {
        this.fmtSrv = fmtSrv;
    }

    /**
     * Set the Repository Service for this class. Mainly for spring tests.
     * @param rSrv the service
     */
    public void setRepositoryService(RepositoryService rSrv) {
        this.rSrv = rSrv;
    }

    /**
     * Set the Repository Service for this class. Mainly for spring tests.
     * @param newUISrv the service
     */
    public void setUIHelperService(UIHelper newUISrv) {
        this.uiSrv = newUISrv;
    }
}
