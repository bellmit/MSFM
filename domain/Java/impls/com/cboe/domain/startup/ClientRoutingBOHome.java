package com.cboe.domain.startup;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

/**
 * Class meant to be the base home class for all Proxy homes.
 * Routing Proxy that need to set RTT time out can use this home to automatically
 * getting the time out value.  Process xml will need to change to add value for 
 * roundTripTimeoutValue.  If this is not defined, RTT timeout is turned off.
 *   
 * Currently only have some properties that need to set in the process xml.
 * @author liangc, chenj
 *
 */
public abstract class ClientRoutingBOHome extends BOHome
{
    protected Integer rttTimeOutValue = null;
    protected static final int RTT_TIMEOUT_DEFAULT_VALUE = 0;
    
    public final static String RTT_TIME_OUT = ".roundTripTimeoutValue";
    protected boolean initializationErrorFatal;
    protected boolean startupErrorFatal;
    protected Boolean instrumentationEnabled;
    protected Boolean privateOnly;
    public final static String INSTRUMENTATION_ENABLED = ".enableInstrumentation";
    public final static String INSTRUMENTATION_PRIVATE = ".privateInstrumenation";
    protected boolean enable_defaultValue = false;
    protected boolean private_defaultValue = true;
    private static boolean casIsRemote = false;
    private static boolean initialized_casIsRemote = false;
    private static final String CAS_REMOTE = "IsCasRemote";
    
    public ClientRoutingBOHome()
    {
        super();
        initializationErrorFatal = true; // get from config service
        startupErrorFatal = true; // get from config service
        
    }
    
    private void shutdownApplication()
    {
        FoundationFramework ff = FoundationFramework.getInstance();
        ff.shutdownProcess();
    }

    public abstract void clientInitialize() throws Exception;    

    public abstract void clientStart() throws Exception;
   
    public  void clientShutdown() throws Exception
    {
        //To be implemented by subclass if necessary
    }
    
    public void manageFatalException(Exception e)
    {
        Log.exception(this, e);
        Log.alarm(this, "Encountered Fatal Exception during startup.  Aborting Startup!");
        StartupHelper.setStartupStatus(StartupHelper.ERROR);
        shutdownApplication();
    }

    public final void initialize()
    {
        if (!clientIsRemote())
        {
            if (StartupHelper.getStartupStatus() != StartupHelper.ERROR)
            {
                try 
                {
                    clientInitialize();
                    super.initialize();
                } 
                catch (Exception e)
                {
                    if (initializationErrorFatal)
                    {
                        manageFatalException(e);
                    }
                    else
                    {
                        Log.exception(this, e);
                    }
                }
            } 
            else
            {
                Log.alarm(this, "Initialize skipped because of previously logged fatal startup exception");
            }
        }        
    }

    public final void start()
    {
        if (!clientIsRemote())
        {
            if (StartupHelper.getStartupStatus() != StartupHelper.ERROR)
            {
                try 
                {
                    clientStart();
                    super.start();
                } 
                catch (Exception e)
                {
                    if (startupErrorFatal)
                    {
                        manageFatalException(e);
                    }
                    else
                    {
                        Log.exception(this, e);
                    }
                }
            } 
            else
            {
                Log.alarm(this, "Start skipped because of previously logged fatal startup exception");
            }
        }
    }

    public final void shutdown()
    {
        if (!clientIsRemote())
        {
            try 
            {
                clientShutdown();
                super.shutdown();
            } 
            catch (Exception e)
            {
                Log.exception(this, e);
            } 
        }        
    }

    protected boolean getInstrumentationEnablementProperty()
    {
        if(instrumentationEnabled == null)
        {
            try 
            {
                ConfigurationService config = FoundationFramework.getInstance().getConfigService();
                String instrumentationProperty = config.getProperty(getFrameworkFullName()+INSTRUMENTATION_ENABLED, null);
                if(instrumentationProperty == null)
                {
                    Log.information(this, "not found under name:"+getFrameworkFullName()+INSTRUMENTATION_ENABLED);
                    instrumentationEnabled = Boolean.valueOf(enable_defaultValue);
                }
                else
                {
                    instrumentationEnabled = Boolean.valueOf(instrumentationProperty);
                }
            } 
            catch (Exception e)
            {
                Log.information(this, "Could not get enableInstrumentation property.  Using default setting("+enable_defaultValue+")");
                instrumentationEnabled = Boolean.valueOf(enable_defaultValue);
            }
        }
        return instrumentationEnabled.booleanValue();
    }

    protected boolean getInstrumentationProperty()
    {
        if(privateOnly == null)
        {
            try 
            {
                ConfigurationService config = FoundationFramework.getInstance().getConfigService();
                String instrumentationProperty = config.getProperty(getFrameworkFullName()+INSTRUMENTATION_PRIVATE, null);
                if(instrumentationProperty == null)
                {
                    Log.information(this, "not found under name:"+getFrameworkFullName()+INSTRUMENTATION_PRIVATE);
                    privateOnly = Boolean.valueOf(private_defaultValue);
                }
                else
                {
                    privateOnly = Boolean.valueOf(instrumentationProperty);
                }
            } 
            catch (Exception e)
            {
                Log.information(this, "Could not get privateInstrumentation property.  Using default setting("+private_defaultValue+")");
                privateOnly = Boolean.valueOf(private_defaultValue);
            }
        }
        return privateOnly.booleanValue();
    }
   
    /**
     * Indicate whether this program is a remote client or a local client.
     * @return true if remote, false if local.
     */
    public static boolean clientIsRemote()
    {
        if (!initialized_casIsRemote)
        {
            try
            {
                String bool = System.getProperty(CAS_REMOTE);
                if (bool == null)
                {
                    Log.information("Could not find system property " + CAS_REMOTE + ", defaulting to " + casIsRemote);
                }
                else
                {
                    casIsRemote = Boolean.parseBoolean(bool);
                }
            }
            catch(Exception e)
            {
                Log.exception("Error getting system property " + CAS_REMOTE, e);
            }
            initialized_casIsRemote = true;
        }
        return casIsRemote;
    }
    
    /**
     * Returns RTT timeout value in milliseconds
     * @return RTT timeout value in milliseconds
     */
    public int getRoundTripTimeOutValueProperty()
    {
        if(this.rttTimeOutValue == null)
        {
            int rttValue = RTT_TIMEOUT_DEFAULT_VALUE;
            
            try 
            {
                ConfigurationService config = FoundationFramework.getInstance().getConfigService();
                rttValue = config.getInt(getFrameworkFullName()+ RTT_TIME_OUT, RTT_TIMEOUT_DEFAULT_VALUE);
                
                if(rttValue >0 )
                {
                    Log.information(this, "Proxy RTT timeout value (roundTripTimeoutValue in milliseconds) is set at: "+ rttValue);
                }
                else if (rttValue < 0)
                {
                    Log.information(this, "Could not use roundTripTimeoutValue property with negative value: " + rttValue + ".  Using default setting ("+ RTT_TIMEOUT_DEFAULT_VALUE +")");
                    rttValue  = RTT_TIMEOUT_DEFAULT_VALUE;
                }
            } 
            catch (Exception e)
            {
                Log.information(this, "Could not get roundTripTimeoutValue property.  Using default setting ("+ RTT_TIMEOUT_DEFAULT_VALUE +")");
            }
                        
            rttTimeOutValue = new Integer(rttValue);
        }
        return rttTimeOutValue.intValue();
    }
}
