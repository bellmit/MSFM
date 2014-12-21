package com.cboe.domain.util.adminRequest;


/**
 * @author baranski
 *
 */
public class ArReturnResult{
    public FastAdminInvoker getFai()
    {
        return fai;
    }
    public void setFai(FastAdminInvoker p_fai)
    {
        fai = p_fai;
    }
    public String getOutput()
    {
        return output;
    }
    public void setOutput(String p_output)
    {
        output = p_output;
    }
    FastAdminInvoker fai = null;
    String output = null;
}