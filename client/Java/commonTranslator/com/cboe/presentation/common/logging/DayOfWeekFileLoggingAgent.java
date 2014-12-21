//
// -----------------------------------------------------------------------------------
// Source file: DayOfWeekFileLoggingAgent.java
//
// PACKAGE: com.cboe.loggingService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import com.cboe.loggingService.InitializationFailedException;
import com.cboe.loggingService.LoggingAgent;
import com.cboe.loggingService.LoggingFailedException;
import com.cboe.loggingService.LoggingServiceLog;
import com.cboe.loggingService.Message;
import com.cboe.loggingService.Utilities;

/**
 * This logging agent writes to a different log file for each day of the week.
 *
 * @author Thomas Morrow
 * @since Jun 3, 2008
 */
public class DayOfWeekFileLoggingAgent extends LoggingAgent
{
    private PrintWriter outputStream;
    private String filePath;
    private String namePrefix;
    private String dayOfWeek;
    private String fileExt;
    private final ReentrantLock fileLock = new ReentrantLock();


    /**
     * Set the initialization properties for the logging agent. The properties required in this set will depend on the
     * specific implementation of the logging agent. WARNING: the unique name for the agent must be set before this
     * method is called.
     *
     * @throws com.cboe.loggingService.InitializationFailedException
     *          An invalid properties set was given. Either it does not contain all the required properties, or the
     *          format of one of the property values is invalid.
     * @see super.getInitProperties
     * @see super.setUniqueName
     */
    @Override
    public void setInitProperties(Properties properties) throws InitializationFailedException
    {
        super.setInitProperties(properties);
        String myName = "LoggingService.loggingAgent." + getUniqueName();

        filePath = Utilities.getRequiredProperty(properties, myName + ".filePath");
        namePrefix = Utilities.getRequiredProperty(properties, myName + ".namePrefix");
        fileExt = Utilities.getRequiredProperty(properties, myName + ".fileExt");
    }

    /**
     * Get the initialization properties for the logging agent. The properties delivered in this set will depend on the
     * specific implementation of the logging agent and what initialization properties were initially set on it.
     * WARNING: the unique name for the agent must be set before this method is called.
     *
     * @return The initialization properties associated with this logging agent.
     * @see super.setInitProperties
     * @see super.setUniqueName
     */
    @Override
    public Properties getInitProperties()
    {
        String myName = "LoggingService.loggingAgent." + getUniqueName();
        Properties properties = super.getInitProperties();
        properties.setProperty(myName + ".filePath", filePath);
        properties.setProperty(myName + ".namePrefix", namePrefix);
        properties.setProperty(myName + ".fileExt", fileExt);

        return properties;
    }

    /**
     * Initialize the logging agent. In particular, in this method the agent should make contact with the target to
     * which logged messages will be sent. If the initialization is successful this method must set the status of the
     * agent to healthy. WARNING: the initialization properties must be set before this method is called.
     *
     * @throws InitializationFailedException The attempt to initialize the logging agent failed.
     * @see super.setHealthy
     * @see super.setInitProperties
     */
    public void initialize() throws InitializationFailedException
    {
        LoggingServiceLog.trace(this, "initialize()");
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        dayOfWeek = formatter.format(today);

        // Check properties.
        if (null == filePath || null == dayOfWeek || null == namePrefix || null == fileExt)
        {
            throw new InitializationFailedException("Invalid file path.");
        }
        StringBuffer stringBuffer = new StringBuffer(200);
        stringBuffer.append(filePath).append(namePrefix).append('.').append(dayOfWeek).append('.').append(fileExt);
        String fullFilePath = stringBuffer.toString();

        // Attempt initialization.
        try
        {
            boolean append = isAppendToFile(fullFilePath);
            FileOutputStream fos = new FileOutputStream(fullFilePath, append);
            outputStream = new PrintWriter(fos);
            setHealthy(true);
        }
        catch (IOException e)
        {
            LoggingServiceLog.debugException(this, e);
            throw new InitializationFailedException("The file at \"" + fullFilePath + "\" could not be opened in append mode.", e);
        }
    }

    private boolean isAppendToFile(String filePath)
    {
        boolean isAppend = true;
        File logFile = new File(filePath);
        if (logFile.exists())
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
            Date today = new Date();
            Date logFileModifiedDate = new Date(logFile.lastModified());
            String todayString = dateFormat.format(today);
            String logFileModifiedDateString = dateFormat.format(logFileModifiedDate);
            try
            {
                today = dateFormat.parse(todayString);
                logFileModifiedDate = dateFormat.parse(logFileModifiedDateString);
                if (!logFileModifiedDate.equals(today))
                {
                    isAppend = false;
                }
            }
            catch (ParseException e)
            {
                LoggingServiceLog.debugException(this, e);
            }

        }
        return isAppend;
    }

    /**
     * Log a single message.
     *
     * @throws LoggingFailedException The attempt to log the given message failed.
     */
    protected void log(Message message) throws LoggingFailedException
    {
        try
        {
            fileLock.lock();
            outputStream.println(message.toString());
            outputStream.flush();
        }
        finally
        {
            fileLock.unlock();
        }
    }

    /**
     * Test if this agent is the same as the given agent. This test should determine if two logging agents would share
     * the same target. For example, if two file logging agents were logging to the same file, even though they might
     * have different unique names, this test should return true. Note that two agents might be equivalent without being
     * equal, for example two file agents may log to the same file but may have different unique names.
     *
     * @return true if the given agent is equivalent to this agent.
     */
    public boolean isEquivalentTo(LoggingAgent otherAgent)
    {
        LoggingServiceLog.trace(this, "isEquivalentTo()");
        boolean isEquivalent = false;
        if (equals(otherAgent))
        {
            isEquivalent = true;
        }
        else if (otherAgent instanceof DayOfWeekFileLoggingAgent)
        {
            DayOfWeekFileLoggingAgent agent = (DayOfWeekFileLoggingAgent)otherAgent;
            if (filePath.equals(agent.getFilePath()) && namePrefix.equals(agent.getNamePrefix())
                    && dayOfWeek.equals(agent.getDayOfWeek()) && fileExt.equals(agent.getFileExt()))
            {
                isEquivalent = true;
            }
        }
        return isEquivalent;
    }

    public String getDayOfWeek()
    {
        return dayOfWeek;
    }

    public String getFileExt()
    {
        return fileExt;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public String getNamePrefix()
    {
        return namePrefix;
    }
}
