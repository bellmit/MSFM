package com.cboe.domain.iec;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


public class ChannelEventCacheFactory 
{
	static String cacheClass; 

	static {
		cacheClass = System.getProperty( "NewChannelEventCacheClass", "com.cboe.domain.iec.NoPoolingChannelEventCache");
		Log.information("NewChannelEventCacheClass is set to " + cacheClass);
	}
	
	public static ChannelEventCache createChannelEventCache()
	{
		ChannelEventCache eventCache = null;
        try
        {
        	eventCache = (ChannelEventCache)((Class.forName( cacheClass )).newInstance());
        }
        catch(ClassNotFoundException e)
        {
            System.err.println( "Error: unable to find ChannelEventCache class: " + cacheClass );
        }
        catch(java.lang.IllegalAccessException iae)
        {
            System.err.println( "Error: unable to Access ChannelEventCache class: " + cacheClass );
        }
        catch(java.lang.InstantiationException ine)
        {
            System.err.println( "Error: unable to Instantiate ChannelEventCache class: " + cacheClass );
        }
		return eventCache;
	}
}
