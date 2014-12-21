//
// -----------------------------------------------------------------------------------
// Source file: FtpLogFileStorage.java
//
// PACKAGE: com.cboe.presentation.logging
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;


import java.net.InetAddress;
import java.net.UnknownHostException;




import java.text.SimpleDateFormat;
import java.util.Date;

import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.storage.FtpStorage;
import com.cboe.presentation.common.storage.StorageManagerImpl;

/**
 * @author Thomas Morrow
 * @since Jun 11, 2008
 */
public class FtpLogFileStorage extends FtpStorage
{
    public static final String USER_PROPERTY_KEY = "Log.Remote.User";
    public static final String PSWD_PROPERTY_KEY = "Log.Remote.Password";
    public static final String HOST_PROPERTY_KEY = "Log.Remote.Host";
    public static final String PORT_PROPERTY_KEY = "Log.Remote.Port";
    public static final String REMOTE_PATH_PROPERTY_KEY = "Log.Remote.Path";
    public static final String LOCAL_PATH_PROPERTY_KEY = "Log.Local.Path";
    public static final String FTP_LOGS_ENABLED_KEY = "Ftp.Logs.Enabled";
    public static final String UNKNOWN_HOST = "unkownhost";
    private String dayOfWeek;
    private String hostName = null;
    private String localLogFilePath = null;
    private String remoteLogFilePath = null;
    private FilenameFilter filenameFilter;
    private static FtpLogFileStorage instance = null;

    private FtpLogFileStorage()
    {
        initialize();
    }

    private void initialize()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        dayOfWeek = formatter.format(new Date());

        try
        {
            InetAddress addr = InetAddress.getLocalHost();
            hostName = addr.getHostName();
        }
        catch (UnknownHostException e)
        {
            GUILoggerHome.find().exception("Could not resolve hostname for FTPing log files", e);
        }

        if (hostName == null)
        {
            hostName = UNKNOWN_HOST;
        }

        filenameFilter = new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.contains(dayOfWeek);
            }
        };

        if (AppPropertiesFileFactory.isAppPropertiesAvailable())
        {

            user = AppPropertiesFileFactory.find().getValue(StorageManagerImpl.PROPERTY_SECTION, USER_PROPERTY_KEY);
            password = AppPropertiesFileFactory.find().getValue(StorageManagerImpl.PROPERTY_SECTION, PSWD_PROPERTY_KEY);
            host = AppPropertiesFileFactory.find().getValue(StorageManagerImpl.PROPERTY_SECTION, HOST_PROPERTY_KEY);
            port = Integer.parseInt(AppPropertiesFileFactory.find().getValue(StorageManagerImpl.PROPERTY_SECTION, PORT_PROPERTY_KEY));
            localLogFilePath = AppPropertiesFileFactory.find().getValue(StorageManagerImpl.PROPERTY_SECTION, LOCAL_PATH_PROPERTY_KEY);
            remoteLogFilePath = AppPropertiesFileFactory.find().getValue(StorageManagerImpl.PROPERTY_SECTION, REMOTE_PATH_PROPERTY_KEY);
        }
        if (user == null || password == null || host == null || port == 0 || localLogFilePath == null || remoteLogFilePath == null)
        {
            GUILoggerHome.find().alarm("Initialization failed for com.cboe.presentation.logging.FtpLogFileStorage");
        }
    }

    public static synchronized FtpLogFileStorage getInstance()
    {
        if (instance == null)
        {
            instance = new FtpLogFileStorage();
        }
        return instance;
    }

    public synchronized void copyLogFiles()
    {
        File logDir = new File(localLogFilePath);
        if (logDir.exists() && logDir.isDirectory())
        {
            File[] logFiles = logDir.listFiles(filenameFilter);
            for (File file : logFiles)
            {
                if (file.exists())
                {
                    try
                    {
                        /*
                         * create an appropriate name
                         */
                        String name = new StringBuilder(100).append(
                                remoteLogFilePath).append('/').append(hostName)
                                .append('/').append(file.getName()).toString();

                        /*
                         * make a stream and store it :) 
                         */
                        InputStream inputStream = new FileInputStream(file);
                        store(name, inputStream);

                    }
                    catch (IOException e)
                    {
                        GUILoggerHome.find().exception(e);
                    }
                }
            }
        }
    }

    public static boolean isFtpLogsEnabled()
    {
        boolean enabled = false;
        if (AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String enabledStr = AppPropertiesFileFactory.find().getValue(StorageManagerImpl.PROPERTY_SECTION, FTP_LOGS_ENABLED_KEY);
            enabled = Boolean.parseBoolean(enabledStr);
        }
        return enabled;
    }





}
