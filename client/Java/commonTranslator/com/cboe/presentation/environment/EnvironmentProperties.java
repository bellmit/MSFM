package com.cboe.presentation.environment;

import java.util.Map;


public interface EnvironmentProperties
{
    public static final String INIT_REF_IOR = "initRef";
    public static final String SBT_PREFIX = "sbtPrefix";
    public static final String MONITOR_PREFIX = "monitorPrefix";
    public static final String EXTENT_MAP_PREFIX = "extentMapPrefix";
    public static final String PROCESS_LIST_REF = "processListRef";
    public static final String ALERT_ERROR_PREFIX = "alert.errorThreshold";
    public static final String ALERT_WARNING_PREFIX = "alert.warningThreshold";

    public static final String AMQ_SERVER_FAILOVER = "serviceName.Jms.ActiveMQ.UrlList";
    public static final String AMQ_SERVER_OVERRIDE = "serviceName.Jms.Override.BrokerUrlList";

    /**
     * Every environment has a read-only name that is specified when it is created.
     */
    public String getName();

    /**
     * Retrieve the SBT_PREFIX value that is used to prefix all service names
     */
    public String getSBTPrefix();

    /**
     * Specify/change the SBT_PREFIX value that is used to prefix all service names
     */
    public void setSBTPrefix(String prefix);

    /**
     * Retrieve the monitoring service channel prefix that is used for all channel names in this environment. Note: this
     * may be a ',' tokenized string
     */
    public String getMonitorPrefix();

    /**
     * Specify/change the monitoring service prefix value
     */
    public void setMonitorPrefix(String newPrefix);

    /**
     * Retrieve the extent map service channel prefix that is used for all channel names in this environment. Note: this
     * may be a ',' tokenized String
     */
    public String getExtentMapPrefix();

    /**
     * Specify/change the extent map service channel prefix that is used for all channe names  in this environment
     */
    public void setExtentMapPrefix(String newPrefix);

    /**
     * Retrieve the URL of the initial references IOR for this environment
     */
    public String getInitIORRef();

    /**
     * Specify/change the URL of the initial references IOR for this environment
     */
    public void setInitIORRef(String url);

    /**
     * Retrieve the URL for the file containing process list details such as orb port number, host, etc.
     *
     * @return URL The location of the file containing the details for all relevant processes
     */
    public String getProcessListRef();

    /**
     * Set the process list detail URL
     */
    public void setProcessListRef(String url);

    public String getAMQServerFailoverURL();

    public void setAMQServerFailoverURL(String urlStr);

    public String getAMQServerOverrideURL();

    public void setAMQServerOverrideURL(String urlStr);
    /**
     * Set the value of property specified by name.   If the property named already exists, its value is overwritten.
     *
     * @see java.util.Properties#setProperty(String,String)
     */
    public void addServiceProperty(String name, String value);

    /**
     * Adds all of the properties present in props in the serviceProps, overwriting existing values when already
     * present.
     *
     * @see java.util.Map#putAll(Map)
     */
    public void addAllServiceProperties(Map props);

    /**
     * Returns a copy of the serviceProperties
     */
    public Map getServiceProperties();

    /*
     * Set generic property
     */
    public void addProperty(String name, String value);

    public String toString();

    public long getAlertErrorThreshold();

    public void setAlertErrorThreshold(long alertErrorThreshold);

    public long getAlertWarningThreshold();

    public void setAlertWarningThreshold(long alertWarninghreshold);

    public String getProperty(String propertyKey);

    public Map getOtherProperties();
}