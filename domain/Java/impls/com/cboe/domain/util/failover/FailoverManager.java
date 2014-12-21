package com.cboe.domain.util.failover;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.cboe.ORBInfra.ORB.Orb;
import com.cboe.domain.util.InputOutputStream;
import com.cboe.domain.util.StreamLogger;
import com.cboe.domain.util.adminRequest.ArContext;
import com.cboe.domain.util.adminRequest.ArReturnResult;
import com.cboe.domain.util.remoteShell.ExecuteTransportCommand;
import com.cboe.domain.util.remoteShell.ExitStatusException;
import com.cboe.domain.util.remoteShell.Transport;
import com.cboe.domain.util.remoteShell.TransportCommand;
import com.cboe.domain.util.remoteShell.TransportFactory;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedCommand;

/**
 * @author baranski
 *
 */
public class FailoverManager
{
    static final long AR_TIMEOUT = 10000;
    static final long TRANSPORT_TIMEOUT = 10000;
    static final String secondPhaseNamePattern = ".*TradeServer.*";
    static final String PREPARE_SLAVE_ARG = "ALL";
    static final String DISABLE_MASTER_ARG = "LifeLine";
    static final int UNKNOWN_SYNCH_STATUS = 0;
    static final int IN_SYNCH_SYNCH_STATUS = 1;
    static final int OUT_OF_SYNCH_SYNCH_STATUS = 2;
    static final int ERROR_SYNCH_STATUS = -1;
    
    private static final long STATUS_TIMER_VALUE = 30000; // in millis
    
    
    
    
    private Future<Transport> masterTransportFuture = null;
    private Future<Transport> slaveTransportFuture  = null;
    private ArrayList<SynchStatusUpdateListener> statusListeners = new ArrayList<SynchStatusUpdateListener>();
    private StatusTimerTask stt = null;
    private Timer statusTimer = new Timer("StatusTimerThread");
    private int synchStatus = UNKNOWN_SYNCH_STATUS;
    private long synchStatusReceivedTime = 0;

    FailoverManagerData data = new FailoverManagerData(new ArrayList<ProcessInfo>(), new ArrayList<ProcessInfo>(), new TransportFactory(), new InputOutputStream(), new InputOutputStream(), false);
    
    /**
     * 
     * @return
     */

    public ArContext getSlaveArContext(){
        return data.slaveArContext;   
    }
    
    public void setSlaveArContext(ArContext context){
        data.slaveArContext = context;   
    }
    public FailoverManagerData getData(){
      return data;   
    }
    /**
     * 
     * @return
     */
    public ArContext getMasterArContext(){
        return data.masterArContext;   
    }
    
    public void setMasterArContext(ArContext context){
        data.masterArContext = context;   
    }

    public StreamLogger getErrorLog()
    {
        return data.errorLog;
    }
    /**
     * @return
     */
    public InputStream getInfoInputStream()
    {
        return data.infoInputStream;
    }

    /**
     * @return
     */
    public StreamLogger getInfoLog()
    {
        return data.infoLog;
    }

    /**
     * 
     * @return
     */
    public InputStream getErrorInputStream()
    {
        return data.errorInputStream;
    }

    /**
     * @return
     */
    public String getMasterHost()
    {
        return data.masterHost;
    }


    /**
     * @return
     */
    public String getMasterUserId()
    {
        return data.masterUserId;
    }


    /**
     * @return
     */
    public String getSlaveHost()
    {
        return data.slaveHost;
    }


    /**
     * @return
     */
    public String getSlaveUserId()
    {
        return data.slaveUserId;
    }



    /**
     * @return
     */
    public Transport getMasterTransport()
    {
        return data.masterTransport;
    }


    /**
     * @return
     */
    public Transport getSlaveTransport()
    {
        return data.slaveTransport;
    }

    /**
     * @return
     */
    public TransportFactory getTransportFactory()
    {
        return data.transportFactory;
    }


    /**
     * @return
     */

    /**
     * @param p_masterHost
     * @param p_masterUserId
     * @param p_masterPassword
     * @param p_slaveHost
     * @param p_slaveUserId
     * @param p_slavePassword
     * @param p_keyFileName
     * @param p_processNames
     * @param collectTiming
     */
    public FailoverManager(String p_masterHost, String p_masterUserId, String p_masterPassword,
            String p_slaveHost, String p_slaveUserId, String p_slavePassword, String p_keyFileName, String[] p_processNames)
    {
        data.masterHost = p_masterHost;
        data.masterPassword = p_masterPassword;
        data.masterUserId = p_masterUserId;
        data.slaveHost = p_slaveHost;
        data.slavePassword = p_slavePassword;
        data.slaveUserId = p_slaveUserId;
        data.pKeyFile = p_keyFileName;
        data.processNames = p_processNames;
    }

    public void initialize(boolean skipMaster) throws Exception{
      initialize(skipMaster, true, false);
    }
    public void sendStatusEvent(SynchStatus[] p_ss)
    {
        SynchStatusUpdateListener[] listeners = getListeners();
        for (int i = 0; i < listeners.length; i++){
            listeners[i].acceptSynchStatusUpdate(p_ss);
        }
        
    }  
    public  void registerForStatusUpdates(SynchStatusUpdateListener sul){
        boolean start = false;
        synchronized(statusListeners){
            if(statusListeners.size() == 0){
                start = true;   
            }
            statusListeners.add(sul);
            if(start){
                startStatusUpdates();
            }
        }
    }

    private void startStatusUpdates()
    {
       
        stt = new StatusTimerTask(this);
        statusTimer.schedule(stt, 0, STATUS_TIMER_VALUE);
        
    }

    public void deRegisterForStatusUpdates(SynchStatusUpdateListener sul){
        synchronized(statusListeners){
            statusListeners.remove(sul);
            if(statusListeners.size() == 0){
                stopStatusUpdates();   
            }
        }
    }

    private void stopStatusUpdates()
    {
        stt.cancel();
    }

    private SynchStatusUpdateListener[] getListeners(){
        synchronized(statusListeners){
            return statusListeners.toArray(new SynchStatusUpdateListener[statusListeners.size()]);
        }
    }
    
    public  SynchStatus[] getStatusWithTimeout(long timeoutInMillis, ExecutorService p_tpe) throws InterruptedException, ExecutionException, TimeoutException{
        return (p_tpe.submit(new SynchStatusCommand(this))).get(timeoutInMillis, TimeUnit.MILLISECONDS);
    }
    public  SynchStatus[] getStatus(){
        
        ArrayList<SynchStatus> returnValue = new ArrayList<SynchStatus>();
        String command = "synchPointStatus";
        String[] args = {"true"};
        ArContext slaveContext = getSlaveArContext();
        synchronized(statusTimer){
            
            try
            {
                if(data.statusTpe == null){
                    data.statusTpe = Executors.newFixedThreadPool(15); //Use fix number of threads for status to avoid running out of threads if timeouts occur
                }
                initTransports();
                if (data.slaveTransport == null || !data.slaveTransport.isConnected()){
                    findAndInitializeValidSlave(true); //find a valid slave and initialize it
                }

                if(!slaveContext.isArCommandAdded(command, args)){
                    slaveContext.addArCommand(command, args, getErrorLog(), getInfoLog(), AR_TIMEOUT, data.statusTpe, true);
                }
                List<ArReturnResult> rrList = null;
                try{
                    rrList = slaveContext.executeAll(getErrorLog(), getInfoLog(), AR_TIMEOUT, data.statusTpe, command, args, true);
                }
                catch(TimeoutException te){
                    // This is handled later
                }
                ArReturnResult result = null;
                for(int k = 0; k < rrList.size(); k++){
                    result = rrList.get(k);
                    String orbName = result.getFai().getOrbName();
                    SynchStatus synchStatus = new SynchStatus();
                    synchStatus.setInSynchState(SynchStatus.ERROR);
                    synchStatus.setOrbName(orbName);
                    returnValue.add(synchStatus);
                    if(result.getFai().isTimeout()){
                        String error = "Process: " + orbName + " " + command + " admin request timed out. Timeout value was " + AR_TIMEOUT + " ms";
                        getErrorLog().println(error);
                        getInfoLog().println(error);
                        synchStatus.setInSynchState(SynchStatus.TIMEOUT);
                        continue;
                    }
                    getSynchStatusFromServer(synchStatus, result.getOutput());            
                }
                return returnValue.toArray(new SynchStatus[returnValue.size()]);
            }
            catch (Exception e)
            {
                getErrorLog().exception("Exception caught during status updater initialization", e);
                getInfoLog().println("Unable to to get status updates - exception occurred: " + e.getMessage());
                stt.cancel(); // stop the timer
            }
        }
        return new SynchStatus[0];
       
    }
    
    
    private void getSynchStatusFromServer(SynchStatus synchStatus, String serverOutput) throws Exception
    {
        try{
            if(serverOutput.contains("No synchpoint status maintained on the master side")){
                throw new Exception("Called the master side instead of slave");   
            }
            String[] tokens = serverOutput.split("\\|");
            for(int i = 0; i < tokens.length; i++){
                if(tokens[i].trim().startsWith("timestamp=")){
                    synchStatus.setLastSynchTimestamp(tokens[i].split("=")[1]);
                }else if(tokens[i].trim().startsWith("synchPointsMissed=")){
                    synchStatus.setNbrOfSynchPointsMissed(Integer.parseInt(tokens[i].split("=")[1]));
                }
                else if(tokens[i].trim().startsWith("result=")){
                    String result = tokens[i].split("=")[1];
                    result = result.trim();
                    if(result.equals("IN_SYNCH")){
                        synchStatus.setInSynchState(SynchStatus.IN_SYNCH);
                    }else if(result.equals("OUT_OF_SYNCH")){
                        synchStatus.setInSynchState(SynchStatus.OUT_OF_SYNCH);
                    }else{
                        synchStatus.setInSynchState(SynchStatus.UNKNOWN_STATE);
                    }
                }
                else if(tokens[i].trim().startsWith("Count")){
                    synchStatus.appendMismatchDetail("\n " + tokens[i].trim());
                }
            }
        }catch(Exception e1){
            
            getErrorLog().exception("Exception caught while parsing the synchPointStatus ar command output: " + synchStatus.getOrbName() + ":" + serverOutput  , e1);
            throw e1;
        }
    }

    /**
     * 
     */
    public void initTransports(){
        if(data.tpe == null || 
                (data.masterTransport != null && !data.masterTransport.isConnected()) || (data.slaveTransport != null && !data.slaveTransport.isConnected()) || 
                masterTransportFuture == null || slaveTransportFuture == null){
         
        data.tpe = Executors.newCachedThreadPool();
        Orb.init();
        slaveTransportFuture = initSlaveTransport();
        masterTransportFuture = initMasterTransport();
        }
    }
    
    
    public synchronized void initialize(boolean skipMaster, boolean p_prepareSlave, boolean p_disableMaster) throws Exception{
        
     initialize(skipMaster,p_prepareSlave, p_disableMaster, true);  
    }
    
    
    /**
     * 
     * @param skipMaster
     * @param p_prepareSlave
     * @param p_disableMaster
     * @throws Exception
     */
    public synchronized void initialize(boolean skipMaster, boolean p_prepareSlave, boolean p_disableMaster, boolean skipSynchStatusCheck) throws Exception{

        initTransports();   

        findAndInitializeValidSlave(skipMaster);

        intializeArCommands(skipMaster, p_prepareSlave, p_disableMaster);

        data.initialized = true;
        
        if(!skipSynchStatusCheck){
            checkSynchStatus();
        }
        
        
    }

    private void checkSynchStatus() throws SynchStatusException
    {
        StringBuffer msg = null;
        try{
            SynchStatus[] ss = getStatusWithTimeout(AR_TIMEOUT, data.tpe);
            for(int i = 0; i < ss.length; i++){
                if((ss[i].getInSynchState() != SynchStatus.IN_SYNCH) || (ss[i].getNbrOfSynchPointsMissed() > 0)){
                    if(msg == null){
                        msg = new StringBuffer("The following process(es) have synchronization problems: \n");   
                    }
                    msg.append(ss[i].toString() + "\n");
                }
            }
        }catch(Exception e1) {
            getErrorLog().exception("Exception occurred while checking the synch status before failover", e1);
            throw new SynchStatusException("Unable to verify the synch status of the cluster", e1);
        }
        if(msg != null){
            throw new SynchStatusException(msg.toString());
        }
    }

    protected void intializeArCommands(boolean skipMaster, boolean p_prepareSlave,
            boolean p_disableMaster) throws Exception, IOException, ExitStatusException,
            InterruptedException, ExecutionException, TimeoutException
    {
        String[] args = {"true"};
        getSlaveArContext().addArCommand("goMasterCommand", args, getErrorLog(), getInfoLog(), AR_TIMEOUT, data.tpe, false);
        
        String[] argsUserPoa = {PREPARE_SLAVE_ARG};
        if(p_prepareSlave){
            getSlaveArContext().addArCommand("prepareSlaveOnFailOverCommand", argsUserPoa, getErrorLog(), getInfoLog(), AR_TIMEOUT, data.tpe, false);
        }
        
        if(!skipMaster && p_disableMaster){
            if(data.piAlMaster.size() == 0){
                getProcessInfo(data.masterTransport, data.piAlMaster);  
                initMasterFai();
            }
            String[] argsUserPoa1 = {DISABLE_MASTER_ARG};
            getMasterArContext().addArCommand("disableMasterOnFailOverCommand", argsUserPoa1, getErrorLog(), getInfoLog(), AR_TIMEOUT, data.tpe, false);   
        }
    }

    protected void findAndInitializeValidSlave(boolean skipMaster) throws Exception
    {
        Exception masterFailedException = null;
        Exception slaveFailedException = null;
        boolean validSlave = false;
        
        if(!skipMaster){
            try{
                data.masterTransport = masterTransportFuture.get(TRANSPORT_TIMEOUT, TimeUnit.MILLISECONDS);
                getErrorLog().println("Finished getting Master side command transport to " + data.masterHost + " ...");
            }
            catch(Exception e){
                getInfoLog().println("CANNOT CONTINUE FAILOVER WITHOUT CONNECTING TO HOST: " + data.masterHost );
                getErrorLog().println("CANNOT CONTINUE FAILOVER WITHOUT CONNECTING TO HOST: " + data.masterHost);
                //Just log this exception here
                getErrorLog().exception("Transport initialization failed", e);
                MasterFailedException mfe = new MasterFailedException("Unable to communicate with host: " + data.masterHost,e);
                masterFailedException = mfe; //remember that this failed
            }
        }
        getInfoLog().println("Checking if " + data.slaveHost + " is a valid slave");
        try{
            getErrorLog().println("Getting Slave side command transport to " + data.slaveHost + " ...");
            data.slaveTransport = slaveTransportFuture.get(TRANSPORT_TIMEOUT, TimeUnit.MILLISECONDS);
            getErrorLog().println("Finished getting Slave side command transport to " + data.slaveHost + " ...");
            getProcessInfo(data.slaveTransport, data.piAlSlave); //get slave processInfo
        }
        catch(Exception e){
            getErrorLog().exception("Exception thrown while attempting to execute a shell command on host: " + data.slaveHost,e );
            getInfoLog().println("Exception thrown while attempting to execute a shell command on host: " + data.slaveHost);

            if(!skipMaster){
                getInfoLog().println("CANNOT CONTINUE FAILOVER WITHOUT CONNECTING TO HOST: " + data.slaveHost );
                getErrorLog().println("CANNOT CONTINUE FAILOVER WITHOUT CONNECTING TO HOST: " + data.slaveHost);
                if(masterFailedException == null){ //if the other side was initialized ok assume that they are the slave
                    slaveFailedException = new MasterFailedException("Unable to communicate with host: " + data.slaveHost,e);
                }
                else{
                    // It looks like both sides are having issues. Re-throw the current exception
                    throw e;
                }
            }
        }

        try{
            if(data.slaveTransport != null){ //if couldn't initialize the transport skip the rest. This cannot be the valid slave
                initSlaveFai();
                validSlave = checkIfValidSlave(getSlaveArContext());
            }
        }catch(Exception e){ //This is not fatal since this may be the master that is hanging 
            getInfoLog().println("Exception thrown while checking for valid slave on host: " + data.slaveHost);
            getErrorLog().println("Exception thrown while checking for valid slave on host: " + data.slaveHost);
        }

        if(masterFailedException != null){
            if(validSlave){
                getErrorLog().println("Master side failed. Slave side is valid.");
                //Slave looks ok but the master failed/ Need to stop and throw MasterFailedException
                throw  masterFailedException;        
            }else{
                getErrorLog().println("Neither side is a valid slave. Failover is not possible.");
                throw new Exception("Neither side is a valid slave. Failover is not possible.");
            }
        }

        if(!validSlave){//either Master side or processes are down
            getInfoLog().println(data.slaveHost + " is not a valid slave host. Will check if " + data.masterHost + " is a valid slave");
            getErrorLog().println(data.slaveHost + " is not a valid slave host. Will check if " + data.masterHost + " is a valid slave");
            swapMasterAndSlave(); //original master becomes slave and original slave becomes master
            try{
                if((data.slaveTransport == null) || !data.slaveTransport.isConnected()){
                    slaveTransportFuture = initSlaveTransport(); //if previously skipped, now we need to init  
                    data.slaveTransport = slaveTransportFuture.get(TRANSPORT_TIMEOUT, TimeUnit.MILLISECONDS);
                }
                getProcessInfo(data.slaveTransport, data.piAlSlave); //get new slave's processInfo
                initSlaveFai(); // init fast admin invokers
                validSlave = checkIfValidSlave(getSlaveArContext());
            }
            catch(Exception e){
                getInfoLog().println("Exception thrown while checking for valid slave on host: " + data.slaveHost);
                getErrorLog().exception("Exception thrown while checking for valid slave on host: " + data.slaveHost, e);
            }
            if(!validSlave){
                //Neither side seems to be a slave. Either we are running 2 masters or slave processes are down
                //Cannot continue with the fail-over
                getErrorLog().println("Neither side is a valid slave. Failover is not possible.");
                throw new Exception("Neither side is a valid slave. Failover is not possible.");
            }
            else{

                if(slaveFailedException != null){
                    //this is a valid slave but the other side transport is not up. throw MasterFailed Exception to let them know.    
                    throw slaveFailedException;
                }

            }
        }
    }

    /**
     * 
     */
    private void swapMasterAndSlave()
    {

        getErrorLog().println("Swapping master and slave side");
        ArContext copyCtx = null;
        String masterCopy = null;
        Transport masterCopyTransport = null;
        ArrayList<ProcessInfo> piAlCopy = null;

        masterCopy = data.masterHost;
        data.masterHost = data.slaveHost;
        data.slaveHost = masterCopy;

        masterCopy = data.masterPassword;
        data.masterPassword = data.slavePassword;
        data.slavePassword = masterCopy;

        masterCopy = data.masterUserId;
        data.masterUserId = data.slaveUserId;
        data.slaveUserId = masterCopy;

        masterCopyTransport = data.masterTransport;
        data.masterTransport = data.slaveTransport;
        data.slaveTransport = masterCopyTransport;

        piAlCopy = data.piAlMaster;
        data.piAlMaster = data.piAlSlave;
        data.piAlSlave = piAlCopy;

        copyCtx = data.masterArContext;
        data.masterArContext = data.slaveArContext;
        data.slaveArContext = copyCtx;
    }


    /**
     * @param p_faiAl
     * @return
     * @throws UnsupportedCommand
     * @throws InterruptedException
     */
    protected boolean checkIfValidSlave(ArContext ctx) throws UnsupportedCommand, InterruptedException
    {
        if(ctx.getFaiList().size() == 0){
            getErrorLog().println("The slave process list is empty");
            return false;
        }
        String[] args = new String[0];
        ArReturnResult result = null;
        boolean setRR = false;
        try{
            setRR = ctx.addArCommand("isValidSlaveCommand", args, getErrorLog(), getInfoLog(), AR_TIMEOUT, data.tpe, false);
        }
        catch(Exception e){

            //Some or all processes may be down or hanging. Probably this is not the slave side then
            getErrorLog().exception("Execution exception occurred while setting up isValidSlaveCommand: " , e);
        }
        if(setRR == false){
            getErrorLog().println("Unable to set up isValidSlaveCommand: ");
            return false;
        }
        
        List<ArReturnResult> rrList = null;
        try{
            rrList = ctx.executeAll(getErrorLog(), getInfoLog(), AR_TIMEOUT, data.tpe, "isValidSlaveCommand", new String[0], false);
        }
        catch (Exception e)
        {
            //Exception already logged inside the context. Just return false here.
            return false;
        }
        for(int k = 0; k < rrList.size(); k++){
                result = rrList.get(k);
            if(result.getFai().isTimeout()){
                String error = "Process: " + ctx.getFaiList().get(k).getOrbName() + " isValidSlaveCommand admin request timed out. Timeout value was " + AR_TIMEOUT + " ms";
                getErrorLog().println(error);
                getInfoLog().println(error);
                return false; 
            }
            else if(!result.getOutput().trim().equalsIgnoreCase("true")){
                String msg = "Process: " + ctx.getFaiList().get(k).getOrbName() + " FF state is NOT Slave";
                getErrorLog().println(msg);
                getInfoLog().println(msg);
                return false;
            }
            String msg = "Process: " + ctx.getFaiList().get(k).getOrbName() + " FF state is Slave";
            getErrorLog().println(msg);
            getInfoLog().println(msg);
        }
        String msg = "All processes on " + data.slaveHost + " are valid Slaves. !!!";
        getErrorLog().println(msg);
        getInfoLog().println(msg);
        return true;
    }
    
    public long failover(boolean skipMaster, boolean p_useSinglePhase, boolean p_restartKilledProcesses, boolean p_asynchRestart) throws Exception{
      return failover(skipMaster, p_useSinglePhase, p_restartKilledProcesses,  p_asynchRestart, true, false);   
    }

    /**
     * @param skipMaster
     * @param p_restartKilledProcesses
     * @param p_asynchRestart 
     * @param p_disableMaster 
     * @param p_prepareSlave 
     * @return Time in millisecunds it took to execute the failover
     * @throws Exception
     */
    public long failover(boolean skipMaster, boolean p_useSinglePhase, boolean p_restartKilledProcesses, boolean p_asynchRestart, boolean p_prepareSlave, boolean p_disableMaster) throws Exception{
        if(!data.initialized){
            initialize(skipMaster, p_prepareSlave, p_disableMaster);   
        }
        if(p_prepareSlave){
            prepareSlave();
        }
        
        long p1 = System.currentTimeMillis();
        if(!skipMaster){
            if(p_disableMaster){
                disableMaster();
            }else{
                String pids = getPidsToKill();
                boolean validPid = false;
                if(pids != null){ 
                    if(pids.length() > 0){
                        validPid = true;
                    }
                }
                if(validPid){
                  //THE FAILOVER STARTS HERE !!!
                    p1 = System.currentTimeMillis(); // About to kill the Master. Start the timer now.
                    killMaster(pids);
                }
                else{
                    getInfoLog().println("Unable to retrieve master PID. Failover aborted.");
                    getErrorLog().println("Unable to retrieve master PID. Failover aborted." );
                    throw new Exception("Unable to retrieve master PID. Failover aborted.");
                }
            }
        }
        goMasterOnSlave(p_useSinglePhase);
        // THE FAILOVER ENDS HERE
        long p2 = System.currentTimeMillis();

        String logResults ="Failover Time: " + " " + (p2 - p1);
        getErrorLog().getInputStream().feed(logResults.getBytes(), logResults.length());
        getErrorLog().println("\n\n FAILOVER WAS SUCCESSFUL - FAILOVER TIME WAS " + (p2 - p1) + " millis. \n\n");
        if(p_restartKilledProcesses){
            startSlaveAfterFailover(p_asynchRestart);
        }
        swapMasterAndSlave();
        return (p2 - p1);
    }

    /**
     * @throws Exception
     */
    protected void disableMaster() throws Exception
    {
        String[] args = {DISABLE_MASTER_ARG};
        List<ArReturnResult> rrList = getMasterArContext().executeAll(getErrorLog(), getInfoLog(), AR_TIMEOUT, data.tpe, "disableMasterOnFailOverCommand", args, false);  // This will change the POA state to discarding on the master side
        boolean timeOut = false;
        for(ArReturnResult rr:rrList){
            if(rr.getFai().isTimeout()){
                timeOut = true;
                getErrorLog().println("disableMasterOnFailOverCommand timed out while calling the following process: " +  rr.getFai().getOrbName());
                getInfoLog().println("disableMasterOnFailOverCommand timed out while calling the following process: " +  rr.getFai().getOrbName());
            }
        }
        if(timeOut){
            throw new TimeoutException("disableMasterOnFailOverCommand command timed out");   
        }
    }

    /**
     * @throws Exception
     */
    protected void prepareSlave() throws Exception
    {
        String[] argsUserPOA = {PREPARE_SLAVE_ARG};
        List<ArReturnResult> rrList = getSlaveArContext().executeAll(getErrorLog(), getInfoLog(), AR_TIMEOUT, data.tpe, "prepareSlaveOnFailOverCommand", argsUserPOA, false);  //This will change POA state to holding on the slave side
        boolean timeOut = false;
        for(ArReturnResult rr:rrList){
            if(rr.getFai().isTimeout()){
                timeOut = true;
                getErrorLog().println("prepareSlaveOnFailOverCommand timed out while calling the following process: " +  rr.getFai().getOrbName());
                getInfoLog().println("prepareSlaveOnFailOverCommand timed out while calling the following process: " +  rr.getFai().getOrbName());
            }
        }
        if(timeOut){
            throw new TimeoutException("prepareSlaveOnFailOverCommand command timed out");   
        }
    }

    /**
     * @param p_restart 
     * @throws IOException
     * @throws ExitStatusException
     * @throws InterruptedException
     */
    private void startSlaveAfterFailover(boolean p_asynchRestart) throws IOException,
    ExitStatusException, InterruptedException
    {

        data.masterTransport.executeCommand(". ./.profile; /usr/bin/nohup $RUN_DIR/bin/startTradeengSystem BC Slave >/dev/null 2>&1 ", false, 0, p_asynchRestart, getErrorLog());
    }


    /**
     * @throws Exception
     */
    protected void goMasterOnSlave(boolean p_useSinglePhase) throws Exception
    {
        if(!p_useSinglePhase){
            data.slaveArContext.setSecondPhasePattern(secondPhaseNamePattern);
        }
        else{
            data.slaveArContext.setSecondPhasePattern(null);  
        }
        String[] args = {"true"};
        List<ArReturnResult> rrList = getSlaveArContext().executeAll(getErrorLog(), getInfoLog(), AR_TIMEOUT, data.tpe, "goMasterCommand", args, false);
        boolean timeOut = false;
        for(ArReturnResult rr:rrList){
            if(rr.getFai().isTimeout()){
                timeOut = true;
                getErrorLog().println("goMasterCommand timed out while calling the following process: " +  rr.getFai().getOrbName());
                getInfoLog().println("goMasterCommand timed out while calling the following process: " +  rr.getFai().getOrbName());
            }
        }
        if(timeOut){
            throw new TimeoutException("goMasterTimeout");   
        }

        data.slaveArContext.setSecondPhasePattern(null);
    }

    /**
     * @return
     */
    protected Future<Transport> initMasterTransport()
    {
        getErrorLog().println("Started initializing master side command transport to " + data.masterHost + " ...");
        Future<Transport> rr = data.tpe.submit(new TransportCommand(getTransportFactory(),data.masterHost, data.masterUserId, data.masterPassword, data.pKeyFile));
        return rr;
    }

    /**
     * @return
     * @throws Exception
     */
    protected Future<Transport> initSlaveTransport()
    {
        getErrorLog().println("Started initializing Slave side command transport to " + data.slaveHost + " ...");        
        Future<Transport> rr = data.tpe.submit(new TransportCommand(getTransportFactory(),data.slaveHost, data.slaveUserId, data.slavePassword, data.pKeyFile));
        return rr;
    }

    /**
     * @throws Exception
     */
    protected void initSlaveFai() throws Exception{
        getErrorLog().println("Started initializing Slave side Admin service connections to " + data.slaveHost + " ...");
        getSlaveArContext().initFastAdminInvokers(data.slaveHost,data.piAlSlave, getErrorLog());
        getErrorLog().println("Finished initializing Slave side Admin service connections to " + data.slaveHost + " ...");
    }   

    /**
     * @throws Exception
     */
    protected void initMasterFai() throws Exception{
        getErrorLog().println("Started initializing Master side Admin service connections to " + data.masterHost + " ...");
        getMasterArContext().initFastAdminInvokers(data.masterHost,data.piAlMaster, getErrorLog());
        getErrorLog().println("Finished initializing Master side Admin service connections to " + data.masterHost + " ...");
    } 

    /**
     * @throws IOException
     */
    protected void closeTransports() throws IOException
    {

        if(data.masterTransport != null){
            data.masterTransport.close();
        }
        if(data.slaveTransport != null){
            data.slaveTransport.close();
        }
    }

    public void close(){
        try
        {
            closeTransports();
        }
        catch (IOException e)
        {
            //ignore
        }   
        if(data.tpe != null){
            data.tpe.shutdown();
        }
        
        if(data.statusTpe != null){
            data.statusTpe.shutdown();
        }
    }
    
    protected String getPidsToKill() throws IOException, ExitStatusException, InterruptedException, ExecutionException, TimeoutException{
        StringBuffer command = new StringBuffer("grep 'PID' ");
        if((data.processNames == null) || data.processNames.length == 0){// if not specified, assume all processes
            command.append("run_dir/pid/*.pid ");
        }
        else{
            for (int i = 0; i < data.processNames.length; i++){
                command.append("run_dir/pid/" + data.processNames[i] + ".pid ");
            }
        }
        getErrorLog().println("Executing command to get the PIDS for the master processes to be killed on " + data.masterTransport.getHostName() + "/" + data.masterTransport.getUserName());
        command.append("| awk -F\"=\" '{ print $2 }' | tr '\n' ' ' ");
        String cmd = command.toString();
        ExecuteTransportCommand etc = new ExecuteTransportCommand(data.masterTransport,cmd, true, 0, false, getErrorLog());
        getInfoLog().println("Executing command to get the PIDS of processes to be killed on host: " + data.masterHost);
        Future<String> rf = data.tpe.submit(etc);
        String output =  rf.get(TRANSPORT_TIMEOUT, TimeUnit.MILLISECONDS);
        getInfoLog().println("get the PIDS of processes to be killed on host: " + data.masterHost + " the pids are:" + output );
        getErrorLog().println("get the PIDS of processes to be killed on host: " + data.masterHost + " the pids are:" + output );
        return output;
    }

    /**
     * @throws IOException
     * @throws ExitStatusException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    protected void killMaster(String pids) throws IOException, ExitStatusException, InterruptedException, ExecutionException, TimeoutException{

        
        String cmd = "kill -9 " + pids;
        ExecuteTransportCommand etc = new ExecuteTransportCommand(data.masterTransport,cmd, false, 0, true, getErrorLog());
        getInfoLog().println("Executing command to kill processes on host: " + data.masterHost);
        getErrorLog().println("Executing command to kill the master processes on " + data.masterTransport.getHostName() + "/" + data.masterTransport.getUserName());
        Future<String> rf = data.tpe.submit(etc);
        rf.get(TRANSPORT_TIMEOUT, TimeUnit.MILLISECONDS);
        getInfoLog().println("Done killing processes on host: " + data.masterHost);
        getErrorLog().println("Done killing processes on host: " + data.masterHost);
    }

    /**
     * @param p_transport
     * @param piAl
     * @throws IOException
     * @throws ExitStatusException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    protected void getProcessInfo(Transport p_transport, ArrayList<ProcessInfo> piAl) throws IOException, ExitStatusException, InterruptedException, ExecutionException, TimeoutException{

        piAl.clear();
        StringBuffer command = new StringBuffer("/bin/cat ");
        if((data.processNames == null) || data.processNames.length == 0){// if not specified, assume all processes
            command.append("~/run_dir/pid/*.pid");
        }
        else{
            for (int i = 0; i < data.processNames.length; i++){
                command.append("~/run_dir/pid/" + data.processNames[i] + ".pid ");
            }
        }

        String cmd = command.toString();
        String output = null;

        ExecuteTransportCommand etc = new ExecuteTransportCommand(p_transport,cmd, true, 0, false, getErrorLog());
        Future<String> rf = data.tpe.submit(etc);
        output = rf.get(TRANSPORT_TIMEOUT, TimeUnit.MILLISECONDS);
        extractProcessInfo(output, piAl);

    }


    /**
     * @param pidOut
     * @param piAl
     */
    protected void extractProcessInfo(String pidOut, ArrayList<ProcessInfo> piAl)
    {
        String[] lines;
        lines = pidOut.split("\n");
        ProcessInfo pi = null;
        for(int n = 0; n<lines.length; n++){
            if(lines[n].matches("PID=.*")){ // This line matches the pid information
                String[] tokens = lines[n].split("=");
                pi = new ProcessInfo();
                pi.setPid(tokens[1]);
            }
            // Now look for orb name and port for this pid, if exists
            else if(lines[n].trim().matches("ORB_NAME=.*")){
                String orbName = lines[n].split("=")[1];
                pi.setOrbName(orbName);
                if(data.processNames != null && data.processNames.length > 0){
                    for(int m = 0; m<data.processNames.length; m++){
                        if(orbName.matches(".*" + data.processNames[m] + ".*")){   
                            piAl.add(pi); //only add this one if there is a match
                            break;
                        }
                    }
                }else{
                    piAl.add(pi);
                }

            }
            else if(lines[n].trim().matches("IIOP_PORT_NBR=.*")){
                String portNum = lines[n].split("=")[1];
                pi.setPort(portNum);
            }
        }
    }


    /**
     * @param piAl
     * @return
     */
    protected String getOrbNames(ArrayList<ProcessInfo> piAl){
        StringBuffer sb = new StringBuffer();   
        for(ProcessInfo pi:piAl){
            sb.append(pi.getOrbName());
            sb.append(" ");
        }
        return sb.toString();

    }
    
  
    /**
     * @param args
     */
    public static void main(String[] args){
        
        FailoverManagerInitiator fmi = new FailoverManagerInitiator();
        fmi.initialize(args);
        int exitCode = fmi.start();
        System.exit(exitCode);
    }

  
}

