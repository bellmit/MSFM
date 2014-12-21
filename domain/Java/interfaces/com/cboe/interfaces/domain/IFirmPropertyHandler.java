package com.cboe.interfaces.domain;

import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;

public interface IFirmPropertyHandler
{
    public static final String ADD = "ADD";
    public static final String UPDATE = "UPDATE";
    public static final String REMOVE = "REMOVE";
    
    public void addProperties(BasePropertyKey aKey, BasePropertyGroup property);
    
    public void updateProperties(BasePropertyKey aKey, BasePropertyGroup property);
    
    public void removeProperties(BasePropertyKey aKey, BasePropertyGroup property);

}
