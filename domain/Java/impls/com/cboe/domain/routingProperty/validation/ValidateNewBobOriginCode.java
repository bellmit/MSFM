package com.cboe.domain.routingProperty.validation;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import com.cboe.domain.routingProperty.common.OriginCode;
import com.cboe.domain.routingProperty.key.SessionClassOriginKey;
import com.cboe.domain.routingProperty.properties.NewBobOriginCodeContingencyTypeMappingGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;

public class ValidateNewBobOriginCode extends AbstracBasePropertyGroupValidator
{

	@Override
    public boolean isValid(BasePropertyGroup basePropertyGroup, StringBuffer validationReport)
    {
	    boolean retVal = true;
	    
	    if(NewBobOriginCodeContingencyTypeMappingGroup.ROUTING_PROPERTY_TYPE.equals(basePropertyGroup.getType())){
	    	retVal = false;
	    	NewBobOriginCodeContingencyTypeMappingGroup group = (NewBobOriginCodeContingencyTypeMappingGroup)basePropertyGroup;
	    	
	    	if(group.getPropertyKey() instanceof SessionClassOriginKey){
	    		SessionClassOriginKey key = (SessionClassOriginKey)group.getPropertyKey() ;
	    		try
                {
	               OriginCode origin = (OriginCode)key.getFieldValue( new PropertyDescriptor(SessionClassOriginKey.ORIGIN_CODE_PROPERTY_NAME, SessionClassOriginKey.class));
	               switch(origin.originCode){
	            	   case 'B':
	            	   case 'C':
	            	   case 'F':
	            	   case 'I':   
	            	   case 'K':
	            	   case 'M':
	            	   case 'N':
	            	   case 'W':
	            	   case 'X':
	            	   case 'Y':
	            		   retVal = true;
	            		   break;
	            	   default:
	            		   retVal = false;
	               }
	               
	               
                }
                catch (IllegalAccessException e)
                {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                }
                catch (IntrospectionException e)
                {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                }
	    	}
	    }
	    if(!retVal){
	    	validationReport.append("Invalid Origin Code key \n");
	    	validationReport.append("Valid Origin Codes are \"B,C,F,I,K,M,N,W,X,Y\" \n");
	    }
	    return retVal;
	    
    }

}
