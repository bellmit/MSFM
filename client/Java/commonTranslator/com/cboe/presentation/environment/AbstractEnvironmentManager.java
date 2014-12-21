package com.cboe.presentation.environment;

import java.util.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.net.URL;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.properties.PropertiesFile;


/**
 * This class is the default implementation of the EnvironmentManager interface. It is responsible for reading and
 * processing the environment definition data stored in the application properties file.
 * <p/>
 * It has a notion of the current environment that the user is connected to, as well as the default environment that a
 * user can connect to.
 * <p/>
 * These properties are loaded once at startup and not re-read.  When initially loaded, some token substitution is
 * performed on the following tokens: %pid%   	The process identifier of the application.  This value defined by the
 * System property PID, or a random number from 1024-64000 (NT). %host%	The name of the machine this application is
 * executing on.   This value is defined by the value InetAddress.getLocalHost().getHostName()
 */
public abstract class AbstractEnvironmentManager implements EnvironmentManager
{

    protected static final String ENVIRONMENT_LIST_SECTION_HEADING = "Environments";
    protected static final String SYS_PID_PROPERTY_KEY = "PID";
    protected static final String SYS_HOSTNAME_PROPERTY_KEY = "HOSTNAME";
    protected static final String SYS_HOSTNAME_PROPERTY_DEFAULT = "localhost";


    protected static final String APP_ALL_SVC_PROPERTY_KEY      = "AllServiceNames";
    protected static final String APP_ALL_ENV_PROPERTY_KEY      = "AllEnvironments";
    protected static final String APP_ALL_OTHER_PROPERTY_KEY    = "AllOtherEnvironmentProperty";
    protected static final String APP_AUTO_CONNECT_PROPERTY_KEY = "AutoConnect";

    protected static final String APP_PID_TOKEN = "%pid%";
    protected static final String APP_HOST_TOKEN = "%host%";
    protected static final String APP_PREFIX_TOKEN = "%sbtPrefix%";

    protected static final String ENV_BASE = "Environment.";
    protected static final String VALUE_LIST_DELIMITER = ",";

    protected Map<String, EnvironmentProperties> environments;
    protected EnvironmentProperties defaultEnvironment;
    protected EnvironmentProperties currentEnvironment;

    protected String pid;
    protected String sbtPrefix;
    protected String host;
    protected boolean autoConnect;

    /**
     * Create an (incomplete) environment This method does not add the environment to the working set (since it isn't
     * complete).
     */
    public abstract EnvironmentProperties createEnvironment(String name);

    /**
     * Create an environment with the minimum "complete" info. This method adds the environment to the working set
     */
    public abstract EnvironmentProperties createEnvironment(String name, String prefix, String iorRef);


    /**
     * Retrieve an EnvironmentProperty by name
     */
    public EnvironmentProperties getEnvironment(String name)
    {
        return environments.get(name);
    }

    /**
     * Properties are loaded implicitly by the EnvironmentManager.  This method returns a collection of all loaded
     * EnvironmentProperties objects Specified by com.cboe.infra.presentation.EnvironmentManager
     *
     * @return The environments loaded by this EnvironmentManager, or an empty collection if there was an error loading
     *         the environment properties.
     */
    public Collection getEnvironments()
    {
        Collection rv = new ArrayList();

        if (environments == null)
        {
            initializeEnvironments();
        }

        if (environments != null)
        {
            rv = environments.values();
        }

        return rv;
    }

    /**
     * Get the default environment.
     *
     * @return Return the default EnvironmetProperties dataset if it has been specified, null otherwise Specified by
     *         com.cboe.infra.presentation.EnvironmentManager
     */
    public EnvironmentProperties getDefaultEnvironment()
    {
        return defaultEnvironment;
    }

    /**
     * Calling this method causes the EnvironmentManager to change the default PropertyEnvironment.  There can only ever
     * be a single default environment. Specified by com.cboe.infra.presentation.EnvironmentManager
     */
    public void setDefaultEnvironment(EnvironmentProperties env)
    {
        if (!environments.values().contains(env) && env != null)
        {
            addEnvironment(env, false);
        }

        defaultEnvironment = env;

        // wait until after we change the default to save,
        // otherwise the change will not be reflected in the props file
        saveEnvironments();
    }

    /**
     * Get the current environment.   The current environment is initially equal to the default environment, if set.
     * Otherwise, the current environment can be specified during the Connect To process
     *
     * @see com.cboe.infra.presentation.actions.ConnectToAction
     */
    public EnvironmentProperties getCurrentEnvironment()
    {
        return currentEnvironment;
    }

    /**
     * Set the current environment.  Typically, the current environment is set by the user selecting options from the
     * ConnectionPropertiesPanel
     */
    public void setCurrentEnvironment(EnvironmentProperties newCurrent)
    {
        // there is ALWAYS a current environment.
        if (newCurrent == null)
        {
            return;
        }

        if (!environments.values().contains(newCurrent))
        {
            // the current environment is not reflected in the props
            // file, so we can go ahead and save before we do the switch
            addEnvironment(newCurrent);
        }

        currentEnvironment = newCurrent;
        sbtPrefix = currentEnvironment.getSBTPrefix();
    }

    /**
     * Add an EnvironmentProperties dataset to the current working set. The duration/scope of this change is for the
     * lifetime of the process only! To persist these changes, call {@link #saveAll() saveAll()}. Specified by
     * com.cboe.infra.presentation.EnvironmentManager
     */
    public void addEnvironment(EnvironmentProperties env)
    {
        addEnvironment(env, true);
    }

    /**
     * Remove an EnvironmentProperties dataset from the current working set. The duration/scope of this change is for
     * the lifetime of the process only! To persist these changes, call {@link #saveAll() saveAll()}. Specified by
     * com.cboe.infra.presentation.EnvironmentManager
     */
    public void removeEnvironment(EnvironmentProperties env)
    {
        environments.remove(env.getName());
    }

    /**
     * Environments are loaded implicitly, but must be saved/persisted explicitly Specified by
     * com.cboe.infra.presentation.EnvironmentManager
     */
    public void saveAll()
    {
        saveEnvironments();
    }

    public boolean isAutoConnect()
    {
        return autoConnect;
    }

    /**
     * This constructor should only by invoked by the EnvironmentManagerFactory
     */
    protected AbstractEnvironmentManager()
    {
        initialize();
        initializeEnvironments();
    }

    protected void initialize()
    {
        pid = System.getProperty(SYS_PID_PROPERTY_KEY);
        if (pid == null)
        {
            pid = Integer.toString((int) (Math.random() * 64000));
        }

        try
        {
            host = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException uhe)
        {
            host = System.getProperty("HOSTNAME", "UNKNOWN_HOST");
        }

        String autoConnectStr = AppPropertiesFileFactory.find().getValue(ENVIRONMENT_LIST_SECTION_HEADING, APP_AUTO_CONNECT_PROPERTY_KEY);
        if (autoConnectStr == null)
        {
            GUILoggerHome.find().alarm("EnvironmentManager", APP_AUTO_CONNECT_PROPERTY_KEY + " is missing from the Properties Files..." );
        }

        autoConnect = Boolean.parseBoolean(autoConnectStr);
    }

    protected  Map<String, String> configureDefaultProperties(String environmentName, Map environmentList)
    {
        Map<String, String> properties = new HashMap<String, String>();
        sbtPrefix = translateIn((String)
                environmentList.get(ENV_BASE + environmentName + '.' + EnvironmentProperties.SBT_PREFIX));

        String ior = translateIn((String) environmentList.get(ENV_BASE + environmentName + "." + EnvironmentProperties.INIT_REF_IOR));
        properties.put(EnvironmentProperties.INIT_REF_IOR, ior.trim());

        String monSvcChnlPrefix = translateIn((String) environmentList.get(ENV_BASE + environmentName + "." + EnvironmentProperties.MONITOR_PREFIX));
        properties.put(EnvironmentProperties.MONITOR_PREFIX, monSvcChnlPrefix.trim());

        String extentMapChnlPrefix = translateIn((String) environmentList.get(ENV_BASE + environmentName + "." + EnvironmentProperties.EXTENT_MAP_PREFIX));
        properties.put(EnvironmentProperties.EXTENT_MAP_PREFIX, extentMapChnlPrefix.trim());

        String processListStr = translateIn((String) environmentList.get(ENV_BASE + environmentName + "." + EnvironmentProperties.PROCESS_LIST_REF));
        properties.put(EnvironmentProperties.PROCESS_LIST_REF, processListStr.trim());

        String alertErrorThresholdStr = translateIn((String) environmentList.get(ENV_BASE + environmentName + "." + EnvironmentProperties.ALERT_ERROR_PREFIX));
        properties.put(EnvironmentProperties.ALERT_ERROR_PREFIX, alertErrorThresholdStr.trim());

        String alertWarningThresholdStr = translateIn((String) environmentList.get(ENV_BASE + environmentName + "." + EnvironmentProperties.ALERT_WARNING_PREFIX));
        properties.put(EnvironmentProperties.ALERT_WARNING_PREFIX, alertWarningThresholdStr.trim());

        String amqServerFailoverStr = translateIn((String) environmentList.get(ENV_BASE + environmentName + "." + EnvironmentProperties.AMQ_SERVER_FAILOVER));
        properties.put(EnvironmentProperties.AMQ_SERVER_FAILOVER, amqServerFailoverStr.trim());

        String amqServerOverrideStr = translateIn((String) environmentList.get(ENV_BASE + environmentName + "." + EnvironmentProperties.AMQ_SERVER_OVERRIDE));
        properties.put(EnvironmentProperties.AMQ_SERVER_OVERRIDE, amqServerOverrideStr.trim());
        return properties;
    }

    protected void configureProcessList(String url, EnvironmentProperties env)
    {
        try
        {
            // test the URL, but we still set it as a string
            URL processListURL = new URL(url);

            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("AbstractEnvironmentManager.initializeEnvironments() - " +
                                           EnvironmentProperties.PROCESS_LIST_REF +
                                           processListURL.toString(), GUILoggerBusinessProperty.COMMON);
            }

            env.setProcessListRef(url);
        }
        catch (MalformedURLException mue)
        {
            GUILoggerHome.find().exception("AbstractEnvironmentManager.initializeEnvironments() - "
                                            + EnvironmentProperties.PROCESS_LIST_REF
                                            + " not a valid URL", mue);
        }
    }

    protected void configureAlerts(Map<String, String> properties, EnvironmentProperties env)
    {

        String alertErrorThresholdStr = properties.get(EnvironmentProperties.ALERT_ERROR_PREFIX);
        String alertWarningThresholdStr = properties.get(EnvironmentProperties.ALERT_WARNING_PREFIX);

        try
        {   // Try to convert the alerts to the right values
            if ((alertErrorThresholdStr != null) && (alertErrorThresholdStr.length() > 0))
            {
                long alertErrorThreshold = Long.parseLong(alertErrorThresholdStr);
                env.setAlertErrorThreshold(alertErrorThreshold);
            }

        }
        catch (NumberFormatException nfe)
        {
            GUILoggerHome.find().exception("AbstractEnvironmentManager.initializeEnvironments() - "
                                            + " not a valid alert error threshold", nfe);
        }

        try
        {   // Try to convert the alerts to the right values
            if ((alertWarningThresholdStr != null) && (alertWarningThresholdStr.length() > 0))
            {
                long alertWarningThreshold = Long.parseLong(alertWarningThresholdStr);
                env.setAlertWarningThreshold(alertWarningThreshold);
            }

        }
        catch (NumberFormatException nfe)
        {
            GUILoggerHome.find().exception("AbstractEnvironmentManager.initializeEnvironments() - "
                                            + " not a valid alert warning threshold", nfe);
        }
    }

    protected  void configureServices(String environmentName, Map environmentList, EnvironmentProperties env)
    {
        String allServiceNames = (String) environmentList.get(APP_ALL_SVC_PROPERTY_KEY);
        // now add in all the CORBA service names (for binding to ExtentMap, ProcessWatcher and others)
        if (allServiceNames != null)
        {
            String[] serviceNames = allServiceNames.split(VALUE_LIST_DELIMITER);

            for (int k = 0; k < serviceNames.length; ++k)
            {
                if (GUILoggerHome.find().isDebugOn())
                {                                                       
                    GUILoggerHome.find().debug("Attempting to add service name " + serviceNames[k], GUILoggerBusinessProperty.COMMON);
                }

//                if (serviceNames[k].equals("ORB.OrbName"))
//                {
//                    int i = 0;
//                }

                String name = translateIn((String) environmentList.get(ENV_BASE + environmentName + ".serviceName." + serviceNames[k]));

                if (GUILoggerHome.find().isDebugOn())
                {
                    GUILoggerHome.find().debug("Adding service property " + serviceNames[k] + " with value " + name
                                                + " to environment",
                                                GUILoggerBusinessProperty.COMMON);
                }

                env.addServiceProperty(serviceNames[k].trim(), name);
            }
        }
    }

    protected  void configureOtherEnvironmentProperty(String environmentName, Map environmentList, EnvironmentProperties env)
    {
        String allOtherProperties = (String) environmentList.get(APP_ALL_OTHER_PROPERTY_KEY);
        // now add in all the CORBA service names (for binding to ExtentMap, ProcessWatcher and others)
        if (allOtherProperties != null)
        {
            String[] otherProperties = allOtherProperties.split(VALUE_LIST_DELIMITER);

            for (int k = 0; k < otherProperties.length; ++k)
            {
                if (otherProperties[k].endsWith(".*"))
                {
                    String   otherPropertyKeyPrefix = otherProperties[k].substring(0, otherProperties[k].length() - 2);
                    Iterator envPropertyKeySetIter  = environmentList.keySet().iterator();
                    int      otherPropertyKeyPfxLen = otherPropertyKeyPrefix.length();
                    while (otherPropertyKeyPfxLen > 0  &&  envPropertyKeySetIter.hasNext())
                    {
                        String pfx            = null;
                        String prop           = null;
                        String name           = null;
                        String envPropertyKey = (String) envPropertyKeySetIter.next();
                        if (envPropertyKey.startsWith(ENV_BASE + environmentName + "." + otherPropertyKeyPrefix))
                        {
                            pfx  = ENV_BASE + environmentName + "." + otherPropertyKeyPrefix + ".";
                        }
                        else if (envPropertyKey.startsWith(ENV_BASE + "*." + otherPropertyKeyPrefix))
                        {
                            pfx  = ENV_BASE + ".*" + otherPropertyKeyPrefix + ".";
                        }

                        if (pfx != null)
                        {
                            prop = envPropertyKey.length() > pfx.length() ? envPropertyKey.substring(pfx.length()) : null;
                            name = prop != null ? translateIn((String) environmentList.get(envPropertyKey)) : null;
                            addPropertyToEnv(env, prop, name);
                        }
                    }
                }
                else
                {
                    String name = translateIn((String) environmentList.get(ENV_BASE + environmentName + "." + otherProperties[k]));

                    // if it didn't find it for the specified environment, look for it in the wild-card (*) environement (applies to all env)
                    if (name == null  ||  name.length() < 1)
                    {
                        name = translateIn((String) environmentList.get(ENV_BASE + "*." + otherProperties[k]));
                    }

                    addPropertyToEnv(env, otherProperties[k], name);
                }
            }
        }
    }

    protected void addPropertyToEnv(EnvironmentProperties env, String prop, String name)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("Attempting to add property name=[" + prop.trim() + "] with value=[" + name
                                        + "] to environment", GUILoggerBusinessProperty.COMMON);
        }

        if (prop != null  &&  name != null)
        {
            env.addProperty(prop.trim(), name);
        }
    }

    /*
     * Load the environments definitions from the application properties file,
     * and create the pre-defined EnvironmentProperties objects set.
     * Set the default and current environments, if possible
     */
    protected void initializeEnvironments()
    {
        sbtPrefix = "";

        try
        {
            environments = new HashMap<String, EnvironmentProperties>();
            PropertiesFile propsFile = AppPropertiesFileFactory.find();

            Map environmentList = propsFile.getSection(ENVIRONMENT_LIST_SECTION_HEADING);
            String allEnvironmentsValue = (String) environmentList.get(APP_ALL_ENV_PROPERTY_KEY);

            if (allEnvironmentsValue != null)
            {
                String[] environmentNames = allEnvironmentsValue.split(VALUE_LIST_DELIMITER);

                // for each environment in the list...
                for (int i = 0; i < environmentNames.length; ++i)
                {
                    Map<String, String> tmpProperties = configureDefaultProperties(environmentNames[i], environmentList);

                    String defaultFlag = translateIn((String) environmentList.get(ENV_BASE + environmentNames[i] + ".default"));

                    EnvironmentProperties env = createEnvironment(environmentNames[i].trim(), sbtPrefix.trim(),
                                                                  tmpProperties.get(EnvironmentProperties.INIT_REF_IOR));

                    // create the EnvironmentProperties object with the core properties
                    env.setMonitorPrefix(tmpProperties.get(EnvironmentProperties.MONITOR_PREFIX));
                    env.setExtentMapPrefix(tmpProperties.get(EnvironmentProperties.EXTENT_MAP_PREFIX));
                    env.setAMQServerFailoverURL(tmpProperties.get(EnvironmentProperties.AMQ_SERVER_FAILOVER));
                    env.setAMQServerOverrideURL(tmpProperties.get(EnvironmentProperties.AMQ_SERVER_OVERRIDE));

                    String processListRef = tmpProperties.get(EnvironmentProperties.PROCESS_LIST_REF);
                    if(processListRef != null && processListRef.length() > 0)
                    {
                        configureProcessList(tmpProperties.get(EnvironmentProperties.PROCESS_LIST_REF), env);
                    }
                    
                    configureAlerts(tmpProperties, env);

                    if (GUILoggerHome.find().isDebugOn())
                    {
                        GUILoggerHome.find().debug("Created environment " + env, GUILoggerBusinessProperty.COMMON);
                    }

                    configureServices(environmentNames[i], environmentList, env);
                    configureOtherEnvironmentProperty(environmentNames[i], environmentList, env);

                    environments.put(env.getName(), env);

                    if (GUILoggerHome.find().isDebugOn())
                    {
                        GUILoggerHome.find().debug("Added environment " + env + " to environment list", GUILoggerBusinessProperty.COMMON);
                    }

                    boolean isDefault = Boolean.valueOf(defaultFlag);
                    if (isDefault)
                    {
                        defaultEnvironment = env;
                    }
                }
            }
            else
            {
                if (GUILoggerHome.find().isDebugOn())
                {
                    GUILoggerHome.find().debug("No environments specified in application properties.", GUILoggerBusinessProperty.COMMON);
                }
            }
        }
        catch (Throwable t)
        {
            GUILoggerHome.find().exception(t);
        }
        finally
        {
            sbtPrefix = "";
        }

    }

    protected String translateIn(String raw)
    {
        String rv = (raw == null) ? "" : raw;
        if (rv.indexOf(APP_PREFIX_TOKEN) != -1)
        {
            rv = rv.replaceAll(APP_PREFIX_TOKEN, sbtPrefix);
        }

        if (rv != null && rv.indexOf(APP_PID_TOKEN) != -1)
        {
            rv = rv.replaceAll(APP_PID_TOKEN, pid);
        }

        if (rv != null && rv.indexOf(APP_HOST_TOKEN) != -1)
        {
            rv = rv.replaceAll(APP_HOST_TOKEN, host);
        }
        
        if (rv != null)
        {
            rv = rv.trim();
        }

        return rv; 
    }

    protected String translateOut(String tokenized)
    {
        String rv = (tokenized == null) ? "" : tokenized;
        if (sbtPrefix != null && sbtPrefix.length() > 0 && tokenized.indexOf(sbtPrefix) != -1)
        {
            rv = tokenized.replaceAll(sbtPrefix, APP_PREFIX_TOKEN);
        }

        if (rv != null && rv.indexOf(pid) != -1)
        {
            rv = rv.replaceAll(pid, APP_PID_TOKEN);
        }

        if (rv != null && rv.indexOf(host) != -1)
        {
            rv = rv.replaceAll(host, APP_HOST_TOKEN);
        }
        
        if (rv != null)
        {
            rv = rv.trim();
        }

        return rv;
    }

    protected void addEnvironment(EnvironmentProperties env, boolean shouldSave)
    {
        if (!environments.containsKey(env.getName()))
        {
            environments.put(env.getName(), env);
            registerEnvironment(env);
        }

        if (shouldSave)
        {
            saveEnvironments();
        }
    }


    protected void registerEnvironment(EnvironmentProperties env)
    {
        PropertiesFile propsFile = AppPropertiesFileFactory.find();
        String envList = propsFile.getValue(ENVIRONMENT_LIST_SECTION_HEADING, "AllEnvironments");
        // we need to add this to the list of environments, otherwise it will never
        // be read in when we initialize an EnvironmentManager
        String envName = env.getName().toUpperCase();
        envList += VALUE_LIST_DELIMITER + envName;
        propsFile.addValue(ENVIRONMENT_LIST_SECTION_HEADING, APP_ALL_ENV_PROPERTY_KEY, envList);

        // now add the environment section itself
        String iorKey = ENV_BASE + envName + "." + EnvironmentProperties.INIT_REF_IOR;
        String prefixKey = ENV_BASE + envName + "." + EnvironmentProperties.SBT_PREFIX;
        String defaultFlagKey = ENV_BASE + envName + ".default";
        String monSvcChannelPrefixKey = ENV_BASE + envName + "." + EnvironmentProperties.MONITOR_PREFIX;
        String extentMapChannelPrefixKey = ENV_BASE + envName + "." + EnvironmentProperties.EXTENT_MAP_PREFIX;
        String processListKey = ENV_BASE + envName + "." + EnvironmentProperties.PROCESS_LIST_REF;
        String alertErrorPrefixKey = ENV_BASE + envName + "." + EnvironmentProperties.ALERT_ERROR_PREFIX;
        String alertWarningPrefixKey = ENV_BASE + envName + "." + EnvironmentProperties.ALERT_WARNING_PREFIX;

        propsFile.addValue(ENVIRONMENT_LIST_SECTION_HEADING, iorKey, env.getInitIORRef());
        propsFile.addValue(ENVIRONMENT_LIST_SECTION_HEADING, prefixKey, env.getSBTPrefix());
        propsFile.addValue(ENVIRONMENT_LIST_SECTION_HEADING, defaultFlagKey, "false");
        propsFile.addValue(ENVIRONMENT_LIST_SECTION_HEADING, monSvcChannelPrefixKey, env.getMonitorPrefix());
        propsFile.addValue(ENVIRONMENT_LIST_SECTION_HEADING, extentMapChannelPrefixKey, env.getExtentMapPrefix());
        propsFile.addValue(ENVIRONMENT_LIST_SECTION_HEADING, processListKey, env.getProcessListRef().toString());
        propsFile.addValue(ENVIRONMENT_LIST_SECTION_HEADING, alertErrorPrefixKey,
                           Long.toString(env.getAlertErrorThreshold()));
        propsFile.addValue(ENVIRONMENT_LIST_SECTION_HEADING, alertWarningPrefixKey,
                           Long.toString(env.getAlertWarningThreshold()));

        Map svcProps = env.getServiceProperties();
        Iterator spIter = svcProps.keySet().iterator();
        while (spIter.hasNext())
        {
            String key = (String) spIter.next();
            String val = (String) svcProps.get(key);
            propsFile.addValue(ENVIRONMENT_LIST_SECTION_HEADING, ENV_BASE + envName + ".serviceName." + key, val);
        }
    }

    protected boolean saveEnvironments()
    {
        boolean rv = true;
        // find the current default env flag
        PropertiesFile propsFile = AppPropertiesFileFactory.find();
        Map environmentList = propsFile.getSection(ENVIRONMENT_LIST_SECTION_HEADING);
        String allEnvironmentsValue = (String) environmentList.get(APP_ALL_ENV_PROPERTY_KEY);

        if (allEnvironmentsValue != null)
        {
            String[] environmentNames = allEnvironmentsValue.split(VALUE_LIST_DELIMITER);
            // for each environment that we read in on initialization (or later added) ...
            for (int m = 0; m < environmentNames.length; ++m)
            {
                // first, replace the actual sbt_prefix String (e.g. "Prod") with the
                // token "%sbtPrefix%, so that when we read them in again, the token
                // substitution works
                EnvironmentProperties ep = environments.get(environmentNames[m]);

                Map svcProps = ep.getServiceProperties();
                Iterator propKeys = svcProps.keySet().iterator();

                while (propKeys.hasNext())
                {
                    String key = (String) propKeys.next();
                    String val = (String) svcProps.get(key);
                    String newVal = translateOut(val);
//					val.replaceFirst(ep.getSBTPrefix(),APP_PREFIX_TOKEN);
                    propsFile.addValue(ENVIRONMENT_LIST_SECTION_HEADING,
                                       ENV_BASE + environmentNames[m] + ".serviceName." + key, newVal);
                }
                // if it purports to be the default environment
                if (defaultEnvironment != null && defaultEnvironment.equals(environments.get(environmentNames[m])))
                {
                    propsFile.addValue(ENVIRONMENT_LIST_SECTION_HEADING, ENV_BASE + environmentNames[m] + ".default",
                                       "true");
                }
                else
                {
                    // reset it to default=false
                    propsFile.addValue(ENVIRONMENT_LIST_SECTION_HEADING, ENV_BASE + environmentNames[m] + ".default",
                                       "false");
                }
            }
        }

        // now that all is in order, save
        try
        {
            propsFile.save();
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception("Error while saving environments to properties file: " + e.getMessage(), e);
            rv = false;
        }

        return rv;
    }
}