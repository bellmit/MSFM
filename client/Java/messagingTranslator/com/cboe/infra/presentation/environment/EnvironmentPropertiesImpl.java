//
// -----------------------------------------------------------------------------------
// Source file: EnvironmentPropertiesImpl.java
//
// PACKAGE: com.cboe.infra.presentation.environment
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.environment;

import java.util.*;

import com.cboe.presentation.environment.EnvironmentProperties;

/**
 *
 */
public class EnvironmentPropertiesImpl implements EnvironmentProperties
{
    protected String name;
    protected String sbtPrefix;
    protected String initRefIOR;
    protected String processListRef;
    protected String monitorChannelPrefix;
    protected String extentMapChannelPrefix;
    protected String amqServerFailoverURL;
    protected String amqServerOverrideURL;
    protected long   alertErrorThreshold;
    protected long   alertWarningThreshold;

    protected final static int ALERT_ERROR_THRESHOLD_DEFAULT = 5096;
    protected final static int ALERT_WARNING_THRESHOLD_DEFAULT = 1024;

    protected Map serviceProperties;
    protected Map<String,String> otherEnvironmentProperty;

    /**
     * This class should ever be created by an EnvironmentManager
     */
    EnvironmentPropertiesImpl(String name)
    {
        serviceProperties = new TreeMap();
        this.name = name;
        sbtPrefix = "Unknown";
        monitorChannelPrefix = "Unknown";
        extentMapChannelPrefix = "Unknown";
        initRefIOR = "Unknown";
        amqServerFailoverURL = "tcp://Unknown:5112,tcp://Unknown:5112";
        //serviceProperties.put( INIT_REF_IOR, "Unknown" );
        alertErrorThreshold = ALERT_ERROR_THRESHOLD_DEFAULT;
        alertWarningThreshold = ALERT_WARNING_THRESHOLD_DEFAULT;
    }

    /**
     * This class should only ever be created by an EnvironmentManager
     */
    EnvironmentPropertiesImpl(String environmentName, String prefix, String iorRef)
    {
        this.name = environmentName;
        this.sbtPrefix = prefix;
        initRefIOR = iorRef;
        serviceProperties = new TreeMap();
        //serviceProperties.put( INIT_REF_IOR, iorRef );
        alertErrorThreshold = ALERT_ERROR_THRESHOLD_DEFAULT;
        alertWarningThreshold = ALERT_WARNING_THRESHOLD_DEFAULT;
    }

    /**
     * Every environment has a read-only name that is specified when it is created.
     */
    public String getName()
    {
        return name;
    }


    /**
     * Retrieve the SBT_PREFIX value that is used to prefix all service names
     */
    public String getSBTPrefix()
    {
        return sbtPrefix;
    }

    /**
     * Specify/change the SBT_PREFIX value that is used to prefix all service names
     */
    public void setSBTPrefix(String prefix)
    {
        sbtPrefix = prefix;
    }

    /**
     * Retrieve the monitoring service channel prefix that is used for all channel names
     * in this environment.
     * Note: this may be a ',' tokenized string
     */
    public String getMonitorPrefix()
    {
        return monitorChannelPrefix;
    }

    /**
     * Specify/change the monitoring service prefix value
     */
    public void setMonitorPrefix(String newPrefix)
    {
        monitorChannelPrefix = newPrefix;
    }

    /**
     * Retrieve the extent map service channel prefix that is used for all channel names
     * in this environment.
     * Note: this may be a ',' tokenized String
     */
    public String getExtentMapPrefix()
    {
        return extentMapChannelPrefix;
    }

    /**
     * Specify/change the extent map service channel prefix that is used for all channe
     * names  in this environment
     */
    public void setExtentMapPrefix(String newPrefix)
    {
        extentMapChannelPrefix = newPrefix;
    }

    /**
     * Retrieve the URL of the initial references IOR for this environment
     */
    public String getInitIORRef()
    {
        return initRefIOR;
        //return (String) serviceProperties.get(INIT_REF_IOR);
    }

    /**
     * Specify/change the URL of the initial references IOR for this environment
     */
    public void setInitIORRef(String url)
    {
        initRefIOR = url;
        //serviceProperties.put( INIT_REF_IOR, url );
    }

    /**
     * Retrieve the URL for the file containing process list details
     * such as orb port number, host, etc.
     * @return URL The location of the file containing the details for all relevant processes
     */
    public String getProcessListRef()
    {
        return processListRef;
    }

    /**
     * Set the process list detail URL
     */
    public void setProcessListRef(String theRef)
    {
        processListRef = theRef;
    }

    /**
     * Set the value of property specified by name.   If the property named already exists,
     * its value is overwritten.
     * @see java.util.Properties#setProperty(String,String)
     */
    public void addServiceProperty(String name, String value)
    {
        serviceProperties.put(name, value);
    }

    /**
     * Adds all of the properties present in props in the serviceProps,
     * overwriting existing values when already present.
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void addAllServiceProperties(Map props)
    {
        serviceProperties.putAll(props);
    }

    /**
     * Returns a copy of the serviceProperties
     */
    public Map getServiceProperties()
    {
        return serviceProperties;
    }

    public String getAMQServerFailoverURL()
    {
        return amqServerFailoverURL;
    }

    public void setAMQServerFailoverURL(String urlStr)
    {
        amqServerFailoverURL = urlStr;
    }

    public String getAMQServerOverrideURL()
    {
        return amqServerOverrideURL;
    }

    public void setAMQServerOverrideURL(String urlStr)
    {
        amqServerOverrideURL = urlStr;
    }

    public String toString()
    {
        StringBuffer rv = new StringBuffer("{");
        rv.append(name);
        rv.append(",");

        rv.append(sbtPrefix);
        rv.append(",");
        rv.append(( String ) serviceProperties.get(INIT_REF_IOR));
        rv.append("}");
        return rv.toString();

    }

    public long getAlertErrorThreshold()
    {
        return alertErrorThreshold;
    }

    public void setAlertErrorThreshold(long alertErrorThreshold)
    {
        this.alertErrorThreshold = alertErrorThreshold;
    }

    public long getAlertWarningThreshold()
    {
        return alertWarningThreshold;
    }

    public void setAlertWarningThreshold(long alertWarningThreshold)
    {
        this.alertWarningThreshold = alertWarningThreshold;
    }

    public String getProperty(String propertyKey) {
        return (String)otherEnvironmentProperty.get(propertyKey);
    }

    public void addProperty(String name, String value) {
       otherEnvironmentProperty.put(name,value);
    }

    public Map getOtherProperties() {
        return otherEnvironmentProperty;
    }

}
