//
// -----------------------------------------------------------------------------------
// Source file: ProcessDetailFinder.java
//
// PACKAGE: com.cboe.infra.presentation.network
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.*;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.presentation.environment.EnvironmentManagerFactory;
import com.cboe.presentation.environment.EnvironmentProperties;


/**
 * Singleton.  This class retrieves details about a process - its orb name, process name, port number (useful for
 * getting filters from CommandConsoleProxy).
 */
public class ProcessDetailFinder
{
    private static ProcessDetailFinder instance = new ProcessDetailFinder();
    private Map orbKeysHost = new HashMap();
    private Map orbKeysPort = new HashMap();

    public static ProcessDetailFinder getInstance()
    {
        return instance;
    }

    public short getPortNum(String name)
    {
        short rv = -1;
        Short port = (Short) orbKeysPort.get(getRealOrbName(name));
        if (port != null)
        {
            rv = port.shortValue();
        }
        return rv;
    }

    public String getHost(String name)
    {
        return (String) orbKeysHost.get(getRealOrbName(name));
    }

    public String getOrbName(String name)
    {
        return getRealOrbName(name);
    }

    private String getRealOrbName(String name)
    {
        String realOrbName = name;
        if (name.indexOf("/") != -1)
        {
            String[] orbNameParts = name.split("/");
            // Get first non-empty part.  NodeNames are typically like the following:
            // /{$SBT_PREFIX}SomeName[/FurtherParts]
            for (int i = 0; i < orbNameParts.length; i++)
            {
                if (orbNameParts[i].length() > 0)
                {
                    realOrbName = orbNameParts[i];
                    break;
                }
            }
        }
        return realOrbName;
    }

    private ProcessDetailFinder()
    {
        EnvironmentProperties props = EnvironmentManagerFactory.find().getCurrentEnvironment();
        if (props != null)
        {
            String processListRef = props.getProcessListRef();
            BufferedReader in = null;
            try
            {
                URL processListURL = new URL(processListRef);
                InputStream is = processListURL.openStream();
                in = new BufferedReader(new InputStreamReader(is));
                while (true)
                {
                    String line = in.readLine();
                    if (line == null || line.length() == 0)
                    {
                        break;
                    }
                    String[] tokens = line.split(",");
                    if (tokens.length >= 3)
                    {
                        try
                        {
                            String host = tokens[0];
                            String orbName = tokens[2];
                            Short port = new Short(tokens[3]);
                            orbKeysHost.put(orbName, host);
                            orbKeysPort.put(orbName, port);
                        }
                        catch (NumberFormatException nfe)
                        {
                            GUILoggerHome.find().alarm(
                                    "ProcessDetailFinder.<INIT> - Skipping " + line + " port " + tokens[2] +
                                    " not a number");
                        }
                    } // end correct-line-format-if
                } // end read-file-loop
            }
            catch (MalformedURLException mue)
            {
                GUILoggerHome.find().exception(
                        "ProcessDetailFinder.<INIT> - Malformed URL (" + processListRef + "): " + mue.getMessage(),
                        mue);
            }
            catch (FileNotFoundException fnfe)
            {
                GUILoggerHome.find().exception(
                        "ProcessDetailFinder.<INIT> - Could not open file " + processListRef + ": File not found.",
                        fnfe);
            }
            catch (IOException ioe)
            {
                GUILoggerHome.find().exception(
                        "ProcessDetailFinder.<INIT> - Could not read file " + processListRef + ": " + ioe.getMessage(),
                        ioe);
            }
        }
    }

}
