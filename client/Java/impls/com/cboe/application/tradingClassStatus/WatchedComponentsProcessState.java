/**
 * 
 */
package com.cboe.application.tradingClassStatus;

import java.util.HashMap;
import java.util.Iterator;

import com.cboe.interfaces.application.TCSProcessWatcherManager;
import com.cboe.processWatcher.ProcessStateConstants;
import com.cboe.processWatcher.WatchedProcess;

/**
 * @author Arun Ramachandran Dec 12, 2010
 *
 */
public class WatchedComponentsProcessState {
	
	private String processName;
	private boolean clusterUp;
	HashMap<String, WatchedProcess> cluster = new HashMap<String, WatchedProcess>();
	private TCSProcessWatcherManager processManager;
	
	public WatchedComponentsProcessState(String processGroupName, TCSProcessWatcherManager processManager) {
		this.processName = processGroupName; 
		this.processManager = processManager;
	}
	
	public boolean isClusterUp() {
		return clusterUp;
	}
	
	//#############################################################################
    // PW Event code will be inserted into each PW event, so the consumers of
    // these events will know more detail informations about those events.
    //
    // Define Legal PW Event Codes
    // 1. UpByHelpButCommFailureBySelf: some other PWs think the target is up,
    //    but I could not communicate with the target
    
	// 2. UpByHelpButTimeoutBySelf: some other PWs think the target is up,
    //    but my attempt to communicate with it times out
    
	// 3. UpNoResponseButCommFailureBySelf: No responses from other PWs during
    //    a time frame, I will assume my box is isolated from network somehow,
    //    so nobody can hear my request. In this case, assume the target is
    //    still up
    
	// 4. UpNoResponseButTimeoutBySelf: No reponses from other PWs during a time
    //    a time frame, and my attempt to communicate times out, I will assume
    //    my box is isolated from network somehow, so body can hear my request.
    //    In this case, assume the target is still up.
    
	// 5. DownCommFailure: My attempt to communicate with target gets CommFailure,
    //    after asking for helps from other PWs, the responses I receive during
    //    a time frame are all negative.
    
	// 6. DownTimeout: My attempt to communicate with target gets timeout,
    //    after asking for helps from other PWs, the responses I receive during
    //    a time frame are all nagative.
    
	// 7. UpNormal: My attempt communicate with target is successful.
    //#############################################################################

	public void updateClusterState(WatchedProcess wpStateChanged, int status) {
		String host = wpStateChanged.getHost();
		cluster.put(host, wpStateChanged);
		processState();
	}


	private void processState() {
		
		//ProcessState STATE_PROCESS_DOWN_POA_DOWN = -2;     
	    //ProcessState STATE_PROCESS_DOWN_NO_POA = -1; //do not know POA name, but process is down
		//ProcessState STATE_UNKNOWN = 0;
	    //ProcessState STATE_PROCESS_UP_NO_POA = 1;   //do not know POA name, but process is up
	    //ProcessState STATE_INACTIVE_POA = 2;         //imply process up
	    //ProcessState STATE_DISCARDING_POA = 3;   //imply process up
	    //ProcessState STATE_HOLDING_POA = 4;      //imply process up
	    //ProcessState STATE_ACTIVE_POA = 5;       //imply process up
		
		short clusterstate = ProcessStateConstants.STATE_UNKNOWN; //start with unknown state;
		for (Iterator<String> it = cluster.keySet().iterator();it.hasNext();) {
			WatchedProcess wp = cluster.get(it.next());
			if(wp.getState()>clusterstate) {
				clusterstate = wp.getState();
			}
		} 
		if(clusterstate == ProcessStateConstants.STATE_ACTIVE_POA) {
			clusterUp = true;
			processManager.removeFromDownList(processName);
		}else {
			clusterUp = false;
			processManager.addToDownList(processName);
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(processName+" | ClusterState:"+clusterUp+" | ");
		for(Iterator<String> it = cluster.keySet().iterator(); it.hasNext();) {
			WatchedProcess wp = cluster.get(it.next());
			sb.append(wp.getHost()).append(" | ").append(wp.getOrbName()).append(" | ").append(wp.getState()).append("\n");
		}
		return sb.toString();
	}
}
