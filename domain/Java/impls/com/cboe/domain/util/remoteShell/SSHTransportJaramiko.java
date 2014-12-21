package com.cboe.domain.util.remoteShell;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import net.lag.jaramiko.Channel;
import net.lag.jaramiko.ClientTransport;
import net.lag.jaramiko.ConsoleLog;
import net.lag.jaramiko.PKey;
import net.lag.jaramiko.SSHException;

import com.cboe.domain.util.StreamLogger;

public class SSHTransportJaramiko extends AbstractTransport
{
    public static int TRANSPORT_INIT_TIMEOUT = 15000;
    public static int OPEN_SESSION_TIMEOUT = 15000;
    public static int COMMAND_TIMEOUT = 10000;
    public static int KEEPALIVE_INTERVAL = 1000;
    public static int PORT = 22;

    private ClientTransport clientTransport;
    
    public SSHTransportJaramiko(String p_host, String p_userName, String p_password, String p_KeyFilePath) throws IOException
    {
        super(p_host, PORT, p_userName);
        startNewSocket();
        authenticate(p_userName, p_password, p_KeyFilePath);
    }

    public SSHTransportJaramiko(String p_host, int port, String p_userName, String p_password, String p_KeyFilePath) throws IOException
    {
        super(p_host, port, p_userName);
        startNewSocket();
        authenticate(p_userName, p_password, p_KeyFilePath);
    }

    private void authenticate(String p_userName, String p_password, String p_KeyFilePath)
            throws IOException
    {
        clientTransport = new ClientTransport(getSocket());
        clientTransport.setLog(new ConsoleLog());
        clientTransport.setDumpPackets(true);
        clientTransport.start(null, TRANSPORT_INIT_TIMEOUT);
       
        if(p_KeyFilePath != null){
            try{
                FileInputStream pKeysFile;
                pKeysFile = new FileInputStream(p_KeyFilePath);
                PKey pKey = PKey.readPrivateKeyFromStream(pKeysFile, null);   
                authenticate(p_userName, pKey);
                return;
            }
            catch(Exception e){
                // try again , this time using password
                startNewSocket();
                clientTransport = new ClientTransport(getSocket());
                clientTransport.start(null, TRANSPORT_INIT_TIMEOUT);
                authenticate(p_userName, p_password);
            }
        }
        
    }



    private void authenticate(String p_userName, String p_password) throws IOException
    {
        
        String[] next = clientTransport.authPassword(p_userName, p_password, TRANSPORT_INIT_TIMEOUT); 
        if (next.length > 0) {
            throw new IOException("Auth too complex: " + Arrays.asList(next));
        }
        userName = p_userName;
    }

    private void authenticate(String p_userName, PKey p_key) throws IOException
    {
        clientTransport.start(null, TRANSPORT_INIT_TIMEOUT);
        String[] next = clientTransport.authPrivateKey(p_userName, p_key, TRANSPORT_INIT_TIMEOUT); 
        if (next.length > 0) {
            throw new IOException("Auth too complex: " + Arrays.asList(next));
        }
        userName = p_userName;
    }

    public String executeCommand(String p_cmd, boolean checkExit, int expectedExitStatus, boolean ignoreOutput, StreamLogger sl) throws IOException, ExitStatusException
    {
        StringBuffer sout = new StringBuffer();
        if(sl != null){
        sl.println("Executing command: " + p_cmd + " on host: " + getHostName() +" ...");
        }
        Channel channel = clientTransport.openSession(OPEN_SESSION_TIMEOUT);
        try{
        channel.execCommand(p_cmd, COMMAND_TIMEOUT);
        }
        catch(SSHException sshe){
         //If the channel got closed for some reason , try again
            if(channel.isClosed()){
                channel = clientTransport.openSession(OPEN_SESSION_TIMEOUT);
                channel.execCommand(p_cmd, COMMAND_TIMEOUT);
            }
            else{
             throw sshe;   
            }
        }
        InputStream chanIn = channel.getInputStream();
        if(sl != null){
         sl.println("Finished executing command: " + p_cmd + " on host: " + getHostName() + " .");   
        }
        if(checkExit){
            int returnCode = channel.getExitStatus(COMMAND_TIMEOUT);
            if( returnCode != expectedExitStatus){
                StringBuffer serr = new StringBuffer();
                InputStream chanErr = channel.getStderrInputStream();
                if(sl != null){
                    getData(serr, sl.getInputStream(), chanErr);
                }
                else{
                    getData(serr, null, chanErr);
                }
                throw new ExitStatusException("Command: " + p_cmd + " returned exit status: " + returnCode + " expected exit status: " + 
                        expectedExitStatus +'\n' + serr.toString());    
            }
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
        
        channel.close();
        return sout.toString();
    }

    public void setClientTransport(ClientTransport clientTransport)
    {
        this.clientTransport = clientTransport;
    }

    public ClientTransport getClientTransport()
    {
        return clientTransport;
    }

    public void close()
    {
        clientTransport.close();  
    }

    public boolean isConnected()
    {
        return clientTransport.isAuthenticated();
    }
}