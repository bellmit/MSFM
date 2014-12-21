package com.cboe.domain.routingProperty.common;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class ContingencyTypeListBasePropertyImplBeanInfo extends SimpleBeanInfo
{
	  private PropertyDescriptor[] allowedDescriptors;

	    /**
	     * Returns the property descriptor for the destinationListValue method only
	     */
	    public PropertyDescriptor[] getPropertyDescriptors()
	    {
	        if(allowedDescriptors == null)
	        {
	            allowedDescriptors = new PropertyDescriptor[1];
	            try
	            {
	                allowedDescriptors[0] =
	                        new IndexedPropertyDescriptor("contingencyTypeListValue", ContingencyTypeListBasePropertyImpl.class);
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
	     * Returns the default property index for the contingencyTypeListValue.
	     */
	    public int getDefaultPropertyIndex()
	    {
	        return 0;
	    }
}
