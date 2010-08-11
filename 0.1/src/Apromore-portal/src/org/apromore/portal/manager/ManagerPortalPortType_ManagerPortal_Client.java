
package org.apromore.portal.manager;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

/**
 * This class was generated by Apache CXF 2.2.9
 * Wed Aug 04 17:46:27 EST 2010
 * Generated source version: 2.2.9
 * 
 */

public final class ManagerPortalPortType_ManagerPortal_Client {

    private static final QName SERVICE_NAME = new QName("http://www.apromore.org/manager/service_portal", "ManagerPortalService");

    private ManagerPortalPortType_ManagerPortal_Client() {
    }

    public static void main(String args[]) throws Exception {
        URL wsdlURL = ManagerPortalService.WSDL_LOCATION;
        if (args.length > 0) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        ManagerPortalService ss = new ManagerPortalService(wsdlURL, SERVICE_NAME);
        ManagerPortalPortType port = ss.getManagerPortal();  
        
        {
        System.out.println("Invoking writeUser...");
        org.apromore.portal.model_manager.WriteUserInputMsgType _writeUser_payload = new org.apromore.portal.model_manager.WriteUserInputMsgType();
        org.apromore.portal.model_manager.UserType _writeUser_payloadUser = new org.apromore.portal.model_manager.UserType();
        java.util.List<org.apromore.portal.model_manager.SearchHistoriesType> _writeUser_payloadUserSearchHistories = new java.util.ArrayList<org.apromore.portal.model_manager.SearchHistoriesType>();
        org.apromore.portal.model_manager.SearchHistoriesType _writeUser_payloadUserSearchHistoriesVal1 = new org.apromore.portal.model_manager.SearchHistoriesType();
        _writeUser_payloadUserSearchHistoriesVal1.setSearch("Search-278664471");
        _writeUser_payloadUserSearchHistoriesVal1.setNum(Integer.valueOf(1248440529));
        _writeUser_payloadUserSearchHistories.add(_writeUser_payloadUserSearchHistoriesVal1);
        _writeUser_payloadUser.getSearchHistories().addAll(_writeUser_payloadUserSearchHistories);
        _writeUser_payloadUser.setFirstname("Firstname-983815673");
        _writeUser_payloadUser.setLastname("Lastname-377790823");
        _writeUser_payloadUser.setEmail("Email-784575711");
        _writeUser_payloadUser.setUsername("Username1097086978");
        _writeUser_payloadUser.setPasswd("Passwd1150917128");
        _writeUser_payload.setUser(_writeUser_payloadUser);
        org.apromore.portal.model_manager.WriteUserOutputMsgType _writeUser__return = port.writeUser(_writeUser_payload);
        System.out.println("writeUser.result=" + _writeUser__return);


        }
        {
        System.out.println("Invoking editProcessData...");
        org.apromore.portal.model_manager.EditProcessDataInputMsgType _editProcessData_payload = new org.apromore.portal.model_manager.EditProcessDataInputMsgType();
        _editProcessData_payload.setProcessName("ProcessName660399750");
        _editProcessData_payload.setId(Integer.valueOf(1220240709));
        _editProcessData_payload.setDomain("Domain-1958885089");
        _editProcessData_payload.setOwner("Owner-932409701");
        _editProcessData_payload.setRanking("Ranking1707827520");
        _editProcessData_payload.setNewName("NewName-1200625554");
        _editProcessData_payload.setPreName("PreName-683905801");
        org.apromore.portal.model_manager.EditProcessDataOutputMsgType _editProcessData__return = port.editProcessData(_editProcessData_payload);
        System.out.println("editProcessData.result=" + _editProcessData__return);


        }
        {
        System.out.println("Invoking writeEditSession...");
        org.apromore.portal.model_manager.WriteEditSessionInputMsgType _writeEditSession_payload = new org.apromore.portal.model_manager.WriteEditSessionInputMsgType();
        org.apromore.portal.model_manager.EditSessionType _writeEditSession_payloadEditSession = new org.apromore.portal.model_manager.EditSessionType();
        _writeEditSession_payloadEditSession.setUsername("Username1560092866");
        _writeEditSession_payloadEditSession.setNativeType("NativeType-2046026286");
        _writeEditSession_payloadEditSession.setProcessId(Integer.valueOf(-1246515537));
        _writeEditSession_payloadEditSession.setProcessName("ProcessName17005381");
        _writeEditSession_payloadEditSession.setVersionName("VersionName1304479093");
        _writeEditSession_payloadEditSession.setDomain("Domain1886834848");
        _writeEditSession_payloadEditSession.setWithAnnotation(Boolean.valueOf(false));
        _writeEditSession_payloadEditSession.setAnnotation("Annotation1160731100");
        _writeEditSession_payload.setEditSession(_writeEditSession_payloadEditSession);
        org.apromore.portal.model_manager.WriteEditSessionOutputMsgType _writeEditSession__return = port.writeEditSession(_writeEditSession_payload);
        System.out.println("writeEditSession.result=" + _writeEditSession__return);


        }
        {
        System.out.println("Invoking exportFormat...");
        org.apromore.portal.model_manager.ExportFormatInputMsgType _exportFormat_payload = new org.apromore.portal.model_manager.ExportFormatInputMsgType();
        _exportFormat_payload.setFormat("Format-712806714");
        _exportFormat_payload.setProcessId(Integer.valueOf(21956540));
        _exportFormat_payload.setProcessName("ProcessName254672145");
        _exportFormat_payload.setVersionName("VersionName-1802405088");
        _exportFormat_payload.setAnnotationName("AnnotationName-1602282682");
        _exportFormat_payload.setWithAnnotations(Boolean.valueOf(false));
        _exportFormat_payload.setOwner("Owner-322072276");
        org.apromore.portal.model_manager.ExportFormatOutputMsgType _exportFormat__return = port.exportFormat(_exportFormat_payload);
        System.out.println("exportFormat.result=" + _exportFormat__return);


        }
        {
        System.out.println("Invoking readNativeTypes...");
        org.apromore.portal.model_manager.ReadNativeTypesInputMsgType _readNativeTypes_payload = new org.apromore.portal.model_manager.ReadNativeTypesInputMsgType();
        _readNativeTypes_payload.setEmpty("Empty109662136");
        org.apromore.portal.model_manager.ReadNativeTypesOutputMsgType _readNativeTypes__return = port.readNativeTypes(_readNativeTypes_payload);
        System.out.println("readNativeTypes.result=" + _readNativeTypes__return);


        }
        {
        System.out.println("Invoking readDomains...");
        org.apromore.portal.model_manager.ReadDomainsInputMsgType _readDomains_payload = new org.apromore.portal.model_manager.ReadDomainsInputMsgType();
        _readDomains_payload.setEmpty("Empty1330804647");
        org.apromore.portal.model_manager.ReadDomainsOutputMsgType _readDomains__return = port.readDomains(_readDomains_payload);
        System.out.println("readDomains.result=" + _readDomains__return);


        }
        {
        System.out.println("Invoking readUser...");
        org.apromore.portal.model_manager.ReadUserInputMsgType _readUser_payload = new org.apromore.portal.model_manager.ReadUserInputMsgType();
        _readUser_payload.setUsername("Username832119356");
        org.apromore.portal.model_manager.ReadUserOutputMsgType _readUser__return = port.readUser(_readUser_payload);
        System.out.println("readUser.result=" + _readUser__return);


        }
        {
        System.out.println("Invoking readAllUsers...");
        org.apromore.portal.model_manager.ReadAllUsersInputMsgType _readAllUsers_payload = new org.apromore.portal.model_manager.ReadAllUsersInputMsgType();
        _readAllUsers_payload.setEmpty("Empty-24449894");
        org.apromore.portal.model_manager.ReadAllUsersOutputMsgType _readAllUsers__return = port.readAllUsers(_readAllUsers_payload);
        System.out.println("readAllUsers.result=" + _readAllUsers__return);


        }
        {
        System.out.println("Invoking deleteEditSession...");
        org.apromore.portal.model_manager.DeleteEditSessionInputMsgType _deleteEditSession_payload = new org.apromore.portal.model_manager.DeleteEditSessionInputMsgType();
        _deleteEditSession_payload.setEditSessionCode(Integer.valueOf(-1450303615));
        org.apromore.portal.model_manager.DeleteEditSessionOutputMsgType _deleteEditSession__return = port.deleteEditSession(_deleteEditSession_payload);
        System.out.println("deleteEditSession.result=" + _deleteEditSession__return);


        }
        {
        System.out.println("Invoking deleteProcessVersions...");
        org.apromore.portal.model_manager.DeleteProcessVersionsInputMsgType _deleteProcessVersions_payload = new org.apromore.portal.model_manager.DeleteProcessVersionsInputMsgType();
        java.util.List<org.apromore.portal.model_manager.ProcessVersionIdentifierType> _deleteProcessVersions_payloadProcessVersionIdentifier = new java.util.ArrayList<org.apromore.portal.model_manager.ProcessVersionIdentifierType>();
        org.apromore.portal.model_manager.ProcessVersionIdentifierType _deleteProcessVersions_payloadProcessVersionIdentifierVal1 = new org.apromore.portal.model_manager.ProcessVersionIdentifierType();
        java.util.List<java.lang.String> _deleteProcessVersions_payloadProcessVersionIdentifierVal1VersionName = new java.util.ArrayList<java.lang.String>();
        _deleteProcessVersions_payloadProcessVersionIdentifierVal1.getVersionName().addAll(_deleteProcessVersions_payloadProcessVersionIdentifierVal1VersionName);
        _deleteProcessVersions_payloadProcessVersionIdentifierVal1.setProcessid(Integer.valueOf(-1282574686));
        _deleteProcessVersions_payloadProcessVersionIdentifier.add(_deleteProcessVersions_payloadProcessVersionIdentifierVal1);
        _deleteProcessVersions_payload.getProcessVersionIdentifier().addAll(_deleteProcessVersions_payloadProcessVersionIdentifier);
        org.apromore.portal.model_manager.DeleteProcessVersionsOutputMsgType _deleteProcessVersions__return = port.deleteProcessVersions(_deleteProcessVersions_payload);
        System.out.println("deleteProcessVersions.result=" + _deleteProcessVersions__return);


        }
        {
        System.out.println("Invoking importProcess...");
        org.apromore.portal.model_manager.ImportProcessInputMsgType _importProcess_payload = new org.apromore.portal.model_manager.ImportProcessInputMsgType();
        javax.activation.DataHandler _importProcess_payloadProcessDescription = null;
        _importProcess_payload.setProcessDescription(_importProcess_payloadProcessDescription);
        _importProcess_payload.setProcessName("ProcessName825287321");
        _importProcess_payload.setVersionName("VersionName1569805851");
        _importProcess_payload.setNativeType("NativeType969473477");
        _importProcess_payload.setDomain("Domain412418842");
        _importProcess_payload.setUsername("Username2050266781");
        _importProcess_payload.setLastUpdate("LastUpdate837780543");
        _importProcess_payload.setCreationDate("CreationDate-2015540052");
        _importProcess_payload.setDocumentation("Documentation-200462207");
        org.apromore.portal.model_manager.ImportProcessOutputMsgType _importProcess__return = port.importProcess(_importProcess_payload);
        System.out.println("importProcess.result=" + _importProcess__return);


        }
        {
        System.out.println("Invoking readProcessSummaries...");
        org.apromore.portal.model_manager.ReadProcessSummariesInputMsgType _readProcessSummaries_payload = new org.apromore.portal.model_manager.ReadProcessSummariesInputMsgType();
        _readProcessSummaries_payload.setSearchExpression("SearchExpression-1036479128");
        org.apromore.portal.model_manager.ReadProcessSummariesOutputMsgType _readProcessSummaries__return = port.readProcessSummaries(_readProcessSummaries_payload);
        System.out.println("readProcessSummaries.result=" + _readProcessSummaries__return);


        }
        {
        System.out.println("Invoking readEditSession...");
        org.apromore.portal.model_manager.ReadEditSessionInputMsgType _readEditSession_payload = new org.apromore.portal.model_manager.ReadEditSessionInputMsgType();
        _readEditSession_payload.setEditSessionCode(Integer.valueOf(-1450615184));
        org.apromore.portal.model_manager.ReadEditSessionOutputMsgType _readEditSession__return = port.readEditSession(_readEditSession_payload);
        System.out.println("readEditSession.result=" + _readEditSession__return);


        }
        {
        System.out.println("Invoking updateProcess...");
        org.apromore.portal.model_manager.UpdateProcessInputMsgType _updateProcess_payload = new org.apromore.portal.model_manager.UpdateProcessInputMsgType();
        javax.activation.DataHandler _updateProcess_payloadNative = null;
        _updateProcess_payload.setNative(_updateProcess_payloadNative);
        _updateProcess_payload.setEditSessionCode(Integer.valueOf(955352050));
        _updateProcess_payload.setUsername("Username-770994821");
        _updateProcess_payload.setNativeType("NativeType-1482371073");
        _updateProcess_payload.setProcessId(Integer.valueOf(494291509));
        _updateProcess_payload.setDomain("Domain-1678692647");
        _updateProcess_payload.setPreVersion("PreVersion558464192");
        org.apromore.portal.model_manager.UpdateProcessOutputMsgType _updateProcess__return = port.updateProcess(_updateProcess_payload);
        System.out.println("updateProcess.result=" + _updateProcess__return);


        }

        System.exit(0);
    }

}
