package com.cboe.domain.startup;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
/**
 * Class meant to be the base home class for all server homes.  
 * Currently only have some properties that need to set in the process xml.
 * @author liangc, chenj
 *
 */
public abstract class ServerBOHome extends BOHome
{
    protected Boolean instrumentationEnabled;
    protected Boolean privateOnly;
    public final static String INSTRUMENTATION_ENABLED = ".enableInstrumentation";
    public final static String INSTRUMENTATION_PRIVATE = ".privateInstrumenation";
    protected boolean enable_defaultValue = false;
    protected boolean private_defaultValue = true;
    
    public boolean getInstrumentationEnablementProperty()
    {
        if(instrumentationEnabled == null)
        {
            try {
                ConfigurationService config = FoundationFramework.getInstance().getConfigService();
                String instrumentationProperty = config.getProperty(getFrameworkFullName()+INSTRUMENTATION_ENABLED, null);
                if(instrumentationProperty == null)
                {
                    instrumentationEnabled = new Boolean(enable_defaultValue);
                }
                else
                {
                    instrumentationEnabled = new Boolean(instrumentationProperty);
                    Log.information(this, "instrumentationEnabled: " + instrumentationEnabled);
                }

            } catch (Exception e)
            {
                Log.information(this, "Could not get enableInstrumentation property.  Using default setting("+enable_defaultValue+")");
                instrumentationEnabled = new Boolean(enable_defaultValue);
            }
        }
        return instrumentationEnabled.booleanValue();
    }


    public boolean getInstrumentationProperty()
    {
        if(privateOnly == null)
        {
            try {
                ConfigurationService config = FoundationFramework.getInstance().getConfigService();
                String instrumentationProperty = config.getProperty(getFrameworkFullName()+INSTRUMENTATION_PRIVATE, null);
                if(instrumentationProperty == null)
                {
                    privateOnly = new Boolean(private_defaultValue);
                }
                else
                {
                    privateOnly = new Boolean(instrumentationProperty);
                    Log.information(this, "instrumentationProperty:privateOnly: "+ privateOnly);                    
                }
                
            } catch (Exception e)
            {
                Log.information(this, "Could not get privateInstrumentation property.  Using default setting("+private_defaultValue+")");
                privateOnly = new Boolean(private_defaultValue);
            }
        }
        return privateOnly.booleanValue();
    }
}