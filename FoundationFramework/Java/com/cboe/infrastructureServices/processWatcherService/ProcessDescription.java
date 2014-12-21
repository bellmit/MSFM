package com.cboe.infrastructureServices.processWatcherService;

public class ProcessDescription {
	
	/**
	 * The following public static constants define the legal process type
	 * 
	 * 1. NOT_DEFINED: we do not know the process type
	 * 2. SOURCE: the process/component from where users initiate their 
	 *    	      loging session. This kind of component/process is also
	 *    		  monitored by SessionManagementService
	 * 3. CONNECTION: the process/component to which SOURCE components connect to. 
	 * 				  This type of process/component can be thought as gateway 
	 * 				  through which source components gain access to the trading 
	 * 				  system. This kind of component/process is also monitored by
	 *                SessionManagementService because their status will have a direct 
	 *                impact on the user login session
	 * 4. CRITICAL: the process/components which are the core processes of the 
	 *              system.
	 * 5. GENERAL: the process/components which are important but not essential.
	 */
	public static final short PROCESS_TYPE_NOT_DEFINED = 0;
	public static final short PROCESS_TYPE_SOURCE = 1;
	public static final short PROCESS_TYPE_CONNECTION = 2;
	public static final short PROCESS_TYPE_CRITICAL = 3;
	public static final short PROCESS_TYPE_GENERAL = 4;
	public static final boolean VARIABLE_PING_RATE = false;
	
	private String processName;
	private String orbName;
	private String hostName;
	private int portNumber;
	private String poaName;
	private boolean variablePingRate = VARIABLE_PING_RATE;
	//default to GENERAL type
	private short type = PROCESS_TYPE_GENERAL;
	//most processes may not reference other processes. Most likely the processes
	//referencing other processes are SOURCE type process/components
	private String[] referencedProcesses = new String[0];

	public ProcessDescription(){}
	
	public String getProcessName(){
		return processName;
	}
	
	public void setProcessName(String aProcessName){
		processName = aProcessName;
	}
	
	public String getOrbName(){
		return orbName;
	}
	
	public void setOrbName(String aOrbName) {
		orbName = aOrbName;
	}
	
	public String getHostName(){
		return hostName;
	}
	
	public void setHostName(String aHost){
		hostName = aHost;
	}
	
	public int getPortNumber(){
		return portNumber;
	}
	
	public void setPortNumber(int aNumber){
		portNumber = aNumber;
	}
	
	public String getPOAName(){
		return poaName;
	}
	
	public void setPOAName(String aName){
		poaName = aName;
	}
	
	public short getType(){
		return type;
	}
	
	public void setType(short aShort){
		type = aShort;
	}
	
	public String[] getReferencedProcesses(){
		return referencedProcesses;
	}
	
	public void setReferencedProcesses(String[] processes){
		referencedProcesses = processes;
	}
	
	public boolean isVariablePingRate(){
		return variablePingRate;
	}
	
	public void setVariablePingRate(boolean aBoolean) {
		variablePingRate = aBoolean;
	}
}
