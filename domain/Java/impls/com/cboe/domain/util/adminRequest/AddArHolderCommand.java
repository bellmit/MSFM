package com.cboe.domain.util.adminRequest;

import java.util.concurrent.Callable;

import com.cboe.domain.util.StreamLogger;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedCommand;

/**
 * @author baranski
 *
 */
public class AddArHolderCommand implements Callable<Boolean>{

    FastAdminInvoker fai = null;
    String cmd;
    String[] arguments;
    StreamLogger infoLog;
    StreamLogger errorLog;

    /**
     * @param p_fai
     * @param p_cmd
     * @param p_arguments
     */
    public AddArHolderCommand(FastAdminInvoker p_fai, String p_cmd, String[] p_arguments, StreamLogger p_infoLog, StreamLogger p_errorLog)
    {
        fai=p_fai;
        cmd=p_cmd;
        arguments=p_arguments;
        errorLog = p_errorLog;
        infoLog = p_infoLog;
        
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    public Boolean call() throws UnsupportedCommand
    {
        boolean rr = false;
        fai.addCommandHolder(cmd, arguments);
        rr = true;
        return new Boolean(rr);
    }

}
