package com.cboe.interfaces.application;

/**
 *
 * @author Keith A. Korecky
 *
 */
public interface HeartBeatCollector
{
   public void acceptHeartBeat( com.cboe.idl.cmiAdmin.HeartBeatStruct heartBeat );
}
