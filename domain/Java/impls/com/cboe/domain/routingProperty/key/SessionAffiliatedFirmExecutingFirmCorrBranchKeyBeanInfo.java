package com.cboe.domain.routingProperty.key;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class SessionAffiliatedFirmExecutingFirmCorrBranchKeyBeanInfo extends
		SimpleBeanInfo {
	  private PropertyDescriptor[] allowedDescriptors;

	    public PropertyDescriptor[] getPropertyDescriptors()
	    {
	        if(allowedDescriptors == null)
	        {
	            allowedDescriptors = new PropertyDescriptor[5];
	            try
	            {
	                allowedDescriptors[0] = new PropertyDescriptor(SessionAffiliatedFirmExecutingFirmCorrBranchKey.TRADING_SESSION_PROPERTY_NAME,
	                		SessionAffiliatedFirmExecutingFirmCorrBranchKey.class);

	                allowedDescriptors[1] = new PropertyDescriptor(SessionAffiliatedFirmExecutingFirmCorrBranchKey.AFFILIATED_FIRM_PROPERTY_NAME,
	                		SessionAffiliatedFirmExecutingFirmCorrBranchKey.class);
	                
	             	allowedDescriptors[2] = new PropertyDescriptor(SessionAffiliatedFirmExecutingFirmCorrBranchKey.EXECUTING_FIRM_PROPERTY_NAME,
	                		SessionAffiliatedFirmExecutingFirmCorrBranchKey.class);
	                
	                allowedDescriptors[3] = new PropertyDescriptor(SessionAffiliatedFirmExecutingFirmCorrBranchKey.CORRESPONDENT_PROPERTY_NAME,
	                		SessionAffiliatedFirmExecutingFirmCorrBranchKey.class);

	                allowedDescriptors[4] = new PropertyDescriptor(SessionAffiliatedFirmExecutingFirmCorrBranchKey.BRANCH_PROPERTY_NAME,
	                		SessionAffiliatedFirmExecutingFirmCorrBranchKey.class);
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
	     * Returns the default property index for the booleanValue.
	     */
	    public int getDefaultPropertyIndex()
	    {
	        return 0;
	    }
}
