package com.cboe.infrastructureServices.instrumentationService;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimerFactory;
import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimer;

public class TransactionTimerFactoryImpl implements TransactionTimerFactory
{
	/* Note: all of these instances are thin proxies; the underlying implementation will be singletons.
		Therefore, rather than keeping track of these here, we will just create a new instance each time
		we are called. */
	private static TransactionTimerFactory instance;
	private static final String localInstanceClassName =
	   "com.cboe.infrastructureServices.instrumentationService.TransactionTimerLocalImpl";
	private static final String remoteInstanceClassName =
	   "com.cboe.infrastructureServices.instrumentationService.TransactionTimerRemoteImpl";
	private static final String serviceContextInstanceClassName =
	   "com.cboe.infrastructureServices.instrumentationService.TransactionTimerServiceContextImpl";
	private static final String defaultInstanceClassName =
	   "com.cboe.infrastructureServices.instrumentationService.TransactionTimerRemoteImpl";

   private TransactionTimerFactoryImpl()
	{
	}
	public static synchronized TransactionTimerFactory getInstance()
	{
		if ( null == instance )
		{
			instance = new TransactionTimerFactoryImpl();
		}
		return instance;
	}
	private TransactionTimer createInstance( String className, String destination )
	{
			 TransactionTimer retVal = null;
		    try
		    {
			    Class c = Class.forName( className );;
			    Object o = c.newInstance();
				 ((TransactionTimerInternal)o).initialize( destination );
				 retVal = (TransactionTimer)o;
			}
			catch (Exception e)
			{
				FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "TransactionTimer.getInstance", "Failed to get transaction timer instance.", e);
			}
		return retVal;
	}
	public TransactionTimer getTransactionTimer()
	{
		return createInstance( defaultInstanceClassName, null );
	}
	public TransactionTimer getLocalTransactionTimer()
	{
		return createInstance( localInstanceClassName, null );
	}
	public TransactionTimer getLocalTransactionTimer( String fileName )
	{
		return createInstance( localInstanceClassName, fileName );
	}
	public TransactionTimer getRemoteTransactionTimer()
	{
		return createInstance( remoteInstanceClassName, null );
	}
	public TransactionTimer getRemoteTransactionTimer( String channelName )
	{
		return createInstance( remoteInstanceClassName, channelName );
	}
	public TransactionTimer getServiceContextTransactionTimer()
	{
		return createInstance( serviceContextInstanceClassName, null );
	}
}
