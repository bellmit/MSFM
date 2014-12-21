package com.cboe.domain.routingProperty.validation;

import com.cboe.domain.AbstractFirmPropertyHandler;
import com.cboe.domain.routingProperty.properties.EnableNewBOBGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;

/*
 * 	This class is strict to validate EnableNewBobGroup firm property.
 *  The Firm key in this property should only allow default value.
 * 
 */

public class ValidateDefaultFirm extends AbstracBasePropertyGroupValidator
{
	public ValidateDefaultFirm(){
		
	}

	@Override
    public boolean isValid(BasePropertyGroup basePropertyGroup, StringBuffer validationReport)
    {
		boolean isValid = true;
		if(EnableNewBOBGroup.FIRM_PROPERTY_TYPE.equals(basePropertyGroup.getType())){
			if(!AbstractFirmPropertyHandler.DEFAULT_STR_VALUE.equals(basePropertyGroup.getFirmNumber()) &&
					!AbstractFirmPropertyHandler.DEFAULT_STR_VALUE.equals(basePropertyGroup.getExchangeAcronym())){
				validationReport.append("Invalid Firm and Exchange key - Both firm and Exchange should be defaulted\n");
				isValid = false;
			}
		}
		return isValid;
		
    }
}
