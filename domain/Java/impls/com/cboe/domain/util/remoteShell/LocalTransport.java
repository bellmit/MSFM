package com.cboe.domain.util.remoteShell;

import java.io.IOException;
import java.io.InputStream;

import com.cboe.domain.util.StreamLogger;


public class LocalTransport extends AbstractTransport
{

    public LocalTransport(String p_userName)
    {
        super(p_userName);
    }
 
    public void close()
    {
        //nothing to be done here
    }

    public String executeCommand(String p_cmd, boolean p_checkExit, int p_expectedExitStatus, boolean ignoreOutput, StreamLogger sl)
            throws IOException, ExitStatusException, InterruptedException
    {
        
        Process p = Runtime.getRuntime().exec(p_cmd);
        
            int returnCode = p.waitFor();
            if(p_checkExit){
                if( returnCode != p_expectedExitStatus){
                    StringBuffer serr = new StringBuffer();
                    InputStream chanErr = p.getErrorStream();
                    if(sl != null){
                        getData(serr,sl.getInputStream(), chanErr);
                    }else{
                        getData(serr,null, chanErr);
                    }
                        
                    throw new ExitStatusException("Command: " + p_cmd + " returned exit status: " + returnCode + " expected exit status: " + 
                            p_expectedExitStatus +'\n' + serr.toString());    
                }
            }
            StringBuffer sout = new StringBuffer();
            if(ignoreOutput){
                if(sl != null){
                    getData(sout,sl.getInputStream(), p.getInputStream());
                }else{
                    getData(sout,null, p.getInputStream());
                }
            }
            return sout.toString();
        
        
        
    }

   
    public boolean isConnected()
    {
       
        return true;
    }

}
