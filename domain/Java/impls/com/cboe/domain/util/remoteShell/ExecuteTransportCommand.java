package com.cboe.domain.util.remoteShell;

import java.util.concurrent.Callable;

import com.cboe.domain.util.StreamLogger;

/**
 * @author baranski
 *
 */
public class ExecuteTransportCommand implements Callable<String>{

    Transport transport;
    String cmd;
    boolean checkExit; 
    int expectedExitStatus; 
    boolean ignoreOutput; 
    StreamLogger sl;


    /**
     * @param p_transport
     * @param p_cmd
     * @param p_checkExit
     * @param p_expectedExitStatus
     * @param p_ignoreOutput
     * @param p_sl
     */
    public ExecuteTransportCommand(Transport p_transport, String p_cmd, boolean p_checkExit,
            int p_expectedExitStatus, boolean p_ignoreOutput, StreamLogger p_sl)
    {
        super();
        transport = p_transport;
        cmd = p_cmd;
        checkExit = p_checkExit;
        expectedExitStatus = p_expectedExitStatus;
        ignoreOutput = p_ignoreOutput;
        sl = p_sl;
    }


    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    public String call() throws Exception
    {
        return transport.executeCommand(cmd, checkExit, expectedExitStatus, ignoreOutput, sl);
    }
}    