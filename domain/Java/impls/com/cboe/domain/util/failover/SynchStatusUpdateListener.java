package com.cboe.domain.util.failover;

public interface SynchStatusUpdateListener
{
  public void acceptSynchStatusUpdate(SynchStatus[] ss); 
}
