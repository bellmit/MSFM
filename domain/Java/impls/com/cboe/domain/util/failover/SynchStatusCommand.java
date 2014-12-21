package com.cboe.domain.util.failover;

import java.util.concurrent.Callable;


public class SynchStatusCommand implements Callable<SynchStatus[]>{

    private FailoverManager fm;

    public SynchStatus[] call() throws Exception
    {
        return fm.getStatus();
    }

    public SynchStatusCommand(FailoverManager p_fm)
    {
        super();
        fm = p_fm;
    }

}