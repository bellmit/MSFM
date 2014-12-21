package com.cboe.infrastructureServices.instrumentationService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.Instrumentor;

/**
 * This is a helper class for formatting instrumentor user data
 */
public class ServerUserDataHelper
{
	// Mike Neher
	// needed to change the dependency on userDataTypes.
	public interface UserDataTypes
	{
	    public final static String EXCEPTION="3";
	    public final static String INFO="9";
	}
	
    private static Date currentTimestamp = new Date();
    private static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
    public static final String THREAD_RUNNING_TEXT = "thread started running";

    public synchronized static void setUserData(Instrumentor aInstrumentor, Exception e)
    {
        Instrumentor instrumentor = aInstrumentor;
        if (instrumentor == null)
        {
            return;
        }

        String msgAndStack = "";
        if (e != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            msgAndStack = sw.toString();
            sw = null;
            pw = null;
        }
        else {
            msgAndStack = "No specific exception caught.";
        }

       // report exception in string format in userData field
        currentTimestamp.setTime(System.currentTimeMillis());
        String timeString = dateFormater.format(currentTimestamp);
        instrumentor.setUserData(UserDataTypes.EXCEPTION + "=" + timeString + ": " + msgAndStack);
        Log.information("ServerUserDataHelper::setUserData get exception string=" + msgAndStack);
    }

    public synchronized static void setUserData(Instrumentor instrumentor, String userDataType, String data)
    {
        currentTimestamp.setTime(System.currentTimeMillis());
        String timeString = dateFormater.format(currentTimestamp);
        instrumentor.setUserData(userDataType + "=" + timeString + ": " + data);
    }

    public synchronized static void setUserData(Instrumentor instrumentor, String data)
    {
        setUserData(instrumentor, UserDataTypes.INFO, data);
    }

}
