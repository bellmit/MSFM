package com.cboe.cfix.fix.session;

import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.PropertiesHelper;
import com.cboe.client.util.ClassHelper;
import com.cboe.util.ExceptionBuilder;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.*;
import java.net.Socket;

/**
 * FixMDXSessionManager.java
 * Replaces FixSessionManager.java for MDX enabled CFIX
 *
 * @author Dmitry Volpyansky / Vivek Beniwal
 *
 */

public class FixMDXSessionManager implements FixSessionManagerIF
{
    protected Properties          sessionProperties;
    protected HashMap             sessionMap = new HashMap();
    protected HashMap             sessionInformationMap = new HashMap();
    protected FixMessageFactoryIF fixMessageFactory;
    protected Class               fixSessionClass;
    protected String              propertyPrefix;
    protected String              engineName;

    public static final String UNSUCCESSFUL_CONNECTION_SESSION_INFORMATION_NAME = "_Unsuccessful_Connections_";

    public void initialize(String propertyPrefix, Properties sessionProperties) throws Exception
    {
        if(Log.isDebugOn())
        {
            Log.debug("Initialized FixMDXSessionManager. CFIX is MDX Enabled.");
        }

        this.propertyPrefix    = propertyPrefix;
        this.sessionProperties = sessionProperties;

        this.fixSessionClass   =                       Class.forName(PropertiesHelper.instance().getProperty(sessionProperties, propertyPrefix, "cfix.fixSessionManager.fixSessionClassMDX", "com.cboe.cfix.fix.fix42.session.FixMDXSession"));
        this.fixMessageFactory = (FixMessageFactoryIF) ClassHelper.loadClass(PropertiesHelper.instance().getProperty(sessionProperties, propertyPrefix, "cfix.fixSessionManager.fixMessageFactoryClass", "com.cboe.cfix.fix.fix42.generated.messages.FixMessageFactory"));
    }

    public FixMessageFactoryIF getFixMessageFactory()
    {
        return fixMessageFactory;
    }

    public void setEngineName(String engineName)
    {
        this.engineName = engineName;
    }

    public String getEngineName()
    {
        return engineName;
    }

    public synchronized FixSessionIF createFixSession(Socket socket) throws Exception
    {
        FixSessionIF fixSession = (FixSessionIF) fixSessionClass.newInstance();

        fixSession.setTargetCompID(engineName);

        fixSession.initialize(this, propertyPrefix, sessionProperties);

        fixSession.resetSocket(socket);

        fixSession.setPort(socket.getLocalPort());

        fixSession.addFixSessionListener(this);

        return fixSession;
    }

    public int size()
    {
        return sessionMap.size();
    }

    public synchronized FixSessionIF getFixSessionByName(String name)
    {
        return (FixSessionIF) sessionMap.get(name);
    }

    public synchronized FixSessionInformationIF getFixSessionInformationByName(String name)
    {
        FixSessionInformationIF fixSessionInformation = (FixSessionInformationIF) sessionInformationMap.get(name);
        if (fixSessionInformation == null && UNSUCCESSFUL_CONNECTION_SESSION_INFORMATION_NAME.equals(name))
        {
            fixSessionInformation = (FixSessionInformationIF) ClassHelper.loadClass("com.cboe.cfix.fix.fix42.session.FixSessionInformation"); //BUGBUG HARDCODED for 4.2

            sessionInformationMap.put(UNSUCCESSFUL_CONNECTION_SESSION_INFORMATION_NAME, fixSessionInformation);
        }

        return fixSessionInformation;
    }

    public synchronized void copyFixSessionList(List list)
    {
        list.addAll(sessionMap.values());
    }

    public synchronized void sessionReset(FixSessionIF fixSession)
    {

    }

    public synchronized void sessionLogonRequest(FixSessionIF fixSession, FixMessageIF fixMessage) throws Exception
    {
        String senderCompID  = fixMessage.getSenderCompID();

        if (null == fixSession.getPropertiesHelper().getProperties().getProperty("session." + senderCompID + ".cfix.fixSession.senderCompID"))
        {
            throw ExceptionBuilder.dataValidationException("SenderCompID(" + senderCompID + ") Not Configured In This Engine", 0);
        }

        String sessionName   = senderCompID;
        String sessionPrefix = "session." + senderCompID;

        if (sessionMap.containsKey(sessionName))
        {
            throw ExceptionBuilder.dataValidationException("SenderCompID(" + senderCompID + ") Already Logged In", 0);
        }

        PropertiesHelper propertiesHelper = fixSession.getPropertiesHelper();
        propertiesHelper.setPrefix(sessionPrefix);

        fixSession.setSenderCompID(senderCompID);

        if (propertiesHelper.getPropertyBoolean("cfix.fixSession.held", "false"))
        {
            throw ExceptionBuilder.dataValidationException("Session Disallows Logins (Placed On Hold By Operations)", 0);
        }

        String startUpAtHHMMString = propertiesHelper.getProperty("cfix.fixSession.startUpAtHHMM", "00:00");
        if (startUpAtHHMMString != null)
        {
            int hour = startUpAtHHMMString.charAt(0) - '0';
            hour = hour * 10 + startUpAtHHMMString.charAt(1) - '0';

            int minute = startUpAtHHMMString.charAt(3) - '0';
            minute = minute * 10 + startUpAtHHMMString.charAt(4) - '0';

            if (hour != 0 || minute != 0)
            {
                GregorianCalendar currentTime = new GregorianCalendar();
                GregorianCalendar startUpAtHHMMTime = new GregorianCalendar(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DATE), hour, minute, 0);

                if (currentTime.getTime().getTime() < startUpAtHHMMTime.getTime().getTime())
                {
                    throw ExceptionBuilder.dataValidationException("Session Disallows Logins (Prior To " + startUpAtHHMMTime.getTime() + ")", 0);
                }
            }
        }

        FixSessionInformationIF fixSessionInformation = (FixSessionInformationIF) sessionInformationMap.get(sessionName);

        if (fixSessionInformation == null)
        {
            String klass = propertiesHelper.getProperty("cfix.fixSession.fixSessionInformationClass", "com.cboe.cfix.fix.fix42.session.FixSessionInformation");
            if (klass == null)
            {
                throw ExceptionBuilder.dataValidationException("SenderCompID(" + senderCompID + ") Misconfigured In This Engine", 0);
            }

            fixSessionInformation = (FixSessionInformationIF) ClassHelper.loadClassWithExceptions(klass);
        }

        fixSessionInformation.initialize(fixSession);

        sessionMap.put(sessionName, fixSession);
        sessionInformationMap.put(sessionName, fixSessionInformation);

        fixSession.setFixSessionInformation(fixSessionInformation);
    }

    public synchronized void sessionStarting(FixSessionIF fixSession)
    {

    }

    public synchronized void sessionTargetLoggedIn(FixSessionIF fixSession)
    {

    }

    public synchronized void sessionTerminating(FixSessionIF fixSession, int sessionEndingFlags)
    {
        sessionMap.remove(fixSession.getSenderCompID());
    }

    public synchronized void sessionEnded(FixSessionIF fixSession, int sessionEndingFlags)
    {

    }

    public synchronized void copyFixSessionMap(Map map)
    {
        map.putAll(sessionMap);
    }

    public synchronized void copyFixSessionInformationMap(Map map)
    {
        map.putAll(sessionInformationMap);
    }
}
