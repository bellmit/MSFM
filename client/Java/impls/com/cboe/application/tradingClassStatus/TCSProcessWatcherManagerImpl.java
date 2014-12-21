package com.cboe.application.tradingClassStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.cboe.idl.cmiConstants.TradingClassStatusIndicators;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.session.TradingSessionStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.TCSProcessWatcherManager;
import com.cboe.interfaces.events.ProcessWatcherStatus;
import com.cboe.processWatcher.ProcessStatusPoaStateListener;
import com.cboe.processWatcher.ProcessWatcherPoaStateProxyECImpl;
import com.cboe.processWatcher.WatchedProcess;

 class TCSProcessWatcherManagerImpl extends BObject implements
		ProcessStatusPoaStateListener,TCSProcessWatcherManager {
	
	private static final WatchedProcess[] DEFAULT_PW_LIST = new WatchedProcess[0];
	//TODO: add property entry to processes.cas/fix.xml
	public static final String PW_EC_PROPERTY = "ProcessWatcher.ChannelName";
	private ProcessWatcherPoaStateProxyECImpl pw; // infra ProcessWatcher Proxy Object
	private String processWatcherEventChannelName;
	private ConcurrentLinkedQueue<String> bcDownList;
	private ConcurrentHashMap<String,WatchedComponentsProcessState> intrestedProcesses;
	private boolean turnOffTCS = new Boolean(System.getProperty("TurnOffTCS", "true")).booleanValue();
	private HashMap<String, ServerNamingConvention> namingConvention;
	private HashMap<String, String> orbToGroupName;
	private final ProductConfigurationQueryHelper pqHelper;
	private final TradingClassStatusSubscriptionsContainer subscriptions;;
	
	public TCSProcessWatcherManagerImpl() {
		bcDownList = new ConcurrentLinkedQueue<String>();
		intrestedProcesses = new ConcurrentHashMap<String, WatchedComponentsProcessState>();
		orbToGroupName = new HashMap<String, String>();
		namingConvention = new HashMap<String, ServerNamingConvention>();
		pqHelper = ProductConfigurationQueryHelper.getInstance();
		subscriptions = TradingClassStatusSubscriptionsContainer.getInstance();
		initialize();
	}

	public void processUp(WatchedProcess watchedProcess,
			String eventOriginator, short reasonCode) {
		String grpName = getGroupName(watchedProcess.getOrbName());
		if(!turnOffTCS && grpName !=null) {
			WatchedComponentsProcessState component = intrestedProcesses.get(grpName);
			if(component!=null) {
				component.updateClusterState(watchedProcess, ProcessWatcherStatus.PROCESS_UP);
			}
		}
		if(Log.isDebugOn()) {
			Log.debug("processUp called for group "+grpName+" and process "+watchedProcess);
		}
	}

	public void processDown(WatchedProcess watchedProcess,
			String eventOriginator, short reasonCode) {
		String grpName = getGroupName(watchedProcess.getOrbName());
		if(!turnOffTCS && grpName !=null) {
			if(!bcDownList.contains(grpName)){
				WatchedComponentsProcessState component = intrestedProcesses.get(grpName);
				if(component!=null) {
					component.updateClusterState(watchedProcess, ProcessWatcherStatus.PROCESS_DOWN);
				}
			}
		}
		if(Log.isDebugOn()) {
			Log.debug("processDown called for group "+grpName+" and process "+watchedProcess);
		}
	}

	public void processPoaStateUp(WatchedProcess watchedProcess,
			String eventOriginator, short reasonCode) {
		String grpName = getGroupName(watchedProcess.getOrbName());
		if(!turnOffTCS && grpName !=null) {
			WatchedComponentsProcessState component = intrestedProcesses.get(grpName);
			if(component!=null) {
				component.updateClusterState(watchedProcess, ProcessWatcherStatus.POA_UP);
			}
		}
		if(Log.isDebugOn()) {
			Log.debug("processPoaStateUp called for group "+grpName+" and process "+watchedProcess);
		}
	}

	public void processPoaStateDown(WatchedProcess watchedProcess,
			String eventOriginator, short reasonCode) {
		String grpName = getGroupName(watchedProcess.getOrbName());
		if(!turnOffTCS && grpName !=null) {
			WatchedComponentsProcessState component = intrestedProcesses.get(grpName);
			if(component!=null) {
				component.updateClusterState(watchedProcess, ProcessWatcherStatus.POA_DOWN);
			}
		}
		if(Log.isDebugOn()) {
			Log.debug("processPoaStateDown called for group "+grpName+" and process "+watchedProcess);
		}
	}

	public void registerWithProcessWatcher(){
		try {
			WatchedProcess[] pwList = getPWList();
			List<String> processNames = createInterestedProcessesList(pwList);
			getProcessWatcher().addProcessStatusListener(this, processNames);
		} catch (Exception e) {
			Log.exception(this, e);
		}
	}

	public WatchedProcess[] getPWList() {
		WatchedProcess[] pwList;
		try {
			List<WatchedProcess> watchList = getProcessWatcher()
					.getProcessWatchList();
			pwList = watchList.toArray(new WatchedProcess[watchList.size()]);
		} catch (Exception e) {
			Log.exception(this, e);
			pwList = DEFAULT_PW_LIST;
		}
		return pwList;
	}

	private void initialize(){
		pw = null;
		processWatcherEventChannelName = System.getProperty(PW_EC_PROPERTY,
				"ProdProcessWatcher");
		TradingSessionStruct tss[] = ProductConfigurationQueryHelper.getInstance().getTradingSessions();
		for (int i = 0; i < tss.length; i++) {
			String sessionName = tss[i].sessionName;
			ServerNamingConvention tmpSNC = new ServerNamingConvention(sessionName);
			namingConvention.put(sessionName, tmpSNC);
		}
	}

	/**
	 * 
	 */
	private List<String> createInterestedProcessesList(WatchedProcess[] pwList) {
		String[] pcsGroups = ProductConfigurationQueryHelper.getInstance().getProductGroups();
		List<String> processNames = new ArrayList<String>();
		for (int i = 0; i < pcsGroups.length; i++) {
			String groupName = pcsGroups[i];
			for (int j = 0; j < pwList.length; j++) {
				WatchedProcess wp = pwList[j];
				if(wp !=null && wp.getOrbName().toLowerCase().contains("ohserver")) {
					addToInterestedProcess(wp.getOrbName().substring(0, wp.getOrbName().indexOf(wp.getHost())), wp);
					orbToGroupName.put(wp.getOrbName(), wp.getOrbName().substring(0, wp.getOrbName().indexOf(wp.getHost())));
					processNames.add(wp.getProcessName());
					pwList[j] = null;
				}else if(wp !=null && (wp.getOrbName().contains(groupName))&&(wp.getOrbName().substring(0, wp.getOrbName().indexOf(wp.getHost()))).equals(groupName)) {
					addToInterestedProcess(groupName, wp);
					orbToGroupName.put(wp.getOrbName(), groupName);
					processNames.add(wp.getProcessName());
					pwList[j] = null;
				}
			}
		}
		if(Log.isDebugOn()) {
			Log.debug("orbToGroupName"+orbToGroupName.toString());	
		}
		Log.information(this, "TCS Watched Processes : "+getFormattedString(intrestedProcesses));
		return processNames;
	}
	
	
	private void addToInterestedProcess(String groupNameKey, WatchedProcess wpValue) {
		WatchedComponentsProcessState tmpAL = intrestedProcesses.get(groupNameKey);
		if(tmpAL == null){
			tmpAL = new WatchedComponentsProcessState(groupNameKey, this);
			intrestedProcesses.put(groupNameKey, tmpAL);
		}
		tmpAL.updateClusterState(wpValue,  wpValue.getState());
	}
	

	private String getFormattedString(
			ConcurrentHashMap<String, WatchedComponentsProcessState> map) {
		StringBuffer sb = new StringBuffer();
		for (Iterator<String> it = map.keySet().iterator();it.hasNext();) {
			String type = it.next();
			sb.append("\n"+type);
			sb.append("\n");
			sb.append(map.get(type));
		}
		return sb.toString();
	}

	private ProcessWatcherPoaStateProxyECImpl getProcessWatcher()
			throws Exception {
		try {
			if (pw == null) {
				pw = new ProcessWatcherPoaStateProxyECImpl(
						processWatcherEventChannelName);
				Log.information("Connected to :<"+processWatcherEventChannelName+"> processWatcher");
			}
		} catch (Exception e) {
			Log.exception(this, e);
			throw e;
		}
		return pw;
	}
	
	
	public String turnOffTCSNotifications(String[] turnOff){
		String message;
		if(turnOff[0].equalsIgnoreCase("false")){
			turnOffTCS = false;
			message = "TCS Notification is set to:ON via ar command";
			Log.alarm(this, message);
		}else if(turnOff[0].equalsIgnoreCase("true")){
			turnOffTCS = true;
			Log.information(this, "BC Down list before cleanup:"+bcDownList);
			bcDownList.clear();
			message = "TCS Notification is set to:OFF via ar command";
			Log.alarm(this, message);
		}else if(turnOff[0].equalsIgnoreCase("showList")){
			message = "List of BC down process(es)\n"+TradingClassStatusUtil.arrayToString(bcDownList.toArray());
			Log.alarm(this, message);
		}else {
			turnOffTCS = false;
			message = "Expected 'true / false / showList' to turn 'ON / OFF / DISPLAY' TCS Notification: Set to default:ON";
			Log.alarm(this, message);
		}
		return message;
	}
	
	
	public boolean isProcessDown(String groupName, String sessionName, boolean isOrder){
		if(!bcDownList.isEmpty() && !turnOffTCS) {
			String grpName = groupName;
			if(isOrder) {
				grpName = namingConvention.get(sessionName).getFullOHServerName(grpName);
			}
			boolean processDown = bcDownList.contains(grpName); 
			if(Log.isDebugOn()) {
				Log.debug(this, "isProcessDown:<"+processDown+"> was called for session:<"+sessionName+"> and group:<"+groupName+">");
			}
			return processDown;
		}
		return false;
	}
	
	private String getGroupName(String orbName) {
		return orbToGroupName.get(orbName);
	}
	
	@Override
	public boolean addToDownList(String groupName) {
		boolean isAdded = false;
		if(!turnOffTCS && !bcDownList.contains(groupName)) {
			subscriptions.notifyUsers(groupName, TradingClassStatusIndicators.CLOSED_OUTAGE);
			isAdded = bcDownList.add(groupName);
			Log.information("Cluster process :<"+groupName+"> is down");
		}
		return isAdded;
	}

	@Override
	public boolean removeFromDownList(String groupName) {
		boolean isRemoved = false;
		if(bcDownList.contains(groupName)) {
			subscriptions.notifyUsers(groupName, TradingClassStatusIndicators.OPEN_AFTER_OUTAGE);
			isRemoved = bcDownList.remove(groupName);
			Log.information("Cluster process :<"+groupName+"> is up");
		}
		return isRemoved;
	}

	@Override
	public boolean isServerDownListEmpty() {
		return bcDownList.isEmpty();
	}

	@Override
	public boolean isProcessDown(OrderStruct order) {
		String[] sessionNames = order.sessionNames;
		String sessionname = null;
		if(sessionNames!=null && sessionNames.length>0) {
			sessionname = sessionNames[0];
		}
		String grpName = pqHelper.getGroupNameForClass(order.classKey, sessionname);
		return isProcessDown(grpName, sessionname, true);
	}

	@Override
	public boolean isProcessDown(int classKey, String sessionName,
			boolean isOrder) {
		return isProcessDown(pqHelper.getGroupNameForClass(classKey, sessionName), sessionName, isOrder);
	}	
}

