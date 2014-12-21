package com.cboe.remoteApplication.startup;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.remoteApplication.RemoteCASConfigurationService;
import com.cboe.interfaces.application.RemoteComponentStatusCollector;
import com.cboe.remoteApplication.marketData.*;
import com.cboe.remoteApplication.shared.RemoteServicesHelper;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.status.RemoteComponentStatusListener;

import java.util.List;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * @author Jing Chen
 */
public class RemoteCASConfigurationServiceImpl extends BObject implements RemoteCASConfigurationService, RemoteComponentStatusCollector
{
    private String configurationString;
    private List configurationGroups;
    protected boolean isInitialized;
    protected RemoteComponentStatusListener remoteComponentStatusListener;

    public RemoteCASConfigurationServiceImpl(String groups)
    {
        configurationString = groups;
        configurationGroups = new LinkedList();
        remoteComponentStatusListener = new RemoteComponentStatusListener(this);

        try
        {
            if (!isInitialized)
            {
                ServicesHelper.getProductConfigurationService(); // on first call, will load PCS cache
                initializeConfigurations();
                isInitialized = true;
                Log.notification(this, "CAS Re-Initialization is Complete.");
                publishRecovery();
            }
        }
        catch(Exception e)
        {
            Log.exception(this, e);
        }
    }

    public synchronized void initializeConfigurations()
    {
        try
        {
            StringTokenizer t = new StringTokenizer(configurationString, ",");
            while (t.hasMoreTokens())
            {
                String s = t.nextToken();
                int groupKey = ServicesHelper.getProductConfigurationService().getGroupKey(s);
                configurationGroups.add(Integer.valueOf(groupKey));
            }
        }
        catch (Exception e)
        {
            Log.exception(this, e);
        }
        Iterator i = configurationGroups.iterator();
        while(i.hasNext())
        {
            Integer groupKey = (Integer)i.next();
            CurrentMarketRequestManagerFactory.create(groupKey);
            BookDepthRequestManagerFactory.create(groupKey);
            ExpectedOpeningPriceRequestManagerFactory.create(groupKey);
            NBBORequestManagerFactory.create(groupKey);
            TickerRequestManagerFactory.create(groupKey);
            RecapRequestManagerFactory.create(groupKey);
        }
    }

    public synchronized void cleanUpConfigurations()
    {
        Iterator i = configurationGroups.iterator();
        while(i.hasNext())
        {
            Integer groupKey = (Integer)i.next();
            CurrentMarketRequestManagerFactory.remove(groupKey);
            BookDepthRequestManagerFactory.remove(groupKey);
            ExpectedOpeningPriceRequestManagerFactory.remove(groupKey);
            NBBORequestManagerFactory.remove(groupKey);
            TickerRequestManagerFactory.remove(groupKey);
            RecapRequestManagerFactory.remove(groupKey);
        }
    }

    public synchronized int[] getGroupKeys()
    {
        int[] groupKeys = new int[configurationGroups.size()];
        Iterator i = configurationGroups.iterator();
        int j = 0;
        while(i.hasNext())
        {
            groupKeys[j] = ((Integer)i.next()).intValue();
            j++;

        }
        return groupKeys;
    }

    public synchronized void acceptRemoteComponentStatusUp()
    {
        try
        {
            if (!isInitialized)
            {
                ServicesHelper.getProductConfigurationService(); // on first call, will load PCS cache
                initializeConfigurations();
                isInitialized = true;
                Log.notification(this, "RemoteCAS Re-Initialization is Complete.");
                publishRecovery();
            }
        }
        catch(Exception e)
        {
            Log.exception(this, e);
        }
    }

    public synchronized void acceptRemoteComponentStatusDown()
    {
        if(isInitialized)
        {
            cleanUpConfigurations();
            if (Log.isDebugOn())
            {
                Log.debug(this, "RemoteCAS configuration cleanup complete");
            }
            isInitialized = false;
        }
    }

    protected void publishRecovery()
    {
        Iterator i = configurationGroups.iterator();
        int groupKey = 0;
        while(i.hasNext())
        {
            groupKey = ((Integer)i.next()).intValue();
            RemoteServicesHelper.getRemoteCASRecoveryPublisher().acceptMarketDataRecoveryForGroup(groupKey);
        }
    }
}
