/**
 * 
 */
package com.cboe.domain.tradingProperty;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * @author misbahud
 *
 */
public class AutoLinkDisqualifiedExchangesImplBeanInfo  extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

    public PropertyDescriptor[] getPropertyDescriptors()
    {

       if(allowedDescriptors == null)
        {
            allowedDescriptors = new PropertyDescriptor[2];
            try
            {
                allowedDescriptors[0] =
                    new PropertyDescriptor("autoLinkDisqualifiedExchanges", AutoLinkDisqualifiedExchangesImpl.class);
                allowedDescriptors[1] =
                    new PropertyDescriptor("autoLinkDisqualifiedExchangesFlag", AutoLinkDisqualifiedExchangesImpl.class);
            }
            catch(IntrospectionException e)
            {
                Log.exception("Could not create PropertyDescriptor.", e);
                allowedDescriptors = null;
                return super.getPropertyDescriptors();
            }
        }
        return allowedDescriptors;
    }

    public int getDefaultPropertyIndex()
    {
        return 0;
    }
}