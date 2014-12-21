package com.cboe.interfaces.remoteApplication;

/**
 * @author Jing Chen
 */
public interface RemoteCASConfigurationService
{
    public void initializeConfigurations();
    public void cleanUpConfigurations();
    public int[] getGroupKeys();
}
