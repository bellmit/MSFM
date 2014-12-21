package com.cboe.domain.util.failover;

public class SynchStatusException extends Exception
{

    public SynchStatusException(String p_string, Exception p_e1)
    {
        super(p_string, p_e1);
    }

    public SynchStatusException(String p_msg)
    {
        super(p_msg);
    }

}
