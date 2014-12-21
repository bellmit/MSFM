package com.cboe.domain;

import com.cboe.domain.routingProperty.RoutingPropertyHelper;
import com.cboe.idl.cmiConstants.ProductClass;
import com.cboe.interfaces.domain.IAffiliatedFirmPropertyHandler;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;


public abstract class AbstractAffiliatedFirmPropertyHandler
{
    //  constants
	public static final int NOT_FOUND = -1;
    public static final String DEFAULT_STR_VALUE = RoutingPropertyHelper.DEFAULT_STR_VALUE;
    public static final int DEFAULT_CLASS_KEY = ProductClass.DEFAULT_CLASS_KEY;

    public void addProperties(BasePropertyKey aKey, BasePropertyGroup property)
	{
        updateCache(IAffiliatedFirmPropertyHandler.ADD, aKey, property);
    }
    
    public void updateProperties(BasePropertyKey aKey, BasePropertyGroup property)
	{
        updateCache(IAffiliatedFirmPropertyHandler.UPDATE, aKey, property);
    }
    
    public void removeProperties(BasePropertyKey aKey, BasePropertyGroup property)
	{
        updateCache(IAffiliatedFirmPropertyHandler.REMOVE, aKey, property);
    }
    
    public abstract void updateCache(String command, BasePropertyKey aKey, BasePropertyGroup property);

}
