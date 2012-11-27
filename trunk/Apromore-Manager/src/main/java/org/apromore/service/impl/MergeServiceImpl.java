package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionMergeProcess;
import org.apromore.exception.ImportException;
import org.apromore.exception.SerializationException;
import org.apromore.graph.canonical.Canonical;
import org.apromore.model.ParameterType;
import org.apromore.model.ParametersType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionIdType;
import org.apromore.model.ProcessVersionIdsType;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.MergeService;
import org.apromore.service.ProcessService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.ToolboxData;
import org.apromore.toolbox.similaritySearch.tools.MergeProcesses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

/**
 * Implementation of the MergeService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional
public class MergeServiceImpl implements MergeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeServiceImpl.class);

    private ProcessModelVersionRepository processModelVersionRepo;
    private CanonicalConverter converter;
    private ProcessService processSrv;
    private UserInterfaceHelper ui;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param processModelVersionRepository Annotation Repository.
     * @param canonicalConverter Native Repository.
     * @param processService Native Type repository.
     * @param uiHelper the User Interface Helper.
     */
    @Inject
    public MergeServiceImpl(final ProcessModelVersionRepository processModelVersionRepository, final CanonicalConverter canonicalConverter,
            final ProcessService processService, final UserInterfaceHelper uiHelper) {
        processModelVersionRepo = processModelVersionRepository;
        converter = canonicalConverter;
        processSrv = processService;
        ui = uiHelper;
    }


    /**
     * @see org.apromore.service.MergeService#mergeProcesses(String, String, String, String, String, org.apromore.model.ParametersType, org.apromore.model.ProcessVersionIdsType)
     * {@inheritDoc}
     */
    @Override
    public ProcessSummaryType mergeProcesses(String processName, String version, String domain, String username, String algo,
            ParametersType parameters, ProcessVersionIdsType ids) throws ExceptionMergeProcess {
        List<ProcessModelVersion> models = new ArrayList<ProcessModelVersion>(0);
        for (ProcessVersionIdType cpf : ids.getProcessVersionId()) {
            models.add(processModelVersionRepo.findProcessModelVersionByBranch(cpf.getProcessId(), cpf.getVersionName()));
        }

        ProcessSummaryType pst = null;
        try {
            ToolboxData data = convertModelsToCPT(models);
            data = getParametersForMerge(data, algo, parameters);
            Canonical pg = converter.convert(performMerge(data));

            SimpleDateFormat sf = new SimpleDateFormat(Constants.DATE_FORMAT);
            String created = sf.format(new Date());
            ProcessModelVersion pmv = processSrv.addProcessModel(processName, version, username, null, null, domain, "", created, created, pg);
            pst = ui.createProcessSummary(processName, pmv.getId(), pmv.getVersionName(), version, null, domain, created, created, username);
        } catch (SerializationException se) {
            LOGGER.error("Failed to convert the models into the Canonical Format.", se);
        } catch (ImportException ie) {
            LOGGER.error("Failed Import the newly merged model.", ie);
        }

        return pst;
    }


    /* Responsible for getting all the Models and converting them to CPT internal format */
    private ToolboxData convertModelsToCPT(List<ProcessModelVersion> models) throws SerializationException {
        ToolboxData data = new ToolboxData();

        for (ProcessModelVersion pmv : models) {
            data.addModel(pmv, converter.convert(processSrv.getCanonicalFormat(pmv)));
        }

        return data;
    }


    /* Loads the Parameters used for the Merge */
    private ToolboxData getParametersForMerge(ToolboxData data, String method, ParametersType params) {
        data.setAlgorithm(method);

        for (ParameterType p : params.getParameter()) {
            if (ToolboxData.MODEL_THRESHOLD.equals(p.getName())) {
                data.setModelthreshold(p.getValue());
            } else if (ToolboxData.LABEL_THRESHOLD.equals(p.getName())) {
                data.setLabelthreshold(p.getValue());
            } else if (ToolboxData.CONTEXT_THRESHOLD.equals(p.getName())) {
                data.setContextthreshold(p.getValue());
            } else if (ToolboxData.SKIP_N_WEIGHT.equals(p.getName())) {
                data.setSkipnweight(p.getValue());
            } else if (ToolboxData.SUB_N_WEIGHT.equals(p.getName())) {
                data.setSubnweight(p.getValue());
            } else if (ToolboxData.SKIP_E_WEIGHT.equals(p.getName())) {
                data.setSkipeweight(p.getValue());
            } else if (ToolboxData.REMOVE_ENT.equals(p.getName())) {
                data.setRemoveEntanglements(p.getValue() == 1);
            }
        }

        return data;
    }


    /* Does the merge. */
    private CanonicalProcessType performMerge(ToolboxData data) {
        ArrayList<CanonicalProcessType> models = new ArrayList<CanonicalProcessType>(data.getModel().values());
        return MergeProcesses.mergeProcesses(models, data.isRemoveEntanglements(), data.getAlgorithm(),
                data.getModelthreshold(), data.getLabelthreshold(), data.getContextthreshold(), data.getSkipnweight(),
                data.getSubnweight(), data.getSkipeweight());
    }
}
