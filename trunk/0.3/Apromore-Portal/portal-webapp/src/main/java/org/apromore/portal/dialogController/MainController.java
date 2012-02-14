package org.apromore.portal.dialogController;

import org.apromore.portal.common.Constants;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDao;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.exception.ExceptionWriteEditSession;
import org.apromore.model.DomainsType;
import org.apromore.model.EditSessionType;
import org.apromore.model.NativeTypesType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SearchHistoriesType;
import org.apromore.model.UserType;
import org.apromore.model.UsernamesType;
import org.apromore.model.VersionSummaryType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

/*
* Controller for the window index.zul
*
*/
public class MainController extends BaseController {

    private Window mainW;
    private HeaderController header;
    private MenuController menu;
    private ProcessTableController processtable;
    private SimpleSearchController simplesearch;
    private UserType currentUser;                // the connected user, if any
    private ShortMessageController shortmessageC;
    private Window shortmessageW;
    private String host;
    private String OryxEndPoint_xpdl;
    private String OryxEndPoint_epml;
    private Logger LOG;
    private String msgWhenClose;
    private List<SearchHistoriesType> searchHistory;

    // uncomment when ready
    //private NavigationController navigation;
    //private RefinedSearchController refinedsearch;

    /**
     * onCreate is executed after the main window has been created
     * it is responsible for instantiating all necessary controllers
     * (one for each window defined in the interface)
     * <p/>
     * see description in index.zul
     *
     * @throws InterruptedException
     */
    public void onCreate() throws InterruptedException {
        try {
            // if client browser is not gecko3 based (such as firefox) raise an exception
            if (!Executions.getCurrent().isGecko() && !Executions.getCurrent().isGecko3()) {
                throw new Exception("Sorry, we currently support firefox only.");
            }
            this.LOG = Logger.getLogger(MainController.class.getName());
            /**
             * to get data
             */
            this.mainW = (Window) this.getFellow("mainW");
            this.shortmessageW = (Window) this.getFellow("shortmessagescomp").getFellow("shortmessage");
            this.shortmessageC = new ShortMessageController(shortmessageW);
            this.processtable = new ProcessTableController(this);
            this.header = new HeaderController(this);
            this.simplesearch = new SimpleSearchController(this);
            this.menu = new MenuController(this);

            //this.navigation = new NavigationController (this);

            this.currentUser = null;
            this.searchHistory = new ArrayList<SearchHistoriesType>();
            this.msgWhenClose = null;
            // read Oryx access point in properties
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(Constants.PROPERTY_FILE);
            ;
            Properties properties = new Properties();
            properties.load(inputStream);
            this.host = properties.getProperty("Host");
            this.OryxEndPoint_xpdl = properties.getProperty("OryxEndPoint_xpdl");
            this.OryxEndPoint_epml = properties.getProperty("OryxEndPoint_epml");

        } catch (Exception e) {
            String message = null;
            if (e.getMessage() == null) {
                message = "Please contact Apromore's administrator";
            } else {
                message = e.getMessage();
            }
            e.printStackTrace();
            Messagebox.show("Repository not available (" + message + ")", "Attention", Messagebox.OK,
                    Messagebox.ERROR);
        }
    }

    /**
     * register an event listener for the clientInfo event (to prevent user to close the browser window)
     */
    public void onClientInfo(ClientInfoEvent event) {
        //Clients.confirmClose(this.msgWhenClose); doesn't work....
        Clients.confirmClose(Constants.MSG_WHEN_CLOSE);
    }

    /**
     * Display version processes in processSummaries: if isQueryResult, the query
     * is given by version of process
     *
     * @param processSummaries
     * @param isQueryResult
     * @param process
     * @param version
     * @throws Exception
     */
    public void displayProcessSummaries(ProcessSummariesType processSummaries,
                                        Boolean isQueryResult,
                                        ProcessSummaryType process, VersionSummaryType version) throws Exception {
        int activePage = this.processtable.getPg().getActivePage();
        this.processtable.emptyProcessSummaries();
        this.processtable.newPaging();
        this.processtable.displayProcessSummaries(processSummaries, isQueryResult, process, version);
        int lastPage = this.processtable.getPg().getPageCount() - 1;
        if (lastPage < activePage) {
            this.processtable.getPg().setActivePage(lastPage);
        } else {
            this.processtable.getPg().setActivePage(activePage);
        }
    }

    // disable/enable features depending on user status
    public void updateActions() {

        Boolean connected = this.currentUser != null;

        // disable/enable menu items in menu bar
        Iterator<Component> itC = this.menu.getMenuB().getFellows().iterator();
        while (itC.hasNext()) {
            Component C = itC.next();
            if (C.getClass().getName().compareTo("org.zkoss.zul.Menuitem") == 0) {
                if (C.getId().compareTo("processMerge") != 0) {
                    ((Menuitem) C).setDisabled(!connected);
                }
            }
        }
    }

    public void reloadProcessSummaries() throws Exception {
        ProcessSummariesType processSummaries = managerService.readProcessSummaries("");
        String message = null;
        if (processSummaries.getProcessSummary().size() > 1) {
            message = " processes.";
        } else {
            message = " process.";
        }
        this.displayMessage(processSummaries.getProcessSummary().size() + message);
        this.simplesearch.clearSearches();
        this.displayProcessSummaries(processSummaries, false, null, null);
    }

    /**
     * reset displayed informations:
     * - short message
     * - process summaries
     * - simple search
     *
     * @throws Exception
     */
    public void resetUserInformation() throws Exception {
        eraseMessage();
        this.currentUser = null;
        this.simplesearch.clearSearches();
    }

    /**
     * Forward to the controller ProcessTableController the request to
     * add the process to the table
     *
     * @param returnedProcess
     */
    public void displayNewProcess(ProcessSummaryType returnedProcess) {
        this.processtable.displayNewProcess(returnedProcess);
        this.displayMessage(this.processtable.getProcessHM().size() + " processes.");
    }

    /**
     * Send request to Manager: deleted process versions given as parameter
     *
     * @param processVersions
     * @throws InterruptedException
     * @throws javax.xml.bind.JAXBException
     * @throws org.apromore.portal.exception.ExceptionDao
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public void deleteProcessVersions(
            Map<ProcessSummaryType, List<VersionSummaryType>> processVersions)
            throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, ExceptionDao, JAXBException {
        try {
            getService().deleteProcessVersions(processVersions);
            this.processtable.unDisplay(processVersions);
            String message;
            int nb = 0;

            // to count how many process version(s) deleted
            Collection<List<VersionSummaryType>> sumTypes = processVersions.values();
            for (List<VersionSummaryType> sumType : sumTypes) {
                nb += sumType.size();
            }
            if (nb > 1) {
                message = nb + " process versions deleted.";
            } else {
                message = " One process version deleted.";
            }
            displayMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Deletion failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
                    Messagebox.ERROR);
        }
    }

    /**
     * Call editor to edit process version whose id is processId, name is processName and version
     * name is version. nativeType identifies language to be used to edit the process version.
     * If annotation is instantiated, it identifies the annotation file to be used.
     * If readOnly=1, annotations only are editable.
     *
     * @param version
     * @param nativeType
     * @param annotation
     * @param readOnly
     * @throws InterruptedException
     * @throws Exception
     */
    public void editProcess(ProcessSummaryType process, VersionSummaryType version,
                            String nativeType, String annotation, String readOnly) throws InterruptedException {

        String instruction = "", url = getHost();
        int offsetH = 100, offsetV = 200;
        int editSessionCode;
        EditSessionType editSession = new EditSessionType();
        editSession.setDomain(process.getDomain());
        editSession.setNativeType(nativeType);
        editSession.setProcessId(process.getId());
        editSession.setProcessName(process.getName());
        editSession.setUsername(this.getCurrentUser().getUsername());
        editSession.setVersionName(version.getName());
        editSession.setCreationDate(version.getCreationDate());
        editSession.setLastUpdate(version.getLastUpdate());
        if (annotation == null) {
            editSession.setWithAnnotation(false);
        } else {
            editSession.setWithAnnotation(true);
            editSession.setAnnotation(annotation);
        }
        try {
            // create and store an edit session
            editSessionCode = getService().writeEditSession(editSession);
            if ("XPDL 2.1".compareTo(nativeType) == 0) {
                url += getOryxEndPoint_xpdl() + Constants.SESSION_CODE;
            } else if ("EPML 2.0".compareTo(nativeType) == 0) {
                url += getOryxEndPoint_epml() + Constants.SESSION_CODE;
            } else {
                throw new ExceptionWriteEditSession("Native format not supported.");
            }
            url += "=" + editSessionCode;
            // add one parameter READ_ONLY: value is 1 when user chose to edit annotations,
            // otherwise value is 0.
            url += "&" + Constants.ANNOTATIONS_ONLY + "=" + readOnly;
            instruction += "window.open('" + url + "','','top=" + offsetH + ",left=" + offsetV
                    + ",height=600,width=800,scrollbars=1,resizable=1'); ";
            // Send http post to Oryx
            Clients.evalJavaScript(instruction);
        } catch (Exception e) {
            Messagebox.show("Cannot edit " + process.getName() + " ("
                    + e.getMessage() + ")", "Attention", Messagebox.OK,
                    Messagebox.ERROR);
        }
    }

    public void displayMessage(String mes) {
        this.shortmessageC.displayMessage(mes);
    }

    public void eraseMessage() {
        this.shortmessageC.eraseMessage();
    }


    public HeaderController getHeader() {
        return header;
    }

    public void setHeader(HeaderController header) {
        this.header = header;
    }

    public MenuController getMenu() {
        return menu;
    }

    public void setMenu(MenuController menu) {
        this.menu = menu;
    }

    public ProcessTableController getProcesstable() {
        return processtable;
    }

    public void setProcesstable(ProcessTableController processtable) {
        this.processtable = processtable;
    }

    public SimpleSearchController getSimplesearch() {
        return simplesearch;
    }

    public void setSimplesearch(SimpleSearchController simplesearch) {
        this.simplesearch = simplesearch;
    }

    public UserType getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserType currentUser) {
        this.currentUser = currentUser;
        if (currentUser == null) {
            this.msgWhenClose = null;
        } else {
            this.msgWhenClose = Constants.MSG_WHEN_CLOSE;
            this.searchHistory = this.currentUser.getSearchHistories();
        }
    }

    public ShortMessageController getShortmessageC() {
        return shortmessageC;
    }

    public void setShortmessageC(ShortMessageController shortmessageC) {
        this.shortmessageC = shortmessageC;
    }

    public Window getShortmessageW() {
        return shortmessageW;
    }

    public void setShortmessageW(Window shortmessageW) {
        this.shortmessageW = shortmessageW;
    }

    public String getOryxEndPoint_xpdl() {
        return OryxEndPoint_xpdl;
    }

    public String getOryxEndPoint_epml() {
        return OryxEndPoint_epml;
    }

    public Logger getLOG() {
        return LOG;
    }

    public String getHost() {
        return host;
    }

    public List<SearchHistoriesType> getSearchHistory() {
        return searchHistory;
    }

    /**
     * get list of domains
     */
    public List<String> getDomains() throws ExceptionDomains {
        DomainsType domainsType;
        domainsType = getService().readDomains();
        return domainsType.getDomain();
    }

    /**
     * get list of users' names
     *
     * @return
     * @throws org.apromore.portal.exception.ExceptionAllUsers
     *
     */
    public List<String> getUsers() throws ExceptionAllUsers {
        UsernamesType usernames = getService().readAllUsers();
        return usernames.getUsername();
    }

    /**
     * get list of formats: <k, v> belongs to getNativeTypes() <=> the file extension k
     * is associated with the native type v (<xpdl,XPDL 1.2>)
     *
     * @throws org.apromore.portal.exception.ExceptionFormats
     *
     */
    public HashMap<String, String> getNativeTypes() throws ExceptionFormats {
        HashMap<String, String> formats = new HashMap<String, String>();
        NativeTypesType nativeTypesDB = getService().readNativeTypes();
        for (int i = 0; i < nativeTypesDB.getNativeType().size(); i++) {
            formats.put(nativeTypesDB.getNativeType().get(i).getExtension(),
                    nativeTypesDB.getNativeType().get(i).getFormat());
        }
        return formats;
    }

}
