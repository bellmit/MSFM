package com.cboe.domain.startup;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

public abstract class ClientBOHome extends BOHome
{
    protected boolean initializationErrorFatal;
    protected boolean startupErrorFatal;
    protected Boolean instrumentationEnabled;
    protected Boolean privateOnly;
    public final static String INSTRUMENTATION_ENABLED = ".enableInstrumentation";
    public final static String INSTRUMENTATION_PRIVATE = ".privateInstrumenation";
    protected boolean enable_defaultValue = false;
    protected boolean private_defaultValue = true;

    public ClientBOHome()
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

    public void clientInitialize()
        throws Exception
    {
        // to be implemented
    }

    public void clientStart()
        throws Exception
    {
        // to be implemented
    }

    public void clientShutdown()
        throws Exception
    {
        // to be implemented
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
        if (StartupHelper.getStartupStatus() != StartupHelper.ERROR)
        {
            try {
                clientInitialize();
                super.initialize();
            } catch (Exception e)
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
        } else
        {
            Log.alarm(this, "Initialize skipped because of previously logged fatal startup exception");
        }
    }

    public final void start()
    {
        if (StartupHelper.getStartupStatus() != StartupHelper.ERROR)
        {
            try {
                clientStart();
                super.start();
            } catch (Exception e)
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
        } else
        {
            Log.alarm(this, "Start skipped because of previously logged fatal startup exception");
        }
    }

    public final void shutdown()
    {
        try {
            clientShutdown();
            super.shutdown();
        } catch (Exception e)
        {
            Log.exception(this, e);
        }
    }

    protected boolean getInstrumentationEnablementProperty()
    {
        if(instrumentationEnabled == null)
        {
            try {
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
            } catch (Exception e)
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
            try {
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
            } catch (Exception e)
            {
                Log.information(this, "Could not get privateInstrumentation property.  Using default setting("+private_defaultValue+")");
                privateOnly = Boolean.valueOf(private_defaultValue);
            }
        }
        return privateOnly.booleanValue();
    }
}