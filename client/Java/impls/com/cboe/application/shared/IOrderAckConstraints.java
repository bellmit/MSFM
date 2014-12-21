package com.cboe.application.shared;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: lowery
 * Date: Jan 8, 2007                                                                        su
 * Time: 2:33:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class IOrderAckConstraints
{
    private static final String TOKENIZER_DELIMITERS = ",; ";
    private static IOrderAckConstraints localInstance = null;

    private StringBuilder  sessionStr = new StringBuilder();
    private static List<String> sessions = Collections.synchronizedList(new ArrayList<String>(5));

    private IOrderAckConstraints (String sessions)
    {
        try
        {
            loadProperties(sessions);
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    public static IOrderAckConstraints getInstance(String sessions)
    {
        if (localInstance == null)
        {
            localInstance = new IOrderAckConstraints(sessions);
        }

        return localInstance;
    }

    private void loadProperties(String theSessions) throws Exception
    {
        String sessionProperty = theSessions;

        if ((sessionProperty != null) && (sessionProperty.length() > 0))
        {
            StringTokenizer sessionTokenizer = new StringTokenizer(sessionProperty, TOKENIZER_DELIMITERS, false);

            while (sessionTokenizer.hasMoreTokens())
            {
                String s = sessionTokenizer.nextToken();
                sessions.add(s.trim());
                sessionStr.append(s);
                sessionStr.append(" ");
            }
        }
        else
        {
            sessions.add("NoSessions");
            sessionStr.append("No IOrder Sessions");
            Log.information("IOrderAckConstrains::loadProperties() no sessions defined in xml file.");
        }
    }

    public boolean isSuppressSession(String session)
    {
        return (sessions.contains(session));
    }

    public String toString()
    {
        return sessionStr.toString();
    }
}