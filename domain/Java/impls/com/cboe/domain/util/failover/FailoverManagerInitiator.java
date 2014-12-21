package com.cboe.domain.util.failover;

import java.io.IOException;
import java.util.Scanner;

public class FailoverManagerInitiator{

    final int NUM_ARGS = 17;
    FailoverManager fm;
    final static String FAILOVER_COMMAND = "-failover";
    final static String STATUS_COMMAND = "-status";

    String command;
    String masterHost;
    String masterUserId;
    String masterPassword;
    String slaveHost;
    String slaveUserId;
    String slavePassword;
    String privateKeyFilePath;
    String[] processes;
    boolean skipMaster;
    boolean noAsk;
    boolean useSinglePhase;
    boolean restarSlaveAfterFailover;
    boolean asynchRestart;
    boolean prepareSlave; 
    boolean disableMaster; 
    boolean skipSynchStatusCheck;

    FailoverManager initialize(String args[]){
        int exitCode = 1;
        String[] all_args = new String[NUM_ARGS];
        //copy to another array to make the booleans at the end optional and defaulted to false
        System.arraycopy(args, 0, all_args, 0 , Math.min(args.length, NUM_ARGS));  
        command = all_args[0];
        masterHost = all_args[1];
        masterUserId = all_args[2];
        masterPassword = all_args[3];
        slaveHost = all_args[4];
        slaveUserId = all_args[5];
        slavePassword = all_args[6];
        privateKeyFilePath = all_args[7];
        processes = all_args[8].trim().split(",");
        skipMaster=Boolean.parseBoolean(all_args[9]);
        noAsk=Boolean.parseBoolean(all_args[10]);
        useSinglePhase = Boolean.parseBoolean(all_args[11]);
        restarSlaveAfterFailover = Boolean.parseBoolean(all_args[12]);
        asynchRestart = Boolean.parseBoolean(all_args[13]);
        //If prepareSlave is true an ar command will be issued on the slave before killing the master that will change the POA state to holding
        prepareSlave = Boolean.parseBoolean(all_args[14]); 
        //If disableMaster is true the Master will not be killed, instead an ar command will be issued to change the POA state to discarding
        disableMaster = Boolean.parseBoolean(all_args[15]); 
        skipSynchStatusCheck = Boolean.parseBoolean(all_args[16]);


        fm = new FailoverManager(masterHost, masterUserId, masterPassword,
                slaveHost, slaveUserId, slavePassword, privateKeyFilePath, processes);

        return fm;
    }

    public int start(){

        int exitCode = 1;
        

        if(command.equals(STATUS_COMMAND)){
            SynchStatusUpdateListener sul = new SynchStatusUpdateListener(){
                public void acceptSynchStatusUpdate(SynchStatus[] p_ss)
                {
                    if(p_ss.length > 0){
                        System.out.println(SynchStatus.getPrintHeader());
                    }
                    for(int i = 0; i< p_ss.length; i++){
                        System.out.println(p_ss[i].toString());
                    }
                    System.out.println("");
                }
            };
            fm.registerForStatusUpdates(sul);

            try
            {
                while(true){
                    while(System.in.available() ==0){

                        try
                        {
                            Thread.currentThread().sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                        }   
                    }
                    if(System.in.read() == 'x'){
                        break;
                    }
                }
            }
            catch (IOException e)
            {
                return 0;
            }
            return 0;
        }
        try
        { 
            new LogReaderThread(fm.getErrorInputStream(), System.err).start();
            try{
                fm.initialize(skipMaster,prepareSlave, disableMaster, noAsk?true:skipSynchStatusCheck);
            }
            catch (SynchStatusException sse){
                String response = "";
                System.out.println("\nSOME OF THE PROCESSES YOU ARE ABOOUT TO FAILOVER ARE REPORTING SYNCHRONIZATION PROBLEMS\n");
                System.out.println(sse.getMessage());
                Scanner scanner = new Scanner(System.in);
                System.out.print("\nWould you like to proceed?(yes/no): ");
                response = scanner.nextLine();
                if(!response.equalsIgnoreCase("yes")){
                    System.out.println("Your response was: " + response + ". \n\n\n FAILOVER WAS NOT PERFORMED!");
                    exitCode = 0;
                    return exitCode;
                }
            }
            String response = "";
            System.out.println("\nABOUT TO FAILOVER THE FOLLOWING PROCESSES:\n" + fm.getOrbNames(fm.data.piAlSlave) + "\nFROM " + fm.data.masterHost + " TO " + fm.data.slaveHost);
            if(!noAsk){
                Scanner scanner = new Scanner(System.in);
                System.out.print("\nWould you like to proceed?(yes/no): ");
                response = scanner.nextLine();
            }
            if(noAsk || response.equalsIgnoreCase("yes")){
                long failoverTime = fm.failover(skipMaster, useSinglePhase, restarSlaveAfterFailover, asynchRestart, prepareSlave, disableMaster);
                System.out.println("\n\n FAILOVER WAS SUCCESSFUL - FAILOVER TIME WAS " + failoverTime + " millis. \n\n");
            }else{
                System.out.println("Your response was: " + response + ". \n\n\n FAILOVER WAS NOT PERFORMED!");
            }
            exitCode = 0;
        }
        catch (MasterFailedException mfe){
            new LogReaderThread(fm.getInfoInputStream(), System.out).start();
            fm.getInfoLog().println(mfe.toString());
            fm.getInfoLog().println("Please bring down the Master side manually and rerun the script with -skipMaster option ");
            fm.getErrorLog().println("Please bring down the Master side manually and rerun the script with -skipMaster option ");
            fm.getErrorLog().exception("Failover failed", mfe);
            try
            {
                while(fm.getInfoInputStream().available() > 0) { //empty out the info stream
                    try
                    {
                        Thread.currentThread().sleep(1000);
                    }
                    catch (InterruptedException e1)
                    {}
                }
            }
            catch (IOException e1)
            {
                // ignore this
            }

        }

        catch (Exception e){
            new LogReaderThread(fm.getInfoInputStream(), System.out).start();
            fm.getInfoLog().println(e.toString());
            fm.getInfoLog().println("FAILOVER FAILED");
            fm.getErrorLog().exception("Failover failed", e);
            try
            {
                while(fm.getInfoInputStream().available() > 0) { //empty out the info stream
                    try
                    {
                        Thread.currentThread().sleep(1000);
                    }
                    catch (InterruptedException e1)
                    {}
                }
            }
            catch (IOException e1)
            {
                // ignore this
            }

        }
        catch (Throwable t){
            t.printStackTrace();   

        }
        finally

        {
            try
            {
                fm.closeTransports();
                while(fm.getErrorInputStream().available() > 0) {// empty out the error stream
                    Thread.currentThread().sleep(1000);
                }
                return exitCode;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return exitCode;
            }
        }
    }
}

