package com.cboe.infrastructureServices.foundationFramework.utilities;

import com.cboe.infrastructureServices.eventService.FilterWatcherHome;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;

/**
 *  Helper to return the foundation framework related homes.
 */
public class FoundationFrameworkHomes
{
    protected static FilterWatcherHome filterWatcherHome;

    public static FilterWatcherHome getFilterWatcherHome()
    {
        if (filterWatcherHome == null)
        {
            filterWatcherHome = (FilterWatcherHome)findHome(FilterWatcherHome.HOME_NAME);
        }
        return filterWatcherHome;
    }

    protected static BOHome findHome(String name)
    {
        try
        {
            return HomeFactory.getInstance().findHome(name);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Failed to find home for name '" + name + "'");
        }
    }
}
