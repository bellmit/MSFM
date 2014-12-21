package com.cboe.domain.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import com.cboe.ORBInfra.ORB.Orb;
import com.cboe.domain.util.adminRequest.ArContext;
import com.cboe.domain.util.adminRequest.ArReturnResult;
import com.cboe.domain.util.adminRequest.FastAdminInvoker;
import com.cboe.domain.util.failover.LogReaderThread;
import com.cboe.domain.util.failover.ProcessInfo;

public class ProcessStatus
{
    Calendar cal = Calendar.getInstance();
    public static int returnCode = 0;
    public static final int TIMEOUT = 5000;
    public ProcessStatus(final String p_host1, final String p_host2, String[] orbNames, String[] ports1, String ports2[], boolean orderCounts, 
            long sleepTimeMillis , final StreamLogger p_slErr, final StreamLogger p_slOut) throws Exception

            {
        final ArrayList<ProcessInfo> piAl1 = new ArrayList<ProcessInfo>();
        final ArrayList<ProcessInfo> piAl2 = new ArrayList<ProcessInfo>();
        final ExecutorService tpe = Executors.newCachedThreadPool();
        for(int i = 0; i<orbNames.length && i<ports1.length; i++){
            ProcessInfo pi = new ProcessInfo();
            String orbName = orbNames[i];
            orbName = orbName.replaceFirst(p_host2, p_host1);   
            pi.setOrbName(orbName);
            pi.setPort(ports1[i]);
            piAl1.add(pi);
        }
        for(int i = 0; i<orbNames.length && i<ports2.length; i++){
            ProcessInfo pi = new ProcessInfo();
            String orbName = orbNames[i];
            orbName = orbName.replaceFirst(p_host1, p_host2);   
            pi.setOrbName(orbName);
            pi.setPort(ports2[i]);
            piAl2.add(pi);
        }

        if(orderCounts){
            displayOrderCounts(p_host1, p_host2,  p_slErr, p_slOut,
                    piAl1, piAl2, tpe, sleepTimeMillis);
        }
        else{
            final StreamLogger sl1 = new StreamLogger(new InputOutputStream());
            final StreamLogger sl2 = new StreamLogger(new InputOutputStream());
            Future ft1 = tpe.submit(new Runnable(){
                public void run(){
                    try
                    {
                        startFFStatus(p_host1, p_slErr, p_slOut, piAl1, tpe, sl1);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
            );
            Future ft2 = tpe.submit(new Runnable(){
                public void run(){
                    try
                    {
                        startFFStatus(p_host2, p_slErr, p_slOut, piAl2, tpe, sl2);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
            );
            ft1.get(); 
            sl1.print(System.out);
            ft2.get();
            sl2.print(System.out);
        }
            }

    private void displayOrderCounts(String p_host1, String p_host2,  StreamLogger p_slErr, StreamLogger p_slOut,
            ArrayList<ProcessInfo> piAl1, ArrayList<ProcessInfo> piAl2, ExecutorService p_tpe, long sleepTime) throws Exception,
            InterruptedException, ExecutionException, TimeoutException{

        final StreamLogger slErr = p_slErr;
        final StreamLogger slOut = p_slOut;
        final ExecutorService tpe = p_tpe;
        final int tab1 = 40;
        final int tab2 = 20;
        final int tab3 = 20;
        final int tab4 = 20;

        try{

            final ArContext ac1 = new ArContext();
            final ArContext ac2 = new ArContext();
            ac1.initFastAdminInvokers(p_host1, piAl1, slErr);
            ac2.initFastAdminInvokers(p_host2, piAl2, slErr);
            Future ft1 = tpe.submit(new Runnable(){
                public void run(){
                    try
                    {
                        ac1.addArCommand("getOrderCountForSync", new String[0],slErr,slOut,3000,tpe, true);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
            );
            Future ft2 = tpe.submit(new Runnable(){
                public void run(){
                    try
                    {
                        ac2.addArCommand("getOrderCountForSync", new String[0],slErr,slOut,3000,tpe, true);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
            );
            ft1.get();
            ft2.get();
            boolean done = false;
            while(done == false){
                done = true;    
                for(FastAdminInvoker fai: ac1.getFaiList()){
                    if(fai.getCmdHolder("getOrderCountForSync", new String[0]) == null){
                        ac1.getFaiList().remove(fai);
                        done = false;
                        break;
                    }
                }
            }
            done = false;

            while(done == false){
                done = true;    
                for(FastAdminInvoker fai: ac2.getFaiList()){
                    if(fai.getCmdHolder("getOrderCountForSync", new String[0]) == null){
                        ac2.getFaiList().remove(fai);
                        done = false;
                        break;
                    }
                }
            }
            long [] prevOrderCount = new long[ac1.getFaiList().size()];
            while(true){
                String timestamp = getTimeString();
                System.out.println("\n" + timestamp + fillSpace(tab1 - timestamp.length()) +  p_host1 + fillSpace(tab2 - p_host1.length()) + 
                        p_host2 + fillSpace(tab3 - p_host2.length()) + "Diff" + fillSpace(tab4 - "Diff".length()) + "Rate");
                Future<List<ArReturnResult>> ft3 = tpe.submit(new Callable<List<ArReturnResult>>(){
                    public List<ArReturnResult> call() throws Exception{
                        return ac1.executeAll(slErr, slOut, TIMEOUT, tpe,"getOrderCountForSync", new String[0], true);
                    }
                }
                );

                Future<List<ArReturnResult>> ft4 = tpe.submit(new Callable<List<ArReturnResult>>(){
                    public List<ArReturnResult> call() throws Exception{

                        return ac2.executeAll(slErr, slOut, TIMEOUT, tpe,"getOrderCountForSync", new String[0], true);
                    }
                }
                );

                List<ArReturnResult> rr3 = ft3.get();
                List<ArReturnResult> rr4 = ft4.get();


                int count = 0;
                long orderCount1 = 0;
                long orderCount2 = 0;
                for(int i = 0; i < ac1.getFaiList().size(); i++){
                    String processName = ac1.getFaiList().get(i).getOrbName().replaceFirst(p_host1,"");
                    try{

                        String orderCountString1 = "";
                        String orderCountString2 = "";
                        try{
                            if(rr3.get(i).getOutput().trim().length() == 0){
                              continue;   
                            }
                            orderCount1 = Long.parseLong(rr3.get(i).getOutput().replaceFirst("Order Count:","").trim());
                            orderCountString1 = "" + orderCount1;
                        }
                        catch(Exception e){
                            orderCount1 = 0;
                            orderCountString1="Error";
                        }
                        try{
                            if(rr4.get(i).getOutput().trim().length() == 0){
                                continue;   
                              }
                            orderCount2 = Long.parseLong(rr4.get(i).getOutput().replaceFirst("Order Count:","").trim());
                            orderCountString2 = "" + orderCount2;
                        }
                        catch(Exception e){
                            orderCount2 = 0;
                            orderCountString2="Error";
                        }
                        
                        long diff = Math.abs(orderCount1 - orderCount2);
                        double rate = (orderCount1 - prevOrderCount[count])/(sleepTime/1000);
                        prevOrderCount[count++] = orderCount1;
                        System.out.println("" + processName + fillSpace(tab1 - processName.length()) +
                                orderCountString1 + fillSpace(tab2 - orderCountString1.length()) + 
                                orderCountString2 + fillSpace(tab3 - orderCountString2.length()) + diff +
                                fillSpace(tab4 - (diff + "").length()) + rate);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                Thread.currentThread().sleep(sleepTime);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private String getTimeString(){
        cal.setTimeInMillis(System.currentTimeMillis());
        return String.format("%1$tH:%1$tM:%1$tS.%1$tL", cal);
    }

    private String fillSpace(int size){
        if(size <=0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i =0; i < size; i++){
            sb.append(" ");
        }
        return sb.toString();
    }
    private void startFFStatus(String p_host, StreamLogger p_slErr, StreamLogger p_slOut,
            ArrayList<ProcessInfo> piAl, ExecutorService tpe, StreamLogger sl) throws Exception,
            InterruptedException, ExecutionException, TimeoutException
            {
        try{
            sl.printlnNoTimestamp("\n     " + p_host + ":");
            ArContext ac = new ArContext();
            ac.initFastAdminInvokers(p_host, piAl, p_slErr);
            try{
                ac.addArCommand("isValidSlaveCommand", new String[0],p_slErr,p_slOut,3000,tpe, false);

            }
            catch(Exception e){
                //ignore this one.   
            }
            for(FastAdminInvoker fai:ac.getFaiList()){
                if(fai.getCmdHolder("isValidSlaveCommand", new String[0]) == null){
                    if(fai.isTimeout()){
                        sl.printlnNoTimestamp("     " + fai.getOrbName().replaceFirst(p_host,"") + " timed out");
                        returnCode = 1;
                    }else{
                        sl.printlnNoTimestamp("     " + fai.getOrbName().replaceFirst(p_host,"") + " is not up");
                        returnCode = 1;
                    }
                }
            }
            ArrayList<FastAdminInvoker> potentialMasters = new ArrayList<FastAdminInvoker>();
            List<ArReturnResult> rr = ac.executeAll(p_slErr,p_slOut,TIMEOUT,tpe, "isValidSlaveCommand", new String[0], false);
            for(ArReturnResult arRet:rr){
                if(arRet.getOutput().trim().equals("true")){
                    sl.printlnNoTimestamp("     " + arRet.getFai().getOrbName().replaceFirst(p_host,"") + " is a Slave");  
                }
                else{
                    potentialMasters.add(arRet.getFai());
                }
            }
            ArContext potMasters = new ArContext();
            potMasters.setFaiList(potentialMasters);
            try{
                potMasters.addArCommand("isMasterCommand", new String[0], p_slErr, p_slOut, TIMEOUT, tpe, false);
                potMasters.addArCommand("isDiscardingMasterCommand", new String[0], p_slErr, p_slOut, TIMEOUT, tpe, false);
            }
            catch(Exception e){

            }
            for(FastAdminInvoker fai:potMasters.getFaiList()){
                if(fai.getCmdHolder("isMasterCommand", new String[0]) == null){
                    if(fai.isTimeout()){
                        sl.printlnNoTimestamp("     " + fai.getOrbName().replaceFirst(p_host,"") + " timed out");
                        returnCode = 1;
                    }else{
                        sl.printlnNoTimestamp("     " + fai.getOrbName().replaceFirst(p_host,"") + " is not up");
                        returnCode = 1;
                    }
                }
            }
            List<ArReturnResult> pmrr = potMasters.executeAll(p_slErr, p_slOut, TIMEOUT, tpe, "isMasterCommand", new String[0], false);
            List<ArReturnResult> pdmrr = potMasters.executeAll(p_slErr, p_slOut, TIMEOUT, tpe, "isDiscardingMasterCommand", new String[0], false);
            for(ArReturnResult pmRet:pmrr){
                if(pmRet.getOutput().trim().equals("true")){
                    if((pdmrr.get(findProcess(pdmrr, pmRet))).getOutput().trim().equals("true")){
                        sl.printlnNoTimestamp("     " + pmRet.getFai().getOrbName().replaceFirst(p_host,"") + " is a Discarding Master");
                    }
                    else{
                        sl.printlnNoTimestamp("     " + pmRet.getFai().getOrbName().replaceFirst(p_host,"") + " is a Master");
                    }
                }
                else{
                    sl.printlnNoTimestamp("     " + pmRet.getFai().getOrbName().replaceFirst(p_host,"") + " is comming up");
                    returnCode=1;
                }
            }
        }
        catch(Exception e){
            sl.exception("Exception thrown: ", e);
        }
        finally{
            sl.getInputStream().close();
        }
            }

    private int findProcess(List<ArReturnResult> rrList, ArReturnResult rr){
        
        for(int i = 0; i < rrList.size(); i++){
          ArReturnResult pmRet = rrList.get(i); 
          if(rr.getFai() == pmRet.getFai()){
           return i;   
          }
        }
        return -1;
    }

    public static void main(String[] args){

        Orb.init();
        InputOutputStream iosErr = new InputOutputStream();
        StreamLogger slErr = new StreamLogger(iosErr);
        InputOutputStream iosOut = new InputOutputStream();
        StreamLogger slOut = new StreamLogger(iosOut);

        try
        {
            String host1 = args[0];
            String host2 = args[1];
            String[] oNames = args[2].split(",");
            String[] ports1 = args[3].split(",");
            String[] ports2 = args[4].split(",");
            boolean orderCount = false;
            long millisToWait = 10000;
            if(args.length > 5){
                orderCount = Boolean.parseBoolean(args[5]);
                if(orderCount && args.length>6){
                    millisToWait=Long.parseLong(args[6]);
                }
            }
            new LogReaderThread(iosErr, System.err).start();
            ProcessStatus ps = new ProcessStatus(host1, host2, oNames,
                    ports1, ports2, orderCount, millisToWait, slErr, slOut);

            System.out.println("");
        }
        catch (Exception e){
            slErr.exception("", e);  
        }
        finally{
            System.exit(returnCode);   
        }
    }
}

