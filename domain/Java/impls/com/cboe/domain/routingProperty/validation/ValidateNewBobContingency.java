package com.cboe.domain.routingProperty.validation;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import com.cboe.domain.routingProperty.common.OriginCode;
import com.cboe.domain.routingProperty.key.SessionClassOriginKey;
import com.cboe.domain.routingProperty.properties.NewBobOriginCodeContingencyTypeMappingGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;

public class ValidateNewBobContingency extends AbstracBasePropertyGroupValidator
{

	@Override
    public boolean isValid(BasePropertyGroup basePropertyGroup, StringBuffer validationReport)
    {
	    boolean retVal = true;
	    
	    if(NewBobOriginCodeContingencyTypeMappingGroup.ROUTING_PROPERTY_TYPE.equals(basePropertyGroup.getType())){
	    	NewBobOriginCodeContingencyTypeMappingGroup group = (NewBobOriginCodeContingencyTypeMappingGroup)basePropertyGroup;
	    	
	    	if(group.getOriginCodeContingencyTypeMapping().length == 0)
	    	{
	    		retVal = false;
	    	}
	    }
	    if(!retVal){
	    	validationReport.append("Invalid number of contingencies. \n Please specify atleast one contingency \n");
	    }
	    return retVal;
	    
    }

}
