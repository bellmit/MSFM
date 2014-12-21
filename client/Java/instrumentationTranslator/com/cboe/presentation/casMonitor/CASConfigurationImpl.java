// -----------------------------------------------------------------------------------
// Source file: CASConfigurationImpl.java
//
// PACKAGE: com.cboe.presentation.casMonitor
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.casMonitor;

import com.cboe.interfaces.casMonitor.CASConfiguration;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.client.xml.bind.GIConfigurationRequestType;
import com.cboe.client.xml.bind.GIConfigurationResponseType;
import com.cboe.client.xml.bind.GICommandLineArgumentType;

import java.util.Properties;

public class CASConfigurationImpl extends AbstractBusinessModel implements CASConfiguration
{
    private Properties commandLineArgs;
    private String XMLConfiguration;

    private CASConfigurationImpl()
    {
        super();
        initialize();
    }

    protected CASConfigurationImpl(GIConfigurationResponseType configurationResponseType)
    {
        this();
        setData(configurationResponseType);
    }
    private void initialize()
    {
        this.XMLConfiguration = "";
        commandLineArgs = new Properties();
    }
    protected void setData(GIConfigurationResponseType configurationResponseType)
    {
        if(configurationResponseType.getRawXml() != null)
        {
            this.XMLConfiguration = configurationResponseType.getRawXml();
        }
        GICommandLineArgumentType[] commandLineArguments = configurationResponseType.getCommandLineArguments();
        for (int i = 0; i < commandLineArguments.length; i++)
        {
            GICommandLineArgumentType commandLineArgument = commandLineArguments[i];
            commandLineArgs.put(commandLineArgument.getKey(), commandLineArgument.getValue());
        }

    }
    /**
     * Returns command line arguments for a CAS.
     * @return commanfLineArgs Properties
     */
    public Properties getCommandLineArgs()
    {
        return this.commandLineArgs;
    }

    /**
     * Returns XML configuration for a CAS as a String.
     * @return configuration String
     */
    public String getXMLConfiguration()
    {
        return this.XMLConfiguration;
    }
}
