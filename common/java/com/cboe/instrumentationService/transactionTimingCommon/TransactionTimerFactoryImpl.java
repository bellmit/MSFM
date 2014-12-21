package com.cboe.instrumentationService.transactionTimingCommon;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import com.cboe.common.log.Logger;

public class TransactionTimerFactoryImpl
{
   private static TransactionTimerFactory baseImpl;
	private static String baseImplClassName =
		"com.cboe.infrastructureServices.instrumentationService.TransactionTimerFactoryImpl";
	private static String baseImplGetter = "getInstance";
	private static boolean failedGettingClass = false; /* Prevent flooding with messages in case of failure */
	public static synchronized TransactionTimerFactory getInstance()
	{
		if ( ( null == baseImpl ) && !failedGettingClass )
		{
			Class baseImplClass = null;
			try
			{
				baseImplClass = Class.forName( baseImplClassName );
			} catch ( ClassNotFoundException cnfe )
			{
				Logger.sysAlarm( "Unable to retrieve class for FF TT Factory: " + baseImplClassName, cnfe );
				failedGettingClass = true;
			}
			Method getterMethod = null;
			if ( null != baseImplClass )
			{
				try
				{
					getterMethod = baseImplClass.getMethod( baseImplGetter, new Class[ 0 ] );
				}
				catch ( NoSuchMethodException nsme  )
				{
					Logger.sysAlarm( "Unable to retrieve getter method (" +
						baseImplGetter + ") of class for FF TT Factory: " + baseImplClassName, nsme );
					failedGettingClass = true;
				}
				catch ( SecurityException se )
				{
					Logger.sysAlarm( "SecurityException retrieving getter method (" +
						baseImplGetter + ") of class for FF TT Factory: " + baseImplClassName, se );
					failedGettingClass = true;
				}
			}
			if ( null != getterMethod )
			{
				try
				{
					baseImpl = (TransactionTimerFactory) getterMethod.invoke( null, new Object[ 0 ] );
				}
				catch ( IllegalAccessException iae )
				{
					Logger.sysAlarm( "Illegal access invoking getter (" + baseImplGetter +
						") for FF TT Factory: " + baseImplClassName, iae );
					failedGettingClass = true;
				}
				catch ( IllegalArgumentException iae2 )
				{
					Logger.sysAlarm( "Illegal argument invoking getter (" + baseImplGetter +
						") for FF TT Factory: " + baseImplClassName, iae2 );
					failedGettingClass = true;
				}
				catch ( InvocationTargetException ite )
				{
					Logger.sysAlarm( "Invoication target exception invoking getter (" + baseImplGetter +
						") for FF TT Factory: " + baseImplClassName, ite );
					failedGettingClass = true;
				}
			}
		}
		return baseImpl;
	}
}

