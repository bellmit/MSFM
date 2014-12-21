package com.cboe.domain.tradingProperty;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * @author montiel
 *
 */
public class MKTOrderDrillThroughPenniesImplBeanInfo  extends SimpleBeanInfo
{
    private PropertyDescriptor[] allowedDescriptors;

        /**
         * Returns the property descriptors for minimumNBBORange, maximumNBBORange, and noOfPennies methods only
         */
        public PropertyDescriptor[] getPropertyDescriptors()
        {
            if(allowedDescriptors == null)
            {
                allowedDescriptors = new PropertyDescriptor[3];
                try
                {
                    allowedDescriptors[0] =
                        new PropertyDescriptor("minimumNBBORange", MKTOrderDrillThroughPenniesImpl.class );
                    allowedDescriptors[1] =
                        new PropertyDescriptor("maximumNBBORange", MKTOrderDrillThroughPenniesImpl.class);
                    allowedDescriptors[2] =
                        new PropertyDescriptor("noOfPennies", MKTOrderDrillThroughPenniesImpl.class );

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

        /**
         * Returns the default property index for the minimumNBBORange.
         */
        public int getDefaultPropertyIndex()
        {
            return 0;
        }
}