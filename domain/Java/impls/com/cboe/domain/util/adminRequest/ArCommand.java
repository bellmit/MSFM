package com.cboe.domain.util.adminRequest;

import java.util.concurrent.Callable;


/**
 * @author baranski
 *
 */
public class ArCommand implements Callable<ArReturnResult>{

    FastAdminInvoker fai = null;
    String cmd = null;
    String[] args = null;

    /**
     * @param p_fai
     * @param p_p_args 
     * @param p_p_cmd 
     */
    public ArCommand(FastAdminInvoker p_fai, String p_cmd, String[] p_args)
    {
        fai=p_fai;
        cmd = p_cmd;
        args = p_args;

    }
    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    public ArReturnResult call() throws Exception
    {
        ArReturnResult arr = new ArReturnResult();
        arr.fai = fai;
        arr.output = fai.invoke(cmd, args);
        return arr;
    }

}