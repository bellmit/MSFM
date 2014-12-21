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
public class ProxyBOHome extends ServerBOHome
{
    protected Integer rttTimeOutValue = null;
    protected static final int RTT_TIMEOUT_DEFAULT_VALUE = 0;
    public final static String RTT_TIME_OUT = ".roundTripTimeoutValue";
    
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
