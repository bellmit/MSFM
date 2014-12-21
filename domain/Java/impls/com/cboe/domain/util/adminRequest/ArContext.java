package com.cboe.domain.util.adminRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.cboe.domain.util.StreamLogger;
import com.cboe.domain.util.failover.ProcessInfo;
import com.cboe.infrastructureServices.interfaces.adminService.CommandHolder;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedCommand;

public class ArContext
{
    public void setFaiList(ArrayList<FastAdminInvoker> p_faiList)
    {
        faiList = p_faiList;
    }


    ArrayList<FastAdminInvoker> faiList = new ArrayList<FastAdminInvoker>();
    String secondPhasePattern;




    public void setSecondPhasePattern(String p_secondPhasePattern)
    {
        secondPhasePattern = p_secondPhasePattern;
    }



    public ArrayList<FastAdminInvoker> getFaiList()
    {
        return faiList;
    }



    /**
     * 
     * @param piAl
     * @param errorLog
     * @param tm
     * @throws Exception
     */
    public void initFastAdminInvokers(String host, List<ProcessInfo> piAl, StreamLogger errorLog) throws Exception
    {
        
            faiList.clear();
        for(int i = 0; i<piAl.size(); i++){
            errorLog.println("Started initializing connection to " + piAl.get(i).getOrbName() + ":" +  piAl.get(i).getPort() + " ...");
            FastAdminInvoker fai = new FastAdminInvoker(host, Integer.parseInt(piAl.get(i).getPort()), piAl.get(i).getOrbName());
            fai.initialize();
            errorLog.println("Finished initializing connection to " + piAl.get(i).getOrbName() + ":" +  piAl.get(i).getPort() + ".");
            faiList.add(fai);
        }
    }
    
    public boolean isArCommandAdded(String p_cmd, String[] p_args){
        boolean atLeastOnefound = false;
        for(FastAdminInvoker fai:faiList){
            if(fai.getCmdHolder(p_cmd, p_args) == null){ // command not found
                if(fai.isKnownUnsupportedCommand(p_cmd, p_args)){
                  continue;  // ignore unsupported 
                }
                else{
                  return false;   // not found and not unsupported 
                }
            }else{
                atLeastOnefound = true;
            }
        }
        if(atLeastOnefound){
            return true;  // ok if some unsupported but at least one has been found
        }
        return false;  //all of them were unsupported, fail the test
    }

    /**
     * @param faiAl
     * @param cmd
     * @param arguments
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public boolean addArCommand(String cmd, String[] arguments, StreamLogger errorLog,
            StreamLogger infoLog, long timeoutInMillis, ExecutorService tpe, boolean ignoreUnsupported) throws Exception {

        ArrayList<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
        boolean timeout = false;
        Exception exp = null;
        UnsupportedCommand unsupportedException = null;
        boolean brr = true;
        for(FastAdminInvoker fai:faiList){
            AddArHolderCommand setArcmd = new AddArHolderCommand(fai, cmd, arguments, infoLog, errorLog);
            errorLog.println("Setting up ar command: " + cmd + " on process: " + fai.getOrbName());
            Future<Boolean> rrFuture = tpe.submit(setArcmd);
            futures.add(rrFuture);
        }
        int index =0;
        Boolean arr = false;
        boolean foundAtLeastOne = false;
        for(Future<Boolean> ft:futures){
            try{
                timeout=false;
                arr = false;
                foundAtLeastOne = true;
                arr = ft.get(timeoutInMillis, TimeUnit.MILLISECONDS);
                errorLog.println("Successfully set up ar command: " + cmd + " on process: " + faiList.get(index).getOrbName());
            }
            catch(TimeoutException toe){
                timeout = true;
                exp = toe;
            }
            catch(ExecutionException ee){// special treatment for unimplemented ar commands if requested
                if((ignoreUnsupported) && (ee.getCause() instanceof UnsupportedCommand)){
                    unsupportedException = (UnsupportedCommand)ee.getCause();
                    faiList.get(index).setUnsupportedCommand(cmd, arguments);
                }
                else{
                    errorLog.println("Unable to set up ar command: " + cmd + " on process: " + faiList.get(index).getOrbName());
                    infoLog.println("Unable to set up ar command: " + cmd + " on process: " + faiList.get(index).getOrbName());
                    throw ee;
                }
            }
            catch(Exception e){
                exp = e;   
            }
            if(timeout){
                faiList.get(index).setTimeout(true);
                errorLog.println("ar command: " + cmd + " on process: " + faiList.get(index).getOrbName() + " has timed out");
            }else{
                faiList.get(index).setTimeout(false);
            }
            if(arr.booleanValue() == false){
                brr = false;
                infoLog.println("Process: " + faiList.get(index).getOrbName() + " is not up");
                errorLog.println("Process: " + faiList.get(index).getOrbName() + " is not up");
            }
            index++;
        }
        if (exp != null){
            throw exp;
        }
        if(!foundAtLeastOne && unsupportedException != null){ //fail if no process implements the ar command
            throw unsupportedException;
        }
        return brr;
    }

    private List<ArReturnResult> executeAllInTwoPhases(StreamLogger errorLog,
            StreamLogger infoLog, long timeoutInMillis, ExecutorService tpe,  String p_cmd, String[] p_args, boolean ignoreUnsupported) throws Exception{

        ArrayList<FastAdminInvoker> firstPhase = new ArrayList<FastAdminInvoker>();
        ArrayList<FastAdminInvoker> secondPhase = new ArrayList<FastAdminInvoker>();

        for(FastAdminInvoker fai:faiList){
            if(fai.getOrbName().matches(secondPhasePattern)){
                secondPhase.add(fai);
            }
            else{
                firstPhase.add(fai);
            }
        }
        ArrayList<ArReturnResult> rr = new ArrayList<ArReturnResult>();
        if(firstPhase.size() > 0){
            rr.addAll(executeAll(errorLog, infoLog, timeoutInMillis, tpe, firstPhase, p_cmd, p_args, ignoreUnsupported));
        }
        // Check for a timeout. Do not execute second phase if the first one timed out
        boolean timeout = false;
        for(ArReturnResult arr:rr){
            FastAdminInvoker fai = arr.getFai();
            if(fai.isTimeout()){
                timeout = true;
                if(fai.getCmdHolder(p_cmd, p_args) != null){
                    String[] commandSegments = fai.getCmdHolder(p_cmd, p_args).value.name.split("\\.");
                    String commandName = commandSegments[commandSegments.length - 1];
                    errorLog.println("Timeout occurred while executing ar command: " + commandName + " on process: " + fai.getOrbName());
                    infoLog.println("Timeout occurred while executing ar command: " + commandName + " on process: " + fai.getOrbName());
                }
            }
        }
        if(timeout){
            StringBuilder aborted = new StringBuilder();
            for(FastAdminInvoker fai:secondPhase){
                if(aborted.length() > 0){
                  aborted.append(",");   
                }
                aborted.append(fai.orbName);
            }
            errorLog.println("Aborting ar command on the following processes: " + aborted.toString());
            infoLog.println("Aborting ar command on the following processes: " + aborted.toString());
            throw new TimeoutException("Aborted second phase of ar command");
        }

    
        if(secondPhase.size() > 0){
            rr.addAll(executeAll(errorLog, infoLog, timeoutInMillis, tpe, secondPhase, p_cmd, p_args, ignoreUnsupported));
        }
        return rr;
    }

    private List<ArReturnResult> executeAll(StreamLogger errorLog,
            StreamLogger infoLog, long timeoutInMillis, ExecutorService tpe, List<FastAdminInvoker> p_fai, String p_cmd, String[] p_args, boolean ignoreUnsupported) throws Exception{

        Exception exp = null;
        boolean timeout = false;
        ArrayList<String> orbNames = new ArrayList<String>();
        String commandName = null;
        ArrayList<Future<ArReturnResult>> futures = new ArrayList<Future<ArReturnResult>>();
        ArrayList<ArReturnResult> rr = new ArrayList<ArReturnResult>();
        for(FastAdminInvoker fai:p_fai){
            if(ignoreUnsupported){
                if(fai.isKnownUnsupportedCommand(p_cmd, p_args)){
                    String arguments = "";
                    for(int i= 0 ; i < p_args.length; i++){
                        arguments = arguments + " " + p_args[i];
                    }
                    errorLog.println("Skipping process: " + fai.getOrbName() + " for command: " + p_cmd + " args: " +  arguments + " as requested by the caller");
                    continue;
                }
            }
            CommandHolder cmdHolder = fai.getCmdHolder(p_cmd, p_args);
            if(cmdHolder == null){
                String arguments = "";
                for(int i= 0 ; i < p_args.length; i++){
                    arguments = arguments + " " + p_args[i];
                }
                errorLog.println("Warning skipping process: " + fai.getOrbName() + " for command: " + p_cmd + " args: " +  arguments + " since the process is probably down");
                infoLog.println("Warning skipping process: " + fai.getOrbName() + " for command: " + p_cmd + " args: " +  arguments + " since the process is probably down");
                continue; //This one process is probably down. Continue to the next one.
            }
                ArCommand arCmd = new ArCommand(fai, p_cmd, p_args);
                String[] commandSegments = cmdHolder.value.name.split("\\.");
                commandName = commandSegments[commandSegments.length - 1];
                errorLog.println("Executing ar command: " + commandName + " on process: " + fai.getOrbName());
                Future<ArReturnResult> rrFuture = tpe.submit(arCmd);
                futures.add(rrFuture);
                orbNames.add(fai.getOrbName());
        }
        int index =0;
        ArReturnResult arr = null;
        for(Future<ArReturnResult> ft:futures){
            try{
                timeout =  false;
                arr = ft.get(timeoutInMillis, TimeUnit.MILLISECONDS);
            }
            catch(TimeoutException toe){
                timeout = true;
                int idx = futures.indexOf(ft);
                if(idx == -1){
                 continue;   
                }
                FastAdminInvoker fai = p_fai.get(idx);
                arr = new ArReturnResult();
                arr.setFai(fai);
            }
            catch(Exception e){
                String error = "Failed to execute ar command: " + commandName + " on process: " + orbNames.get(index);
                errorLog.exception(error, e);
                infoLog.println(error);
                exp = e;   
            }
            if(timeout){
                faiList.get(index).setTimeout(true);   
                errorLog.println("Command: " + commandName + " on process: " + orbNames.get(index) + " has timed out");
            }else{
                faiList.get(index).setTimeout(false);   
                errorLog.println("Successfully executed ar command: " + commandName + " on process: " + orbNames.get(index));
            }
            rr.add(arr);
            index++;
        }
        
        if (exp != null){
            throw exp;
        }
        return rr;
    }

    public List<ArReturnResult> executeAll(StreamLogger errorLog,
            StreamLogger infoLog, long timeoutInMillis, ExecutorService tpe, String p_cmd, String[] p_args, boolean ignoreUnsupported) throws Exception{
        if(secondPhasePattern == null){
            return executeAll(errorLog, infoLog, timeoutInMillis, tpe, faiList, p_cmd, p_args, ignoreUnsupported);
        }else{
            return executeAllInTwoPhases(errorLog, infoLog, timeoutInMillis, tpe, p_cmd, p_args, ignoreUnsupported);            
        }
    }
}
