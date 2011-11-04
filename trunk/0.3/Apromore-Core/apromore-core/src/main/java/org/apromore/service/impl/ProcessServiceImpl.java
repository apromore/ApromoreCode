package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.AnnotationDao;
import org.apromore.dao.CanonicalDao;
import org.apromore.dao.NativeDao;
import org.apromore.dao.ProcessDao;
import org.apromore.dao.jpa.AnnotationDaoJpa;
import org.apromore.dao.jpa.CanonicalDaoJpa;
import org.apromore.dao.jpa.NativeDaoJpa;
import org.apromore.dao.jpa.ProcessDaoJpa;
import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.Canonical;
import org.apromore.service.model.Format;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.Process;
import org.apromore.exception.AnnotationNotFoundException;
import org.apromore.exception.CanonicalFormatNotFoundException;
import org.apromore.exception.ExportFormatException;
import org.apromore.model.AnnotationsType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.service.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.List;

/**
 * Implementation of the UserService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class ProcessServiceImpl implements ProcessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessServiceImpl.class);

    @Autowired
    private ProcessDao prsDao;
    @Autowired
    private CanonicalDao canDao;
    @Autowired
    private NativeDao natDao;
    @Autowired
    private AnnotationDao annDao;


    /**
     * @see org.apromore.service.ProcessService#readProcessSummaries(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessSummariesType readProcessSummaries(String searchExpression) {
        ProcessSummariesType processSummaries = new ProcessSummariesType();

        // Firstly, do we need to use the searchExpression

        // Now... Build the Object tree from this list of processes.
        buildProcessSummaryList(processSummaries);

        return processSummaries;
    }

    /**
     * @see org.apromore.service.ProcessService#exportFormat(long, String, String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public DataSource exportFormat(final long processId, final String version, final String format) throws ExportFormatException {
        String read;
        DataSource dataSource;
        try {
            if (Constants.CANONICAL.compareTo(format) == 0) {
                read = canDao.getCanonical(processId, version);
            } else if (format.startsWith(Constants.ANNOTATIONS)) {
                String type = format.substring(Constants.ANNOTATIONS.length() + 3, format.length());
                read = annDao.getAnnotation(processId, version, type);
            } else {
                read = natDao.getNative(processId, version, format);
            }
            dataSource = new ByteArrayDataSource(read, "text/xml");
        } catch (Exception e) {
            throw new ExportFormatException(e.getMessage(), e.getCause());
        }
        return dataSource;
    }

    /**
     * Returns the Canonical format as XML.
     * @see org.apromore.service.ProcessService#getCanonicalAnf(long, String, boolean, String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Format getCanonicalAnf(final long processId, final String version, final boolean withAnn, final String annName)
            throws ExportFormatException {
        Format result = new Format();
        try {

            String annotation = "";
            String canonical = canDao.getCanonical(processId, version);
            if (withAnn) {
                annotation = annDao.getAnnotation(processId, version, annName);
                result.setAnf(new ByteArrayDataSource(annotation, "text/xml"));
            }
            result.setCpf(new ByteArrayDataSource(canonical, "text/xml"));

        } catch (CanonicalFormatNotFoundException cfnfe) {
            throw new ExportFormatException(cfnfe.getMessage(), cfnfe.getCause());
        } catch (AnnotationNotFoundException afnfe) {
            throw new ExportFormatException(afnfe.getMessage(), afnfe.getCause());
        } catch (IOException e) {
            throw new ExportFormatException(e.getMessage(), e.getCause());
        }
        return result;
    }


    /*
     * Builds the list of process Summaries and kicks off the versions and annotations.
     */
    private void buildProcessSummaryList(ProcessSummariesType processSummaries) {
        Process process;
        ProcessSummaryType processSummary;
        List<Object[]> processes = prsDao.getAllProcesses();

        for (Object[] proc : processes) {
            process = (Process) proc[0];
            processSummary = new ProcessSummaryType();

            processSummary.setId(Long.valueOf(process.getProcessId()).intValue());
            processSummary.setName(process.getName());
            processSummary.setDomain(process.getDomain());
            processSummary.setRanking(proc[1].toString());
            if (process.getNativeType() != null) {
                processSummary.setOriginalNativeType(process.getNativeType().getNatType());
            }
            if (process.getUser() != null) {
                processSummary.setOwner(process.getUser().getUsername());
            }
            buildVersionSummaryTypeList(processSummary);

            processSummaries.getProcessSummary().add(processSummary);
        }
    }

    /*
     * Builds the list of version Summaries for a process.
     */
    private void buildVersionSummaryTypeList(ProcessSummaryType processSummary) {
        VersionSummaryType versionSummary;
        List<Canonical> canonicals = canDao.findByProcessId((long) processSummary.getId());

        for (Canonical canonical : canonicals) {
            versionSummary = new VersionSummaryType();

            versionSummary.setName(canonical.getVersionName());
            versionSummary.setCreationDate(canonical.getCreationDate());
            versionSummary.setLastUpdate(canonical.getLastUpdate());
            versionSummary.setRanking(canonical.getRanking());
            buildNativeSummaryList((long) processSummary.getId(), versionSummary);

            processSummary.getVersionSummaries().add(versionSummary);
            processSummary.setLastVersion(versionSummary.getName());
        }
    }

    /**
     * Builds the list of Native Summaries for a version summary.
     */
    private void buildNativeSummaryList(long id, VersionSummaryType versionSummary) {
        AnnotationsType annotation;
        List<Native> natives = natDao.findNativeByCanonical(id, versionSummary.getName());

        for (Native nat : natives) {
            annotation = new AnnotationsType();

            if (nat.getNativeType() != null) {
                annotation.setNativeType(nat.getNativeType().getNatType());
            }
            buildAnnotationNames(nat, annotation);

            versionSummary.getAnnotations().add(annotation);
        }
    }

    /**
     * Populate the Annotation names.
     */
    private void buildAnnotationNames(Native nat, AnnotationsType annotation) {
        List<Annotation> anns = annDao.findByUri(nat.getUri());
        for (Annotation ann : anns) {
            annotation.getAnnotationName().add(ann.getName());
        }
    }




    /**
     * Set the Process DAO object for this class. Mainly for spring tests.
     * @param prsDAOJpa the process Dao.
     */
    public void setProcessDao(ProcessDaoJpa prsDAOJpa) {
        prsDao = prsDAOJpa;
    }

    /**
     * Set the Canonical DAO object for this class. Mainly for spring tests.
     * @param canDAOJpa the Canonical Dao.
     */
    public void setCanonicalDao(CanonicalDaoJpa canDAOJpa) {
        canDao = canDAOJpa;
    }

    /**
     * Set the Native DAO object for this class. Mainly for spring tests.
     * @param natDAOJpa the Native Dao.
     */
    public void setNativeDao(NativeDaoJpa natDAOJpa) {
        natDao = natDAOJpa;
    }

    /**
     * Set the Annotation DAO object for this class. Mainly for spring tests.
     * @param annDAOJpa the Annotation Dao.
     */
    public void setAnnotationDao(AnnotationDaoJpa annDAOJpa) {
        annDao = annDAOJpa;
    }
}
