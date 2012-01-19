package org.apromore.manager.service;

import de.epml.TypeEPML;
import org.apromore.common.Constants;
import org.apromore.exception.ExceptionCanoniseVersion;
import org.apromore.exception.ExceptionVersion;
import org.apromore.exception.ExportFormatException;
import org.apromore.manager.canoniser.ManagerCanoniserClient;
import org.apromore.manager.da.ManagerDataAccessClient;
import org.apromore.manager.toolbox.ManagerToolboxClient;
import org.apromore.mapper.DomainMapper;
import org.apromore.mapper.NativeTypeMapper;
import org.apromore.mapper.UserMapper;
import org.apromore.model.*;
import org.apromore.model.ObjectFactory;
import org.apromore.service.*;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.Format;
import org.apromore.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.wfmc._2008.xpdl2.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * The WebService Endpoint Used by the Portal.
 *
 * This is the only web service available in this application.
 */
@Endpoint
public class ManagerPortalEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerPortalEndpoint.class.getName());

    private static final String NAMESPACE = "urn:qut-edu-au:schema:apromore:manager";

    @Autowired
    private CanoniserService canSrv;
    @Autowired
    private ProcessService procSrv;
    @Autowired
    private FormatService frmSrv;
    @Autowired
    private DomainService domSrv;
    @Autowired
    private UserService userSrv;

    @Autowired
    private ManagerDataAccessClient daClient;
    @Autowired
    private ManagerCanoniserClient caClient;
    @Autowired
    private ManagerToolboxClient tbClient;


    @PayloadRoot(namespace = NAMESPACE, localPart = "EditProcessDataRequest")
    @ResponsePayload
    public JAXBElement<EditProcessDataOutputMsgType> editProcessData(@RequestPayload final JAXBElement<EditProcessDataInputMsgType> req) {
        LOGGER.info("Executing operation editDataProcess");
        EditProcessDataInputMsgType payload = req.getValue();
        EditProcessDataOutputMsgType res = new EditProcessDataOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            Integer processId = payload.getId();
            String processName = payload.getProcessName();
            String domain = payload.getDomain();
            String username = payload.getOwner();
            String preVersion = payload.getPreName();
            String newVersion = payload.getNewName();
            String ranking = payload.getRanking();
            daClient.EditProcessData(processId, processName, domain, username, preVersion, newVersion, ranking);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createEditProcessDataResponse(res);
    }

    @PayloadRoot(localPart = "MergeProcessesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<MergeProcessesOutputMsgType> mergeProcesses(@RequestPayload final JAXBElement<MergeProcessesInputMsgType> req) {
        LOGGER.info("Executing operation mergeProcesses");
        MergeProcessesInputMsgType payload = req.getValue();
        MergeProcessesOutputMsgType res = new MergeProcessesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            // Build data to send to toolbox
            String algo = payload.getAlgorithm();
            String processName = payload.getProcessName();
            String version = payload.getVersionName();
            String domain = payload.getDomain();
            Integer processId = payload.getProcessId();
            String username = payload.getUsername();
            ParametersType parameters = new ParametersType();
            for (ParameterType p : payload.getParameters().getParameter()) {
                ParameterType param = new ParameterType();
                param.setName(p.getName());
                param.setValue(p.getValue());
                parameters.getParameter().add(param);
            }
            // processes
            ProcessVersionIdsType ids = new ProcessVersionIdsType();
            for (ProcessVersionIdType t : payload.getProcessVersionIds().getProcessVersionId()) {
                ProcessVersionIdType id = new ProcessVersionIdType();
                id.setProcessId(t.getProcessId());
                id.setVersionName(t.getVersionName());
                ids.getProcessVersionId().add(id);
            }
            ProcessSummaryType respFromToolbox = tbClient.MergeProcesses(processName, version, domain, username, algo, parameters, ids);
            res.setProcessSummary(respFromToolbox);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createMergeProcessesResponse(res);
    }


    /* (non-Javadoc)
      * @see org.apromore.manager.service_portal1.ManagerPortalPortType#searchForSimilarProcesses(SearchForSimilarProcessesInputMsgType  payload )*
      */
    @PayloadRoot(localPart = "SearchForSimilarProcessesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<SearchForSimilarProcessesOutputMsgType> searchForSimilarProcesses(
            @RequestPayload final JAXBElement<SearchForSimilarProcessesInputMsgType> req) {
        LOGGER.info("Executing operation searchForSimilarProcesses");
        SearchForSimilarProcessesInputMsgType payload = req.getValue();
        SearchForSimilarProcessesOutputMsgType res = new SearchForSimilarProcessesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            String algo = payload.getAlgorithm();
            Integer processId = payload.getProcessId();
            String versionName = payload.getVersionName();
            Boolean latestVersions = payload.isLatestVersions();
            ParametersType paramsT = new ParametersType();
            for (ParameterType p : payload.getParameters().getParameter()) {
                ParameterType paramT = new ParameterType();
                paramsT.getParameter().add(paramT);
                paramT.setName(p.getName());
                paramT.setValue(p.getValue());
            }
            ProcessSummariesType processes = tbClient.SearchForSimilarProcesses(processId, versionName, latestVersions, algo, paramsT);
            res.setProcessSummaries(processes);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createSearchForSimilarProcessesResponse(res);
    }

    @PayloadRoot(localPart = "WriteAnnotationRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<WriteAnnotationOutputMsgType> writeAnnotation(@RequestPayload final JAXBElement<WriteAnnotationInputMsgType> req) {
        LOGGER.info("Executing operation writeAnnotation");
        WriteAnnotationInputMsgType payload = req.getValue();
        WriteAnnotationOutputMsgType res = new WriteAnnotationOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            Integer editSessionCode = payload.getEditSessionCode();
            String annotName = payload.getAnnotationName();
            Integer processId = payload.getProcessId();
            String version = payload.getVersion();
            String nat_type = payload.getNativeType();
            Boolean isNew = payload.isIsNew();
            DataHandler handler = payload.getNative();
            InputStream native_is = handler.getInputStream();
            caClient.GenerateAnnotation(annotName, editSessionCode, isNew, processId, version, nat_type, native_is);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createWriteAnnotationResponse(res);
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "ReadAllUsersRequest")
    @ResponsePayload
    public JAXBElement<ReadAllUsersOutputMsgType> readAllUsers(@RequestPayload final JAXBElement<ReadAllUsersInputMsgType> message) {
        LOGGER.info("Executing operation readAllUsers");
        ReadAllUsersInputMsgType payload = message.getValue();
        ReadAllUsersOutputMsgType res = new ReadAllUsersOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        UsernamesType allUsers = UserMapper.convertUsernameTypes(userSrv.findAllUsers());
        res.setUsernames(allUsers);
        result.setCode(0);
        result.setMessage("");
        return new ObjectFactory().createReadAllUsersResponse(res);
    }

    @PayloadRoot(localPart = "DeleteEditSessionRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<DeleteEditSessionOutputMsgType> deleteEditSession(@RequestPayload final JAXBElement<DeleteEditSessionInputMsgType> req) {
        LOGGER.info("Executing operation deleteEditSession");
        DeleteEditSessionInputMsgType payload = req.getValue();
        DeleteEditSessionOutputMsgType res = new DeleteEditSessionOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        int code = payload.getEditSessionCode();
        try {
            daClient.DeleteEditSession(code);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createDeleteEditSessionResponse(res);
    }

    @PayloadRoot(localPart = "DeleteProcessVersionsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<DeleteProcessVersionsOutputMsgType> deleteProcessVersions(
            @RequestPayload final JAXBElement<DeleteProcessVersionsInputMsgType> req) {
        LOGGER.info("Executing operation deleteProcessVersions");
        DeleteProcessVersionsInputMsgType payload = req.getValue();
        DeleteProcessVersionsOutputMsgType res = new DeleteProcessVersionsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        List<ProcessVersionIdentifierType> processesP = payload.getProcessVersionIdentifier();
        try {
            List<ProcessVersionIdentifierType> processesDa = new ArrayList<ProcessVersionIdentifierType>();
            Iterator<ProcessVersionIdentifierType> it = processesP.iterator();
            while (it.hasNext()) {
                ProcessVersionIdentifierType processP = it.next();
                ProcessVersionIdentifierType processDa = new ProcessVersionIdentifierType();
                processDa.setProcessid(processP.getProcessid());
                processDa.getVersionName().addAll(processP.getVersionName());
                processesDa.add(processDa);
            }
            daClient.DeleteProcessVersion(processesDa);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createDeleteProcessVersionsResponse(res);
    }


    @PayloadRoot(localPart = "UpdateProcessRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<UpdateProcessOutputMsgType> updateProcess(@RequestPayload final JAXBElement<UpdateProcessInputMsgType> req) {
        LOGGER.info("Executing operation updateProcess");
        UpdateProcessInputMsgType payload = req.getValue();
        UpdateProcessOutputMsgType res = new UpdateProcessOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            DataHandler handler = payload.getNative();
            InputStream native_is = handler.getInputStream();
            int editSessionCode = payload.getEditSessionCode();
            EditSessionType editSessionP = payload.getEditSession();
            EditSessionType editSessionC = new EditSessionType();
            editSessionC.setProcessId(editSessionP.getProcessId());
            editSessionC.setNativeType(editSessionP.getNativeType());
            editSessionC.setAnnotation(editSessionP.getAnnotation());
            editSessionC.setCreationDate(editSessionP.getCreationDate());
            editSessionC.setLastUpdate(editSessionP.getLastUpdate());
            editSessionC.setProcessName(editSessionP.getProcessName());
            editSessionC.setUsername(editSessionP.getUsername());
            editSessionC.setVersionName(editSessionP.getVersionName());
            caClient.CanoniseVersion(editSessionCode, editSessionC, newCpfURI(), native_is);
            result.setCode(0);
            result.setMessage("");
        } catch (ExceptionVersion ex) {
            result.setCode(-3);
            result.setMessage(ex.getMessage());
        } catch (IOException ex) {
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        } catch (ExceptionCanoniseVersion ex) {
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createUpdateProcessResponse(res);
    }


    @PayloadRoot(localPart = "ReadEditSessionRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadEditSessionOutputMsgType> readEditSession(@RequestPayload final JAXBElement<ReadEditSessionInputMsgType> req) {
        LOGGER.info("Executing operation readEditSession");
        ReadEditSessionInputMsgType payload = req.getValue();
        ReadEditSessionOutputMsgType res = new ReadEditSessionOutputMsgType();
        int code = payload.getEditSessionCode();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            EditSessionType editSessionDA = daClient.ReadEditSession(code);
            EditSessionType editSessionP = new EditSessionType();
            editSessionP.setNativeType(editSessionDA.getNativeType());
            editSessionP.setProcessId(editSessionDA.getProcessId());
            editSessionP.setUsername(editSessionDA.getUsername());
            editSessionP.setVersionName(editSessionDA.getVersionName());
            editSessionP.setProcessName(editSessionDA.getProcessName());
            editSessionP.setDomain(editSessionDA.getDomain());
            editSessionP.setCreationDate(editSessionDA.getCreationDate());
            editSessionP.setLastUpdate(editSessionDA.getLastUpdate());
            editSessionP.setWithAnnotation(editSessionDA.isWithAnnotation());
            editSessionP.setAnnotation(editSessionDA.getAnnotation());
            res.setEditSession(editSessionP);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createReadEditSessionResponse(res);
    }

    @PayloadRoot(localPart = "WriteEditSessionRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<WriteEditSessionOutputMsgType> writeEditSession(@RequestPayload final JAXBElement<WriteEditSessionInputMsgType> req) {
        LOGGER.info("Executing operation writeEditSession");
        WriteEditSessionInputMsgType payload = req.getValue();
        WriteEditSessionOutputMsgType res = new WriteEditSessionOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        EditSessionType editSessionP = payload.getEditSession();
        EditSessionType editSessionDA = new EditSessionType();
        editSessionDA.setNativeType(editSessionP.getNativeType());
        editSessionDA.setProcessId(editSessionP.getProcessId());
        editSessionDA.setUsername(editSessionP.getUsername());
        editSessionDA.setVersionName(editSessionP.getVersionName());
        editSessionDA.setProcessName(editSessionP.getProcessName());
        editSessionDA.setDomain(editSessionP.getDomain());
        editSessionDA.setCreationDate(editSessionP.getCreationDate());
        editSessionDA.setLastUpdate(editSessionP.getLastUpdate());
        editSessionDA.setWithAnnotation(editSessionP.isWithAnnotation());
        editSessionDA.setAnnotation(editSessionP.getAnnotation());
        try {
            int code = daClient.WriteEditSession(editSessionDA);

            res.setEditSessionCode(code);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createWriteEditSessionResponse(res);
    }

    @PayloadRoot(localPart = "ExportFormatRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ExportFormatOutputMsgType> exportFormat(@RequestPayload final JAXBElement<ExportFormatInputMsgType> req) {
        LOGGER.info("Executing operation exportFormat");

        ResultType result = new ResultType();
        ExportFormatInputMsgType payload = req.getValue();
        ExportFormatOutputMsgType res = new ExportFormatOutputMsgType();

        // Search for Native
        try {
            DataSource source = null;
            long processId = payload.getProcessId();
            String version = payload.getVersionName();
            String format = payload.getFormat();
            String annName = payload.getAnnotationName();
            boolean withAnn = payload.isWithAnnotations();

            if ((withAnn && annName.equals(Constants.INITIAL_ANNOTATIONS))
                        || format.equals(Constants.CANONICAL)
                        || format.startsWith(Constants.ANNOTATIONS)) {
                source = procSrv.exportFormat(processId, version, format);
            } else {
                Format canFormat = procSrv.getCanonicalAnf(processId, version, withAnn, annName);
                if (withAnn) {
                    source = canSrv.deCanonise(processId, version, format, canFormat.getCpf(), canFormat.getAnf());
                } else {
                    source = canSrv.deCanonise(processId, version, format, canFormat.getCpf(), null);
                }
            }

            res.setNative(new DataHandler(source));
            result.setCode(0);
            result.setMessage("");
        } catch (ExportFormatException efe) {
            LOGGER.error("ExportFormat failed: " + efe.toString());
            result.setCode(-1);
            result.setMessage(efe.getMessage());
        }

        res.setResult(result);
        return new ObjectFactory().createExportFormatResponse(res);
    }

    @PayloadRoot(localPart = "ImportProcessRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ImportProcessOutputMsgType> importProcess(@RequestPayload final JAXBElement<ImportProcessInputMsgType> req) {
        LOGGER.info("Executing operation importProcess");

        ResultType result = new ResultType();
        ImportProcessInputMsgType payload = req.getValue();
        ImportProcessOutputMsgType res = new ImportProcessOutputMsgType();

        try {
            EditSessionType editSession = payload.getEditSession();
            String username = editSession.getUsername();
            String processName = editSession.getProcessName();
            String versionName = editSession.getVersionName();
            String nativeType = editSession.getNativeType();
            String domain = editSession.getDomain();
            String creationDate = editSession.getCreationDate();
            String lastUpdate = editSession.getLastUpdate();

            //Boolean addFakeEvents = payload.isAddFakeEvents();
            DataHandler handler = payload.getProcessDescription();

            //ProcessSummaryType process = caClient.CanoniseProcess(username, processName, newCpfURI(),
            //        versionName, nativeType, is, domain, "", creationDate, lastUpdate, addFakeEvents);
            ProcessSummaryType process = procSrv.importProcess(username, processName, newCpfURI(), versionName,
                    nativeType, handler, domain, "", creationDate, lastUpdate);

            res.setProcessSummary(process);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }

        res.setResult(result);
        return new ObjectFactory().createImportProcessResponse(res);
    }


    @PayloadRoot(localPart = "WriteUserRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<WriteUserOutputMsgType> writeUser(@RequestPayload final JAXBElement<WriteUserInputMsgType> req) {
        LOGGER.info("Executing operation writeUser");
        WriteUserInputMsgType payload = req.getValue();
        WriteUserOutputMsgType res = new WriteUserOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            userSrv.writeUser(UserMapper.convertFromUserType(payload.getUser()));
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(0);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createWriteUserResponse(res);
    }

    @PayloadRoot(localPart = "ReadNativeTypesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadNativeTypesOutputMsgType> readNativeTypes(@RequestPayload final JAXBElement<ReadNativeTypesInputMsgType> req) {
        LOGGER.info("Executing operation readFormats");
        //ReadNativeTypesInputMsgType payload = req.getValue();
        ReadNativeTypesOutputMsgType res = new ReadNativeTypesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            NativeTypesType formats = NativeTypeMapper.convertFromNativeType(frmSrv.findAllFormats());
            result.setCode(0);
            result.setMessage("");
            res.setNativeTypes(formats);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createReadNativeTypesResponse(res);
    }

    /* (non-Javadoc)
      * @see org.apromore.manager.service.ManagerPortalPortType#readDomains(ReadDomainsInputMsgType  payload )*
      */
    @PayloadRoot(localPart = "ReadDomainsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadDomainsOutputMsgType> readDomains(@RequestPayload final JAXBElement<ReadDomainsInputMsgType> req) {
        LOGGER.info("Executing operation readDomains");
        //ReadDomainsInputMsgType payload = req.getValue();
        ReadDomainsOutputMsgType res = new ReadDomainsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            DomainsType domains = DomainMapper.convertFromDomains(domSrv.findAllDomains());
            result.setCode(0);
            result.setMessage("");
            res.setDomains(domains);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createReadDomainsResponse(res);
    }

    /* (non-Javadoc)
     * @see org.apromore.manager.service.ManagerPortalPortType#readUser(ReadUserInputMsgType  payload )
     */
    @PayloadRoot(localPart = "ReadUserRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadUserOutputMsgType> readUser(@RequestPayload final JAXBElement<ReadUserInputMsgType> req) {
        LOGGER.info("Executing operation readUser");
        ReadUserInputMsgType payload = req.getValue();
        ReadUserOutputMsgType res = new ReadUserOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            UserType user = UserMapper.convertUserTypes(userSrv.findUser(payload.getUsername()));
            result.setCode(0);
            result.setMessage("");
            res.setUser(user);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createReadUserResponse(res);
    }

    /* (non-Javadoc)
      * @see org.apromore.manager.service.ManagerPortalPortType#readProcessSummaries(ReadProcessSummariesInputMsgType  payload )*
      */
    @PayloadRoot(localPart = "ReadProcessSummariesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadProcessSummariesOutputMsgType> readProcessSummaries(
            @RequestPayload final JAXBElement<ReadProcessSummariesInputMsgType> req) {
        LOGGER.info("Executing operation readProcessSummaries");
        ReadProcessSummariesInputMsgType payload = req.getValue();
        ReadProcessSummariesOutputMsgType res = new ReadProcessSummariesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        try {
            ProcessSummariesType processes = procSrv.readProcessSummaries(payload.getSearchExpression());
            result.setCode(0);
            result.setMessage("");
            res.setProcessSummaries(processes);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createReadProcessSummariesResponse(res);
    }

    /**
     * Generate a new npf which is the result of writing parameters in process_xml.
     *
     * @param process_xml  the given npf to be synchronised
     * @param nativeType   npf native type
     * @param processName
     * @param version
     * @param username
     * @param lastUpdate
     * @return
     * @throws javax.xml.bind.JAXBException
     */
    private InputStream copyParam2NPF(InputStream process_xml,
                                      String nativeType, String processName,
                                      String version, String username,
                                      String lastUpdate, String documentation) throws JAXBException {

        InputStream res = null;
        if (nativeType.compareTo("XPDL 2.1") == 0) {
            JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
            Unmarshaller u = jc.createUnmarshaller();
            JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(process_xml);
            PackageType pkg = rootElement.getValue();
            copyParam2xpdl(pkg, processName, version, username, lastUpdate, documentation);

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
            m.marshal(rootElement, xpdl_xml);
            res = new ByteArrayInputStream(xpdl_xml.toByteArray());

        } else if (nativeType.compareTo("EPML 2.0") == 0) {
            JAXBContext jc = JAXBContext.newInstance("de.epml");
            Unmarshaller u = jc.createUnmarshaller();
            JAXBElement<TypeEPML> rootElement = (JAXBElement<TypeEPML>) u.unmarshal(process_xml);
            TypeEPML epml = rootElement.getValue();

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
            m.marshal(rootElement, xpdl_xml);
            res = new ByteArrayInputStream(xpdl_xml.toByteArray());
        }
        return res;
    }

    /**
     * Modify pkg (npf of type xpdl) with parameters values if not null.
     *
     * @param pkg
     * @param processName
     * @param version
     * @param username
     * @param lastUpdate
     * @param documentation
     * @return
     */
    private void copyParam2xpdl(PackageType pkg,
                                String processName, String version, String username,
                                String lastUpdate, String documentation) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
        Date date = new Date();
        String creationDate = dateFormat.format(date);

        if (pkg.getRedefinableHeader() == null) {
            RedefinableHeader header = new RedefinableHeader();
            pkg.setRedefinableHeader(header);
            Version v = new Version();
            header.setVersion(v);
            Author a = new Author();
            header.setAuthor(a);
        } else {
            if (pkg.getRedefinableHeader().getVersion() == null) {
                Version v = new Version();
                pkg.getRedefinableHeader().setVersion(v);
            }
            if (pkg.getRedefinableHeader().getAuthor() == null) {
                Author a = new Author();
                pkg.getRedefinableHeader().setAuthor(a);
            }
        }
        if (pkg.getPackageHeader() == null) {
            PackageHeader pkgHeader = new PackageHeader();
            pkg.setPackageHeader(pkgHeader);
            Created created = new Created();
            pkgHeader.setCreated(created);
            ModificationDate modifDate = new ModificationDate();
            pkgHeader.setModificationDate(modifDate);
            Documentation doc = new Documentation();
            pkgHeader.setDocumentation(doc);
        } else {
            if (pkg.getPackageHeader().getCreated() == null) {
                Created created = new Created();
                pkg.getPackageHeader().setCreated(created);
            }
            if (pkg.getPackageHeader().getModificationDate() == null) {
                ModificationDate modifDate = new ModificationDate();
                pkg.getPackageHeader().setModificationDate(modifDate);
            }
            if (pkg.getPackageHeader().getDocumentation() == null) {
                Documentation doc = new Documentation();
                pkg.getPackageHeader().setDocumentation(doc);
            }
        }
        if (processName != null) pkg.setName(processName);
        if (version != null) pkg.getRedefinableHeader().getVersion().setValue(version);
        if (username != null) pkg.getRedefinableHeader().getAuthor().setValue(username);
        if (creationDate != null) pkg.getPackageHeader().getCreated().setValue(creationDate);
        if (lastUpdate != null) pkg.getPackageHeader().getModificationDate().setValue(lastUpdate);
        if (documentation != null) pkg.getPackageHeader().getDocumentation().setValue(documentation);
    }

    /**
     * Generate a cpf uri for version of processId
     * @return the new cpf uri
     */
    private static String newCpfURI() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsSSS");
        Date date = new Date();
        return dateFormat.format(date);
    }



    public void setDaClient(ManagerDataAccessClient daClient) {
        this.daClient = daClient;
    }

    public void setTbClient(ManagerToolboxClient tbClient) {
        this.tbClient = tbClient;
    }

    public void setCaClient(ManagerCanoniserClient caClient) {
        this.caClient = caClient;
    }


    public void setCanServ(CanoniserService canService) {
        this.canSrv = canService;
    }

    public void setUserSrv(UserService userService) {
        this.userSrv = userService;
    }

    public void setProcSrv(ProcessService procService) {
        this.procSrv = procService;
    }

    public void setFrmSrv(FormatService formatService) {
        this.frmSrv = formatService;
    }

    public void setDomSrv(DomainService domainService) {
        this.domSrv = domainService;
    }
}
