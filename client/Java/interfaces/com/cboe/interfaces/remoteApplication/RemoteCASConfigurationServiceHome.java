package com.cboe.interfaces.remoteApplication;

/**
 * @author Jing Chen
 */
public interface RemoteCASConfigurationServiceHome
{
    public final static String HOME_NAME = "RemoteCASConfigurationServiceHome";
    public RemoteCASConfigurationService create();
    public RemoteCASConfigurationService find();
}
