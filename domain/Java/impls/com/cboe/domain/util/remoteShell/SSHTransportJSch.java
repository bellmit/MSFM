
package com.cboe.domain.util.remoteShell;

import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import com.cboe.domain.util.StreamLogger;

public class SSHTransportJSch extends AbstractTransport
{
   
    public static int PORT = 22;

    JSch jsch=null;
    
    private Session clientTransport;
    
    
    public SSHTransportJSch(String p_host, String p_userName, String p_password, String p_KeyFilePath) throws IOException
    {
        super(p_host, PORT, p_userName);
        try
        {
            jsch=new JSch();
            jsch.setLogger(new MyLogger());
            clientTransport=jsch.getSession(p_userName, p_host, PORT);
        }
        catch (JSchException e)
        {
           throw new IOException(e);
        }
        authenticate(p_userName, p_password, p_KeyFilePath);
    }

    public SSHTransportJSch(String p_host, int port, String p_userName, String p_password, String p_KeyFilePath) throws IOException
    {
        super(p_host, port, p_userName);
        try
        {
            jsch=new JSch();
            jsch.setLogger(new MyLogger());
            clientTransport=jsch.getSession(p_userName, p_host, port);
        }
        catch (JSchException e)
        {
           throw new IOException(e);
        }
        authenticate(p_userName, p_password, p_KeyFilePath);
    }

    private void authenticate(String p_userName, String p_password, String p_KeyFilePath)
            throws IOException
    {
        try
        {
            jsch.addIdentity(p_KeyFilePath);
            clientTransport.setConfig("StrictHostKeyChecking", "no");
            clientTransport.setPassword(p_password);
            clientTransport.connect();
        }
        catch (JSchException e)
        {
            throw new IOException(e);
        }
    }


    public String executeCommand(String p_cmd, boolean checkExit, int expectedExitStatus, boolean ignoreOutput, StreamLogger sl) throws IOException, ExitStatusException
    {
       //checkExit=false;
        StringBuffer sout = new StringBuffer();
        Channel channel;
        InputStream chanIn;
        InputStream chanErr;
        try
        {
            channel = clientTransport.openChannel("exec");
            ((ChannelExec)channel).setCommand(p_cmd);
            channel.setInputStream(null);
            chanIn = channel.getInputStream();
            chanErr = channel.getExtInputStream();
            if(sl != null){
                sl.println("About to execute command: " + p_cmd + " on host: " + getHostName() + " .");   
               }
            channel.connect();
        }
        catch (JSchException e)
        {
           throw new IOException(e);
        }
        

        if(sl != null){
         sl.println("Finished executing command: " + p_cmd + " on host: " + getHostName() + " .");   
        }
        
        if(!ignoreOutput){
            if(sl !=null){
                sl.println("Reading stdout data...");
                getData(sout, sl.getInputStream(), chanIn);
                sl.println("Done reading stdout.");
            }
            else{
                getData(sout, null, chanIn);
            }
        }
        
        
        if(checkExit){
            if(sl !=null){
            sl.println("Getting exit status of the command...");
            }
            int returnCode = channel.getExitStatus();
            if(sl !=null){
            sl.println("Done getting exit status of the command: " + returnCode);
            }
            if( returnCode != expectedExitStatus){
                StringBuffer serr = new StringBuffer();
                if(sl != null){
                    sl.println("Received exit status: " + returnCode + " is not equal the expected exit status: " + expectedExitStatus);
                    getData(serr, sl.getInputStream(), chanErr);
                }
                else{
                    getData(serr, null, chanErr);
                }
                
                throw new ExitStatusException("Command: " + p_cmd + " returned exit status: " + returnCode + " expected exit status: " + 
                        expectedExitStatus +'\n' + serr.toString());    
            }
        }

       
        
        channel.disconnect();
        return sout.toString();
    }


    public void close()
    {
        clientTransport.disconnect();  
    }
    
    public static class MyLogger implements com.jcraft.jsch.Logger {
        static java.util.Hashtable name=new java.util.Hashtable();
        static{
          name.put(new Integer(DEBUG), "DEBUG: ");
          name.put(new Integer(INFO), "INFO: ");
          name.put(new Integer(WARN), "WARN: ");
          name.put(new Integer(ERROR), "ERROR: ");
          name.put(new Integer(FATAL), "FATAL: ");
        }
        public boolean isEnabled(int level){
          return true;
        }
        public void log(int level, String message){
          System.err.print(name.get(new Integer(level)));
          System.err.println(message);
        }
      }

    public boolean isConnected()
    {
       return clientTransport.isConnected();
    }
}