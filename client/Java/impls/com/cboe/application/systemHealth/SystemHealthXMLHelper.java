package com.cboe.application.systemHealth;

import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.client.xml.bind.GIUserExceptionType;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.domain.util.TimeServiceWrapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

abstract class SystemHealthXMLHelper
{
    static String logAndConvertException(String xmlRequestSource, String requestName, Exception e)
    {
        logException(requestName, e);

        String stackTrace = getStackTrace(e);

        GIUserExceptionType giUserException = XmlBindingFacade.getInstance().createUserExceptionType(e.getClass().getName(),
                                                               e.getMessage(),
                                                               stackTrace,
                                                               0, (short) 0,
                                                               xmlRequestSource,
                                                               TimeServiceWrapper.formatToDateTime());

        String result = marshal(giUserException);

        if(Log.isDebugOn())
        {
            Log.debug(result);
        }
        
        return result;
    }

    protected static String marshal(Object obj)
    {
        return XmlBindingFacade.getInstance().marshallObject(obj, null, null);
    }
    
    private static void logException(String requestName, Exception e)
    {    
        Log.exception("Exception during processing of System Health Monitor request: " + requestName + ".", e);
    
    }
            
    private static String getStackTrace(Exception e)
    {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        try
        {
            sw.close();
        }
        catch (IOException e1)
        {
        }
        return sw.toString();        
    }
}
