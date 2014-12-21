package com.cboe.domain.util.failover;

import java.util.TimerTask;

public class StatusTimerTask extends TimerTask
{
    FailoverManager fm = null;

    
    public StatusTimerTask(FailoverManager p_failoverManager)
    {
      fm =  p_failoverManager;
    }

    public void run()
    {
        SynchStatus[] ss = fm.getStatus();
        fm.sendStatusEvent(ss);
        
    }

}
