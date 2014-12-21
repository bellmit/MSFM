package com.cboe.uuidServiceCommon;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import com.cboe.common.log.Logger;

public class IdServiceClientWrapperImpl {
    
    private static IdServiceClientWrapper baseImpl;
    private static String baseImplClassName = "com.cboe.uuidServiceClient.IdServiceClientWrapperImpl";
    private static boolean failedGettingClass = false;
    private static String baseImplGetter = "getInstance";


    public static synchronized IdServiceClientWrapper getInstance(){
	if ((null == baseImpl) && (!failedGettingClass)) {
	    Class baseImplClass = null;
            try {
               baseImplClass = Class.forName(baseImplClassName);
	       System.out.println("Found class");
           }
            catch(ClassNotFoundException cnfe) {
		System.out.println("Unable to Find class");
		Logger.sysAlarm( "Unable to retrieve class for IdServiceClient: " + baseImplClassName, cnfe );
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
					     baseImplGetter + ") of class for IdServiceClient: " + baseImplClassName, nsme );
			    failedGettingClass = true;
			}
		    catch ( SecurityException se )
			{
			    Logger.sysAlarm( "SecurityException retrieving getter method (" +
					     baseImplGetter + ") of class for IdServiceClient: " + baseImplClassName, se );
			    failedGettingClass = true;
			}
		}
	    if ( null != getterMethod )
		{
		    try
			{
			    baseImpl = (IdServiceClientWrapper) getterMethod.invoke( null, new Object[ 0 ] );
			}
		    catch ( IllegalAccessException iae )
			{
			    Logger.sysAlarm( "Illegal access invoking getter (" + baseImplGetter +
					     ") IdServiceClient: " + baseImplClassName, iae );
			    failedGettingClass = true;
			}
		    catch ( IllegalArgumentException iae2 )
			{
			    Logger.sysAlarm( "Illegal argument invoking getter (" + baseImplGetter +
					     ") for IdServiceClient: " + baseImplClassName, iae2 );
			    failedGettingClass = true;
			}
		    catch ( InvocationTargetException ite )
			{
			    Logger.sysAlarm( "Invoication target exception invoking getter (" + baseImplGetter +
					     ") for IdServiceClient: " + baseImplClassName, ite );
			    failedGettingClass = true;
			}
		}
	}
	return baseImpl;
    }
    
}
