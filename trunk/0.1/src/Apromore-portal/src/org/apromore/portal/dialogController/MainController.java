package org.apromore.portal.dialogController;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apromore.portal.common.Constants;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDao;
import org.apromore.portal.exception.ExceptionDeleteProcess;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.exception.ExceptionWriteEditSession;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.DomainsType;
import org.apromore.portal.model_manager.EditSessionType;
import org.apromore.portal.model_manager.NativeTypesType;
import org.apromore.portal.model_manager.ProcessSummariesType;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.UserType;
import org.apromore.portal.model_manager.UsernamesType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;



/*
 * Controller for the window index.zul
 * 
 */
public class MainController extends Window {

	private Window mainW;
	private HeaderController header;
	private MenuController menu;
	private ProcessTableController processtable;
	private SimpleSearchController simplesearch;
	private UserType currentUser ;				// the connected user, if any
	private String confirmCloseMsg;
	private ShortMessageController shortmessageC;
	private Window shortmessageW;
	private String host;
	private String OryxEndPoint_xpdl;
	private String OryxEndPoint_epml;
	private Logger LOG;

	// uncomment when ready
	//private NavigationController navigation;
	//private RefinedSearchController refinedsearch;

	/**
	 * onCreate is executed after the main window has been created
	 * it is responsible for instantiating all necessary controllers
	 * (one for each window defined in the interface)
	 * 
	 * see description in index.zul
	 * @throws InterruptedException 
	 */
	public void onCreate() throws InterruptedException {		
		try {
			this.LOG = Logger.getLogger(MainController.class.getName());
			/**
			 * to get data
			 */
			this.mainW = (Window) this.getFellow("mainW");
			this.shortmessageW = (Window) this.getFellow("shortmessagescomp").getFellow("shortmessage");
			this.shortmessageC = new ShortMessageController(shortmessageW);
			this.processtable = new ProcessTableController(this);
			this.header = new HeaderController (this);
			this.simplesearch = new SimpleSearchController(this);
			this.menu = new MenuController(this);

			//this.navigation = new NavigationController (this);

			this.currentUser = null;
			this.confirmCloseMsg ="You are about to leave Apromore. You might loose unsaved data.";
			// read Oryx access point in properties
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(Constants.PROPERTY_FILE);;  
			Properties properties = new Properties();  
			properties.load(inputStream);  
			this.host = properties.getProperty("Host"); 
			this.OryxEndPoint_xpdl = properties.getProperty("OryxEndPoint_xpdl");  
			this.OryxEndPoint_epml = properties.getProperty("OryxEndPoint_epml");  	
		} catch (Exception e) {
			Messagebox.show("Repository not available ("+e.getMessage()+")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	/**
	 * register an event listener for the clientInfo event (to prevent user to close the browser window)
	 */
	public void onClientInfo (ClientInfoEvent event) {
		Clients.confirmClose(this.confirmCloseMsg);
	}

	public void displayProcessSummaries(ProcessSummariesType processSummaries) throws Exception {
		int activePage = this.processtable.getPg().getActivePage();
		this.processtable.emptyProcessSummaries();
		this.processtable.newPaging();
		this.processtable.displayProcessSummaries(processSummaries);
		int lastPage = this.processtable.getPg().getPageCount()-1;
		if (lastPage<activePage) {
			this.processtable.getPg().setActivePage(lastPage);
		} else {
			this.processtable.getPg().setActivePage(activePage);
		}
	}

	public void updateActions (){

		Boolean connected = this.currentUser != null ;

		this.simplesearch.getPrevioussearchesCB().setVisible(connected);
		this.simplesearch.getSimplesearchesBu().setVisible(connected);
		// disable/enable menu items in menu bar
		Iterator<Component> itC = this.menu.getMenuB().getFellows().iterator();
		while (itC.hasNext()) {
			Component C = itC.next();
			if (C.getClass().getName().compareTo("org.zkoss.zul.Menuitem")==0) {
				if (C.getId().compareTo("processMerge")!=0) {
					((Menuitem) C).setDisabled(!connected);
				}
			}
		}
	}

	public void reloadProcessSummaries () throws Exception {
		RequestToManager request = new RequestToManager();
		ProcessSummariesType processSummaries = request.ReadProcessSummariesType("");
		String message = null;
		if (processSummaries.getProcessSummary().size()>1) {
			message = " processes.";
		} else {
			message = " process.";
		}
		this.displayMessage(processSummaries.getProcessSummary().size() + message);
		this.simplesearch.clearSearches();
		this.displayProcessSummaries(processSummaries);
	}

	/**
	 * reset displayed informations:
	 * - short message
	 * - process summaries
	 * - simple search
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
	 * @param returnedProcess
	 */
	public void displayNewProcess(ProcessSummaryType returnedProcess) {
		this.processtable.displayNewProcess(returnedProcess);
		this.displayMessage(this.processtable.getProcessHM().size() + " processes.");
	}

	/**
	 * Send request to Manager: deleted process versions given as parameter
	 * @param processVersions
	 * @throws InterruptedException 
	 * @throws JAXBException 
	 * @throws ExceptionDao 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public void deleteProcessVersions(
			HashMap<ProcessSummaryType, List<VersionSummaryType>> processVersions) throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, ExceptionDao, JAXBException {
		RequestToManager request = new RequestToManager();
		try {
			request.DeleteProcessVersions (processVersions);
			this.processtable.unDisplay(processVersions);
			int nb = 0; // to count how many process version(s) deleted
			Set<ProcessSummaryType> keySet = processVersions.keySet();
			Iterator it = keySet.iterator();
			while (it.hasNext()){
				nb += processVersions.get(it.next()).size();
			}
			String message;
			if (nb > 1) {
				message = " process versions deleted.";
			} else {
				message = " process version deleted.";
			}
			displayMessage(message);
		} catch (ExceptionDeleteProcess e) {
			e.printStackTrace();
			Messagebox.show("Deletion failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	public void editProcess(Integer processId, String processName, String version, 
			String nativeType, String domain, String annotation) throws Exception {

		String instruction="", url=getHost();
		int offsetH = 100, offsetV=200;
		int editSessionCode;
		EditSessionType editSession = new EditSessionType();
		editSession.setDomain(domain);
		editSession.setNativeType(nativeType);
		editSession.setProcessId(processId);
		editSession.setProcessName(processName);
		editSession.setUsername(this.getCurrentUser().getUsername());
		editSession.setVersionName(version);
		if (annotation==null) {
			editSession.setWithAnnotation(false);
		} else {
			editSession.setWithAnnotation(true);
			editSession.setAnnotation(annotation);
		}
		try {
			RequestToManager request = new  RequestToManager();
			editSessionCode = request.WriteEditSession(editSession);
			if ("XPDL 2.1".compareTo(nativeType)==0) {
				url += getOryxEndPoint_xpdl()+"sessionCode=";
			} else if ("EPML 2.0".compareTo(nativeType)==0) {
				url += getOryxEndPoint_epml()+"sessionCode=";
			} else {
				throw new ExceptionWriteEditSession("Native format not supported.");
			}
			url += editSessionCode;
			instruction += "window.open('" + url + "','','top=" + offsetH + ",left=" + offsetV 
			+ ",height=600,width=800,scrollbars=1,resizable=1'); ";
			Clients.evalJavaScript(instruction);
		} catch (ExceptionWriteEditSession e) {
			Messagebox.show("Cannot edit " + processName + " (" 
					+e.getMessage()+")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		}
	}
	public void displayMessage (String mes) {
		this.shortmessageC.displayMessage(mes);
	}

	public void eraseMessage () {
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

	public String getConfirmCloseMsg() {
		return confirmCloseMsg;
	}

	public void setConfirmCloseMsg(String confirmCloseMsg) {
		this.confirmCloseMsg = confirmCloseMsg;
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

	/**
	 * get list of domains
	 */
	public List<String> getDomains() throws ExceptionDomains {			
		RequestToManager request = new RequestToManager();
		DomainsType domainsType;
		domainsType = request.ReadDomains();
		return domainsType.getDomain();
	}
	/**
	 * get list of users' names
	 * @return
	 * @throws ExceptionAllUsers
	 */
	public List<String> getUsers() throws ExceptionAllUsers {
		RequestToManager request = new RequestToManager();
		UsernamesType usernames = request.ReadAllUsers();
		return usernames.getUsername();
	}
	/**
	 * get list of formats: <k, v> belongs to getNativeTypes() <=> the file extension k 
	 * is associated with the native type v (<xpdl,XPDL 1.2>)
	 * @throws ExceptionFormats 
	 */
	public HashMap<String,String> getNativeTypes() throws ExceptionFormats {
		HashMap<String,String> formats = new HashMap<String, String>();
		RequestToManager request = new RequestToManager();
		NativeTypesType nativeTypesDB = request.ReadNativeTypes();
		for (int i=0; i<nativeTypesDB.getNativeType().size();i++){
			formats.put(nativeTypesDB.getNativeType().get(i).getExtension(),
					nativeTypesDB.getNativeType().get(i).getFormat());
		}
		return formats;
	}
}
