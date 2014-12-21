package com.cboe.infrastructureServices.foundationFramework.utilities;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.systemsManagementService.ApplicationPropertyHelper;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;

/**
 * This class was created in VisualAge.
 * @author Werner Kubitsch
 */
public class RouteNameHelper
{
	public final static String ROUTE_NAME = "routeName";
	public final static String REMOTE_ROUTE_NAME = "remoteRouteName";
	public final static String CACHE_REMOTE_ROUTE_NAME = "cacheRemoteRouteName";

	private static String routeName;
	private static String remoteRouteName;
    private static String cacheRemoteRouteName;

/**
 * RouteNameHelper should not be instantiated.
 */
private RouteNameHelper()
{
	super();
}

/**
 * This method gets the remote cacheRouteName from the config service.
 *
 * @author Tom Tharp
 * @return java.lang.String
 */
public static String getCacheRemoteRouteName()
{
	if (cacheRemoteRouteName == null)
	{
		try
		{
			cacheRemoteRouteName = ApplicationPropertyHelper.getProperty(CACHE_REMOTE_ROUTE_NAME);
		}
		catch (NoSuchPropertyException e)
		{
			cacheRemoteRouteName = FoundationFramework.getInstance().getName();
			Log.alarm("No value defined for " + CACHE_REMOTE_ROUTE_NAME + "; using '" + cacheRemoteRouteName + "' as default value");
		}
	}
	return cacheRemoteRouteName;
}

/**
 * This method gets the remote routeName from
 * from the config service.
 *
 * @author Werner Kubitsch
 * @return java.lang.String
 */
public static String getRemoteRouteName()
{
	if (remoteRouteName == null)
	{
		try
		{
			remoteRouteName = ApplicationPropertyHelper.getProperty( REMOTE_ROUTE_NAME );
		}
		catch (NoSuchPropertyException e)
		{
			remoteRouteName = FoundationFramework.getInstance().getName();
			Log.alarm("No value defined for " + REMOTE_ROUTE_NAME + " using " + remoteRouteName + " as default value");
		}
	}
	return remoteRouteName;
}

/**
 * This method gets the routeName for the BusinessService from
 * from the config service.
 *
 * @author Werner Kubitsch
 * @return java.lang.String
 */
public static String getRouteName()
{
	if (routeName == null)
	{
		try
		{
			routeName = ApplicationPropertyHelper.getProperty( ROUTE_NAME );
		}
		catch (NoSuchPropertyException e)
		{
			routeName = FoundationFramework.getInstance().getName();
			Log.alarm("No value defined for " + ROUTE_NAME + " using " + routeName + " as default value");
		}
	}

	return routeName;
}
}
