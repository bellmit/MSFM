package com.cboe.domain;

import com.cboe.domain.routingProperty.RoutingPropertyHelper;
import com.cboe.idl.cmiConstants.ProductClass;
import com.cboe.interfaces.domain.IFirmPropertyHandler;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;


public abstract class AbstractFirmPropertyHandler
{
    //  constants
    public static final int NOT_FOUND = -1;
    public static final String DEFAULT_STR_VALUE = RoutingPropertyHelper.DEFAULT_STR_VALUE;
    public static final int DEFAULT_CLASS_KEY = ProductClass.DEFAULT_CLASS_KEY;
    public static final int DEFAULT_INT_VALUE = RoutingPropertyHelper.DEFAULT_CLASS_KEY;

    public void addProperties(BasePropertyKey aKey, BasePropertyGroup property){
        updateCache(IFirmPropertyHandler.ADD, aKey, property);
    }
    
    public void updateProperties(BasePropertyKey aKey, BasePropertyGroup property){
        updateCache(IFirmPropertyHandler.UPDATE, aKey, property);
    }
    
    public void removeProperties(BasePropertyKey aKey, BasePropertyGroup property){
        updateCache(IFirmPropertyHandler.REMOVE, aKey, property);
    }
    
    public abstract void updateCache(String command, BasePropertyKey aKey, BasePropertyGroup property);

}
