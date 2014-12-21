package com.cboe.instrumentationService.transactionTiming;

/* This class has package access */

import com.cboe.common.log.Logger;
import com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses.MBean;
import com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses.MBeanPropertyChangeEvent;
import com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses.MBeanPropertyChangeListener;
import com.cboe.systemsManagementService.managedObjectFramework.systemManagementAdapter.SystemManagementAdapter;

public class TransactionTimerProperties
{
	static private String eventChannelName = null;
	static private String prefix = null;
	static private String processName = null;
	static private int bufferSize = 0;
	static private int interval = 0;
	static private String defaultFilePath = null;
   static
	{

		processName = System.getProperty("ProcessName");
		int DEFLT_BUFFER_SIZE = 50;
		int DEFLT_INTERVAL = 60000;
		String DEFLT_PROCESS_NAME = "UnknownProcess";
		if ( null == ( prefix = System.getProperty("prefix") ) )
		{
			prefix = System.getenv( "SBT_PREFIX" );
			if ( null == prefix )
			{
				prefix = "";
			}
		}
        String bufferSizeString = System.getProperty( "TransactionTimer.bufferSize" );
        if ( null != bufferSizeString )
        {
            try
            {
                    bufferSize = Integer.parseInt( bufferSizeString );
                    System.out.println( "TransactionTimer buffer size set on through system properties to " + bufferSize );
            }
            catch ( NumberFormatException nfe )
            {
                Logger.sysAlarm(" Invalid number given for TransactionTimer.bufferSize in command line argument: " + 
                    bufferSizeString + ". XML or default will be used." );
            }
        }
		String DEFLT_CHANNEL_NAME = prefix + "InstrumentationChannel";
		try {
			SystemManagementAdapter systemManagementAdapter = SystemManagementAdapter.getInstance();

			MBean transactionTimer = systemManagementAdapter.getMBean("Instrumentation.TransactionTimer");
			String channelname = (String) transactionTimer.getPropertyValue("eventChannelNameProperty");	
			if(channelname.startsWith("<prefix>")){
				channelname = prefix + channelname.substring(8);
			}
			eventChannelName = channelname;

			if ( 0 == bufferSize )
			{
				bufferSize = ((Integer)(transactionTimer.getPropertyValue("bufferSizeProperty"))).intValue();
			}

			interval = ((Integer)(transactionTimer.getPropertyValue("intervalProperty"))).intValue();	

			if( null == processName ) 
			{
				MBean processBean = systemManagementAdapter.getMBean("Process");
				processName = (String) processBean.getName();
			}
			else // use the override if exists
			{
				processName = DEFLT_PROCESS_NAME;
			}

		}
		catch(Throwable tr){
			Logger.sysNotify(" WARNING: Error while obtaining the properties for transactionTimer. Default values will be used ");

			if(null == eventChannelName)
			{
				eventChannelName = DEFLT_CHANNEL_NAME;
			}
			if(0 == bufferSize)
			{
				bufferSize = DEFLT_BUFFER_SIZE;
			}
			if(0 == interval)
			{
				interval = DEFLT_INTERVAL;
			}
			if ( null == processName )
			{
					String orbName = System.getProperty("ORB.OrbName");
					if(null != orbName)
					{
						processName = orbName.replaceFirst(prefix, "");
					}
					else
					{
						processName = DEFLT_PROCESS_NAME;
					}
			}
		}
		/* Determine the path for any local/file output */
		/* Try:
			1. $INFRA_RUN_DIR/log
			2. $RUN_DIR/log
			3. current working directory
		*/
		if ( null != ( defaultFilePath = System.getenv( "INFRA_RUN_DIR" ) ) || 
		    null != ( defaultFilePath = System.getenv( "RUN_DIR" ) ) )
		{
			defaultFilePath += "/log/";
		}
		else
		{
			defaultFilePath = "./";
		}
	
	}
	static String getDefaultChannelName()
	{
		return eventChannelName;
	}
	static String getPrefix()
	{
		return prefix;
	}
	public static String getProcessName()
	{
		return processName;
	}
	public static String getDefaultPath()
	{
		return defaultFilePath;
	}
	static int getBufferSize()
	{
		return bufferSize;
	}
	static int getInterval()
	{
		return interval;
	}
}
